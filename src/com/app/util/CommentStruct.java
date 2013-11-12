package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentStruct {

	public HashMap<String, Object> comment;
	public ArrayList<HashMap<String, Object>> replies;
	public int reply_sequence;
	
	public CommentStruct() {
		// TODO Auto-generated constructor stub
		comment = new HashMap<String, Object>();
		replies = new ArrayList<HashMap<String,Object>>();
	}
	
	public CommentStruct(HashMap<String,Object> comment, ArrayList<HashMap<String,Object>> replies, int reply_sequence)
	{
		this.comment = comment;
		this.replies = replies;
		this.reply_sequence = reply_sequence;
	}
	
	public String toString()
	{
		String str = "";
		str = "comment: "+ comment.toString() +"      replies: "+ replies.toString()+ "          reply_sequence: "+ reply_sequence;
		return str;
	}
}
