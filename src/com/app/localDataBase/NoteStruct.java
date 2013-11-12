package com.app.localDataBase;

public class NoteStruct {
	public int note_id;
	public int user_id;
	public int comment_id;
	public String author;
	public String content;
	public String time;
	public int event_id;
	
	public NoteStruct()
	{
	}
	
	public NoteStruct(int note_id,int userId, int commentId, String author, String content, String time, int event_id)
	{
		this.note_id = note_id;
		this.user_id = userId;
		this.comment_id = commentId;
		this.author = author;
		this.content = content;
		this.time = time;
		this.event_id = event_id;
	}
	
	public String toString()
	{
		String str;
		str =   "note_id: "+ note_id
				+", user_id: "+ user_id
				+", comment_id: "+ comment_id
				+", author: "+ author
				+", content: "+ content
				+", time: "+ time
				+", event_id: "+ event_id;
		return str;
	}

}
