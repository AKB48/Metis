package com.app.Main;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.RegUtils;
import com.app.util.ReturnCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangPW extends Activity{

    private EditText current_pw, new_pw, ack_pw;
    private Button ack_button;
    private String email;
    private MessageHandler myHandler;
    private String curString, newString, ackString;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pw);
        
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        
        init();
    }
    
    private void init()
    {
        current_pw = (EditText)this.findViewById(R.id.current_pw);
        new_pw = (EditText)this.findViewById(R.id.new_pw);
        ack_pw = (EditText)this.findViewById(R.id.ack_pw);
        ack_button = (Button)this.findViewById(R.id.settings_ack_button);
        ack_button.setOnClickListener(ackOnClickListener);
        current_pw.setNextFocusDownId(R.id.new_pw);
        new_pw.setNextFocusDownId(R.id.ack_pw);
        myHandler = new MessageHandler(Looper.myLooper());
    }
    
    OnClickListener ackOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (!checkCurrentPW())
                return;
            if (!checkNewPW())
                return;
            if (!checkAckPW())
                return;
            
            JSONObject params = new JSONObject();
            try {
                params.put("email", email);
                params.put("has_salt", 0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            new HttpSender().Httppost(OperationCode.GET_SALT_VALUE, params, myHandler);
        }
    };
    
    private CharSequence getColorHint(String str)
    {
        return Html.fromHtml("<font color=#ff0000>"+str+"</font>");
    }
    
    private boolean checkCurrentPW()
    {
        curString = current_pw.getText().toString();
        if (null == curString || 0 == curString.length())
        {
            current_pw.setError(getColorHint("请输入当前密码"));
            return false;
        }
        else if (curString.length() < 5 || curString.length() > 15)
        {
            current_pw.setError(getColorHint("密码不正确"));
            return false;
        }
        return true;
    }
     
    private boolean checkNewPW()
    {
        newString = new_pw.getText().toString();
        if (null == newString || 0 == newString.length())
        {
            new_pw.setError(getColorHint("请输入新的密码"));
            return false;
        }
        else if (newString.length() < 5 || newString.length() > 15)
        {
            new_pw.setError(getColorHint("密码长度为5-15"));
            return false;
        }
        return true;
    }
    
    private boolean checkAckPW()
    {
        ackString = ack_pw.getText().toString();
        if (null == ackString || 0 == ackString.length())
        {
            ack_pw.setError(getColorHint("确认新密码"));
            return false;
        }
        else if (!ackString.equals(newString))
        {
            ack_pw.setError(getColorHint("密码不一致"));
            return false;
        }
        return true;
    }
    
    class MessageHandler extends Handler
    {       
        public MessageHandler(Looper looper) {
            // TODO Auto-generated constructor stub
            super(looper);
        }
        
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
            case OperationCode.GET_SALT_VALUE:       
                try {
                    JSONObject jo = new JSONObject(msg.obj.toString());
                    JSONObject params = new JSONObject();
                    if (ReturnCode.GET_SALT == jo.getInt("cmd"))
                    {
                        String salt = jo.getString("salt");
                        params.put("passwd", RegUtils.Md5(curString+salt));
                        params.put("newpw", RegUtils.Md5(newString+salt));
                        params.put("email", email);
                        new HttpSender().Httppost(OperationCode.CHANGE_PW, params, myHandler);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case OperationCode.CHANGE_PW:
                try {
                    JSONObject jo = new JSONObject(msg.obj.toString());
                    if (ReturnCode.NORMAL_REPLY == jo.getInt("cmd"))
                    {
                        setResult(RESULT_OK);
                        ChangPW.this.finish();
                    }
                    else if (ReturnCode.PASSWD_NOT_CORRECT == jo.getInt("cmd"))
                    {
                        current_pw.setError(getColorHint("密码不正确"));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
            }
        }
    }
}
