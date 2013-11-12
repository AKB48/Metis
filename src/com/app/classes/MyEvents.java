package com.app.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.Main.EventMainPage;
import com.app.Main.MainActivity;
import com.app.customwidget.PullUpDownView;
import com.app.customwidget.PullUpDownView.onPullListener;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.notificationObject;
import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.RegUtils;
import com.app.util.ReturnCode;
import com.app.util.cardAdapter;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;


import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyEvents {

	private Context context;
	public View myEventsView;
	public PullUpDownView myEventsPullUpDownView;
	public ListView myEventsListView;
//	private Button notificationBtn;
	private onPullListener myEventsPullUpDownViewListener;
	private OnItemClickListener myEventsListViewListener;
	
	public cardAdapter myEventsAdapter;
	public ArrayList<HashMap<String, Object>> myEventsList = new ArrayList<HashMap<String,Object>>();
	
	private Handler uiHandler;
	private int screenWidth;
	private int userId;
	private HttpSender sender;
	private MsgHandler handler;
	private cardHandler msgCard = new cardHandler( Looper.myLooper());
	
	private Set<Integer> photoIdSet = new HashSet<Integer>();
	private int curRequestAvatarId;
	private JSONArray seqJsonArray = null;
	private boolean firstLoad = true, refreshing=false;
	private Vector<Integer> allEventIDList = new Vector<Integer>();
	private Vector<Integer> requestEventIDList = new Vector<Integer>();
	private int requestIndex = 0;
	private imageUtil forImageUtil;
	public BroadcastReceiver join_broadcastReceiver = new JoinBroadcastReceiver();
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -4;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -5;
	private static final int MSG_WHAT_REFRESH_DONE = -6;
	private static final int MSG_WHAT_GET_MORE_DONE = -7;
	
	public MyEvents(Context context, Handler uiHandler, int screenWidth, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myEventsView = LayoutInflater.from(context).inflate(R.layout.menu_my_events, null);
		this.uiHandler = uiHandler;
		this.screenWidth = screenWidth;
		this.userId = userId;
		
		sender = new HttpSender();
		handler = new MsgHandler( Looper.myLooper() );
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("JoinMsg");
		context.registerReceiver(join_broadcastReceiver, intentFilter);
	}
	
	public View getView()
	{
		return this.myEventsView;
	}
	
	public void init() {
		// TODO Auto-generated method stub
		/**
		 * parent	: the adapterview where the click happened
		 * view 	 	: the view within the adapterview that was clicked
		 * position	: the position of the view in the adapter
		 * id 			: the row id of the item that was clicked
		 */
		myEventsListViewListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
//				if(UserInterface.hasScrolled) // 为了解决滑动主界面进入活动详细信息的bug
//				{
//					return;
//				}
				HashMap<String, Object> EventItem = myEventsList.get(pos);
				
				Intent intent = new Intent();
				intent.setClass(context, EventMainPage.class);
					intent.putExtra("theme", (String)EventItem.get("title"));
					intent.putExtra("location", (String)EventItem.get("location"));
					intent.putExtra("participantsNum", (String)EventItem.get("participantsNum"));
					intent.putExtra("launcher", (String)EventItem.get("launcher"));
					intent.putExtra("remark", (String)EventItem.get("remark"));
					intent.putExtra("date", (String)EventItem.get("date"));			
					intent.putExtra("photolistJsonArray", EventItem.get("photolistJsonArray").toString());
					
					intent.putExtra("id", (Integer)EventItem.get("id"));
					intent.putExtra("event_id", (Integer)EventItem.get("event_id"));
					intent.putExtra("launcher_id", (Integer)EventItem.get("launcher_id"));
				context.startActivity(intent);
			}
		};
		
		myEventsPullUpDownViewListener = new onPullListener() {
			
			@Override
			public void Refresh() {
				// TODO Auto-generated method stub
						allEventIDList.clear();
						myEventsList.clear();
						myEventsAdapter.notifyDataSetChanged();
						requestIndex = 0;
						refreshing = true;		
						
						//重新请求活动
						sendRequest(OperationCode.GET_MY_EVENTS);												
			}
			
			@Override
			public void GetMore() {
				// TODO Auto-generated method stub
						if( hasMoreEvent() )
							sendRequest(OperationCode.GET_MY_EVENTS);	
						else
						{
							Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
							msgLoadMore.obj = "After more " + System.currentTimeMillis();
							msgLoadMore.sendToTarget();
//							Log.e("myevents", "*************no more events*********");
						}
			}
		};
		
