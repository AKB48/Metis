package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;

import com.app.util.HttpSender;
import com.app.util.Messager;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

public class MessagaAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<notificationObject> list;
    private int layoutID;
    private int userId;
    private myHandler mHandler;
    
    public MessagaAdapter(Context context, 
            ArrayList<notificationObject> list,
            int uid)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.userId = uid;
        this.mHandler = new myHandler();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        notificationObject item = list.get(position);
        JSONObject jo = null;
        String noticeString = "";
        int type = -1;
        int fid = -1;
        int event_id = -1;
        final int item_id = item.item_ID;
        try {
            jo = new JSONObject(item.msg);
            type = jo.getInt("cmd");
            
            switch (type) {
            case ReturnCode.ADD_FRIEND_NOTIFICATION:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("id");
                noticeString = context.getResources().getString(R.string.request_for_friend);
                break;
            case ReturnCode.ADD_FRIEND_RESULT:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("id");
                if (jo.getBoolean("result")) {
                    noticeString = context.getResources().getString(R.string.pass_friend_request);
                }
                else {
                    noticeString = context.getResources().getString(R.string.refuse_friend_request);
                }
                break;
            case ReturnCode.NEW_EVENT_NOTIFICATION:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("launcher_id");
                event_id = jo.getInt("event_id");
                noticeString = context.getResources().getString(R.string.request_event_invitation) + " [ " + jo.getString("subject") + " ] ";
                break;
            case ReturnCode.EVENT_INVITE_RESPONSE:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("id");
                event_id = jo.getInt("event_id");
                if (jo.getBoolean("result")) {
                    noticeString = context.getResources().getString(R.string.pass_event_invitation) + " [ " + jo.getString("subject") + " ] ";
                }
                else {
                    noticeString = context.getResources().getString(R.string.refuse_event_invitation) + " [ " + jo.getString("subject") + " ] ";
                }
                break;
            case ReturnCode.REQUEST_EVENT:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("id");
                event_id = jo.getInt("event_id");
                noticeString = context.getResources().getString(R.string.request_for_event) + " [ " + jo.getString("subject") + " ] ";
                break;
            case ReturnCode.REQUEST_EVENT_RESPONSE:
                layoutID = R.layout.friend_requests;
                fid = jo.getInt("launcher_id");
                event_id = jo.getInt("event_id");
                if (jo.getBoolean("result")) {
                    noticeString = context.getResources().getString(R.string.pass_event_request) + " [ " + jo.getString("subject") + " ] ";
                }
                else {
                    noticeString = context.getResources().getString(R.string.refuse_event_request) + " [ " + jo.getString("subject") + " ] ";
                }
                break;
            default:
                layoutID = -1;
                noticeString = "";
                break;
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if (layoutID == -1 || type == -1)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = mInflater.inflate(layoutID, null);
            viewHolder = new ViewHolder();
            viewHolder.fname = (TextView)convertView.findViewById(R.id.friend_name);
            viewHolder.confirm_msg = (TextView)convertView.findViewById(R.id.confirm_msg);
            viewHolder.notice = (TextView)convertView.findViewById(R.id.valication_result);
            viewHolder.avatar = (ImageView)convertView.findViewById(R.id.nc_avatar);
            viewHolder.leftBtn = (Button)convertView.findViewById(R.id.nc_pass);
            viewHolder.rightBtn = (Button)convertView.findViewById(R.id.nc_refuse);
            
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        try {
            if (type == ReturnCode.NEW_EVENT_NOTIFICATION || type == ReturnCode.REQUEST_EVENT_RESPONSE) {
                viewHolder.fname.setText(jo.getString("launcher"));
            }
            else {
                viewHolder.fname.setText(jo.getString("name"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bitmap avatar_bmp = imageUtil.getInstance().getAvatar(fid);
        if (null != avatar_bmp)
        {
            viewHolder.avatar.setImageBitmap(avatar_bmp);
        }
        else 
        {
            viewHolder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultavatar));    
        }
        try {
            if (type == ReturnCode.ADD_FRIEND_NOTIFICATION || type == ReturnCode.REQUEST_EVENT)
            {
                viewHolder.confirm_msg.setText(jo.getString("confirm_msg"));
                viewHolder.confirm_msg.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.confirm_msg.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        viewHolder.notice.setText(noticeString);
        
        final int final_type = type;
        final int final_fid = fid;
        final int final_event_id = event_id;
        OnClickListener passOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (final_type) {
                case ReturnCode.ADD_FRIEND_NOTIFICATION:
                    sendMessage(userId, final_fid, 998, 1, item_id, -1);
                    break;
                case ReturnCode.REQUEST_EVENT:
                    sendMessage(userId, final_fid, 994, 1, item_id, final_event_id);
                    break;
                default:
                    break;
                }
            }
        };
        
        OnClickListener refuseOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (final_type) {
                case ReturnCode.ADD_FRIEND_NOTIFICATION:
                    sendMessage(userId, final_fid, 998, 0, item_id, -1);
                    break;
                case ReturnCode.REQUEST_EVENT:
                    sendMessage(userId, final_fid, 994, 0, item_id, final_event_id);
                default:
                    break;
                }
            }
        };
        
        OnClickListener deleteOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mHandler.removeItem(item_id);
                MessagaAdapter.this.notifyDataSetChanged();
            }
        };
        
        switch (type) {
        case ReturnCode.ADD_FRIEND_NOTIFICATION:
        case ReturnCode.REQUEST_EVENT:
            viewHolder.leftBtn.setOnClickListener(passOnClickListener);
            viewHolder.leftBtn.setVisibility(View.VISIBLE);
            viewHolder.leftBtn.setText(R.string.pass);
            viewHolder.rightBtn.setOnClickListener(refuseOnClickListener);
            viewHolder.rightBtn.setVisibility(View.VISIBLE);
            viewHolder.rightBtn.setText(R.string.refuse);
            break;
        case ReturnCode.ADD_FRIEND_RESULT:
        case ReturnCode.NEW_EVENT_NOTIFICATION:
        case ReturnCode.EVENT_INVITE_RESPONSE:
        case ReturnCode.REQUEST_EVENT_RESPONSE:
            viewHolder.leftBtn.setVisibility(View.GONE);
            viewHolder.rightBtn.setOnClickListener(deleteOnClickListener);
            viewHolder.rightBtn.setVisibility(View.VISIBLE);
            viewHolder.rightBtn.setText(R.string.delete);
            break;
        default:
            
            break;
        }
          
        return convertView;
    }
    
    private void sendMessage(int uid,int fid, int cmd, int result, int item_id, int event_id)
    {
        JSONObject params = new JSONObject();
        try {
            params.put("id", uid);
            params.put("cmd", cmd);
            params.put("item_id", item_id);
            if (-1 != event_id) {
                params.put("event_id", event_id);
                params.put("requester_id", fid);
            }
            else {
                params.put("friend_id", fid);
            }
            if(result==1)
                params.put("result", true);
            else 
                params.put("result", false);
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("FriendsCenter", "我同意或者拒绝时，给服务器发送的是："+params.toString());
        HttpSender http = new HttpSender();
        if (ReturnCode.ADD_FRIEND_RESULT == cmd) {
            http.Httppost(OperationCode.ADD_FRIEND, params, mHandler);
        }
        else if (ReturnCode.REQUEST_EVENT_RESPONSE == cmd) {
            http.Httppost(OperationCode.PARTICIPATE_EVENT, params, mHandler);
        }
        else {
            Log.i("MessageAdapter", "sendMessage cmd Error");
        }
    }
    
    private class ViewHolder
    {
        TextView fname;
        TextView confirm_msg;
        TextView notice;
        ImageView avatar;
        Button leftBtn, rightBtn;
        
        public ViewHolder()
        {
            fname = null;
            confirm_msg = null;
            notice = null;
            avatar = null;
            leftBtn = null;
            rightBtn = null;
        }
    }
    
    public class myHandler extends Handler
    {
        public myHandler() {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what)
            {
            case OperationCode.ADD_FRIEND:
            case OperationCode.PARTICIPATE_EVENT:
                try {
                    JSONObject jo = new JSONObject(msg.obj.toString());
                    int cmd = jo.getInt("cmd");
                    if (ReturnCode.NORMAL_REPLY == cmd)
                    {                        
                        //insert friend into database
                        if (OperationCode.ADD_FRIEND == msg.what && 1 == jo.getInt("result")) {
                            NotificationTableAdapter adapter = new NotificationTableAdapter(context);
                            JSONObject msgJson = new JSONObject(adapter.queryOneMsgData(jo.getInt("item_id")));
                            msgJson.put("uid", userId);
                            FriendStruct newFriend = new FriendStruct(msgJson);
                            TableFriends tableFriends = new TableFriends(context);
                            tableFriends.add(newFriend);
                            Messager.getInstance().notifyChanged(1);
                        }
                        //更新显示信息
                        removeItem(jo.getInt("item_id"));
                        MessagaAdapter.this.notifyDataSetChanged();
                    }
                    else if (ReturnCode.ALREADY_FRIENDS == cmd)
                    {
                        //更新显示信息
                        removeItem(jo.getInt("item_id"));
                        MessagaAdapter.this.notifyDataSetChanged();
                        Toast.makeText(context, R.string.already_friends, Toast.LENGTH_SHORT);
                    }
                    else if (ReturnCode.ALREADY_IN_EVENT == cmd)
                    {
                        //更新显示信息
                        removeItem(jo.getInt("item_id"));
                        MessagaAdapter.this.notifyDataSetChanged();
                        Toast.makeText(context, R.string.already_in_event, Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
                default: break;
            }
        }
        
        public void removeItem(int item_id)
        {
          //删除消息数据表
            NotificationTableAdapter adapter = new NotificationTableAdapter(context);
            adapter.deleteData( item_id );
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).item_ID == item_id)
                {
                    list.remove(i);
                    break;
                }
            }
        }
    }
    
    
}

