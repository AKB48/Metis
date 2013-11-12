package com.app.localDataBase;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotificationTableAdapter
{
	private static final String NotificationDB = "notificationDB"; 
	//本地context对象
	private Context mContext = null;
	//打开数据库时，保存返回的数据库对象
	private SQLiteDatabase mSqLiteDatabase = null;
	//继承自SQLiteOpenHelper
	private MySQLiteOpenHelper mySQLiteOpenHelper = null;
	
	public NotificationTableAdapter(Context context)
	{
		Log.i(NotificationDB, "构造helper对象");
		mContext = context;
		mySQLiteOpenHelper = new MySQLiteOpenHelper(mContext);
	}
	
	public long insertData(int status, int uid, String tag, String msg)
	{
		long resultValue = 0;		
		if ( findMsgFromDB(msg)==false) {			
				Log.i(NotificationDB, "插入数据"+msg);
				SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		
				ContentValues initValues = new ContentValues();
					initValues.put("uid", uid);
					initValues.put("status", status);
					initValues.put("tag", tag);
					initValues.put("msg", msg);
					resultValue = db.insert("notifications", null, initValues);
					db.close();
		}
		return resultValue;
	}
	
	public boolean findMsgFromDB(String msg)
	{
		Log.i(NotificationDB, "搜索数据"+msg);
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		Cursor cursor;
		cursor = db.query(true, "notifications", new String[]{"msg"}, "msg='"+msg+"'", null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			Log.i(NotificationDB, "搜索数据存在");
			db.close();
			return true;
		}
		else {
			Log.i(NotificationDB, "搜索数据不存在");
			db.close();
			return false;
			}
	}
	
	
	public boolean deleteData(long rowId)
	{
		Log.i(NotificationDB, "删除数据项的id为："+rowId);
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		boolean resultValue;
			resultValue = db.delete("notifications", "_id=" + rowId, null)>0;
			db.close();
		return resultValue;
	}
	
	public boolean updateData(long rowId, int status)
	{
		Log.i(NotificationDB, "把id为"+rowId+"的状态更新为"+status);
		ContentValues cv = new ContentValues();
		boolean resultValue;
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
			cv.put("status", status);
			resultValue = db.update("notifications", cv, "_id=" + rowId, null)>0;
		return resultValue;
	}
	
	//返回_id和msg
	public ArrayList<notificationObject> queryData(String tag, int uid)
	{
		Log.i(NotificationDB, "获取tag为"+tag+"的数据项");
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		ArrayList<notificationObject> returnArrayList = 
				new ArrayList<notificationObject>();	
				
		if (db!=null) {
					Cursor cursor = db.rawQuery(
							"SELECT * FROM notifications WHERE uid = ? and tag=?", new String[]{uid+"", tag});
	
					if( cursor!=null )
					{
						if( cursor.moveToFirst() )
						{
							do{
								int ID;
								String msg = null;
								notificationObject item = null;
								
								ID = cursor.getInt( cursor.getColumnIndex("_id") );
								msg = cursor.getString( cursor.getColumnIndex("msg"));
			
								item = new notificationObject(ID, msg);
								returnArrayList.add(item);
								Log.i(NotificationDB, "数据项："+msg);
							}while(cursor.moveToNext());
						}
						else
						{
							Log.i(NotificationDB, "moveToFirst()为false");
						}
					}
					else
					{
						Log.i(NotificationDB, "cursor为null");
					}
		}
		else {
			Log.i(NotificationDB, "db为null");
		}
		 
		db.close();
		return returnArrayList;
	}
	
	
	public String queryOneMsgData(int item_id)
    {
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();   
        String msg = null;
        
        if (db!=null) {
                    Cursor cursor = db.rawQuery(
                            "SELECT * FROM notifications WHERE _id = ?", new String[]{item_id+""});
    
                    if( cursor!=null )
                    {
                        if( cursor.moveToFirst() )
                        {
                            do{
                                msg = cursor.getString( cursor.getColumnIndex("msg"));
                                Log.i(NotificationDB, "数据项："+msg);
                            }while(cursor.moveToNext());
                        }
                        else
                        {
                            Log.i(NotificationDB, "moveToFirst()为false");
                        }
                    }
                    else
                    {
                        Log.i(NotificationDB, "cursor为null");
                    }
        }
        else {
            Log.i(NotificationDB, "db为null");
        }
         
        db.close();
        return msg;
    }

}
