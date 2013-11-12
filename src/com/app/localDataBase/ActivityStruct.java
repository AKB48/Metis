package com.app.localDataBase;

public class ActivityStruct {
	public int user_id;
	public int event_id;
	public String subject;
	public String time;
	public String location;
	public int duration;
	public int launcher;
	public String remark;
		
	public ActivityStruct()
	{
		
	}
	
	public ActivityStruct(int user_id, int event_id,String subject, String time, 
			String location, int duration, int launcher, String remark)
	{
		this.user_id = user_id;
		this.event_id = event_id;
		this.subject = subject;
		this.time = time;
		this.location = location;
		this.duration = duration;
		this.launcher = launcher;
		this.remark = remark;
	}
	
	public String toString()
	{
		String str;
		str =   "user_id: "+ user_id
				+"event_id: "+ event_id
				+", subject: "+ subject
				+", time: "+ time
				+", duration: "+ duration
				+", location: "+ location
				+", launcher: "+ launcher
				+", remark: "+ remark;
		return str;
	}
}
