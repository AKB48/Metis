package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.Main.CommentPage;
import com.app.Main.PhotosWall;
import com.app.localDataBase.NotificationTableAdapter;
import com.example.slidingmenudemo.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class cardAdapter extends BaseAdapter
{
	private static final String TAG = "cardAdapter";
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater mInflater;
	
	private Context context;
	private int resource;
	private String from[];
	private int to[];	
	private int toAvatar[];
	private int screenW;
	private boolean inActivity;
	private int userId = -1;
	private Handler ncHandler;
	private imageUtil forImageUtil  = imageUtil.getInstance();
	private int toAvatarBlock[] = {  R.id.user1Block, R.id.user2Block, R.id.user3Block, R.id.user4Block };
	public static final int CARD_INFO_CHANGE = 555;
	
	//add 2013-10-20
	private static Vector<Integer> notInCacheAvatarIdList = new Vector<Integer>();
	
	public cardAdapter() {
		// TODO Auto-generated constructor stub
		Log.e(TAG, "cardAdapter constructor error: no params");
	}
	
	public cardAdapter(Context context, ArrayList<HashMap<String, Object>> list, int resource,
			String []from, int []to, int []toAvatar, int screenW, boolean inActivity, int userId, Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.resource = resource;
		this.from = from;
		this.to = to;
		this.screenW = screenW;
		this.inActivity = inActivity;
		this.userId = userId;
		this.ncHandler = handler;
		this.toAvatar = toAvatar;
	}
	
	private void SetContentWidth(View main, View v)
    {
		//reference
		View dateView = main.findViewById(R.id.leftDateBlock);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 115 * dateW / 60;
    	int screenWidth = screenW;
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = screenWidth - delPix;
		v.setLayoutParams(params);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		int avatarNum = 4;
		int infoViewNum = to.length;
		
		// Only the first time will get the widgets
		if (convertView==null) 
		{
			convertView = mInflater.inflate(resource, parent, false);
			
			holder = new ViewHolder(avatarNum, infoViewNum);
			holder.firstLayer = convertView.findViewById(R.id.firstLayerCard);
			holder.secondLayer = convertView.findViewById(R.id.secondLayerCard);
			
			holder.activityInfoAllView = convertView.findViewById(R.id.activityInfoAll);
			holder.join = (CircularImage)convertView.findViewById(R.id.joinBtn);
			holder.comment_btn = convertView.findViewById(R.id.comment_btn);
			holder.takephoto_btn = convertView.findViewById(R.id.takephoto_btn);
			holder.slideBtn = convertView.findViewById(R.id.slideBtn);
			
			for( int i=0; i<toAvatarBlock.length; i++)
			{
				holder.avatarViews[i] = convertView.findViewById( toAvatarBlock[i]);
				holder.avatarCImages[i] = (CircularImage)convertView.findViewById( toAvatar[i] );
			}
			for( int k=0; k<infoViewNum; k++)
				holder.infoViews[k] = (TextView)convertView.findViewById( to[k] );
			
			SetContentWidth(convertView, holder.activityInfoAllView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		//set text or set something else about the view		
		init(holder, position, convertView);
		//SetContentWidth(convertView, holder.activityInfoAllView);  //move to first get widgets
		
		return convertView;
	}
	
	private void init(ViewHolder holder, int pos, View view)
	{
		HashMap<String, Object> item = list.get(pos);
		Boolean open = (Boolean)item.get("open");
		
		if( open.equals(false))
		{
			holder.secondLayer.setVisibility(View.GONE);
			holder.firstLayer.setVisibility(View.VISIBLE);
			initFirstLayer( item, holder, pos);
		}
		else
		{
			holder.firstLayer.setVisibility(View.GONE);
			holder.secondLayer.setVisibility(View.VISIBLE);
			initSecondLayer(item, view, pos);
		}
	}
	
	private void initFirstLayer(final HashMap<String, Object> item, final ViewHolder holder, final int pos)
	{
		JSONArray avatarJsonArray = (JSONArray) item.get("photolistJsonArray");
		int length = avatarJsonArray.length();
		
		try {
			int i=0;

			for ( ; i<length && i<4; i++)           //本地有头像，用本地的头像
			{
				holder.avatarViews[i].setVisibility(View.VISIBLE);
				
				final int id = avatarJsonArray.getInt(i);				
				Bitmap bitmap = forImageUtil.getBitmapFromMemCache(id);

				if( bitmap!=null )
				{
					holder.avatarCImages[i].setImageBitmap(bitmap);		
					//Log.e("TAG", "avatar in cache " + id);
				}
				else
				{
					holder.avatarCImages[i].setImageResource(R.drawable.defaultavatar);
					
					boolean haveAcquire = notInCacheAvatarIdList.contains( id );
					if( false==haveAcquire )
					{
						notInCacheAvatarIdList.add( id );
						
						new Thread()
						{
							public void run()
							{						
									boolean inFile = imageUtil.fileExist(id);
									if( inFile!=false)
									{
										Bitmap bitmap = imageUtil.getLocalBitmapBy(id);
										if( bitmap!=null )
										{
											forImageUtil.addBitmapToMemoryCache(id, bitmap);
											
											Message msg = new Message();
											msg.what = CARD_INFO_CHANGE;
											ncHandler.sendMessage(msg);
										}
									}								
								}					
						}.start();
					}
				}

			}
			
			for( ; i<4; i++)
			{
				holder.avatarViews[i].setVisibility(View.GONE);
			}

		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if( inActivity==false )
		{
			holder.join.setImageResource(R.drawable.join);	
			holder.join.setOnClickListener(	new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						join(pos);					
					}
			});
		}
		
		holder.comment_btn.setTag(pos);
		holder.comment_btn.setOnClickListener(buttonsOnClickListener);
		holder.takephoto_btn.setTag(pos);
		holder.takephoto_btn.setOnClickListener(buttonsOnClickListener);
		holder.slideBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetSecondWidth(holder.secondLayer);
				holder.secondLayer.setVisibility(View.VISIBLE);
				slideview(holder.firstLayer, 0, -(screenW));					
				item.put("open", true);
			}
		});
		
		for (int i = 0; i < to.length; i++) 
		{
			holder.infoViews[i].setText( (String)list.get(pos).get( from[i] ) );
		}
	}
	
	private void SetSecondWidth(View main)
    {
		//reference
		View dateView = main.findViewById(R.id.secondLayer_LeftBlock);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 50 * dateW / 40;
    	int screenWidth = screenW;
    	
    	View imageFlowView = main.findViewById(R.id.second_imageFlow);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageFlowView.getLayoutParams();
		params.width = (screenWidth - delPix)/2;
		imageFlowView.setLayoutParams(params);
		
		View second_commentsBlock = main.findViewById(R.id.second_commentsBlock);
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)second_commentsBlock.getLayoutParams();
		params2.width = (screenWidth - delPix)/2;
		second_commentsBlock.setLayoutParams(params2);
	}
	
	private void initSecondLayer(final HashMap<String, Object> item, final View view, int pos)
	{
		SetSecondWidth(view);
		
		View comment_btn = view.findViewById(R.id.second_comment_btn);
		View takephoto_btn = view.findViewById(R.id.second_takephoto_btn);
		View slideBtn = view.findViewById(R.id.second_slideBtn);
		
		comment_btn.setTag(pos);
		comment_btn.setOnClickListener(buttonsOnClickListener);
		takephoto_btn.setTag(pos);
		takephoto_btn.setOnClickListener(buttonsOnClickListener);
		slideBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				view.findViewById(R.id.firstLayerCard).setVisibility(View.VISIBLE);
				slideview(view.findViewById(R.id.secondLayerCard), 0, (screenW));	
				item.put("open", false);
			}
		});
	}
	
	public void slideview(final View view, final float p1,  final double p2) 
	{
		long durationMillis = 500;
		
	    TranslateAnimation animation = new TranslateAnimation(p1, (float) p2, 0, 0); 
		animation.setDuration(durationMillis);	    
		
	    animation.setAnimationListener(new Animation.AnimationListener() 
	    {
	        @Override
	        public void onAnimationStart(Animation animation) 
	        {
	        }
	        
	        @Override
	        public void onAnimationRepeat(Animation animation) 
	        {
	        }
	        
	        @Override
	        public void onAnimationEnd(Animation animation) 
	        {
	            int left = view.getLeft()+(int)(p2-p1);
	            int top = view.getTop();
	            int width = view.getWidth();
	            int height = view.getHeight();
	            
	            view.clearAnimation();  //must before layout(l,t,r,b)
	            view.layout(left, top, left+width, top+height);
				
				Message msg = new Message();
				msg.what = CARD_INFO_CHANGE;
				ncHandler.sendMessage(msg);
	        }
	    });
	    
	    view.startAnimation(animation);
	}
	
	private OnClickListener buttonsOnClickListener = new OnClickListener() 
	{

		@Override
		public void onClick(View v) {
			
			int position = (Integer) v.getTag();
			HashMap<String, Object> an_activity = list.get(position);
			int event_id = Integer.parseInt(an_activity.get("event_id").toString());
			
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
				case R.id.comment_btn:
				case R.id.second_comment_btn:
					Intent intent = new Intent();
						intent.putExtra("userId", userId);
						intent.putExtra("eventId", event_id);
					intent.setClass(context, CommentPage.class);
					context.startActivity(intent);
					break;
				case R.id.takephoto_btn:
				case R.id.second_takephoto_btn:					
					Intent intent2 = new Intent();
						intent2.putExtra("userId", userId);
						intent2.putExtra("eventId", event_id);
					intent2.setClass(context, PhotosWall.class);
					context.startActivity(intent2);	
					
//					intent2.putExtra("userId", userId);
//					intent2.putExtra("photo_id", 11);
//					intent2.setClass(context, PhotoMainPage.class);
//					context.startActivity(intent2);
					break;
				default:
					break;
			}
		}
	};
	
	private void join( final int pos)
	{
		final int currentUId = (Integer) list.get(pos).get("id");
		final int eventId = (Integer) list.get(pos).get("event_id");
		final int item_id = (Integer) list.get(pos).get("item_id");
		
//		Toast.makeText(context, "join: pos="+pos + " uid=" + currentUId, Toast.LENGTH_SHORT).show();
		
		AlertDialog.Builder builder = new Builder(context);
		
		builder.setTitle("活动验证").setIcon(R.drawable.ic_launcher).setMessage("加入活动吗？").create();
		builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				JSONObject params = new JSONObject();
				try {
					params.put("cmd", 997);
					params.put("id", currentUId);
					params.put("event_id", eventId);
					params.put("result", 1);
					HttpSender httpSender = new HttpSender();
					httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
					Log.e("cardAdapter", "join: " + params);
					
					//to MyEvents.java
					Intent intent = new Intent("JoinMsg");
					intent.putExtra("item_id", item_id);
					context.sendBroadcast(intent);
					
//					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
//					adapter.deleteData( item_id );
					
					list.remove(pos);
					cardAdapter.this.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				JSONObject params = new JSONObject();
//				try {
//					params.put("cmd", 997);
//					params.put("id", currentUId);
//					params.put("event_id", eventId);
//					params.put("result", 0);
//					HttpSender httpSender = new HttpSender();
//					httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
//					Log.e("cardAdapter", "refuse: " + params);
//					
//					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
//					adapter.deleteData( item_id );
//					
//					list.remove(pos);
//					cardAdapter.this.notifyDataSetChanged();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});
		
		builder.show();
	}
	
	class ViewHolder
	{
		View firstLayer;
		View secondLayer;
		View []avatarViews;
		View activityInfoAllView;
		CircularImage join;
		View comment_btn;
		View takephoto_btn;
		View slideBtn;
		TextView []infoViews;
		CircularImage []avatarCImages;
		
		public ViewHolder(){}
		public ViewHolder(int avatarNum, int infoViewsNum)
		{
			avatarViews = new View[avatarNum];
			avatarCImages = new CircularImage[avatarNum];
			infoViews = new TextView[infoViewsNum];			
		}
	}

}
