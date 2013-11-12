package com.app.HeartBeating;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WebSocketUtil
{
	protected static final String TAG = "WebSocketUtil";
	private WebSocketClient client;
	private int uid;
	private Handler handler;
	private boolean connectOrNot = false;
	
	public WebSocketUtil() {
		Log.e(TAG, "*****construct of websocketutil: parameter not enough*****");
	}
	
	public WebSocketUtil(int uid, Handler handler)
	{
		this.uid = uid;
		this.handler = handler;

//		client = new WebSocketClient(URI.create("ws://172.18.186.175:10088/"), socketListener, null);
		client = new WebSocketClient(URI.create("ws://222.200.182.183:10088/"), socketListener, null);
	}
	
	public void connectToServer()
	{
//		Log.i(TAG, "***** begin to connect post server...  *****");
		client.connect();
	}
	
	public void disconToServer()
	{
		Log.i(TAG, "***** disconnect from post server...  *****");
		client.disconnect();
	}
	
	private Listener socketListener = new Listener() {
		
		@Override
		public void onMessage(byte[] data) {
			// TODO Auto-generated method stub
			Log.i(TAG, String.format("*****Got binary message!*****"));
		}
		
		@Override
		public void onMessage(String message) {
			// TODO Auto-generated method stub
//			Log.i(TAG, String.format("*****Got string message! %s*****", message));
			sendMsg(message);
//			sendMsg(decodeUnicode(message));
		}
		
		@Override
		public void onError(Exception error) {
			// TODO Auto-generated method stub
			Log.e(TAG, "*****Error!*****", error);
		}
		
		@Override
		public void onDisconnect(int code, String reason) {
			// TODO Auto-generated method stub
			Log.i(TAG, String.format("*****Disconnected! Code: %d Reason: %s*****", code, reason));

			connectOrNot = false;
			
		}
		
		@Override
		public void onConnect() {
			// TODO Auto-generated method stub
//			client.send(uid);
			
			try {
				JSONObject params = new JSONObject();
				params.put("uid", uid);
				client.send(params.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 Log.i(TAG, "*****  Connected!    *****");	
			 connectOrNot = true;
		}
	};
	
	public boolean isconnect()
	{
		return connectOrNot;
	}
	
	public void setIsConnect(boolean connect)
	{
		connectOrNot = connect;
	}
	
	public void sendMsg(String str)
	{
		Message msg = handler.obtainMessage();
		msg.obj = str;
		handler.sendMessage(msg);
//		Log.i(TAG, "*****send received message*****");
	}
	
	public void send(String string)
	{
		client.send(string);
	}
	
}
