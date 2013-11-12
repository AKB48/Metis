package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.Main.MainActivity;
import com.app.localDataBase.TableFriends;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterForFriendList extends BaseAdapter{

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> list;
	private int layoutID;
	private String key[];
	private int viewID[];
	
	public AdapterForFriendList(Context context, 
			ArrayList<HashMap<String, Object>> list, 
			int layoutId, String key[], 
			int viewId[] )
	{
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutId;
		this.key = key;
		this.viewID = viewId;
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
		final int pos = position;
		if(convertView == null)
		{
			convertView = mInflater.inflate(layoutID, null);
			viewHolder = new ViewHolder();
			viewHolder.fname = (TextView)convertView.findViewById(R.id.friend_list_item_fname);
			//viewHolder.gender = (TextView)convertView.findViewById(R.id.friend_list_item_gender);
			//viewHolder.email = (TextView)convertView.findViewById(R.id.friend_list_item_email);
			viewHolder.avatar = (ImageView)convertView.findViewById(R.id.friend_list_item_avatar);
			viewHolder.deleteBtn = (Button)convertView.findViewById(R.id.friend_list_item_deleteBtn);
			
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		int viewIdLength = viewID.length;
		HashMap<String, Object> item = list.get(pos);
		int fid = Integer.parseInt(item.get("fid").toString());
		for(int i=0;i<viewIdLength;i++)
		{
			switch(viewID[i])
			{
			case R.id.friend_list_item_fname:
			    if (fid >= 0)
			    {
			        viewHolder.fname.setText((String)item.get(key[i]));
			    }
			    else {
			        viewHolder.fname.setText("");
                }
				break;
//			case R.id.friend_list_item_gender:
//				viewHolder.gender.setText((String)item.get(key[i]));
//				break;
//			case R.id.friend_list_item_email:
//				viewHolder.email.setText((String)item.get(key[i]));
//				break;
			case R.id.friend_list_item_avatar:
			    if (fid > 0)
			    {
			        Bitmap avatar_bitmap = imageUtil.getInstance().getAvatar(fid);
			        if (null != avatar_bitmap)
                    {
                        viewHolder.avatar.setImageBitmap(avatar_bitmap);
                    }
                    else 
                    {
                        viewHolder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultavatar));    
                    }
			    }
			    else
			    {
			        //int width = viewHolder.avatar.getWidth();
			        //int height = viewHolder.avatar.getHeight();
			        int width =  MainActivity.dip2px(context, 50);
			        int height = width;
			        Bitmap newb = Bitmap.createBitmap( width, height, Config.ARGB_8888 );  
			        Canvas canvasTemp = new Canvas( newb );  
			        canvasTemp.drawColor(Color.WHITE);
			        if (fid == -1) {
			            Paint p = new Paint();   
    			        p.setColor(Color.CYAN);  
    			        p.setTextSize(100);
    			        String showText = (String)item.get("fname");
    			        float dif= p.measureText(showText) / 2;
    			        canvasTemp.drawText(showText, width/2-dif, height/2+dif, p);  
			        }
			        Drawable drawable = new BitmapDrawable(newb);
			        viewHolder.avatar.setImageDrawable(drawable);
			    }
			    break;
				default:
					Log.v("test1", "nothing");
					break;
			}
		}
//		if (fid > 0)
//		{
//    		OnClickListener deleteBtnListener = new OnClickListener() {
//    			
//    			@Override
//    			public void onClick(View v) {
//    				// TODO Auto-generated method stub
//    				String uid = list.get(pos).get("uid").toString();
//    				String fid = list.get(pos).get("fid").toString();
//    				TableFriends tf = new TableFriends(context);
//    				tf.delete(uid,fid);
//    				list.remove(pos);
//    				AdapterForFriendList.this.notifyDataSetChanged();
//    			}
//    		};
//    		viewHolder.deleteBtn.setOnClickListener(deleteBtnListener);
//    		viewHolder.deleteBtn.setVisibility(View.VISIBLE);
//		}
//		else {
//		    viewHolder.deleteBtn.setVisibility(View.GONE);
//        }
		viewHolder.deleteBtn.setVisibility(View.GONE);
		
		return convertView;
	}
	
	@Override        
	public boolean isEnabled(int position) 
	{                 
	    if (Integer.parseInt(list.get(position).get("fid").toString()) == -1)   
	        return false;   
	    else                    
	        return super.isEnabled(position);   
	} 
	
	private class ViewHolder
	{
		TextView fname;
		TextView gender;
		TextView email;
		ImageView avatar;
		Button deleteBtn;
		
		public ViewHolder()
		{
			fname = null;
			gender = null;
			email = null;
			avatar = null;
			deleteBtn = null;
		}
	}
	

}
