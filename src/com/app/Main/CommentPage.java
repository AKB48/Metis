package com.app.Main;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.classes.FriendCenter;
import com.app.util.AdapterForComment;
import com.app.util.CommentStruct;
import com.app.util.HttpSender;
import com.app.util.Messager;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentPage extends Activity{
	
	private final String TAG = "COMMENT PAGE";
	private int userId;
	private int eventId;
	private int comment_master = 0;
	private int comment_sequence = 0;
	private int reply_sequence = 0;
	
	private View contentView;
	private Button addCommentBtn;
	private Button backBtn;
	private ListView commentListView;
	
	private View footerView;
	private TextView footerTextView;
	
	private AdapterForComment commentAdapter;
	private ArrayList<CommentStruct> commentList = new ArrayList<CommentStruct>();
	
	private myHandler cpHandler = new myHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment_page, null);
		setContentView(contentView);
		init();
		super.onCreate(savedInstanceState);
	}

	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		eventId = intent.getIntExtra("eventId", -1);
		addCommentBtn = (Button)contentView.findViewById(R.id.comment_page_addcommentBtn);
		addCommentBtn.setOnClickListener(buttonsOnClickListener);
		backBtn = (Button)contentView.findViewById(R.id.comment_page_backBtn);
		commentListView = (ListView)contentView.findViewById(R.id.comment_page_listview);
		footerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pulldown_footer, null);
		footerTextView = (TextView) footerView.findViewById(R.id.pulldown_footer_text);
		footerTextView.setText("点击查看更多");
