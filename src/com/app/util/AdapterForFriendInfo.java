package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.localDataBase.FriendStruct;
import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterForFriendInfo extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<FriendStruct> list;
    private int layoutID;
    private int userId;
    private EditText dialogInputET;
    private MessageHandler mHandler;
    
    public AdapterForFriendInfo(Context context, 
            ArrayList<FriendStruct> list, int userId)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = R.layout.friend_info;
        this.userId = userId;
        this.mHandler = new MessageHandler(Looper.myLooper());
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (list.size() == 0 || position < 0 || position > list.size() )
        {
            return null;
        }
        else {
            return list.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = mInflater.inflate(layoutID, null);
            viewHolder = new ViewHolder();
            viewHolder.fname = (TextView)convertView.findViewById(R.id.friend_info_name);
            viewHolder.gender = (ImageView)convertView.findViewById(R.id.friend_info_gender);
            viewHolder.avatar = (ImageView)convertView.findViewById(R.id.friend_info_avatar);
            viewHolder.location = (TextView)convertView.findViewById(R.id.friend_info_location);
            viewHolder.sign = (TextView)convertView.findViewById(R.id.friend_info_sign);
            viewHolder.addBtn = (Button)convertView.findViewById(R.id.add_frinend_bt);
            
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        
        final FriendStruct item = list.get(position);
        Bitmap bitmap = imageUtil.getInstance().getAvatar(item.fid);
        if (null != bitmap) {
            viewHolder.avatar.setImageBitmap(bitmap);
        }
        else {
            viewHolder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultavatar));
        }

        viewHolder.fname.setText(item.fname);
        if (1 == Integer.parseInt(item.gender)) {
            viewHolder.gender.setImageDrawable(context.getResources().getDrawable(R.drawable.male));
        }
        else {
            viewHolder.gender.setImageDrawable(context.getResources().getDrawable(R.drawable.female));
        }
        viewHolder.location.setText(item.location);
        viewHolder.sign.setText(item.sign);
        if (item.isFriend) {
            viewHolder.addBtn.setVisibility(View.GONE);
        }
        else {
            viewHolder.addBtn.setVisibility(View.VISIBLE);
            viewHolder.addBtn.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    /**
                     * 弹框输入验证信息
                     */                   
                    dialogInputET = new EditText(context);
                    new AlertDialog.Builder(context)
                        .setTitle("请输入添加验证消息:")
                        .setView(dialogInputET)
                        .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                            
                            public void onClick(DialogInterface dialog, int which) {        
                                    final String inputStr = dialogInputET.getText().toString().trim();
                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            sendAddFriendRequest(item.fid, inputStr);
                                        }
                                    }.start();          
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();      
                }
            });
        }
  
        return convertView;
    }
    
    
    private void sendAddFriendRequest(int fid, String inputStr)
    {
        JSONObject addFriendParams = new JSONObject();
            try {
                addFriendParams.put("id", userId);
                addFriendParams.put("friend_id", fid);
                addFriendParams.put("cmd", 999);
                addFriendParams.put("confirm_msg", inputStr);
            
                HttpSender http = new HttpSender();
                http.Httppost(OperationCode.ADD_FRIEND, addFriendParams, mHandler);                          
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    
    @Override        
    public boolean isEnabled(int position) 
    {                 
        return false;   // can not click each item
    } 
    
    private class ViewHolder
    {
        TextView fname;
        ImageView gender;
        TextView location;
        TextView sign;
        ImageView avatar;
        Button addBtn;
        
        public ViewHolder()
        {
            fname = null;
            gender = null;
            location = null;
            avatar = null;
            sign = null;
            addBtn = null;
        }
    }
    
    
    class MessageHandler extends Handler
    {
        public MessageHandler(Looper looper)
        {
            super(looper);
        }
        
        public void handleMessage(Message msg) 
        {
            switch (msg.what)
            {
            case OperationCode.ADD_FRIEND:
                try
                {                           
                    JSONObject jo = new JSONObject(msg.obj.toString());
                    int cmdSearch   = jo.getInt("cmd");
                    if( ReturnCode.NORMAL_REPLY == cmdSearch)
                    {
                        Toast.makeText(context, "请等待对方验证", Toast.LENGTH_SHORT).show();
                    }
                    else if( ReturnCode.ALREADY_FRIENDS==cmdSearch)  {     
                        Toast.makeText(context, R.string.already_friends, Toast.LENGTH_SHORT).show();
                    }
                    else 
                        Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }               
                break;
            default:
                break;
            }
        }
    }
    

}
