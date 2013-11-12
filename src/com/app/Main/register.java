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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class register extends Activity
{
	private EditText regEmailET, regUNameET, regPwdET, regPwdAgainET;
	private boolean flag;
	private String regEmail, regName, regPwd;
	private int regGender;
	
	private MsgHandler handler;
	private HttpSender sender;
	private int userId;
	private Intent serviceIntent = null;
	private 		JSONObject reqparams;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		 
		handler = new MsgHandler( Looper.myLooper() );
		sender = new HttpSender();

		setLayoutReg();
	}
	
	private void setLayoutReg()
	{
		View top = this.findViewById(R.id.topBlock);
			initLayoutH(top, 0.3);
		View topLogin = this.findViewById(R.id.topLoginBlock);
			initLayoutH(topLogin, 0.27);
		View secondBlock = this.findViewById(R.id.secondBlock);
			initLayoutH(secondBlock, 0.03);
		
		View buttonBlock = this.findViewById(R.id.InfoBlock);
			initLayoutH(buttonBlock, 0.65);	
		View loginBtn = this.findViewById(R.id.regBtn);
			initLayoutH(loginBtn, 0.08);
			loginBtn.setOnClickListener(clickListener);
			
		regEmailET = (EditText)this.findViewById(R.id.regEmail);
			initLayoutH(regEmailET, 0.1);
		regUNameET = (EditText)this.findViewById(R.id.regUName);
			initLayoutH(regUNameET, 0.1);	
		regPwdET = (EditText)this.findViewById(R.id.regPwd);
			initLayoutH(regPwdET, 0.1);	
		regPwdAgainET = (EditText)this.findViewById(R.id.regPwdAgain);
			initLayoutH(regPwdAgainET, 0.1);
			
		RadioGroup regGender = (RadioGroup)this.findViewById(R.id.reg_genderGroup);		
			initLayoutH(regGender, 0.1);
			
			 View toLogin = this.findViewById(R.id.loginhint);
	         toLogin.setOnClickListener(clickListener);
	}
	
	private void initLayoutH(View v, double rate)
	{
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
		params.height = (int) (rate * screenH);
		v.setLayoutParams(params);		
	}
	
private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.loginhint:
				Intent intent2 = new Intent();
				intent2.setClass(register.this, login.class);			
			startActivity(intent2);	
			register.this.finish();
				break;
			case R.id.regBtn:
				toregister();

				break;
			default:
				break;
			}
		}
	};
	
	private void toregister()
	{
		String saltStr = null;
	
		regEmail = getRegEmail();
		if( regEmail!=null )
		{
			regName = getRegName();
			if( regName!=null )
			{
				regPwd = getRegPwd();
				if( regPwd!=null)
				{
					regGender = getRegGender();
					saltStr = RegUtils.getRandomString(6);
					
					reqparams = new JSONObject();
					try{
						reqparams.put("email", regEmail);
						reqparams.put("name", regName);
						reqparams.put("passwd", RegUtils.Md5(  regPwd + saltStr ));
						reqparams.put("salt", saltStr);
						reqparams.put("gender", regGender);
						
						progressDialog = new ProgressDialog(register.this);
						progressDialog.setMessage("正在注册...请确保你已经联网");
						progressDialog.setTitle("请稍候");
						progressDialog.show();
						new Thread()
						{
							public void run() {
								sender.Httppost(OperationCode.REGISTER, reqparams, handler);
							}
						}.start();
//						sender.Httppost(OperationCode.REGISTER, params, handler);
					}
					catch (JSONException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
				}
			}
		}
	}
	
	public CharSequence getColorHint(String str)
	{
		return Html.fromHtml("<font color=#ff0000>"+str+"</font>");
	}
	
	private String getRegEmail()
	{
		Editable emailText = regEmailET.getText();
		String emailStr = emailText.toString();
		String emailRegex="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		if( emailStr.matches(emailRegex))			
			return emailStr;			
		else 
		{
			flag = false;
			regEmailET.setError(getColorHint("请输入正确的邮箱地址"));
			return null;
		}
	}
	
	private String getRegName()
	{
		Editable userNameText = regUNameET.getText();
		String userNameStr = userNameText.toString();
		if( userNameStr.length()>=2 && userNameStr.length()<=10)			
			return userNameStr;
		
		else 
		{
			flag = false;			
			regUNameET.setError(getColorHint("用户名长度为2-10哦"));	
			return null;
		}
	}
	
	private String getRegPwd()
	{
		boolean pwdFlag = true;
		Editable pwdText = regPwdET.getText();
		String pwdStr = pwdText.toString();
		if( pwdStr.length()<5 || pwdStr.length()>15)
		{
			pwdFlag = false;
			flag = false;
			regPwdET.setError(getColorHint("密码长度为5-15哦"));
		}
		
		Editable confirmPwdText = regPwdAgainET.getText();
		String confirmPwdStr = confirmPwdText.toString();
		if( confirmPwdStr.equals(pwdStr))
		{							
			if(pwdFlag)				
				return pwdStr;				
			else
				return null;
		}
		else
		{
			flag = false;
			regPwdAgainET.setError(getColorHint("密码不一致哦"));	
			return null;
		}
	}
	
	private int getRegGender()
	{
		RadioButton manRadio = (RadioButton)this.findViewById(R.id.reg_genderMan);

		if( manRadio.isChecked())
			return 1;
		else 
			return 0;
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
				case OperationCode.REGISTER:
					progressDialog.dismiss();
					 String response = msg.obj.toString();
					 try{
							if (response != "DEFAULT")
							{
								 JSONObject json = new JSONObject(response);
									int cmd = json.getInt("cmd");
									if (ReturnCode.NORMAL_REPLY == cmd)
									{
										//跳转到登录页面
										Log.i("Register", "注册成功");
										
										Intent intent = new Intent();
											intent.setClass(register.this, login.class);
											intent.putExtra("pwd", regPwd);
											intent.putExtra("email", regEmail);
										startActivity(intent);	
										finish();
										Toast.makeText(register.this, "注册成功", Toast.LENGTH_SHORT).show();	
									}
									else if (ReturnCode.USER_EXIST == cmd)								
										Toast.makeText(register.this, "用户已存在", Toast.LENGTH_SHORT).show();	
									else 
										Toast.makeText(register.this, "穿越了", Toast.LENGTH_SHORT).show();	
							}
							else
								Toast.makeText(register.this, "网络异常", Toast.LENGTH_SHORT).show();
						 } 
						 catch (JSONException e)
						{
						e.printStackTrace();
						}	
					 break;
					 
					default:
						break;
			}
		}
	}

}
