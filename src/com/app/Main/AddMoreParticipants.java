package com.app.Main;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.AdapterForPaticipantList;
import com.app.util.AdapterForPaticipantList.FriendInfor;
import com.app.util.AdapterForPaticipantList.ViewHolder;
import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddMoreParticipants extends Activity
{

	private Button viewFriendBtn;
	private Button submitBtn;
	private ListView participantList;
	private AdapterForPaticipantList adapter;
	private MessageHandler msgHandler;
	private Vector<String> participantSet = new Vector<String>();
	private int userId;
	private int event_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmoreparticipants);
		
		userId = getIntent().getIntExtra("userId", 0);
		event_id =  getIntent().getIntExtra("event_id", 0);
		
		msgHandler = new MessageHandler(Looper.myLooper());
		
		viewFriendBtn = (Button)findViewById(R.id.viewFriendBtn);
		submitBtn = (Button)findViewById(R.id.submitMoreParticipants);
		
		viewFriendBtn.setOnClickListener(viewFriendsListener);
		submitBtn.setOnClickListener(submitBtnListener);
	}
	
	private OnClickListener submitBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (participantSet.size()==0) {
				Toast.makeText(AddMoreParticipants.this, "请选择你要添加的好友", Toast.LENGTH_SHORT).show();	
			}
			else {
					try {
						JSONObject params = new JSONObject();
						params.put("id", userId);		
						params.put("event_id", event_id);			
						params.put("friends", participantSet);
						Log.i("add more participants: " , params.toString());
						new HttpSender().Httppost(OperationCode.INVITE_FRIENDS, params, msgHandler);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
	};
	
	private OnClickListener viewFriendsListener = new OnClickListener() {
		LayoutInflater factory;
		View dialogView;
		AlertDialog dialogShowContact;
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			factory = LayoutInflater.from(AddMoreParticipants.this);
			dialogView = factory.inflate(R.layout.showcontactlist, null);
			
			participantList = (ListView)dialogView.findViewById(R.id.show_contact_list);
			adapter = new AdapterForPaticipantList(AddMoreParticipants.this, userId);
			
			participantList.setAdapter(adapter);
			participantList.setItemsCanFocus(false);
			participantList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			participantList.setOnItemClickListener(lvListener);
			
			dialogShowContact = new AlertDialog.Builder( AddMoreParticipants.this)
					.setTitle("选择你要添加的朋友")
					.setView(dialogView)
					.setPositiveButton("确定", chooseFriPosListener)
					.setNegativeButton("取消",null)
					.create();
			dialogShowContact.show();
		}
	};
	
		private DialogInterface.OnClickListener chooseFriPosListener = new DialogInterface.OnClickListener()
		{
			private String output = "";
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (participantList.getCount()!=0) 
					submitBtn.setVisibility(View.VISIBLE);				
				else 
					submitBtn.setVisibility(View.GONE);	
				
				output = "";
				participantSet.clear();
				
				for (int i = 0; i < participantList.getCount(); i++)
				{
					if(AdapterForPaticipantList.isSelected.get(i))
					{
						output += ((FriendInfor)adapter.getItem(i)).u_name + "\n";
						participantSet.add( ((FriendInfor)adapter.getItem(i)).u_id );
					}
				}
				
				TextView participantTV = (TextView)findViewById(R.id.moreparticipantList);
				participantTV.setText(output);
			}
		};
		
		//Item点击的时候，添加或删除
		private OnItemClickListener lvListener = new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				// TODO Auto-generated method stub
				ViewHolder vHolder = (ViewHolder)view.getTag();
				vHolder.cBox.toggle();
				
				//cBox.isChecked()判断是否被选择，选中为true，没选中为false
				AdapterForPaticipantList.isSelected.put(position, vHolder.cBox.isChecked());
			}
			
		};
		
		class MessageHandler extends Handler
		{
			public MessageHandler(Looper looper)
			{
				super(looper);
			}
			@Override
			public void handleMessage(Message msg) {
				String returnStr = msg.obj.toString();
				Log.i("add more Participants return value: ", returnStr);
				if( returnStr == "DEFAULT" )
				{
					Toast.makeText(AddMoreParticipants.this, "网络异常", Toast.LENGTH_SHORT).show();
				}
				else {
					switch (msg.what) {
					case OperationCode.INVITE_FRIENDS:
						try {
							JSONObject jsonResponse = new JSONObject(returnStr);
							int cmd = jsonResponse.getInt("cmd");

							if(ReturnCode.NORMAL_REPLY == cmd)		
							{
								Toast.makeText(AddMoreParticipants.this, "拉好友成功，已发送请求", Toast.LENGTH_SHORT).show();	
								submitBtn.setVisibility(View.GONE);	
								finish();
							}
							else if(ReturnCode.SERVER_FAIL == cmd)
								Toast.makeText(AddMoreParticipants.this, "服务器出问题了，请稍后再试@_@", Toast.LENGTH_SHORT).show();
							else 
								Toast.makeText(AddMoreParticipants.this, "穿越了@_@", Toast.LENGTH_SHORT).show();
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
						break;

					default:
						break;
					}
				}
			}
		}

}
