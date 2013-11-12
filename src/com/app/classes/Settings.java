package com.app.classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;


import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.app.Main.ChangPW;
import com.app.Main.MainActivity;
import com.app.customwidget.OnWheelChangedListener;
import com.app.customwidget.WheelView;
import com.app.util.Area;
import com.app.util.AreaXMLParser;
import com.app.util.AvatarDialog;
import com.app.util.City;
import com.app.util.HttpSender;
import com.app.util.ListWheelAdapter;
import com.app.util.OperationCode;
import com.app.util.ReturnCode;
import com.app.util.State;
import com.app.util.imageUtil;
import com.example.slidingmenudemo.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings {
    private Activity activity;
    private View settingsView;
    private final String TAG = "imageupload";
    private String path;
    private Uri uri;
    private String imageStr;
    private ImageView avatar, gender;
    private EditText location, description, myName;
    private TextView myEmail, changPW;
    private AvatarDialog avatarDialog;
    private int userId, genderInt;
    private MessageHandler handler;
    private boolean isFirstVisit, areaListPrepare;
    private ArrayList<State> stateList;
    public final int CASE_PHOTO = 0;
    public final int CASE_CAMERA = 1;
    public final int CASE_CHANGE_PW = 2;
    public final int MSG_WHAT_ON_AREA_LIST_PREPARED = -2;
    
    public Settings(Activity activity, int userId) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.settingsView = LayoutInflater.from(activity).inflate(R.layout.menu_settings, null);
        this.userId = userId;
        this.genderInt = -1;
        isFirstVisit = true;
        areaListPrepare = false;
        handler = new MessageHandler(Looper.myLooper());
        this.stateList = null;
        init();
    }
    
    public View getView()
    {
    	return this.settingsView;
    }
    
    public void setLayout()
    {
        avatar = (ImageView)settingsView.findViewById(R.id.avatar);
        avatar.setOnClickListener(avatarListener);
        gender = (ImageView)settingsView.findViewById(R.id.settings_gender);
        gender.setOnClickListener(genderChangeOnClickListener);
        location = (EditText)settingsView.findViewById(R.id.settings_location);
        location.setFocusable(false);
        location.setFocusableInTouchMode(false);
        location.setLongClickable(false);
        location.setOnClickListener(locationOnClickListener);
        location.setHint(R.string.location_hint);
        location.setHintTextColor(Color.parseColor("#ffaaaaaa"));
        description = (EditText)settingsView.findViewById(R.id.settings_description);
        description.setFocusable(false);
        description.setFocusableInTouchMode(false);
        description.setLongClickable(false);
        description.setHint(R.string.description_hint);
        description.setHintTextColor(Color.parseColor("#ffaaaaaa"));
        description.setOnClickListener(editTextOnClickListener);
        myName = (EditText)settingsView.findViewById(R.id.settings_name);
        myName.setFocusable(false);
        myName.setFocusableInTouchMode(false);
        myName.setLongClickable(false);
        myName.setHint(R.string.myName_hint);
        myName.setHintTextColor(Color.parseColor("#ffaaaaaa"));
        myName.setOnClickListener(editTextOnClickListener);
        myEmail = (TextView)settingsView.findViewById(R.id.settings_email);
        changPW = (TextView)settingsView.findViewById(R.id.modift_pw);
        changPW.setOnClickListener(pwChangeOnClickListener);

    }

    private void init() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setLayout();
                
            }        
        }).start();
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (stateList == null)
                {
                    try {
                        stateList = AreaXMLParser.doParse(activity);
                    } catch (XmlPullParserException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("In Settings", "XmlPullParserException");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("In Settings", "IOException");
                    }
                    Message msg = handler.obtainMessage(MSG_WHAT_ON_AREA_LIST_PREPARED);
                    msg.sendToTarget();
                }
            }        
        }).start();
    }
    
    public void initData() {
        if (!isFirstVisit)
        {
            return;
        }
        JSONObject params = new JSONObject();
        try {
            params.put("id", userId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new HttpSender().Httppost(OperationCode.GET_USER_INFO, params, handler);
        if (imageUtil.fileExist(userId))
        {
            Bitmap bitmap = imageUtil.getLocalBitmapBy(userId);
//            int scale = UserInterface.dip2px(activity, 70);
//            Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
            avatar.setImageBitmap(bitmap);
        }
        else
        {
            try {
                params.put("operation", 0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
        }
        isFirstVisit = false;
    }
    
    
    public void onAvatarsetFromPhoto(Intent data)
    {
        uri = data.getData();          
        Log.i(TAG, "uri: " + uri.toString());            
        getPath(uri);
        imageStr = getImageStr(path);       
        uploadImage();
    }
    
    public void onAvatarsetFromCamera(Intent data)
    {
        Bundle extras = data.getExtras();
        Bitmap camera_bitmap = (Bitmap) extras.get("data");
        String saveFilePath = "";
      
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            File filePath = new File(sdCardDir.getAbsolutePath() + "/Catherine" );
            if(!filePath.exists())
                filePath.mkdirs();
          
            saveFilePath = filePath + "/" + filename() + ".png";
            FileOutputStream baos= new FileOutputStream(saveFilePath);
            camera_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();
            baos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        imageStr = getImageStr(saveFilePath);
        uploadImage();
    }
    
    public String filename() {
        Random random = new Random(System.currentTimeMillis());
        java.util.Date dt = new java.util.Date(System.currentTimeMillis()); 
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String fileName = fmt.format(dt)+ Math.abs(random.nextInt()) % 100000;      
        return fileName;
    }      
    
    public void getPath( Uri uri)
    {
        String []proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);                  Log.i(TAG, "path: " + path);
    }
    
    
    OnClickListener uploadByPhotosListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            avatarDialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, CASE_PHOTO); 
        }
    };
    
    OnClickListener uploadByCameraListener = new OnClickListener() {
        
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            avatarDialog.dismiss();
            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intentCam, CASE_CAMERA);
        }
    };
    
    OnClickListener avatarListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View layout = inflater.inflate(R.layout.show_avatar,
//                    (ViewGroup)activity. findViewById(R.id.show_avatar));
//            new AlertDialog.Builder(activity).setTitle("Avatar").setView(layout)
//            .setPositiveButton(R.string.ack, null).show();
//            ImageView big_avatar = (ImageView)layout.findViewById(R.id.big_avatar);
//            avatar.setDrawingCacheEnabled(true);
//            Bitmap bigAvatar = imageUtil.scaleBitmap(Bitmap.createBitmap(avatar.getDrawingCache()), 300, 300);
//            big_avatar.setImageBitmap(bigAvatar);
//            //big_avatar.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_launcher));
//            avatar.setDrawingCacheEnabled(false);
            
            //_______________________________________________________
            avatarDialog = new AvatarDialog(activity, R.style.avatar_dialog);//创建Dialog并设置样式主题
            // Window win = avatarDialog.getWindow();
             //LayoutParams params = new LayoutParams();
             //params.x = 0;//设置x坐标
             //params.y = 0;//设置y坐标
             //win.setAttributes(params);
             avatarDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog         
             avatarDialog.show();
             avatarDialog.setAlbumButtonListener(uploadByPhotosListener);
             avatarDialog.setCameraButtonListener(uploadByCameraListener); 
        }
    };
    
    
    OnClickListener locationOnClickListener = new OnClickListener() {
        String oldString;
        WheelView stateItem;
        WheelView cityItem;
        WheelView areaItem;
        
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
            if (!areaListPrepare)
            {
                return ;
            }
            oldString = location.getText().toString();
            LayoutInflater inflater = activity.getLayoutInflater();
            View layout = inflater.inflate(R.layout.area_layout,
                    (ViewGroup)activity. findViewById(R.id.area_layout));
            new AlertDialog.Builder(activity).setView(layout)
            .setPositiveButton(R.string.ack, ackOnClickListener).setNegativeButton(R.string.cancel, null).show();
            stateItem = (WheelView)layout.findViewById(R.id.state);
            cityItem = (WheelView)layout.findViewById(R.id.city);
            areaItem = (WheelView)layout.findViewById(R.id.area);
//            if (stateList == null)
//            {
//                try {
//                    stateList = AreaXMLParser.doParse(activity);
//                } catch (XmlPullParserException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Log.i("In Settings", "XmlPullParserException");
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Log.i("In Settings", "IOException");
//                }
//            }
            Log.i("In settings: ", "In settings on button click////");
            stateItem.setVisibleItems(5);
            stateItem.setCyclic(true);
            stateItem.setInterpolator(new AnticipateOvershootInterpolator());
            stateItem.setAdapter(new ListWheelAdapter<State>(stateList));
            cityItem.setVisibleItems(5);
            cityItem.setCyclic(true);
            cityItem.setInterpolator(new AnticipateOvershootInterpolator());
            areaItem.setVisibleItems(5);
            areaItem.setCyclic(true);
            areaItem.setInterpolator(new AnticipateOvershootInterpolator());
            stateItem.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    ArrayList<City> tmpList = stateList.get(newValue).getAreaList();
                    if (tmpList.size() > 0) {
                        cityItem.setAdapter(new ListWheelAdapter<City>(tmpList));
                        cityItem.setCurrentItem(0, true);
    
    
                        ArrayList<Area> tmpAreas  = tmpList.get(0).getAreaList();
                        if (tmpAreas.size() > 0){
                            areaItem.setAdapter(new ListWheelAdapter<Area>(tmpAreas));
                            areaItem.setCurrentItem(0, true);
                        }else {
                            areaItem.setAdapter(null);
                        }
                    }
                    else {
                        cityItem.setAdapter(null);
                    }
                }
            });
            cityItem.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    int stateIndex = stateItem.getCurrentItem();
                    ArrayList<City> tmpCities = stateList.get(stateIndex).getAreaList();

                    ArrayList<Area> tmpAreas  = tmpCities.get(newValue).getAreaList();
                    if (tmpAreas.size() > 0){
                        areaItem.setAdapter(new ListWheelAdapter<Area>(tmpAreas));
                        areaItem.setCurrentItem(0, true);
                    }else {
                        areaItem.setAdapter(null);
                    }

                }
            });
        }
        
        android.content.DialogInterface.OnClickListener ackOnClickListener = new android.content.DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String newString = stateItem.getCurrentTextItem() + " " + cityItem.getCurrentTextItem() + " " + areaItem.getCurrentTextItem();
                if  (!newString.equals(oldString))
                {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("id", userId);
                        params.put("location", newString);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    new HttpSender().Httppost(OperationCode.CHANGE_SETTINGS, params, handler);
//                    location.setText(newString);
                    
                }
            }
        };
    };

    OnClickListener editTextOnClickListener = new OnClickListener() {
        TextView tmpTextView;
        int titleId;
//        int viewId;
        String oldString;
        String key;
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.settings_name:
//                viewId = R.id.settings_name;
                titleId = R.string.nick_name;
                oldString = myName.getText().toString();
                key = "name";
                break;
            case R.id.settings_description:
//                viewId = R.id.settings_description;
                titleId = R.string.description;
                oldString = description.getText().toString();
                key = "sign";
                break;
            default:
//                viewId = -1;
                titleId = R.string.defaultString;
                oldString = "";
                key = "";
                break;
            }
            LayoutInflater inflater = activity.getLayoutInflater();
            View layout = inflater.inflate(R.layout.edit_window,
                    (ViewGroup)activity. findViewById(R.id.edit_layout));
            new AlertDialog.Builder(activity).setTitle(titleId).setView(layout)
            .setPositiveButton(R.string.ack, ackOnClickListener).setNegativeButton(R.string.cancel, null).show();
            tmpTextView = (TextView)layout.findViewById(R.id.edit_window);
            tmpTextView.setText(oldString);
        }
        
        android.content.DialogInterface.OnClickListener ackOnClickListener = new android.content.DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String newString = tmpTextView.getText().toString();
                if (!newString.equals(oldString))
                {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("id", userId);
                        params.put(key, newString);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }      
                    new HttpSender().Httppost(OperationCode.CHANGE_SETTINGS, params, handler);
