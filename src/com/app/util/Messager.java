package com.app.util;


import com.app.classes.FriendCenter;

import android.os.Handler;
import android.os.Message;


public class Messager {

    private volatile static Messager uniqueInstance = null;
    private Handler fcHandler, ncHandler;
    
    private Messager()
    {
        fcHandler = null;
        ncHandler = null;
    }
    
    
    public static Messager getInstance(){
        if(uniqueInstance == null){
            synchronized(imageUtil.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new Messager();
                }
            }
        }
        
        return uniqueInstance;       
    }
    
    public void regsiterHandler(Handler handler, String className)
    {
        if (className.equals("FriendCenter")) {
            fcHandler = handler;
        }
        else if (className.equals("NotificationCenter")) {
            ncHandler = handler;
        }
    }
    
    public void unregisterHandler(String className) 
    {
        if (className.equals("FriendCenter")) {
            fcHandler = null;
        }
        else if (className.equals("NotificationCenter")) {
            ncHandler = null;
        } 
    }
    
    public void notifyChanged(int notifyType)
    {
        if (null != ncHandler)
        {
            Message msg = ncHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_LIST);
            msg.sendToTarget();
        }
        if (1== notifyType && null != fcHandler)
        {
            Message msg = fcHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_NEW_FRIEND_COME);
            msg.sendToTarget();
        }
    }
    
}
