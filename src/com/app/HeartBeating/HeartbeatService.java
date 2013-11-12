package com.app.HeartBeating;

import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

public class HeartbeatService extends Service implements Runnable {

	private static final String TAG = "HeartBeatService";
	
	private int 			count 		= 			0;				//the num of heart-beat packages have been sent without feedback
	private boolean 	isTip 			= 			true;			//是否给应用发送offline提示
	private int 			sleepSec 	=         	10;
	private Thread 		mThread;
	private int 		uid;
	private boolean 	serviceStop = false;
	private boolean 	bolt = true;
	
	private WebSocketUtil postHelper;
	private MsgHandler handler;
	private long MsgSeq = 0;
	private int MsgNum = 0;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		handler = new MsgHandler( Looper.myLooper());
		
		super.onCreate();
	}
	
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		uid = intent.getExtras().getInt("uid");
		postHelper = new WebSocketUtil(uid, handler);
		postHelper.connectToServer();
		
		mThread = new Thread(this);
		mThread.start();
		count = 0;
		
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "======> service ondestroy <=======");
		postHelper.disconToServer();
		serviceStop = true;
		
		super.onDestroy();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//service 没有停止（没有关闭app ）
		//且 连接上了推送服务器server
		//就发送心跳包
		while (  !serviceStop ) 
		{
			try {
				//超过1个心跳包没有得到feedback了： offline 
				if (count>1) 
				{
					Log.i(TAG, "offline");
					count = 1;
					if( isTip )     //是否给应用发送offline提示
					{
						Log.i(TAG, "======> offline <=======");
						postHelper.setIsConnect(false);
						//提示了一次之后，就不再提示
						isTip = false;
					}
				}
				
				//send an empty package
				if( postHelper.isconnect() )
				{
//					Log.i(TAG, "======> send an empty package <=======");
					postHelper.send("");
					count++;
				}
				else 
				{
//					Log.i(TAG, "======> try to connect again <=======");
					postHelper.connectToServer();
				}
											
				Thread.sleep(1000 * sleepSec);    //send empty package/3s
			} catch (Exception e) {
				e.printStackTrace();
			}
		}//end while
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 处理推送服务器返回的消息
	 * 可能是心跳包feedback
	 * 也可能是推送的消息
	 */
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper) {
			// TODO Auto-generated constructor stub
			super(looper);
		}
		
		public void handleMessage(Message msg) 
		{
//			Log.i(TAG, "=====> receive post message: " + msg.toString());
			String seqStr = null;
			
			String returnStr = msg.obj.toString();
			JSONObject returnJson = null;
			
			try {									
					if ( "".equals(returnStr) ) 		//如果返回心跳包的feedback
					{
						count = 0;
						isTip = true;
					}
					else 
					{
//						Log.i(TAG, "returnStr: " + returnStr);
						returnJson = new JSONObject(returnStr);
						String cmd = returnJson.optString("cmd");
						//not sync, msg+seq
						if ( cmd == "" ) 
						{
							String returnMsgStr= returnJson.optString("msg");
							
							//msg not empty
								if ( "".equals(returnMsgStr) == false) 
								{   
										//can receive msg
										if( MsgNum>0 )
										{
	//										MsgSeq = returnJson.optInt("seq");
											
											seqStr = returnJson.optString("seq");
											MsgSeq = Long.parseLong(  seqStr );		
	//										MsgSeq = Long.parseLong(  seqStr.substring(0, seqStr.length()) );										
											
											MsgNum--;	
											Log.e(TAG, "post msg: " + returnMsgStr + "-------rest msg num= " + MsgNum + " seq: " + MsgSeq);
											
//											try{
//												JSONObject test = new JSONObject( returnMsgStr);
//												Log.e(TAG, "confirm msg: " + test.getString("confirm_msg") );
//											}
//											catch (JSONException e) {
//												// TODO: handle exception
//												e.printStackTrace();
//											}
											
											Intent intent = new Intent("postMsg");
											intent.putExtra("postMsg", returnMsgStr);
											sendBroadcast(intent);	
											
											if ( MsgNum==0 ) 
											{
												JSONObject params = new JSONObject();
												params.put("cmd", "feedback");
												params.put("uid", uid);
												params.put("seq", MsgSeq);
												postHelper.send( params.toString() );
												bolt = true;
												Log.e(TAG, "feed back: " + params.toString());
											}		
										}
									}
									else {   //msg==""//record seq
			//							MsgSeq = returnJson.optLong("seq");
										seqStr = returnJson.optString("seq");
										MsgSeq = Long.parseLong(  seqStr );
//										Log.e(TAG, "seq initialize-----------------------------------------------------------<");
										
										Intent intent = new Intent("connected");
										intent.putExtra("connected", true);
										sendBroadcast(intent);
										Log.e(TAG, "connected, seq: " + seqStr);
									}
						}
						//sync + num
					else if ( "sync".equals(cmd) )      
					{													
						if ( bolt==true ) 
						{
							MsgNum += returnJson.optInt("num");

							if( MsgNum!=0 )
							{
								JSONObject params = new JSONObject();
								params.put("cmd", "request");
								params.put("uid", uid);
//									params.put("seq", MsgSeq);
								postHelper.send( params.toString() );
								bolt = false;                                                       //sync: close
							}
						}
						else
							Log.e(TAG, "被屏蔽的sync");
					}										
				}
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
								
			super.handleMessage(msg);
		}
		
		private boolean isNumeric(String str)
		{
			Pattern pattern = Pattern.compile("[0-9]*");
			return pattern.matcher(str).matches();
		}
	}

}
