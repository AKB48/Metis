package com.app.localDataBase;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FriendStruct {
	public int uid;
	public int fid;
	public String fname;
	public String gender;
	public String email;
	public String location;
	public String sign;
	public boolean isFriend;
	
	public FriendStruct() {
		// TODO Auto-generated constructor stub
		uid = -1;
		fid = -1;
		fname = null;
		gender = null;
		email = null;
		location = null;
		sign = null;
		isFriend = false;
	}
	
	public FriendStruct(JSONObject jo)
	{
		try {
			uid = jo.getInt("uid");
			fid = jo.getInt("id");
			fname =  jo.getString("name");
			gender = jo.getString("gender");
			email = jo.getString("email");
			location = null;
	        sign = null;
	        isFriend = false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FriendStruct(JSONObject jo, boolean extend)
    {
        try {
            uid = -1;
            fid = jo.getInt("id");
            fname =  jo.getString("name");
            gender = jo.getString("gender");
            email = jo.getString("email");
            location = null;
            sign = null;
            isFriend = false;
            if (extend) {
                location = jo.getString("location");
                if (location.equals("null")) {
                    location = "";
                }
                sign = jo.getString("sign");
                if (sign.equals("null")) {
                    sign = "";
                }
                isFriend = jo.getBoolean("isFriend");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static FriendStruct getFromJSON(JSONObject jo)
	{
		FriendStruct fs = new FriendStruct();
		try {
			fs.fid = jo.getInt("id");
//			Log.v("friends","fs.fid "+fs.fid);
			fs.fname = jo.getString("name");
			fs.gender = jo.getString("gender");
			fs.email = jo.getString("email");
			fs.uid = jo.getInt("uid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fs;
	}
	
	
}