//		notificationBtn = (Button)myEventsView.findViewById(R.id.menu_my_events_notificationBtn);
//		notificationBtn.setOnClickListener(buttonsOnClickListener);
		myEventsPullUpDownView = (PullUpDownView)myEventsView.findViewById(R.id.my_events_pull_up_down_view);
		myEventsListView = myEventsPullUpDownView.getListView();
		myEventsPullUpDownView.setOnPullListener(myEventsPullUpDownViewListener);
		
		myEventsListView.setItemsCanFocus(false);
		myEventsListView.setOnItemClickListener(myEventsListViewListener);
		
		//edit by luo
		myEventsAdapter = new cardAdapter(context, 
				myEventsList,
				R.layout.activity_item, 
				new String[]{"title", "day", "monthAndYear","time", "location", "launcher", "remark", "participantsNum"}, 
				new int[]{R.id.activityTitle, R.id.day, R.id.monthAndYear, R.id.time, R.id.location, R.id.launcher, R.id.remark, R.id.participantsNum},
				new int[]{R.id.user1, R.id.user2, R.id.user3, R.id.user4},
				screenWidth,
				true,
				userId,
				msgCard
		);
		
		myEventsListView.setAdapter(myEventsAdapter);
		
		//add 11-10
		myEventsView.findViewById( R.id.to_memu ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.what = MainActivity.TO_MENU_CLICKED;
				uiHandler.sendMessage(msg);
			}
		});
		
		myEventsView.findViewById( R.id.ui_addActivityBtn ).setOnClickListener( new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.what = MainActivity.TO_ADD_ACTIVITY;
				uiHandler.sendMessage(msg);
			}
		});
	}
	
	//add by luo
	private void getActivityFrom( String str)
	{
		JSONObject eventInforJson;
		String subject="主题", time="", location="未定", launcher="谁发起的?", remark="没有备注哦oo";
		int member_count = 0;
		String year="0000", month="00", day="00", hour="00", minute="00", second="00";
		JSONArray photolistJsonArray = null;
		int event_id = 0, launcher_id = 0;
		
		try{
			eventInforJson = new JSONObject(str);
			subject = eventInforJson.optString("subject");
			time = eventInforJson.optString("time");      //活动开始时间
			location = eventInforJson.optString("location");
			launcher = eventInforJson.optString("launcher");
			remark = eventInforJson.optString("remark");
			member_count  = eventInforJson.optInt("member_count");
			photolistJsonArray = eventInforJson.optJSONArray("member");
			event_id = eventInforJson.optInt("event_id");
			launcher_id = eventInforJson.optInt("launcher_id");
			getPhotoId(photolistJsonArray);
		}
		catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		//为空的时候，后台返回字符串None
		if( !"None".equals(time) && time.length()>0 )
		{
			year = time.substring(0, 4);
			month = time.substring(5, 7);
			day = time.substring(8, 10);
			hour = time.substring(11, 13);
			minute = time.substring(14, 16);
			second = time.substring(17,19);
		}
		
		if( "None".equals(time) ) 
			time = "0000-00-00 00:00";
				
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", subject);
		map.put("day", day+"");
		map.put("monthAndYear", month + "月" +year);
		map.put("date", time);  //date detail
		map.put("time", hour+":"+minute);
		map.put("location", location);
		map.put("launcher", "by " + launcher);
		map.put("remark", remark);
		map.put("participantsNum", member_count+"");
		map.put("photolistJsonArray", photolistJsonArray);
		map.put("id", userId);
		map.put("event_id", event_id);
		map.put("launcher_id", launcher_id);
		map.put("item_id", -1);
		map.put("open", false);
		myEventsList.add(map);		
	}
	
	//所有活动卡片要显示的头像的并集photoIdSet
	private void getPhotoId(JSONArray photolistJsonArray)
	{
		try {
			for (int i = 0; i < photolistJsonArray.length(); i++) 
			{
				Integer id = (Integer) photolistJsonArray.get(i);
				photoIdSet.add( id );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadData()
	{
	    myEventsList.clear();
	    
		Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
		msg1.sendToTarget();
		
		//get data from server, send a request~   by luo
		sendRequest(OperationCode.GET_MY_EVENTS);
	}
	
	private void sendRequest(final int opCode)
	{
		final int curRequestAvatarIdTemp = curRequestAvatarId;
		new Thread()
		{
				public void run()
				{
					JSONObject params = new JSONObject();
					try
					{
						switch (opCode) {
						case OperationCode.GET_MY_EVENTS:
							params.put("id", userId);
							sender.Httppost(OperationCode.GET_MY_EVENTS, params, handler);
							break;
						case OperationCode.GET_EVENTS:
							params.put("sequence", new JSONArray( requestEventIDList ) );
							sender.Httppost(OperationCode.GET_EVENTS, params, handler);
							break;
						case OperationCode.GET_AVATAR:
							params.put("id", curRequestAvatarIdTemp);
							params.put("operation", 0);
							new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
							break;
						default:
							break;
						}						
					}
					catch (JSONException e) {
			            // TODO Auto-generated catch block
			            e.printStackTrace();
			        }
				}
		}.start();
	}
	
	//向服务器发送请求头像
	private void getAllavatars()
	{
		new Thread()
		{
				public void run()
				{
					for (int id : photoIdSet) {
						//当local不存有头像的时候，才去拉取头像，否则不拉取
						//或者：第一次取活动要拉取头像，后面就不再拉取头像了=============
						if( !imageUtil.fileExist(id) )
						{
							curRequestAvatarId = id;
							sendRequest( OperationCode.GET_AVATAR );
						}
					}
				}
		}.start();
		
	}
	
	private void initRequestEventList()
	{
		//每次申请10条
		int i=0, length = allEventIDList.size();
		requestEventIDList.clear();
		for (; i < 10 &&  requestIndex<length; i++, requestIndex++) 
		{
			requestEventIDList.add( allEventIDList.get(requestIndex) );
		}
	}
	
	private boolean hasMoreEvent()
	{
		return requestIndex < allEventIDList.size();
	}
	
	class cardHandler extends Handler
	{
		public cardHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case cardAdapter.CARD_INFO_CHANGE:
				myEventsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			String returnStr = msg.obj.toString();
			JSONObject returnJson = null;
			
			JSONArray eventJsonArray = null;
			int returnCMD;			
			
			if( returnStr!="DEFAULT")
			{
				
				try{
					returnJson = new JSONObject( returnStr );
					returnCMD = returnJson.optInt("cmd");
					
						switch ( msg.what ) {
							case OperationCode.GET_MY_EVENTS:																			
								
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{						
									seqJsonArray = returnJson.optJSONArray("sequence");
									if( seqJsonArray==null) break;
									
									int length = seqJsonArray.length();
									if( length>0 )
									{
										allEventIDList.clear();
										for( int i=0; i<length; i++)									
											allEventIDList.add( seqJsonArray.getInt(i) );

										initRequestEventList();
//										//使用events sequence请求活动内容
										sendRequest(OperationCode.GET_EVENTS);										
									}
									else										
									{
										Toast.makeText(context, "当前没有活动", Toast.LENGTH_SHORT).show();	
										//load data done; inform user interface
										Message msgLoad = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
										msgLoad.sendToTarget();
										
										Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
										msgLoadMore.obj = "After more " + System.currentTimeMillis();
										msgLoadMore.sendToTarget();
										firstLoad = false;
									}
								}
								else								
									Toast.makeText(context, "get my events返回其他值了"+returnCMD, Toast.LENGTH_SHORT).show();																	
							break;
								
							case OperationCode.GET_EVENTS:
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{
									eventJsonArray = returnJson.optJSONArray("event_list");
									int length = eventJsonArray.length();
									
									//先清空event list
//									myEventsList.clear();
									for( int k=0; k<length; k++)
										getActivityFrom( eventJsonArray.getString(k) );
										
									if( firstLoad==true )
									{
										//load data done; inform user interface
										Message msg2 = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
										msg2.sendToTarget();
										firstLoad = false;
//										Log.i("myevent", "load done");
									}
									else if( refreshing==true )
									{
										//refresh data done
										Message msgRefresh = uiHandler.obtainMessage(MSG_WHAT_REFRESH_DONE);
										msgRefresh.obj = "After refresh " + System.currentTimeMillis();
										msgRefresh.sendToTarget();
										refreshing=false;
//										Log.i("myevent", "refresh done");
									}
									else   //load more
									{
										Message msgLoadMore = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
										msgLoadMore.obj = "After more " + System.currentTimeMillis();
										msgLoadMore.sendToTarget();
									}
								
									getAllavatars();
								}
								else
									Toast.makeText(context, "get events返回其他值了"+returnCMD, Toast.LENGTH_SHORT).show();	
								break;
								
							case OperationCode.GET_AVATAR:
								
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{
									final int avatarForUserId = returnJson.optInt("id");
									final String imageStr = returnJson.optString("avatar");

									new Thread()
									{
										public void run(){
											
											//存放到map里面，告知有内容更新，在初始化卡片的时候使用
											byte[] temp = imageUtil.String2Bytes(imageStr);
											 try {
										            if(temp!=null)
										            {
										                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);												                										                
										                imageUtil.getInstance().addBitmapToMemoryCache(avatarForUserId, bitmap);

										                //notify card adapter
										                Message msg = new Message();
														msg.what = cardAdapter.CARD_INFO_CHANGE;
														msgCard.sendMessage(msg);
														
														//记得要在子线程中写文件
														imageUtil.savePhoto(avatarForUserId, bitmap);
										            }
										        } catch (Exception e) {
										            // TODO Auto-generated catch block
										            e.printStackTrace();
										        } 
										}
									}.start();
																		
//									myEventsAdapter.notifyDataSetChanged();
								}
								else if ( returnCMD==ReturnCode.REQUEST_FAIL ) 
								{
//									Log.e("myevent", "请求头像失败");
								}
								break;
								
							default:
								break;
							}
						}
						catch (JSONException e) {
				            // TODO Auto-generated catch block
				            e.printStackTrace();
				        }
				
			}
			else
			{
				Toast.makeText(context, "服务器请求超时", Toast.LENGTH_SHORT).show();
			}	
		}
	}
	
	class JoinBroadcastReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if ( "JoinMsg".equals(action) ) {
				int item_id = intent.getIntExtra("item_id", 0);
				
				NotificationTableAdapter adapter = new NotificationTableAdapter(context);
				String str = adapter.queryOneMsgData(item_id);
				joinActivityFrom(str);
				myEventsAdapter.notifyDataSetChanged();
				adapter.deleteData( item_id );
			}
		}
		
	}
	
	private void joinActivityFrom( String str)
	{
		JSONObject eventInforJson;
		String subject="主题", time="", location="未定", launcher="谁发起的?", remark="没有备注哦oo";
		int member_count = 0;
		String year="0000", month="00", day="00", hour="00", minute="00", second="00";
		JSONArray photolistJsonArray = null;
		int event_id = 0, launcher_id = 0;
		
		try{
			eventInforJson = new JSONObject(str);
			subject = eventInforJson.optString("subject");
			time = eventInforJson.optString("time");      //活动开始时间
			location = eventInforJson.optString("location");
			launcher = eventInforJson.optString("launcher");
			remark = eventInforJson.optString("remark");
			member_count  = eventInforJson.optInt("member_count");
			photolistJsonArray = eventInforJson.optJSONArray("member");
			event_id = eventInforJson.optInt("event_id");
			launcher_id = eventInforJson.optInt("launcher_id");
			getPhotoId(photolistJsonArray);
		}
		catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		//为空的时候，后台返回字符串None
		if( !"None".equals(time) && time.length()>0 )
		{
			year = time.substring(0, 4);
			month = time.substring(5, 7);
			day = time.substring(8, 10);
			hour = time.substring(11, 13);
			minute = time.substring(14, 16);
			second = time.substring(17,19);
		}
		
		if( "None".equals(time) ) 
			time = "0000-00-00 00:00";
				
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", subject);
		map.put("day", day+"");
		map.put("monthAndYear", month + "月" +year);
		map.put("date", time);  //date detail
		map.put("time", hour+":"+minute);
		map.put("location", location);
		map.put("launcher", "by " + launcher);
		map.put("remark", remark);
		map.put("participantsNum", member_count+"");
		map.put("photolistJsonArray", photolistJsonArray);
		map.put("id", userId);
		map.put("event_id", event_id);
		map.put("launcher_id", launcher_id);
		map.put("item_id", -1);
		map.put("open", false);
		
//		myEventsList.add(map);	
		myEventsList.add(0, map);
	}
}
