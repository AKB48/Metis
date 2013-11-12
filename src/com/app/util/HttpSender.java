package com.app.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.graphics.Region.Op;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


// Singleton pattern
public class HttpSender {
    
//    private String URL = "http://172.18.186.175:10087/";
    private String URL = "http://222.200.182.183:10087/";
    private String httpUrl = "";
    private String returnContent = "DEFAULT";
    private String TAG = "HttpSender";
    private int operationCode;
    private JSONObject params;
    private MyThread myThread;
    private Handler mHandler;
    
    public HttpSender() {}
    
    public String Httppost(int operationCode, JSONObject params, Handler mHandler)
    {
        this.operationCode = operationCode;
        this.params = params;
        this.mHandler = mHandler;
        myThread = new MyThread();
        myThread.start();
        Log.i("HttpSender", "return content is:"  + returnContent);
        return returnContent;
    }
    
    public void post()
    {
        httpUrl = URL;
        returnContent = "DEFAULT";
        switch (operationCode)
        {
        case OperationCode.REGISTER:
            httpUrl += "register";
            break;
        case OperationCode.LOGIN:
        case OperationCode.GET_SALT_VALUE:
            httpUrl += "login";
            break;
        case OperationCode.UPLOAD_CONTACT_BOOK:
            httpUrl += "upload_phonebook";
            break;
        case OperationCode.ADD_FRIEND:
            httpUrl += "add_friend";
            break;
        case OperationCode.SEARCH_FRIEND:
        	httpUrl += "search_friend";
        	break;
        case OperationCode.SYNCHRONIZE:
        	httpUrl += "synchronize_friends";
        	break;
        case OperationCode.LAUNCH_EVENT:
        	httpUrl += "launch_event";
        	break;
        case OperationCode.PARTICIPATE_EVENT:
        	httpUrl += "participate_event";
        	break;
        case OperationCode.GET_EVENTS:
        	httpUrl += "get_events";
        	break;
        case OperationCode.SEARCH_EVENT:
            httpUrl += "search_event";
            break;
        case OperationCode.GET_MY_EVENTS:
            httpUrl += "get_my_events";
            break;
        case OperationCode.GET_RELEVANT_EVENTS:
            httpUrl += "get_relevant_events";
            break;
        case OperationCode.GET_RECOM_EVENTS:
            httpUrl += "get_recom_events";
            break;
        case OperationCode.GET_IMPORTANT_INFO:
            httpUrl += "get_important_info";
            break;
        case OperationCode.ADD_COMMENT:
            httpUrl += "add_comment";
            break;
        case OperationCode.DELETE_COMMENT:
            httpUrl += "delete_comment";
            break;
        case OperationCode.GET_COMMENTS:
            httpUrl += "get_comments";
            break;
        case OperationCode.ADD_GOOD:
            httpUrl += "add_good";
            break;
        case OperationCode.INVITE_FRIENDS:
        	httpUrl += "invite_friends";
        	break;
        case OperationCode.NOTE:
            httpUrl += "note";
            break;
        case OperationCode.UPLOAD_AVATAR:
        case OperationCode.GET_AVATAR:
            httpUrl += "avatar";
            break;
        case OperationCode.LOGOUT:
            httpUrl += "logout";
            break;
        case OperationCode.GET_USER_INFO:
            httpUrl += "get_user_info";
            break;
        case OperationCode.CHANGE_SETTINGS:
            httpUrl += "change_settings";
            break;
        case OperationCode.CHANGE_PW:
            httpUrl += "change_pw";
            break;
        case OperationCode.ADD_PCOMMENT:
            httpUrl += "add_pcomment";
            break;
        case OperationCode.GET_PCOMMENTS:
            httpUrl += "get_pcomments";
            break;
        case OperationCode.DELETE_PCOMMENT:
            httpUrl += "delete_pcomment";
            break;
        case OperationCode.GET_PHOTO_LIST:
			httpUrl += "get_photo_list";
			break;
        default:
            httpUrl += "json";
            break;
        }
        Log.i("HttpSender", httpUrl);
        HttpPost httppost = new HttpPost(httpUrl);
        HttpClient client = new DefaultHttpClient();
        StringBuilder str = new StringBuilder();
        BufferedReader buffer = null;

        try
        {   
            httppost.setEntity(new StringEntity(params.toString(), HTTP.UTF_8));
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("charset", HTTP.UTF_8);
            // 请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
            // 读取超时
            //client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
            HttpResponse httpRes = client.execute(httppost);
            Log.i("HttpSender", ""+httpRes.getStatusLine().getStatusCode());
            if(httpRes.getStatusLine().getStatusCode() == 200)
            {
                Log.i("HttpSender", "connect ok");
                buffer = new BufferedReader(new InputStreamReader(httpRes.getEntity().getContent()));
                for(String s = buffer.readLine(); s != null ; s = buffer.readLine())
                {
                    str.append(s);
                }
                Log.i(TAG,str.toString());
                buffer.close();
                
//              JSONObject json = new JSONObject(str.toString());
                returnContent = str.toString();
                Message message = Message.obtain();
                message.what = operationCode;
                message.obj = returnContent;
                mHandler.sendMessage(message);
                
            }
        } catch(Exception e)
        {
            e.printStackTrace();
            Log.i("HttpSender", "connect fail ");
        }
        
    }
    
    
    class MyThread extends Thread {
    	public void run() {
    		post();
    	}
    }
    
}
