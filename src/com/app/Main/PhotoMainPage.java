package com.app.Main;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoMainPage extends Activity{

	private final String TAG = "PhotoMainPage";
	private int userId;
//	private int eventId;
	private int photo_id;
	private int author_id;
	private String author;
	private String time;
	private int good;
	private String description;
	private int sequence;
	private boolean isZan;
	private View contentView;
	
	private Button backBtn;
	private TextView photoDeleteBtn;
	private ImageView masterAvatar;
	private TextView masterName;
	private TextView masterTime;
	private ImageView photo;
	private TextView photoDescription;
	private LinearLayout photoZan;
	private ImageView photoZanImage;
	private TextView photoZanSum;
	private ImageView replyImage;
	private ImageView shareImage;
	private ImageView downloadImage;
	private TextView getMoreCommentsBtn;
	private LinearLayout photoCommentListRootView;
	
	private ArrayList<HashMap<String, Object>> photoComments = new ArrayList<HashMap<String,Object>>();
	private myHandler PMPHandler = new myHandler();
	private HttpSender httpSender;
	
	//add by luo
	private boolean scaleOrNot = false;
	private int photoHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.photo_main_page, null);
		setContentView(contentView);
		init();
		
		getCommentsFromServer(sequence);
	}
	
	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
//		eventId = intent.getIntExtra("eventId", -1);
		photo_id = intent.getIntExtra("photo_id", -1);
		sequence = 0;
		isZan = false;
		author = "";
		time = "";
		description = "";
		good= 0;
		httpSender = new HttpSender();
		
		backBtn = (Button) contentView.findViewById(R.id.photo_main_page_backBtn);
		photoDeleteBtn = (TextView)contentView.findViewById(R.id.photo_main_page_deletePhoto);
		masterAvatar = (ImageView)contentView.findViewById(R.id.photo_main_page_masterAvatar);
		masterName = (TextView)contentView.findViewById(R.id.photo_main_page_masterName);
		masterTime = (TextView)contentView.findViewById(R.id.photo_main_page_time);
		photo = (ImageView) contentView.findViewById(R.id.photo_main_page_photo);
		photoDescription = (TextView) contentView.findViewById(R.id.photo_main_page_description);
		photoZan = (LinearLayout) contentView.findViewById(R.id.photo_main_page_photoZan);
		photoZanImage = (ImageView) contentView.findViewById(R.id.photo_main_page_zanImage);
		photoZanSum = (TextView)contentView.findViewById(R.id.photo_main_page_zanSum);
		replyImage = (ImageView) contentView.findViewById(R.id.photo_main_page_replyImage);
		shareImage = (ImageView) contentView.findViewById(R.id.photo_main_page_share);
		downloadImage = (ImageView) contentView.findViewById(R.id.photo_main_page_download);
		getMoreCommentsBtn = (TextView) contentView.findViewById(R.id.photo_main_page_getMoreComments);
		photoCommentListRootView = (LinearLayout) contentView.findViewById(R.id.photo_main_page_commentList);
		
		//modified by luo
		Drawable photoDrawable = PhotosWall.clickedPhoto;
		photo.setImageDrawable(photoDrawable);
		photoHeight = photoDrawable.getBounds().height();	
		//add by luo
		View photoBlock = contentView.findViewById(R.id.photo_main_page_photo_block); 
		photoBlock.setOnClickListener( photoClickListener);
		
		backBtn.setOnClickListener(backBtnOnClickListener);
		photoDeleteBtn.setOnClickListener(photoDeleteonOnClickListener);
		photoZan.setOnClickListener(photoZanOnClickListener);
		replyImage.setOnClickListener(replyImageOnClickListener);
		getMoreCommentsBtn.setOnClickListener(getMoreCommentsonOnClickListener);
		
