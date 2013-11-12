package com.app.Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.AdapterForPaticipantList;
import com.app.util.AdapterForPaticipantList.FriendInfor;
import com.app.util.AdapterForPaticipantList.ViewHolder;
import com.app.util.CircularImage;
import com.app.util.HttpSender;
import com.app.util.ListViewUtility;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.TimeStruct;
import com.example.slidingmenudemo.R;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends Activity
{
	private static final String ADD_ACTIVITY_TAG = "AddActivity";

	private View submitActivityBtn, cancelActivityBtn, addparticipantBlock;
	private EditText themeEditText, locEditText, remarkEditText;
	private TextView dateTextView;
	private CheckBox canjoinCheckBox;
	private Calendar calendar;
	private boolean canjoin;

	private ListView participantList;
	private AdapterForPaticipantList adapter;
	private Vector<String> participantSet = new Vector<String>();
	
	private String activityTheme, activityLoc="", activityDate="";	
	private String remarkStr;
	
	private int userId;
	private String email;
	private MessageHandler messageHandler;
	
	private HttpSender sender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addactivity_main);
		
		CircularImage join = (CircularImage)findViewById(R.id.addParticipantBtn);
		join.setImageResource(R.drawable.join);
		
		calendar = Calendar.getInstance();
		
		themeEditText = (EditText)findViewById(R.id.activityTheme);
		dateTextView = (TextView)findViewById(R.id.activityDate);
		dateTextView.setOnClickListener( addTimeListener );
		locEditText = (EditText)findViewById(R.id.activityLoc);
		remarkEditText = (EditText)findViewById(R.id.backup);
		
		addparticipantBlock = (View)findViewById(R.id.addParticipantBlock);
		addparticipantBlock.setOnClickListener(addPaticipantListener);
		
		canjoinCheckBox = (CheckBox)findViewById(R.id.canjoin);
		
		submitActivityBtn = (View)findViewById(R.id.submitActivity);
		submitActivityBtn.setOnClickListener(submitActivityListener);
		cancelActivityBtn = (View)findViewById(R.id.cancelActivity);
		cancelActivityBtn.setOnClickListener(submitActivityListener);
		
		userId = getIntent().getIntExtra("userId", 0);
		email = getIntent().getStringExtra("email");
		
		messageHandler = new MessageHandler(Looper.myLooper());
		sender = new HttpSender();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
