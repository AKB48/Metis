package com.app.util;

public class TimeStruct
{
	int year;
	int month;
	int day;
	int hour;
	int minute;
	
	public void setDate(int y, int m, int d)
	{
		year = y;
		month = m;
		day = d;
	}
	
	public void setTime(int h, int m)
	{
		hour = h;
		minute = m;
	}
	
	public boolean equals(Object o)
	{
		TimeStruct obj = (TimeStruct)o;
		boolean flag = (year==obj.year && 
						month==obj.month && 
						day==obj.day && 
						hour==obj.hour && 
						minute==obj.minute);
		return flag;
	}
	
	public int hashCode()
	{
		return ("" + year + month + day + hour + minute).hashCode();
	}
	
	public String toString() 
	{//datetime∏Ò ΩYYYY-MM-DD HH:MM:SS
		return year + "-" + (month+1) + "-" + day + " "
				+ hour + ":" + minute + ":00";
	}
}