//		footerView.setOnClickListener(buttonsOnClickListener);
		footerTextView.setOnClickListener(buttonsOnClickListener);
		imageUtil.getInstance().registerHandler(cpHandler, "CommentPage");
		
		
		commentAdapter = new AdapterForComment(this, commentList, 
				comment_sequence, reply_sequence,
				userId, eventId,
				cpHandler);
		commentListView.addFooterView(footerView);
		Resources res = this.getResources();
		commentListView.setDivider(res.getDrawable(R.drawable.co_sep));
		commentListView.setAdapter(commentAdapter);
		
		onGetComments();
	}
	
	private OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(TAG, "click has been detected, view id is: "+v.getId());
			switch(v.getId())
			{
			case R.id.comment_page_addcommentBtn:
				addNewComment();
				break;
			case R.id.comment_page_backBtn:
				break;
			case R.id.pulldown_footer_text:
				Log.i(TAG, "get more comments");
				onGetComments();
				break;
				default: break;
			}
		}
	};

	protected void addNewComment() {
		// TODO Auto-generated method stub
		final EditText edtext = new EditText(this);
		AlertDialog.Builder builder = new Builder(this);
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
						// Something may worng
						params.put("id", userId);
						params.put("event_id", eventId);
						params.put("master", 0);
						params.put("content", text);
	//					params.put("sequence", comment_sequence);
						HttpSender httpSender = new HttpSender();
						httpSender.Httppost(OperationCode.ADD_COMMENT, params, cpHandler);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					Toast.makeText(CommentPage.this, "新评论不能为空", Toast.LENGTH_SHORT).show();
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
	
	public void onGetComments()
	{
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("event_id", eventId);
			params.put("master", 0);
			params.put("sequence", comment_sequence);
			HttpSender httpSender = new HttpSender();
			httpSender.Httppost(OperationCode.GET_COMMENTS, params, cpHandler);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getCommentsDone(Message msg)
	{
		try {
			
			JSONObject response = new JSONObject(msg.obj.toString());
			Log.i(TAG, "response: "+response.toString());
			int cmd = response.getInt("cmd");
//			int type = response.getInt("type");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				if(comment_sequence == 0)
				{
					commentList.clear();
				}
				comment_sequence = response.getInt("sequence");
				JSONArray comment_list = response.getJSONArray("comment_list");
				Log.i(TAG, "comment_list: "+comment_list.toString());
				int len1 = comment_list.length();
				for(int i=0;i<len1;i++)
				{
					CommentStruct a_comment = new CommentStruct();
					JSONArray comment = comment_list.getJSONArray(i);
					Log.i(TAG, "comment and reply: "+comment.toString());
					int len2 = comment.length();
					
					for(int j=0;j<len2;j++)
					{
						JSONObject object = comment.getJSONObject(j);
//						object.put("isZan", 0);//测试用
						HashMap<String, Object> reply = new HashMap<String, Object>();
						reply.put("comment_id", object.getInt("comment_id"));
						reply.put("content", object.getString("content"));
						reply.put("author_id", object.getInt("author_id"));
						reply.put("author", object.getString("author"));
						reply.put("time", object.getString("time"));
						reply.put("good", object.getInt("good"));
						reply.put("master", object.getInt("master"));
						reply.put("isZan", object.getInt("isZan")); //判断用户是否点赞
						
						if(j == 0)
						{
							reply.put("comment_num", object.getInt("comment_num"));
							a_comment.comment = reply;
						}
						else
						{
							a_comment.replies.add(reply);
						}
					}
					int size = a_comment.replies.size();
					if(size > 0)
					{
						a_comment.reply_sequence = Integer.parseInt(a_comment.replies.get(size-1).get("comment_id").toString());
					}
					else
					{
						a_comment.reply_sequence = 0;
					}
					Log.i(TAG, "a comment: "+ a_comment.toString());
					commentList.add(a_comment);
					
				}
				commentAdapter.comment_sequence = comment_sequence;
				commentAdapter.notifyDataSetChanged();
				Log.i(TAG,"adapter commentList: "+ commentAdapter.commentList.toString());

			}
			else
			{
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void getMoreRepliesDone(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			CommentStruct commentStruct = new CommentStruct();
			int cmd = response.getInt("cmd");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				int type = response.getInt("type");
				int reply_sequence = response.getInt("sequence");
//				int size = commentList.size();
//				for(int i=0;i<size;i++)
//				{
//					String comment_id = commentList.get(i).comment.get("comment_id").toString();
//					if( comment_id == type+"")
//					{
//						commentStruct = commentList.get(i);
//						flag = true;
//					}
//					
//				}
				int index;
				index = findComment(type);
				
				if(index != -1)
				{
					commentStruct = commentList.get(index);
					commentStruct.reply_sequence = reply_sequence;
					ArrayList<HashMap<String, Object>> replies = commentStruct.replies;
					JSONArray reply_list = response.getJSONArray("comment_list");
					int length = reply_list.length();
					for(int i=0;i<length;i++)
					{
						JSONObject a_reply = reply_list.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("comment_id", a_reply.getInt("comment_id"));
						map.put("content", a_reply.getString("content"));
						map.put("author_id", a_reply.getInt("author_id"));
						map.put("author", a_reply.getString("author"));
						map.put("time", a_reply.getString("time"));
						map.put("isZan", a_reply.getInt("isZan"));
						map.put("master", a_reply.getInt("master"));
						map.put("good", a_reply.getInt("good"));
						replies.add(map);
					}
					
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addCommentOrReplyDone(Message msg)
	{
		try {
			JSONObject response = new JSONObject(msg.obj.toString());
			int cmd = response.getInt("cmd");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				int master = response.getInt("master");
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("comment_id", response.getInt("comment_id"));
				map.put("content", response.getString("content"));
				map.put("author_id", response.getInt("author_id"));
				map.put("author", response.getString("author"));
				map.put("time", response.getString("time"));
				map.put("good", response.getInt("good"));
				map.put("isZan", response.getInt("isZan"));
				map.put("master", master);
				if(master == 0)
				{
					map.put("comment_num", response.getInt("comment_num"));
					Log.i(TAG, "添加comment: "+ map.toString());
					CommentStruct commentStruct = new CommentStruct();
					commentStruct.comment = map;
					commentList.add(0,commentStruct);
				}
				else
				{
					CommentStruct commentStruct = new CommentStruct();
					int index = findComment(master);
					if(index != -1)
					{
						commentStruct = commentList.get(index);
						int comment_num = Integer.parseInt(commentStruct.comment.get("comment_num").toString());
						commentStruct.comment.put("comment_num", comment_num+1);
						ArrayList<HashMap<String, Object>> replies = commentStruct.replies;
						commentStruct.reply_sequence = master;
						replies.add(0,map);
						Log.i(TAG,"加上了reply之后的commentAdapter.commentList对应comment的replies: "+commentAdapter.commentList.get(0).replies.toString());
					}
					else
					{
						Log.i(TAG, "没有找到comment id为"+master+"的CommentStruct");
					}
				}
				commentAdapter.notifyDataSetChanged();
			}
			else
			{
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @author WJL
	 * @description 
	 *
	 * @param comment_id
	 * @param commentStruct Get the CommentStruct whose comment id is the same as "comment_id"
	 * @return
	 */
	private int findComment(int comment_id)
	{
		Log.i(TAG, "查找的comment_id: "+comment_id);
		int size = commentList.size();
		for(int i=0;i<size;i++)
		{
			CommentStruct temp = commentList.get(i);
			int temp_id = Integer.parseInt(temp.comment.get("comment_id").toString());
			Log.i(TAG, "commentList位置"+i+"对应的comment_id为"+temp_id);
			if(temp_id == comment_id)
			{
				Log.i(TAG, "找到指定comment_id在链表中的位置index: "+ i);
				return i;
			}
		}
		return -1;
	}
	
	
	/**
     * @author CL
     * @description 
     * Unregister avatar display util
     * 
     * @return
     */
	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        imageUtil.getInstance().unregisterHandler("CommentPage");
        super.onBackPressed();
    }


	private class myHandler extends Handler
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
			case OperationCode.ADD_COMMENT:
				Log.i(TAG, "添加comment或reply得到的json: "+msg.obj.toString());
				addCommentOrReplyDone(msg);
				break;
			case OperationCode.GET_COMMENTS:
				try {
					JSONObject response = new JSONObject(msg.obj.toString());
					int type = response.getInt("type");
					int sequence = response.getInt("sequence");
					if( sequence >= 0)
					{
						if(type == 0)
						{
							Log.i(TAG, "获取的comments： "+ msg.obj.toString());
							getCommentsDone(msg);
						}
						else
						{
							Log.i(TAG, "获取的replies： "+ msg.obj.toString());
							getMoreRepliesDone(msg);
						}
						commentAdapter.notifyDataSetChanged();
					}
					else
					{
						Toast.makeText(CommentPage.this, "没有更多评论了", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case OperationCode.ADD_GOOD:
				break;
			case FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR:
			    commentAdapter.notifyDataSetChanged();
			    break;
				default: break;
			}
		}
	}
	
}
