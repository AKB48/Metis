package com.app.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.AlertDialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterForComment extends BaseAdapter{

	/**
	 * @author WJL
	 * @description 
	 * 解析commentList每一个CommentStruct元素：
	 * comment:
	 * comment_id(int), content(String), author_id(int), author(String),
	 * time(String), good(int), isZan(int), master(int), comment_num(int)
	 * 
	 * replies中的每一个reply:
	 * comment_id(int), content(String), author_id(int), author(String),
	 * time(String), good(int), isZan(int), master(int)
	 *
	 */
	private final String TAG = "AdapterForComment";
	public ArrayList<CommentStruct> commentList = new ArrayList<CommentStruct>();
	private Context context;
	public int comment_sequence;
//	public int comment_master;
	public int reply_sequence;
	
	private int userId;
	private int eventId;
	
	private Handler cpHandler;
	
	public AdapterForComment(Context context, ArrayList<CommentStruct> commentList, 
			int comment_sequence, int reply_sequence,
			int userId, int eventId,
			Handler cpHander) {
		// TODO Auto-generated constructor stub
		this.commentList = commentList;
		this.context = context;
		this.comment_sequence = comment_sequence;
		this.reply_sequence = reply_sequence;
		this.userId = userId;
		this.eventId = eventId;
		this.cpHandler = cpHander;
//		Log.i(TAG, "adapter commentList: "+commentList.toString());
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		Log.i(TAG, "commentList count: "+ commentList.size());
		return commentList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
//		Log.i(TAG,"第"+arg0+"个评论");
		CommentViewHolder commentViewHolder;
		CommentStruct comment = commentList.get(arg0);
		Resources resources = context.getResources();
		if(arg1 == null)
		{
			arg1 = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
			commentViewHolder = new CommentViewHolder();
			commentViewHolder.comment_authorAvatar = (ImageView)arg1.findViewById(R.id.comment_item_authorAvatar);
			commentViewHolder.comment_authorName = (TextView)arg1.findViewById(R.id.comment_item_authorName);
			commentViewHolder.comment_time = (TextView)arg1.findViewById(R.id.comment_item_time);
			commentViewHolder.comment_zan = (LinearLayout)arg1.findViewById(R.id.comment_item_zan);
			commentViewHolder.comment_zanSum = (TextView)arg1.findViewById(R.id.comment_item_zanSum);
			commentViewHolder.comment_zanImage = (ImageView)arg1.findViewById(R.id.comment_item_zanImage);
			commentViewHolder.comment_reply = (LinearLayout)arg1.findViewById(R.id.comment_item_reply);
			commentViewHolder.comment_replySum = (TextView)arg1.findViewById(R.id.comment_item_replySum);
			commentViewHolder.comment_replyImage = (ImageView)arg1.findViewById(R.id.comment_item_replyImage);
			commentViewHolder.comment_replyList = (LinearLayout)arg1.findViewById(R.id.comment_item_commentlist);
			commentViewHolder.comment_content = (TextView)arg1.findViewById(R.id.comment_item_commentContent);
			commentViewHolder.comment_moreReplies = (TextView)arg1.findViewById(R.id.comment_item_moreReply);
			
			commentViewHolder.comment_zan.setOnClickListener(myOnClickListener);
			commentViewHolder.comment_reply.setOnClickListener(myOnClickListener);
			commentViewHolder.comment_moreReplies.setOnClickListener(myOnClickListener);
			
			commentViewHolder.replyViews = new ArrayList<AdapterForComment.ReplyViewHolder>();
			
			arg1.setTag(commentViewHolder);
		}
		else
		{
			commentViewHolder = (CommentViewHolder) arg1.getTag();
//			Log.i(TAG,"1. view count: "+commentViewHolder.comment_replyList.getChildCount());
		}
		
		int count = commentViewHolder.comment_replyList.getChildCount();
		if(count > 1)
		{
			commentViewHolder.comment_replyList.removeViews(1, count-1);
		}
		Bitmap avatar_bmp = imageUtil.getInstance().getAvatar(Integer.parseInt(comment.comment.get("author_id").toString()));
        if (null != avatar_bmp)
        {
            commentViewHolder.comment_authorAvatar.setImageBitmap(avatar_bmp);
        }
        else {
            commentViewHolder.comment_authorAvatar.setImageDrawable(resources.getDrawable(R.drawable.defaultavatar));
        }
		commentViewHolder.comment_authorName.setText(comment.comment.get("author").toString());
		commentViewHolder.comment_time.setText(comment.comment.get("time").toString());
		commentViewHolder.comment_replyImage.setImageDrawable(resources.getDrawable(R.drawable.re));
		commentViewHolder.comment_replySum.setText("("+comment.comment.get("comment_num").toString()+")");
		
		int comment_isZan = Integer.parseInt(comment.comment.get("isZan").toString());
		if( comment_isZan == 0)
		{
			commentViewHolder.comment_zanImage.setImageDrawable(resources.getDrawable(R.drawable.like));
		}
		else
		{
			commentViewHolder.comment_zanImage.setImageDrawable(resources.getDrawable(R.drawable.liked));
		}
		commentViewHolder.comment_zanSum.setText("("+comment.comment.get("good").toString()+")");
		commentViewHolder.comment_content.setText(comment.comment.get("content").toString());
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("index_comment", arg0);
		map.put("reply_sequence",comment.reply_sequence);
		commentViewHolder.comment_moreReplies.setTag(map);
		
		commentViewHolder.comment_zan.setTag(arg0);
		commentViewHolder.comment_reply.setTag(arg0);
		
		int len = comment.replies.size();
		
		for(int i=0;i<len;i++)
		{
			ReplyViewHolder replyViewHolder = new ReplyViewHolder();
			HashMap<String, Object> reply = comment.replies.get(i);
			replyViewHolder.replyItem = LayoutInflater.from(context).inflate(R.layout.reply_item, null);
			replyViewHolder.reply_zan = (LinearLayout)replyViewHolder.replyItem.findViewById(R.id.reply_item_zan);
			replyViewHolder.reply_content = (TextView)replyViewHolder.replyItem.findViewById(R.id.reply_item_replyContent);
			replyViewHolder.reply_zanImage = (ImageView)replyViewHolder.replyItem.findViewById(R.id.reply_item_zanImage);
			replyViewHolder.reply_zanSum = (TextView)replyViewHolder.replyItem.findViewById(R.id.reply_item_zanSum);
			
			String reply_author = reply.get("author").toString();
			String reply_content = reply.get("content").toString();
			int isZan = Integer.parseInt(reply.get("isZan").toString());
			replyViewHolder.reply_content.setText(reply_author+": "+reply_content);
			
			if(isZan == 0)
			{
				replyViewHolder.reply_zanImage.setImageDrawable(resources.getDrawable(R.drawable.like));
			}
			else
			{
				replyViewHolder.reply_zanImage.setImageDrawable(resources.getDrawable(R.drawable.liked));
			}
			replyViewHolder.reply_zanSum.setText("("+reply.get("good").toString()+")");
			
			HashMap<String, Integer> tag = new HashMap<String, Integer>();
			tag.put("index_comment", arg0);
			tag.put("index_reply", i);
			replyViewHolder.reply_zan.setTag(tag);
			replyViewHolder.reply_zan.setOnClickListener(myOnClickListener);
			
//			RelativeLayout reply_relativeLayout = (RelativeLayout) replyViewHolder.replyItem.findViewById(R.id.reply_item_relativeLayout);
//			LinearLayout.LayoutParams params =  (LayoutParams)reply_relativeLayout.getLayoutParams();
//			params.setMargins(20, 1, 0, 1);
//			reply_relativeLayout.setLayoutParams(params);
			
			commentViewHolder.comment_replyList.addView(replyViewHolder.replyItem);
		}
		return arg1;
	}
	
	View.OnClickListener myOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.comment_item_zan:
				clickCommentZan(v);
				break;
			case R.id.comment_item_reply:
				clickAddCommentReply(v);
				break;
			case R.id.comment_item_moreReply:
				clickMoreReplies(v);
				break;
			case R.id.reply_item_zan:
				clickReplyZan(v);
				break;
				default: break;
			}
		}
	};
	
	
	private class CommentViewHolder
	{
		// comment_item里面的内容
		public ImageView comment_authorAvatar;
		public TextView comment_authorName;
		public TextView comment_time;
		public LinearLayout comment_zan;
		public TextView comment_zanSum;
		public ImageView comment_zanImage;
		public LinearLayout comment_reply;
		public TextView comment_replySum;
		public ImageView comment_replyImage;
		public LinearLayout comment_replyList;
		public TextView comment_content;
		public TextView comment_moreReplies;
		
		public ArrayList<ReplyViewHolder> replyViews;
		
		
	}
	
	private class ReplyViewHolder
	{
		// reply_item里面的内容
		public View replyItem;
		public TextView reply_content;
		public LinearLayout reply_zan;
		public ImageView reply_zanImage;
		public TextView reply_zanSum;
	}

	
	
	protected void clickCommentZan(View v) {
		// TODO Auto-generated method stub
		ImageView zanImage = (ImageView)v.findViewById(R.id.comment_item_zanImage);
		TextView zanSum = (TextView)v.findViewById(R.id.comment_item_zanSum);
		int index_comment = (Integer) v.getTag();
		HashMap<String, Object> comment = commentList.get(index_comment).comment;
		int old_commentZan = Integer.parseInt(comment.get("good").toString());
		int isZan = Integer.parseInt(comment.get("isZan").toString());
		if(isZan == 0)
		{
			zanImage.setImageResource(R.drawable.liked);
			zanSum.setText("("+ (old_commentZan+1)+")");
			comment.put("isZan", 1);
			comment.put("good", old_commentZan+1);
		}
		else
		{
			zanImage.setImageResource(R.drawable.like);
			zanSum.setText("("+ (old_commentZan-1)+")");
			comment.put("isZan", 0);
			comment.put("good", old_commentZan-1);
		}
		
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("event_id",eventId);
			params.put("comment_id", Integer.parseInt(comment.get("comment_id").toString()));
			params.put("operation", Integer.parseInt(comment.get("isZan").toString()));
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.ADD_GOOD, params, cpHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void clickAddCommentReply(View v) {
		// TODO Auto-generated method stub
		int index_comment = (Integer) v.getTag();
		Log.i(TAG, "从tag中得到comment在commentList中的index: "+ index_comment);
		final HashMap<String, Object> comment = commentList.get(index_comment).comment;
		final EditText edtext = new EditText(context);
//		String author_name = comment.get("author").toString();
//		edtext.setText("回复@" + author_name + ": ");
//		edtext.setSelection(edtext.getText().length());
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("请输入新的回复： ")
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
						// Something may worng
						params.put("id", userId);
						params.put("event_id", eventId);
						params.put("master", Integer.parseInt(comment.get("comment_id").toString()));
						params.put("content", text);
						HttpSender httpSender = new HttpSender();
						httpSender.Httppost(OperationCode.ADD_COMMENT, params, cpHandler);
						Log.i(TAG, "发送的comment_id为："+Integer.parseInt(comment.get("comment_id").toString()));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					Toast.makeText(context, "新的回复不能为空", Toast.LENGTH_SHORT).show();
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
	
	protected void clickMoreReplies(View v) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = (HashMap<String, Integer>) v.getTag();
		int index_comment = Integer.parseInt(map.get("index_comment").toString());
		int reply_sequence = Integer.parseInt(map.get("reply_sequence").toString());
		HashMap<String, Object> comment = commentList.get(index_comment).comment;
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("event_id", eventId);
			params.put("master", Integer.parseInt(comment.get("comment_id").toString()));
			params.put("sequence", reply_sequence);
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.GET_COMMENTS, params, cpHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@SuppressWarnings("unchecked")
	protected void clickReplyZan(View v) {
		// TODO Auto-generated method stub
		
		ImageView zanImage = (ImageView)v.findViewById(R.id.reply_item_zanImage);
		TextView zanSum = (TextView)v.findViewById(R.id.reply_item_zanSum);
		HashMap<String, Integer> tag;
		tag = (HashMap<String, Integer>) v.getTag();
		int index_comment = tag.get("index_comment");
		int index_reply = tag.get("index_reply");
//		HashMap<String, Object> comment = commentList.get(index_comment).comment;
		HashMap<String, Object> reply = commentList.get(index_comment).replies.get(index_reply);
		int old_zanSum = Integer.parseInt(reply.get("good").toString());
		int isZan = Integer.parseInt(reply.get("isZan").toString());
		Log.i(TAG,"isZan: "+ isZan);
		if(isZan == 0)
		{
			zanImage.setImageResource(R.drawable.liked);
			zanSum.setText("("+(old_zanSum+1)+")");
			reply.put("isZan", 1);
			reply.put("good", old_zanSum+1);
		}
		else 
		{
			zanImage.setImageResource(R.drawable.like);
			zanSum.setText("("+(old_zanSum-1)+")");
			reply.put("isZan", 0);
			reply.put("good", old_zanSum-1);
		}
		
		JSONObject params = new JSONObject();
		try {
			Log.i(TAG, "发送的comment_id: "+ Integer.parseInt(reply.get("comment_id").toString()));
			params.put("id", userId);
			params.put("event_id",eventId);
			params.put("comment_id", Integer.parseInt(reply.get("comment_id").toString()));
			params.put("operation", Integer.parseInt(reply.get("isZan").toString()));
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.ADD_GOOD, params, cpHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
