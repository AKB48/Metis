package com.app.util;

import java.security.MessageDigest;
import java.util.Random;

public class RegUtils
{

	public static String getRandomString(int length)
	{
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Random random = new Random();
		StringBuffer strBuf = new StringBuffer();
		
		for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        strBuf.append(base.charAt(number));   
	    }   
		
	    return strBuf.toString();
	}
	
	public static String Md5(String str)
	{
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte md5[] = md.digest(str.getBytes("UTF-8"));
			
			StringBuffer sb = new StringBuffer();
		       for (int i = 0; i < md5.length; i++) 
		       {
		         sb.append(Integer.toString((md5[i] & 0xff) + 0x100, 16).substring(1));
		       }
		       
			return sb.toString();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