//		imageUtil.getInstance().registerHandler(PMPHandler, "PhotoMainPage");
	}
	
	private void getCommentsFromServer(int sequence)
	{
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("photo_id", photo_id);
			params.put("sequence", sequence);
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.GET_PCOMMENTS, params, PMPHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/********************** Listeners(Begin) ****************************/
	OnClickListener backBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener photoDeleteonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(PhotoMainPage.this);
			builder.setMessage("确定要删除这张图片？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					try {
						//暂时还没有这个功能，以后再说，先放着
						params.put("id", userId);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
			
		}
	};
	
	OnClickListener photoZanOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JSONObject params = new JSONObject();
			try {
				params.put("id", userId);
				params.put("photo_id", photo_id);
				if(isZan)
				{
					isZan = false;
					good--;
					params.put("operation", 2);
					photoZanImage.setImageResource(R.drawable.like);
				}
				else
				{
					isZan = true;
					good++;
					params.put("operation", 3);
					photoZanImage.setImageResource(R.drawable.liked);
				}
				photoZanSum.setText("("+ good + ")");
				httpSender.Httppost(OperationCode.ADD_GOOD, params, PMPHandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	};
	
	OnClickListener replyImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final EditText edtext = new EditText(PhotoMainPage.this);
			AlertDialog.Builder builder = new Builder(PhotoMainPage.this);
			builder.setTitle("请输入新的评论： ")
			.setIcon(R.drawable.ic_launcher)
			.setView(edtext)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					String text = edtext.getText().toString();
					if(!"".equals(text.trim().toString()))
					{
						try {
							params.put("id", userId);
							params.put("photo_id", photo_id);
							params.put("content", text);
		//					params.put("sequence", comment_sequence);
							httpSender.Httppost(OperationCode.ADD_PCOMMENT, params, PMPHandler);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						Toast.makeText(PhotoMainPage.this, "新评论不能为空", Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
		}
	};
	
	OnClickListener shareImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener downloadImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener getMoreCommentsonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			JSONObject params = new JSONObject();
			try {
				params.put("id", userId);
				params.put("photo_id", photo_id);
				params.put("sequence", sequence);
				httpSender.Httppost(OperationCode.GET_PCOMMENTS, params, PMPHandler);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/********************** Listeners(End) ****************************/
	
	
	/********************** myHandler(Begin) ********************************/
	public class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case OperationCode.GET_PCOMMENTS:
				if(sequence == 0)
				{
					handle_GET_PCOMMENTS(msg);
					displayMainInfo();
					displayReplies();
				}
				else
				{
					handle_GET_PCOMMENTS(msg);
					displayReplies();
				}
				break;
			case OperationCode.ADD_PCOMMENT:
				handle_ADD_PCOMMENT(msg);
				break;
			case OperationCode.DELETE_PCOMMENT:
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	/********************** myHandler(End) ********************************/
	
	
	
	/**
	 * @author WJL
	 * @description 
	 * 得到的json格式：
	 * cmd(int), pcomment_list[], sequence(int),
	 * isZan(boolean), photo_id(int), author(int), author_id(int),
	 * good(int), time(String)
	 *
	 *pcomment_list的每个元素是一个json:
	 *pcomment_id(int), photo_id(int), author(String), author_id(int), 
	 *content(String), time(String),
	 *good(int)<这里用不到，因为图片评论没有点赞>
	 *
	 * @param msg 服务器返回的Message
	 */
	private void handle_GET_PCOMMENTS(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			int cmd = response.getInt("cmd");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				if(sequence == 0)
				{
					photoComments.clear();
					isZan = response.getBoolean("isZan");
					photo_id = response.getInt("photo_id");
					author = response.getString("author");
					author_id = response.getInt("author_id");
					time = response.getString("time");
					good = response.getInt("good");
					description = response.getString("description");
				}
				sequence = response.getInt("sequence");
				if(sequence != -1)
				{
					JSONArray comments = response.getJSONArray("pcomment_list");
					int len = comments.length();
					for(int i=0;i<len;i++)
					{
						JSONObject comment = comments.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("content", comment.getString("content"));
						map.put("author", comment.getString("author"));
						map.put("author_id", comment.getInt("author_id"));
						map.put("time", comment.getString("time"));
//					 	map.put("good", comment.getInt("good"));
						map.put("pcomment_id", comment.getInt("pcomment_id"));
						map.put("photo_id", comment.getInt("photo_id"));
						photoComments.add(map);
					}
				}
				
				
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(this, "服务器错误", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @author WJL
	 * @description 
	 * 返回的json是刚才发的一样的评论：
	 * cmd(int), photo_id(int), content(String), time(String),
	 * pcomment_id(int), author_id(int), author(String), 
	 * good(int) <这里用不到good,因为图片的评论没有点赞的功能>
	 *
	 * @param msg
	 */
	private void handle_ADD_PCOMMENT(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			int cmd = response.getInt("cmd");
			if( cmd == ReturnCode.NORMAL_REPLY)
			{
				Toast.makeText(this, "添加评论成功", Toast.LENGTH_SHORT).show();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("photo_id", response.getInt("photo_id"));
				map.put("author", response.getString("author"));
				map.put("author_id", response.getInt("author_id"));
				map.put("content", response.getString("content"));
				map.put("time", response.getString("time"));
				map.put("pcomment_id", response.getInt("pcomment_id"));
				photoComments.add(0,map);
				addNewComment(0, map);
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(this, "服务器错误", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayMainInfo()
	{
//		masterAvatar
		Bitmap avatar = imageUtil.getInstance().getAvatar(author_id);
		masterAvatar.setImageBitmap(avatar);
		if(isZan)
		{
			photoZanImage.setImageResource(R.drawable.liked);
		}
		else
		{
			photoZanImage.setImageResource(R.drawable.like);
		}
		//testing
//		Drawable photoDrawable = PhotosWall.clickedPhoto;
//		photo.setImageDrawable(photoDrawable);
//		photoHeight = photoDrawable.getBounds().height();
		
		//add by luo
		//photo.setOnClickListener( photoClickListener);
//		View photoBlock = contentView.findViewById(R.id.photo_main_page_photo_block); 
//		photoBlock.setOnClickListener( photoClickListener);
		
		photoZanSum.setText("("+ good + ")");
		masterName.setText(author);
		masterTime.setText("上传于 " + time);
		photoDescription.setText(description);
		
	}
	
	//add by luo
	private OnClickListener photoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			AnimationSet animationSet = new AnimationSet(true);
//			if( mAnimationSet!=null && mAnimationSet!=animationSet )
//			{
//				ScaleAnimation scaleAnimation = new ScaleAnimation(2, 0.5f, 2, 0.5f,
//						Animation.RELATIVE_TO_PARENT, 0.5f,
//						Animation.RELATIVE_TO_PARENT, 0.5f);
//				scaleAnimation.setDuration(1000);
//				mAnimationSet.addAnimation(scaleAnimation);
//				mAnimationSet.setFillAfter(false);
//				v.startAnimation(mAnimationSet);
//			}
//			ScaleAnimation scaleAnimation = new ScaleAnimation(1, 2f, 1, 2f,
//					Animation.RELATIVE_TO_SELF, 0.5f,
//					Animation.RELATIVE_TO_SELF, 0.5f);
//			scaleAnimation.setDuration(3000);
//			animationSet.addAnimation(scaleAnimation);
//			animationSet.setFillAfter(true);
//			
//			v.startAnimation(animationSet);
//			mAnimationSet = animationSet;
			
			AnimationSet animationSet = new AnimationSet(true);			
			if( scaleOrNot==false )
			{
				ScaleAnimation scaleAnimation = new ScaleAnimation(1, 2f, 1, 2f,
						Animation.RELATIVE_TO_PARENT, 0.5f,
						Animation.RELATIVE_TO_PARENT, 0f);
				scaleAnimation.setDuration(500);
				scaleAnimation.setAnimationListener( animationListener );
				animationSet.addAnimation(scaleAnimation);
				animationSet.setFillAfter(true);			
				v.startAnimation(animationSet);				
			}
			else
			{
				ScaleAnimation scaleAnimation = new ScaleAnimation(2, 1f, 2, 1f,
						Animation.RELATIVE_TO_PARENT, 0.5f,
						Animation.RELATIVE_TO_PARENT, 0f);
				scaleAnimation.setDuration(300);
				scaleAnimation.setAnimationListener( animationListener );
				animationSet.addAnimation(scaleAnimation);
				animationSet.setFillAfter(true);			
				v.startAnimation(animationSet);												
			}
			
		}
	};
	
	private AnimationListener animationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			if( scaleOrNot==false )
			{
				View view = contentView.findViewById(R.id.photo_main_page_photo_block); 
				SetPhotoBlockHeight(view, 2);
			}
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if( scaleOrNot==true )
			{
				View view = contentView.findViewById(R.id.photo_main_page_photo_block); 
				SetPhotoBlockHeight(view, 1);
			}
			
			scaleOrNot = !scaleOrNot;			
		}
	};
	
	private void SetPhotoBlockHeight(View v, double rate)
    {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (photoHeight * rate);
		v.setLayoutParams(params);
	}
	
	private void displayReplies()
	{
		int size = photoComments.size();
		photoCommentListRootView.removeAllViews();
		for(int i=0;i<size;i++)
		{
			HashMap<String, Object> map = photoComments.get(i);
			addNewComment(-1,map);
		}
	}
	
	private void addNewComment(int index, HashMap<String, Object> map)
	{
		View v = LayoutInflater.from(this).inflate(R.layout.photo_reply_item, null);
		ImageView authorAvatar = (ImageView) v.findViewById(R.id.photo_reply_item_authorAvatar);
		TextView authorName = (TextView) v.findViewById(R.id.photo_reply_item_authorName);
		TextView replyTime = (TextView)v.findViewById(R.id.photo_reply_item_time);
		TextView replyContent = (TextView)v.findViewById(R.id.photo_reply_item_replyContent);
		String author = map.get("author").toString();
		int author_id = Integer.parseInt(map.get("author_id").toString());
		String time = map.get("time").toString();
		String content = map.get("content").toString();
		Bitmap avatar = imageUtil.getInstance().getAvatar(author_id);
		authorAvatar.setImageBitmap(avatar);
		authorName.setText(author);
		replyTime.setText(time);
		replyContent.setText(content);
		if( index < 0)
		{
			photoCommentListRootView.addView(v);
		}
		else
		{
			photoCommentListRootView.addView(v, index);
		}
	}
}
