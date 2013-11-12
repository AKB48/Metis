package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.TableFriends;
import com.example.slidingmenudemo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AdapterForPaticipantList extends BaseAdapter
{

	private LayoutInflater mInflater;
	private List<FriendInfor> mData;
	private int userId;
	public static Map<Integer, Boolean> isSelected;
	private Context context;
	
	public AdapterForPaticipantList (Context context, int userId)
	{
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.userId = userId;
		init();
	}
	
	private void init()
	{
		mData = new ArrayList<FriendInfor>();
		TableFriends tbFriends = new TableFriends(context);
		ArrayList<FriendStruct> friendsList = tbFriends.getAllFriends(userId+"");
		for (FriendStruct friendStruct : friendsList) 
			mData.add( new FriendInfor(friendStruct.fid+"", friendStruct.fname) );
		
//		for(int i=1; i<=5; i++)
//		{
//			mData.add(new FriendInfor("00"+i, "µË³ÉÁÖ"+i));
//			mData.add(new FriendInfor("01"+i, "Íõ½òÁè"+i));
//			mData.add(new FriendInfor("02"+i, "ÑîÁøÁØ"+i));
//		}
		
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < mData.size(); i++)		
			isSelected.put(i, false);
	}
	
	public int getCount()
	{
		// TODO Auto-generated method stub
		return mData.size();
	}

	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if(convertView==null)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.vlist, null);
			
			holder.contactName = (TextView)convertView.findViewById(R.id.contactName);
			holder.cBox = (CheckBox)convertView.findViewById(R.id.cBox);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.contactName.setText(mData.get(position).u_name.toString());
		holder.cBox.setChecked(isSelected.get(position));
		
		return convertView;
	}
	
	public final class ViewHolder
	{
		public TextView contactName;
		public CheckBox cBox;
	}
	
	public final class FriendInfor
	{
		public FriendInfor(String id, String name)
		{
			u_id = id;
			u_name = name;
		}
		
		public String u_id;
		public String u_name;
	}

}
