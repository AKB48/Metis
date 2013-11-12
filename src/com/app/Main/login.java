package com.app.Main;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.util.HttpSender;
import com.app.util.OperationCode;
import com.app.util.RegUtils;
import com.app.util.ReturnCode;
import com.example.slidingmenudemo.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class login extends Activity
{
	private MsgHandler handler;
	private HttpSender sender;
	
	private AutoCompleteTextView loginEmailAC;
	private EditText loginPwdET;
	private String loginEmail, loginPwd, responseSalt;
	private CheckBox savePwdCheckBox;
	private SharedPreferences sp;
	
	private int userId;
	private MyBroadcastReceiver broadcastReceiver;
	private Intent serviceIntent = null;
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		 
		Intent intent = getIntent();
		loginPwd = intent.getStringExtra("pwd");
		loginEmail = intent.getStringExtra("email");
		
		handler = new MsgHandler( Looper.myLooper() );
		sender = new HttpSender();

		setLayoutLogin();
	}
	
	private void setLayoutLogin()
	{
		View top = this.findViewById(R.id.topBlock);
			initLayoutH(top, 0.5);
		View topLogin = this.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.47);
		View secondBlock = this.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = this.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.45);	
		View loginBtn = this.findViewById(R.id.loginBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		loginEmailAC = (AutoCompleteTextView)this.findViewById(R.id.loginEmail);
			initLayoutH(loginEmailAC, 0.1);
		if( loginEmail!=null )
			loginEmailAC.setText( loginEmail);
		
		loginPwdET = (EditText)this.findViewById(R.id.loginPwd);
			initLayoutH(loginPwdET, 0.1);
		if( loginPwd!=null )
			loginPwdET.setText( loginPwd);
		
		savePwdCheckBox = (CheckBox)this.findViewById(R.id.loginRemember);	
			initLayoutH(savePwdCheckBox, 0.1);
			
		sp = this.getSharedPreferences("loginInfo", MODE_PRIVATE);
		savePwdCheckBox.setChecked(true);
		loginEmailAC.setThreshold(1);
		loginEmailAC.addTextChangedListener(loginEmailWatcher);
		
		 View toReg =  this.findViewById(R.id.signuphint);
         toReg.setOnClickListener( clickListener );
        
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.signuphint:
				Intent intent2 = new Intent();
					intent2.setClass(login.this, register.class);			
				startActivity(intent2);	
				login.this.finish();
				break;
			case R.id.loginBtn:
				progressDialog = new ProgressDialog(login.this);
				progressDialog.setMessage("登录中...请确保你已经联网");
				progressDialog.setTitle("请稍候");
				progressDialog.show();
				new Thread()
				{
					public void run() {
						tologin();
					}
				}.start();
//				tologin();
				break;
			default:
				break;
			}
		}
	};
	
	private void tologin()
	{
		loginEmail = loginEmailAC.getText().toString();
		loginPwd = loginPwdET.getText().toString();
		
		getLoginSalt();
	}
	
	private void getLoginSalt()
	{
		try{
			JSONObject params = new JSONObject();
			params.put("email", loginEmail);
			params.put("passwd", "");
			params.put("has_salt", false);
			
			sender.Httppost(OperationCode.GET_SALT_VALUE, params, handler);
		}
		catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void loginTry()
	{
		try{
			JSONObject params = new JSONObject();
			params.put("email", loginEmail);
			params.put("passwd", RegUtils.Md5(  loginPwd + responseSalt ));
			params.put("has_salt", true);
			
			sender.Httppost(OperationCode.LOGIN, params, handler);
		}
		catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private TextWatcher loginEmailWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub
			String[] allUserName = new String[sp.getAll().size()];     // 已存在的键值对数目
			allUserName = sp.getAll().keySet().toArray(new String[0]);
			//getAll()返回hash map，由key-value组成
			//keySet()返回 a set of keys
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					login.this, 
					android.R.layout.simple_list_item_1,
					allUserName);

			loginEmailAC.setAdapter(adapter);  //设置数据适配器		
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			loginPwdET.setText(
					sp.getString(loginEmailAC.getText().toString(), "")
					);
		}
	};
	
	private void initLayoutH(View v, double rate)
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (rate * screenH);
		v.setLayoutParams(params);		
	}
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{									 
				case OperationCode.GET_SALT_VALUE:
					
					try {
						String responseGetSalt = null;
						JSONObject responseGetSaltJson;
						int cmdGetSalt;
						
						responseGetSalt = msg.obj.toString();
						if( responseGetSalt != "DEFAULT" )
						{
								responseGetSaltJson = new JSONObject(responseGetSalt);							
								cmdGetSalt = responseGetSaltJson.getInt("cmd");		
								
								if( ReturnCode.GET_SALT==cmdGetSalt )  //返回盐值
								{									
									responseSalt = responseGetSaltJson.getString("salt");	
									Log.i("Login", "盐值: " + responseSalt);
									//如果responseSalt==null，说明用户不存在; 否则，向服务器请求登录
									if( null != responseSalt )
									{
//										loginTry();	
//										progressDialog = new ProgressDialog(login.this);
//										progressDialog.setMessage("登录中...请确保你已经联网");
//										progressDialog.setTitle("请稍候");
//										progressDialog.show();
										new Thread()
										{
											public void run() {
												loginTry();
											}
										}.start();
									}
								}
								else if( ReturnCode.USER_NOT_FOUND==cmdGetSalt)  //用户不存在的话，只是提示，不跳转		
								{
									Toast.makeText(login.this, "用户不存在,请先注册", Toast.LENGTH_SHORT).show();	
									progressDialog.dismiss();
								}
								else if(cmdGetSalt == ReturnCode.SERVER_FAIL)		    //连接失败	
								{
									Toast.makeText(login.this, "连接失败", Toast.LENGTH_SHORT).show();	
									progressDialog.dismiss();
								}
								else if(cmdGetSalt == ReturnCode.USER_ALREADY_ONLINE)
								{
									Toast.makeText(login.this, "已经在其他手机登陆了,不能重复登陆", Toast.LENGTH_SHORT).show();
									progressDialog.dismiss();
								}
								else							                                                    //ELSE
								{
									Toast.makeText(login.this, "你已经穿越...", Toast.LENGTH_SHORT).show();	
									progressDialog.dismiss();
								}
						}
						else		//	网络异常 return DEFAULT
						{
							Toast.makeText(login.this, "网络异常", Toast.LENGTH_SHORT).show();
							progressDialog.dismiss();
						}
					} catch (JSONException e){
						e.printStackTrace();
					}
					break;
					
				case OperationCode.LOGIN:
//					progressDialog.dismiss();
					try {
						String responseLogin;
						JSONObject respLoginJson;
						int cmdLogin;
						
						responseLogin = msg.obj.toString();
						respLoginJson = new JSONObject(responseLogin);
						cmdLogin = respLoginJson.getInt("cmd");
						
						//登录成功
						if( ReturnCode.LOGIN_SUC == cmdLogin)
						{
							//选择了保存密码，且登录成功时保存密码
							if( savePwdCheckBox.isChecked())									
								sp.edit().putString(loginEmail, loginPwd).commit();																			
							
							//获取用户id
							userId = respLoginJson.getInt("id");																	
							sp.edit().putString("id"+loginEmail, ""+userId);								
							
							Log.i("Login", "Login succeed");
							
							//跳转到UserInterface.java
							serviceIntent = new Intent("HeartbeatService");
							serviceIntent.putExtra("uid", userId);
							startService(serviceIntent);
							
							IntentFilter intentFilter = new IntentFilter();
							intentFilter.addAction("connected");
							broadcastReceiver = new MyBroadcastReceiver();
							login.this.registerReceiver( broadcastReceiver, intentFilter);			
//							progressDialog.dismiss();
						}
						else if (ReturnCode.PASSWD_NOT_CORRECT==cmdLogin)								
						{
							Toast.makeText(login.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();					
							progressDialog.dismiss();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}				
					break;
				case OperationCode.LOGOUT:
						Toast.makeText(login.this, "logout", Toast.LENGTH_SHORT).show();	
					break;
					default:
						break;
			}
		}
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			//连接推送服务器成功再跳转
			if ( "connected".equals(action) ) {
				Log.i("test", "跳转啦");
				Intent intent2 = new Intent();
					intent2.setClass(login.this, MainActivity.class);
					intent2.putExtra("userId", userId);
					intent2.putExtra("email", loginEmail);				
				startActivity(intent2);	
				unregisterReceiver( broadcastReceiver);
				progressDialog.dismiss();
				login.this.finish();
			}
		}
		
	}

}
