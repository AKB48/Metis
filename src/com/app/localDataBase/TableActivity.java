package com.app.localDataBase;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableActivity {
	private Context context;
	private MySQLiteOpenHelper myHelper;
	
	public TableActivity(Context context)
	{
		this.context = context;
		myHelper = new MySQLiteOpenHelper(context);
	}
	
	public boolean add(ActivityStruct activity)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			ContentValues turple = new ContentValues();
			
			turple.put("user_id", activity.user_id);
			turple.put("event_id", activity.event_id);
			turple.put("subject", activity.subject);
			turple.put("time", activity.time);
			turple.put("location", activity.location);
			turple.put("launcher", activity.launcher);
			turple.put("duration", activity.duration);
			turple.put("remark", activity.remark);
			
			db.insert("activity", null, turple);
			db.close();
			Log.i("TableActivity","1插入成功");
		}
		else
		{
			Log.i("TableActivity","1插入失败");
			return false;
		}
		return true;
	}
	
	public boolean add(HashMap<String, Object> activity)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			ContentValues turple = new ContentValues();
			
			turple.put("user_id", Integer.parseInt(activity.get("user_id").toString()));
			turple.put("event_id", Integer.parseInt(activity.get("event_id").toString()));
			turple.put("subject", activity.get("subject").toString());
			turple.put("time", activity.get("time").toString());
			turple.put("duration", Integer.parseInt(activity.get("duration").toString()));
			turple.put("location", activity.get("location").toString());
			turple.put("launcher", Integer.parseInt(activity.get("launcher").toString()));
			turple.put("remark", activity.get("remark").toString());
			
			db.insert("activity", null, turple);
			db.close();
			Log.i("TableActivity","2插入成功");
		}
		else
		{
			Log.i("TableActivity","2插入失败");
			return false;
		}
		return true;
	}
	
	public ActivityStruct query(String eventId)
	{
		SQLiteDatabase db = myHelper.getReadableDatabase();
		ActivityStruct activity = null;
		if(db.isOpen())
		{
			Cursor cr = db.query(true, "activity", null, "event_id=?", new String[]{eventId}, null, null, null, null);
			if(cr.moveToFirst())
			{
				activity = new ActivityStruct();
				activity.user_id = cr.getInt(cr.getColumnIndex("user_id"));
				activity.event_id = cr.getInt(cr.getColumnIndex("event_id"));
				activity.subject = cr.getString(cr.getColumnIndex("subject"));
				activity.time = cr.getString(cr.getColumnIndex("time"));
				activity.duration = cr.getInt(cr.getColumnIndex("duration"));
				activity.location = cr.getString(cr.getColumnIndex("location"));
				activity.launcher = cr.getInt(cr.getColumnIndex("launcher"));
				activity.remark = cr.getString(cr.getColumnIndex("remark"));
				Log.i("TableActivity", "查到了活动： "+ activity.toString());
			}
			db.close();
		}
		else
		{
			Log.i("TableActivity", "没有查到活动： event_id = "+ eventId);
		}
		return activity;
	}
	
	public HashMap<String, Object> toHashMap(ActivityStruct activity)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", activity.user_id);
		map.put("event_id", activity.event_id);
		map.put("subject", activity.subject);
		map.put("time", activity.time);
		map.put("duration", activity.duration);
		map.put("location", activity.location);
		map.put("launcher", activity.launcher);
		map.put("remark", activity.remark);
		Log.i("TableActivity", "转化成的HashMap: "+map.toString());
		return map;
	}
	
	

}
