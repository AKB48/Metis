package com.app.localDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper{
	private static int DB_VERSION = 1;
	private static String DB_NAME = "EvanDataBase.db";
	
	private String TABLE_NAME1 = "friends";
	private String SQL_createTable1 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME1+
			" (uid INTEGER,"+
			"fid INTEGER, " +
			"fname varchar(20), " +
			"gender varchar(20), " +
			"email varchar(30), " +
			"PRIMARY KEY(uid, fid))";
	
	//edit by luo
	private String TABLE_NAME2 = "notifications";
	private String SQL_createTable2 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME2
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ "uid INTEGER, "
			+ "status INTEGER, " 
			+ "tag VARCHAR(50), " 
			+ "msg TEXT)";
	
	private String TABLE_NAME3 = "note";
	private String SQL_createTable3 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME3
			+ " ( note_id INTEGER PRIMARY KEY, " 
			+ "user_id INTEGER, "
			+ "comment_id INTEGER, "
			+ "author TEXT, "
			+ "content TEXT, "
			+ "time TEXT, " 
			+ "event_id INTEGER)";
	
	private String TABLE_NAME4 = "activity";
	private String SQL_createTable4 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME4
			+ "( user_id INTEGER, " 
			+ "event_id INTEGER PRIMARY KEY, "
			+ "subject TEXT, "
			+ "time TEXT, "
			+ "duration INTEGER, "
			+ "location TEXT, "
			+ "launcher INTEGER, "
			+ "remark TEXT )";
	
	
	public MySQLiteOpenHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, DB_VERSION);
		Log.i("NotificationDB", "创建数据库");

	}	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_createTable1);
		db.execSQL(SQL_createTable2);
		db.execSQL(SQL_createTable3);
		db.execSQL(SQL_createTable4);
		Log.i("NotificationDB", "创建数据表");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
