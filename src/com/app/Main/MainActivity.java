package com.app.Main;

import com.app.classes.FriendCenter;
import com.app.classes.MenuSide;
import com.app.classes.MyEvents;
import com.app.classes.RelativeEvents;
import com.app.classes.Settings;
import com.app.util.MyBroadcastReceiver;
import com.example.slidingmenudemo.R;
import com.jeremyfeinstein.slidingmenu.SlidingMenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * SlidingMenuDemo
 * 项目主要依赖：
 * 1. com.jeremyfeinstein.slidingmenu包下三个java文件
 * 2. res/values下的attrs.xml以及ids.xml
 */
public class MainActivity extends Activity {
	private SlidingMenu mSlidingMenu;
	
	private MenuSide mMenuSide;
	private MenuSideHandler mMenuSideHandler;
	
	private MyEvents UI_myEvents;
	private myHandler uiHandler = new myHandler();
	private int displayWidth;
	
	private RelativeEvents UI_relativeEvents = null;
	private FriendCenter UI_friendCenter = null;
	private Settings UI_settings = null;
	
	private int userId = -1;
	private String email;
	private Intent serviceIntent = null;
	private MyBroadcastReceiver broadcastReceiver  = null;
	
	public static int notifyNum = 0;
	public static int activityNotifyNum = 0;
	public static int friendNotifyNum = 0;
	
	public static final int CLICK_ON_MYEVENTS = 0;
	public static final int CLICK_ON_PRIVATE_EVENTS = 1;
	public static final int CLICK_ON_NOTIFY_CENTER = 2;
	public static final int CLICK_ON_SETTING = 3;
	public static final int CLICK_ON_RECOMMAND_EVENTS = 4;
	public static final int CLICK_ON_UPDATE = 5;
	public static final int CLICK_ON_EXIT = 6;
	public static final int TO_MENU_CLICKED = 7;
	public static final int TO_ADD_ACTIVITY = 8;
	public static final int TO_ADD_FRIEND = 9;
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -4;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -5;
	private static final int MSG_WHAT_REFRESH_DONE = -6;
	private static final int MSG_WHAT_GET_MORE_DONE = -7;
	
	View mContentView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		email = intent.getStringExtra("email");
		serviceIntent = new Intent("HeartbeatService");
		
		// 初始化侧边栏菜单和主视图
		mMenuSideHandler = new MenuSideHandler();
		mMenuSide = new MenuSide(this,mMenuSideHandler);
		mMenuSide.init();
		
		displayWidth = getWindowManager().getDefaultDisplay().getWidth();
		
		UI_myEvents = new MyEvents(this, uiHandler, displayWidth, userId);
		UI_myEvents.init();
		setContentView( UI_myEvents.getView() );			// 设置默认的主视图
		
		
		if( null ==UI_settings)		
		{
			UI_settings = new Settings(MainActivity.this, userId);
			
		}
		
		if( null==UI_friendCenter)
		{
			UI_friendCenter = new FriendCenter(MainActivity.this, uiHandler, userId);
			UI_friendCenter.init();                                   //同步好友列表
		}
		
		init();
	}
	
	private void init()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("postMsg");
		broadcastReceiver = new MyBroadcastReceiver(this, userId);
		this.registerReceiver( broadcastReceiver, intentFilter);
		
		// 配置滑动菜单
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSlidingMenu.setMenu(mMenuSide.getMenuView());		// 设置侧边栏菜单
		
		UI_myEvents.loadData();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if( broadcastReceiver!=null )
			unregisterReceiver(broadcastReceiver);
		
		if( serviceIntent!=null){
			stopService(serviceIntent);
			Log.e("test", "ondestroy");
		}
		
		if( UI_myEvents.join_broadcastReceiver!=null )
			unregisterReceiver(UI_myEvents.join_broadcastReceiver);
		
		super.onDestroy();
	}
	
	//按下返回键
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//放在后台执行
		moveTaskToBack(false);
	}

	private void initContentView2() {
		// 主视图2
		mContentView2 = (View) LayoutInflater.from(this)
							.inflate(R.layout.activity_main_2, null);
	}
	
	class MenuSideHandler extends Handler 
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch ( msg.what ) {
			case CLICK_ON_MYEVENTS:
				mSlidingMenu.setContent( UI_myEvents.getView() );
				break;
				
			case CLICK_ON_PRIVATE_EVENTS:
				if( null==UI_relativeEvents)
				{
					UI_relativeEvents = new RelativeEvents(MainActivity.this, userId, displayWidth,uiHandler );
					UI_relativeEvents.init();
				}
				UI_relativeEvents.showNotification();
				mSlidingMenu.setContent( UI_relativeEvents.getView() );
				break;
				
			case CLICK_ON_NOTIFY_CENTER:
				mSlidingMenu.setContent( UI_friendCenter.getView() );
				UI_friendCenter.showFriendList();
				break;
				
			case CLICK_ON_SETTING:
				mSlidingMenu.setContent( UI_settings.getView() );
				UI_settings.initData();
				break;
				
			case CLICK_ON_EXIT:
				Builder dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示")
				.setMessage("确定要退出程序吗？")
				.setPositiveButton("是", 
						new DialogInterface.OnClickListener() {			
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								//退出程序
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
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	}
	
	//单位从dip转化成px
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	
	public class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case TO_MENU_CLICKED:
				mSlidingMenu.showMenu();
				break;
			case TO_ADD_ACTIVITY:
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AddActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("email", email);
				startActivity(intent);
				break;
			case TO_ADD_FRIEND:
				Intent intent1 = new Intent();
				intent1.setClass(MainActivity.this, searchFriend.class);
				intent1.putExtra("userId", userId);
				startActivity(intent1);
				break;
			case MSG_WHAT_ON_LOAD_DATA:
				UI_myEvents.myEventsPullUpDownView.notifyOnLoadData();
				break;
			case MSG_WHAT_LOAD_DATA_DONE:
				UI_myEvents.myEventsAdapter.notifyDataSetChanged();
				UI_myEvents.myEventsPullUpDownView.notifyLoadDataDone();
				break;
			case MSG_WHAT_REFRESH_DONE:
				UI_myEvents.myEventsAdapter.notifyDataSetChanged();
				UI_myEvents.myEventsPullUpDownView.notifyRefreshDone();
				break;
			case MSG_WHAT_GET_MORE_DONE:
				UI_myEvents.myEventsAdapter.notifyDataSetChanged();
				UI_myEvents.myEventsPullUpDownView.notifyGetMoreDone();
				break;
			default: 
					break;
			}
			super.handleMessage(msg);
		}
	}
}
