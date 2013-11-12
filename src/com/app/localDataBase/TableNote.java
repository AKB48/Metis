package com.app.localDataBase;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableNote {
	private Context context;
	private MySQLiteOpenHelper myHelper;
	
	public TableNote(Context context)
	{
		this.context = context;
		myHelper = new MySQLiteOpenHelper(context);
	}
	
	public boolean add(NoteStruct note)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			ContentValues turple = new ContentValues();
			turple.put("note_id", note.note_id);
			turple.put("user_id", note.user_id);
			turple.put("comment_id", note.comment_id);
			turple.put("author", note.author);
			turple.put("content", note.content);
			turple.put("time", note.time);
			turple.put("event_id", note.event_id);
			db.insert("note", null, turple);
			db.close();
			Log.i("TableNote","插入成功： "+ turple.toString());
		}
		else
		{
			Log.i("TableNote","插入成功： "+ note.toString());
			return false;
		}
		return true;
	}
	
	public boolean delete(String column[], String colArgs[])
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			int len = column.length;
			String clause = "";
			for(int i=0;i<len;i++)
			{
				if(i!=0)
				{
					clause += " AND ";
				}
				clause += column[i]+"=?";
			}
			db.delete("note", clause, colArgs);
			db.close();
			Log.i("TableNote", "成功删除");
		}
		else
		{
			Log.i("TableNote", "删除失败");
			return false;
		}
		return true;
	}
	
	public boolean update(String note_id, String newContent)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			ContentValues values = new ContentValues();
			values.put("content", newContent);
			db.update("note", values, "note_id=?", new String[]{note_id});
			Log.i("TableNote","更新成功: "+ values.toString());
		}
		else
		{
			Log.i("TableNote", "更新失败");
			return false;
		}
		return true;
	}
	
	public NoteStruct query(String note_id)
	{
		SQLiteDatabase db = myHelper.getReadableDatabase();
		NoteStruct a_note = null;
		if(db.isOpen())
		{
			Cursor cr = db.query(true, "note", null, "note_id=?", new String[]{note_id}, null, null, null, null, null);
			if(cr.moveToFirst())
			{
				a_note = new NoteStruct();
				a_note.note_id = cr.getInt(cr.getColumnIndex("note_id"));
				a_note.user_id = cr.getInt(cr.getColumnIndex("user_id"));
				a_note.comment_id = cr.getInt(cr.getColumnIndex("comment_id"));
				a_note.author = cr.getString(cr.getColumnIndex("author"));
				a_note.content = cr.getString(cr.getColumnIndex("content"));
				a_note.time = cr.getString(cr.getColumnIndex("time"));
				a_note.event_id = cr.getInt(cr.getColumnIndex("event_id"));
				Log.i("TableNote", "找到了: "+ a_note.toString());
			}
			else
			{
				Log.i("TableNote", "没有查找到: note_id = "+ note_id);
			}
			db.close();
		}
		return a_note;
	}
	
	public ArrayList<NoteStruct> getAllNotesOfActivity(String eventId)
	{
		ArrayList<NoteStruct> notesOfactivity = new ArrayList<NoteStruct>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		if(db.isOpen())
		{
			Cursor cr =  db.query(true, "note", null, "event_id=?", new String[]{eventId}, null, null, null, null);
			while(cr.moveToNext())
			{
				NoteStruct note = new NoteStruct();
				note.note_id = cr.getInt(cr.getColumnIndex("note_id"));
				note.comment_id = cr.getInt(cr.getColumnIndex("comment_id"));
				note.user_id = cr.getInt(cr.getColumnIndex("user_id"));
				note.author = cr.getString(cr.getColumnIndex("author"));
				note.content = cr.getString(cr.getColumnIndex("content"));
				note.event_id = cr.getInt(cr.getColumnIndex("event_id"));
				note.time = cr.getString(cr.getColumnIndex("time"));
				notesOfactivity.add(note);
			}
			db.close();
			Log.i("TableNote","获得活动的notes： "+notesOfactivity.toString());
			
		}
		else
		{
			Log.i("TableNote", "获取活动的notes失败");
		}
		return notesOfactivity;
	}
	
	public ArrayList<HashMap<String, Object>> getAllNotes()
	{
		ArrayList<HashMap<String, Object>> notes = new ArrayList<HashMap<String,Object>>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		if(db.isOpen())
		{
			Cursor cr = db.query("note", null, null, null, null, null, null);
			while(cr.moveToNext())
			{
				HashMap<String , Object> map = new HashMap<String, Object>();
				map.put("note_id", cr.getInt(cr.getColumnIndex("note_id")));
				map.put("user_id", cr.getInt(cr.getColumnIndex("user_id")));
				map.put("comment_id", cr.getInt(cr.getColumnIndex("comment_id")));
				map.put("author", cr.getString(cr.getColumnIndex("author")));
				map.put("content", cr.getString(cr.getColumnIndex("content")));
				map.put("time", cr.getString(cr.getColumnIndex("time")));
				map.put("event_id", cr.getInt(cr.getColumnIndex("event_id")));
				notes.add(map);
			}
			db.close();
		}
		
		return notes;
	}
	
	public ArrayList<Integer> getActivityID()
	{
		ArrayList<Integer> activityIDs = new ArrayList<Integer>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		if(db.isOpen())
		{
			Cursor cr = db.query(true,"note", new String[]{"event_id"},null, null, null, null, null, null);
			while(cr.moveToNext())
			{
//				HashMap<String , Object> map = new HashMap<String, Object>();
//				map.put("event_id", cr.getInt(cr.getColumnIndex("event_id")));
//				map.put("user_id", cr.getInt(cr.getColumnIndex("user_id")));
//				map.put("comment_id", cr.getInt(cr.getColumnIndex("comment_id")));
				activityIDs.add(cr.getInt(cr.getColumnIndex("event_id")));
			}
			db.close();
			Log.i("TableNote", "找到的event_id有： "+ activityIDs.toString());
		}
		else
		{
			Log.i("TableNote", "查找event_id失败");
		}
		return activityIDs;
	}

}