//		Intent intent = new Intent();
//			intent.setClass(AddActivity.this, UserInterface.class);
//			intent.putExtra("userId", userId);
//			intent.putExtra("email", email);
//			startActivity(intent);
		finish();
	}

	//提交按钮
	private OnClickListener submitActivityListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() == R.id.submitActivity)
			{
				String hint = "请输入完整信息：";
				activityTheme = themeEditText.getText().toString().trim();
				activityLoc = locEditText.getText().toString().trim();
				canjoin = canjoinCheckBox.isChecked();
				remarkStr = remarkEditText.getText().toString().trim();
				
				if ("".equals(activityTheme))
				{
					hint += "活动主题";
					Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
				}
				else if ( participantSet.size()==0 )
				{
					hint += "选择参与者";
					Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
				}
				else {
					hint = "发起活动信息完整，正在发送请求...";
					Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
					sendRequest();
				}
			}
			else if( v.getId() == R.id.cancelActivity)
			{
				cancelDialog();
			}
		}
	};
	
	private void cancelDialog()
	{
		Builder dialog = new AlertDialog.Builder(AddActivity.this)
			.setTitle("提示")
			.setMessage("确定要取消活动吗？")
			.setPositiveButton("是", 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//跳回到主页面userinterface
//							Intent intent = new Intent();
//								intent.setClass(AddActivity.this, UserInterface.class);
//								intent.putExtra("userId", userId);
//								intent.putExtra("email", email);
//								startActivity(intent);
							finish();
						}
					})
			.setNegativeButton("否", 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//啥都不做
						}
					});
		dialog.show();
	}
	
	/**
	 *		发起活动的JSON格式：（C->S)
			id:发起者的id
			suject:活动主题
			time:[time1,time2,...]（time的时间格式对应mysql的datetime格式YYYY-MM-DD HH:MM:SS）
			location:[location1,location2,...]
			duration:活动持续时间（天）int
			visibility:可见性bool
			status:活动状态（0代表筹备中 1代表进行中 2代表已结束）
			remark(备注)：（可为空）
			friends:[friend_id1,.....]
	 */
	private void sendRequest()
	{
		JSONObject paramsAddActivity = new JSONObject();
		
		try
		{
			paramsAddActivity.put("id", userId);
			paramsAddActivity.put("subject", activityTheme);		
			paramsAddActivity.put("time", activityDate);
			paramsAddActivity.put("location", activityLoc);
			paramsAddActivity.put("duration", 0);
			paramsAddActivity.put("visibility", canjoin);
			paramsAddActivity.put("status", 0);
			paramsAddActivity.put("remark", remarkStr);
			
			ArrayList<Integer> participantsParam = new ArrayList<Integer>();
			for ( String participant : participantSet) 
				participantsParam.add( Integer.parseInt(participant) );
			paramsAddActivity.put("friends", participantsParam);
			
			//response
			Log.i("AddActivity params:", paramsAddActivity.toString());
			sender.Httppost(OperationCode.LAUNCH_EVENT, paramsAddActivity, messageHandler);
		} catch (JSONException e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	//添加参与者
	private OnClickListener addPaticipantListener = new OnClickListener()
	{
		private LayoutInflater factory;
		private View dialogView;
		private AlertDialog dialogShowContact;
		
		@Override
		public void onClick(View v)
		{
			factory = LayoutInflater.from(AddActivity.this);
			dialogView = factory.inflate(R.layout.showcontactlist, null);
			
			participantList = (ListView)dialogView.findViewById(R.id.show_contact_list);
			adapter = new AdapterForPaticipantList(AddActivity.this,userId);
			
			participantList.setAdapter(adapter);
			participantList.setItemsCanFocus(false);
			participantList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			participantList.setOnItemClickListener(lvListener);
			
			dialogShowContact = new AlertDialog.Builder(AddActivity.this)
				.setTitle("ChooseFriends:")
				.setView(dialogView)
				.setPositiveButton("确定", chooseFriPosListener)
				.setNegativeButton("取消", null)
				.create();
			
			dialogShowContact.show();
		}
	};
	
	//看用户点击选择了哪些参与者
	//participantSet获取的是u_id
	private DialogInterface.OnClickListener chooseFriPosListener = new DialogInterface.OnClickListener()
	{
		private String output = "";
		
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			output = "";
			participantSet.clear();
			
			for (int i = 0; i < participantList.getCount(); i++)
			{
				if(AdapterForPaticipantList.isSelected.get(i))
				{
					//输出的是u_name，但是存在participantSet里面的是u_id
					//如果想要得到u_name,那么participantSet里面可以存放index,然后使用index调用
					//getItem(index)方法获取FriendInfor
					//再通过FriendInfor对象获取名字即可
					output += ((FriendInfor)adapter.getItem(i)).u_name + "\n";
					participantSet.add( ((FriendInfor)adapter.getItem(i)).u_id );
				}
			}
			
			TextView participantTV = (TextView)findViewById(R.id.showparticipant);
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
	
	private OnClickListener addTimeListener = new OnClickListener()
	{		
		private TimeStruct timeStruct;
		private TimePickerDialog timePickerDialog;
		private DatePickerDialog datePickerDialog;
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			timeStruct = new TimeStruct();
			
			datePickerDialog = new DatePickerDialog(
					AddActivity.this, 
					dateCallback, 
					calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), 
					calendar.get(Calendar.DAY_OF_MONTH)
					);
			
			timePickerDialog = new TimePickerDialog(
					AddActivity.this, 
					timeCallBack, 
					calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE), 
					true);	
			
			datePickerDialog.show();
		}
		
		private DatePickerDialog.OnDateSetListener dateCallback = 
				new DatePickerDialog.OnDateSetListener()
				{	
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth)
					{
						timeStruct.setDate(year, monthOfYear, dayOfMonth);
						timePickerDialog.show();
					}
				};
		
		private TimePickerDialog.OnTimeSetListener timeCallBack = 
				new TimePickerDialog.OnTimeSetListener()
				{
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
							timeStruct.setTime(hourOfDay, minute);
							dateTextView.setText(timeStruct.toString());
							activityDate = timeStruct.toString();
					}
				};
	};
	
	class MessageHandler extends Handler
	{
		String resAddActivity = null;
		int cmdAddActivity;
		JSONObject resAddActivityJson = null;
		
		public MessageHandler(Looper looper) {
			super(looper);
		}
		public void handleMessage(Message msg) 
		{
			Log.i("AddActivity", "发起活动返回值"+msg.obj.toString());
			switch (msg.what) {
			case OperationCode.LAUNCH_EVENT:
				
				try {
					resAddActivity = msg.obj.toString();
					if ( resAddActivity!="DEFAULT") 
					{
							resAddActivityJson = new JSONObject(resAddActivity);
							
							cmdAddActivity = resAddActivityJson.getInt("cmd");
							int evenID = resAddActivityJson.getInt("event_id");
//							int evenID = 0;
							if( ReturnCode.NORMAL_REPLY==cmdAddActivity )
							{
								//跳转到用户自己的活动页面
								Log.i("AddActivity", "成功发起活动,evenId:"+evenID);
								//跳转到UserInterface.java
//								Intent intent = new Intent();
//								intent.setClass(AddActivity.this, UserInterface.class);
//								intent.putExtra("userId", userId);
//								intent.putExtra("email", email);
//								startActivity(intent);
								finish();
							}
							else {
								Toast.makeText(AddActivity.this, "发起活动失败，请重试", Toast.LENGTH_SHORT).show();
							}
					}
					else 
						Toast.makeText(AddActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				} catch (JSONException e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}

}