//                    switch (viewId) {
//                    case R.id.settings_name:
//                        myName.setText(newString);
//                        break;
//                    case R.id.settings_description:
//                        description.setText(newString);
//                        break;
//                    default:
//                        break;
//                    }
                }
            }

        };
       
    };
  
    //string -> byte[] -> bitmap -> setImageBitmap to show
    public void setImage(String str)
    {
        byte[] temp = imageUtil.String2Bytes(str);
        try {
            if(temp!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                imageUtil.savePhoto(userId, bitmap);
//                int scale = UserInterface.dip2px(activity, 70);
//                Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
                avatar.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }           
    }
    
    private void uploadImage()
    {
        JSONObject params = new JSONObject();
        
        try
        {
            params.put("id", userId);
            params.put("avatar", imageStr); 
            params.put("operation", 1);
            new HttpSender().Httppost(OperationCode.UPLOAD_AVATAR, params, handler);
            Log.i(TAG, "upload param: " + params.toString());
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
//        new Thread(new Runnable()
//        {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                setImage(imageStr);
//            }
//            
//        }).start();
        
    }
    
    
    //image to string
    public String getImageStr(String imfFilePath) 
    {
        byte[] data = null;
        try {
            InputStream in = new FileInputStream(imfFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
         
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 30, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100 && options>10) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
        } 
             
        data = baos.toByteArray();
        Log.e("imageUtil","size = " + data.length/1024);
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中   
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片     

        //imageStr = Base64.encodeToString(data, Base64.DEFAULT);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    
    
 // 从资源中获取Bitmap
    public Bitmap getBitmapFromResources(int resId) {
        Resources res = activity.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }
    
    public void setInfo(JSONObject jo)
    {
        try {
            myName.setText(jo.getString("name"));
            myEmail.setText(jo.getString("email"));
            if (jo.getInt("gender") == 1)
            {
                genderInt = 1;
                gender.setImageDrawable(activity.getResources().getDrawable(R.drawable.male));
            }
            else {
                genderInt = 0;
                gender.setImageDrawable(activity.getResources().getDrawable(R.drawable.female));
            } 
            String tmpStr;
            tmpStr = jo.getString("location");
            location.setText(tmpStr.equals("null")? "": tmpStr);
            tmpStr = jo.getString("sign");
            description.setText(tmpStr.equals("null")? "": tmpStr);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
    }
    
    OnClickListener genderChangeOnClickListener = new OnClickListener() {
        String[] genderItem = {"女", "男"};
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new AlertDialog.Builder(activity).setTitle(R.string.gender).setIcon(
                    android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                    new String[] { genderItem[0], genderItem[1] }, genderInt,
                    new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         if (genderInt != which)
                         {
                             JSONObject params = new JSONObject();
                             try {
                                params.put("id", userId);
                                params.put("gender", which);
                                new HttpSender().Httppost(OperationCode.CHANGE_SETTINGS, params, handler);
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                         }
                     }
                    }).setNegativeButton("取消", null).show();

        }
    };
    
    OnClickListener pwChangeOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(activity, ChangPW.class);
            intent.putExtra("email", myEmail.getText().toString());
            activity.startActivityForResult(intent, CASE_CHANGE_PW);
        }
    };
       
    
    class MessageHandler extends Handler
    {       
        public MessageHandler(Looper looper) {
            // TODO Auto-generated constructor stub
            super(looper);
        }
        
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
            case OperationCode.GET_AVATAR:       
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject( msg.obj.toString() );
                    if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
                    {
                        String returnStr = returnJson.getString("avatar");  
                        setImage(returnStr);
                    }
                    else 
                    {
                        Bitmap bitmap = getBitmapFromResources(R.drawable.defaultavatar);
                        int scale = MainActivity.dip2px(activity, 70);
                        Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
                        avatar.setImageBitmap(bitmap);
                    }
                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }                                           
                break;
            case OperationCode.UPLOAD_AVATAR:
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject( msg.obj.toString() );
                    Log.i("upload_avatar", returnJson.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                setImage(imageStr);
                imageUtil.getInstance().changeCacheImage(userId);
                break;
            case OperationCode.GET_USER_INFO:
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject(msg.obj.toString());
                    if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
                    {
                       setInfo(returnJson);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case OperationCode.CHANGE_SETTINGS:    
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject(msg.obj.toString());
                    if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
                    {
                       isFirstVisit = true;
                       initData();
                    }
                    else {
                        Toast.makeText(activity, "更改不成功, 请重试", Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case MSG_WHAT_ON_AREA_LIST_PREPARED:
            	Log.e("Setting", "area list prepared");
                areaListPrepare = true;
                break;
            default:
                break;
            }
        }
    }
}
