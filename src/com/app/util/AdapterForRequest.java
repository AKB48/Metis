package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.localDataBase.NotificationTableAdapter;
import com.example.slidingmenudemo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterForRequest extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> list;
	private int layoutID;
	private String key[];
	private int viewID[];
	private AdapterForRequest adapter = this;
	
	public AdapterForRequest(Context context, ArrayList<HashMap<String, Object>> list, int layoutID,
			String key[], int viewID[])
	{
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutID;
		this.key = key;
		this.viewID = viewID;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = mInflater.inflate(layoutID, null);
		
		int keyCount = key.length;
		Button button = new Button(context);
		button = (Button)view.findViewById(R.id.request_result_delete);
		button.setTag(arg0);
		button.setFocusable(false);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int position = Integer.parseInt(arg0.getTag().toString());
				
				//点击了删除request result button的时候，要更新消息数据库 add by luo
				NotificationTableAdapter noticeAdapter = new NotificationTableAdapter(context);			
				HashMap<String, Object> noticeItem = list.get(position);
				int itemID = (Integer) noticeItem.get("item_id");
				noticeAdapter.deleteData(itemID);
				
				// TODO Auto-generated method stub
				Toast.makeText(context, ""+arg0.getTag().toString(), Toast.LENGTH_SHORT).show();
				
				list.remove(position);
				adapter.notifyDataSetChanged();
				
			}
		});
		for(int i=0;i<keyCount;i++)
		{
			
			if(view.findViewById(viewID[i]) instanceof TextView)
			{
				TextView tv = (TextView)view.findViewById(viewID[i]);
				tv.setText((String)list.get(arg0).get(key[i]));
			}
			else 
			{
				
			}
		}
		return view;
	}

}
