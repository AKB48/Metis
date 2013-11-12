package com.app.Main;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.classes.FriendCenter;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;
import com.app.util.HttpSender;
import com.app.util.MessagaAdapter;
import com.app.util.Messager;
import com.app.util.OperationCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NotificationCenter extends Activity{
	
	private final String TAG = "NotificationCenter";
	private ArrayList<notificationObject> messageList;
	private int userId;
	private ListView notificationListView;
	private myHandler mHandler;
	private MessagaAdapter messagaAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_center_notification);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        messageList = new ArrayList<notificationObject>();
        init();
    }
	
	public void init()
	{
	    notificationListView = (ListView)this.findViewById(R.id.notification_list);
	    getNotificationFromDB();
	    messagaAdapter = new MessagaAdapter(this, messageList, userId);
	    notificationListView.setAdapter(messagaAdapter);
	    notificationListView.setDivider(getResources().getDrawable(R.drawable.settings_sep3));
	    mHandler = new myHandler();
	    imageUtil.getInstance().registerHandler(mHandler, "NotificationCenter");
	    Messager.getInstance().regsiterHandler(mHandler, "NotificationCenter");
	}
	
	
	//读取数据表数据
	public void getNotificationFromDB()
	{
		
		NotificationTableAdapter adapter = new NotificationTableAdapter(this);
		ArrayList<notificationObject> addFriendRequestList = adapter.queryData("ADD_FRIEND_REQUEST", userId);
		ArrayList<notificationObject> addFriendVerifyList = adapter.queryData("ADD_FRIEND_VERIFY", userId);
		ArrayList<notificationObject> addActivityRequestList = adapter.queryData("ADD_ACTIVITY_INVITATION", userId);
		ArrayList<notificationObject> addActivityFeedBackList = adapter.queryData("ADD_ACTIVITY_FEEDBACK", userId);
		ArrayList<notificationObject> requestIntoActivityList = adapter.queryData("REQUEST_INTO_ACTIVITY", userId);
		ArrayList<notificationObject> responseIntoActivityList = adapter.queryData("RESPONSE_INTO_ACTIVITY", userId);
		
		messageList.clear();
		messageList.addAll(addFriendRequestList);
		messageList.addAll(addFriendVerifyList);
		messageList.addAll(addActivityRequestList);
		messageList.addAll(addActivityFeedBackList);
		messageList.addAll(requestIntoActivityList);
		messageList.addAll(responseIntoActivityList);
		
	}
	
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
	    imageUtil.getInstance().unregisterHandler("NotificationCenter");
	    Messager.getInstance().unregisterHandler("NotificationCenter");
	    super.onBackPressed();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	// TODO Auto-generated method stub
////    	if(keyCode==KeyEvent.KEYCODE_BACK){
////    	    imageUtil.getInstance().unregisterHandler("NotificationCenter");
////    	}
//	    return super.onKeyUp(keyCode, event);
//	}
	
    public class myHandler extends Handler
    {
        public myHandler() {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what)
            {
            case FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR:
                messagaAdapter.notifyDataSetChanged();
                break;
            case FriendCenter.MSG_WHAT_ON_UPDATE_LIST:
                getNotificationFromDB();
                messagaAdapter.notifyDataSetChanged();
                break;
            default: 
                break;
            }
        }
    }

}
