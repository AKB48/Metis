package com.app.classes;

import com.app.Main.MainActivity;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuSide 
{
	private Context mContext;
	private View mMenuView;
	private Handler mHandler;
	private View myEventBtn, relativeEventBtn, recommandEventBtn, friendCenterBtn, updateBtn, settingBtn, exitBtn;
	
	public MenuSide(Context context, Handler handler)
	{
		this.mContext = context;
		this.mHandler = handler;
	}
	
	public View getMenuView()
	{
		return mMenuView;
	}
	
	// ≤‡±ﬂ¿∏≤Àµ• ”Õº
	public void init()
	{
		mMenuView = (View) LayoutInflater.from(mContext)
				.inflate(R.layout.sidebar_menu, null);
		
		myEventBtn = mMenuView.findViewById(R.id.menu_myevents);
			myEventBtn.setOnClickListener(btnClickListener);
		relativeEventBtn = mMenuView.findViewById(R.id.menu_relativeevents); 
			relativeEventBtn.setOnClickListener(btnClickListener);
		recommandEventBtn = mMenuView.findViewById(R.id.menu_recommendedevents);
			recommandEventBtn.setOnClickListener(btnClickListener);
		friendCenterBtn = mMenuView.findViewById(R.id.menu_friendscenter);
			friendCenterBtn.setOnClickListener(btnClickListener);
		updateBtn = mMenuView.findViewById(R.id.menu_update);
			updateBtn.setOnClickListener(btnClickListener);
		settingBtn = mMenuView.findViewById(R.id.menu_settings);
			settingBtn.setOnClickListener(btnClickListener);
		exitBtn = mMenuView.findViewById(R.id.menu_exit);
			exitBtn.setOnClickListener(btnClickListener);
	}
	
	OnClickListener btnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			switch ( v.getId() ) {
			case R.id.menu_myevents:
				msg.what = MainActivity.CLICK_ON_MYEVENTS;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_relativeevents:
				msg.what = MainActivity.CLICK_ON_PRIVATE_EVENTS;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_recommendedevents:
				msg.what = MainActivity.CLICK_ON_RECOMMAND_EVENTS;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_friendscenter:
				msg.what = MainActivity.CLICK_ON_NOTIFY_CENTER;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_update:
				msg.what = MainActivity.CLICK_ON_UPDATE;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_settings:
				msg.what = MainActivity.CLICK_ON_SETTING;
				mHandler.sendMessage(msg);
				break;
			case R.id.menu_exit:
				msg = Message.obtain();
				msg.what = MainActivity.CLICK_ON_EXIT;
				mHandler.sendMessage(msg);
				break;
			default:
				break;
			}
		}
	};

}
