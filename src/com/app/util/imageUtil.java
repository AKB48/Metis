package com.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.classes.FriendCenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


public class imageUtil 
{
    private final static String APP_PATH = Environment.getExternalStorageDirectory() + "/Catherine/";
	private final static String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Catherine/Avatar/";
	public final static String PHOTO_WALL_PATH = Environment.getExternalStorageDirectory() + "/Catherine/PhotosWall/";
//	private HashMap<Integer, Bitmap> imageMap = new HashMap<Integer, Bitmap>();
	
	private volatile static imageUtil uniqueInstance = null;
	private LruCache<Integer, Bitmap> mMemoryCache;/*, mPhotoWallMemCache;*/
	private int maxMemory;
	private int cacheSize;
	private myHandler mHandler;
	private Handler fcHandler, ncHandler, commentHandler, searchFriendHandler;
	
	private imageUtil()
	{
		//get max available vm memory
		maxMemory = (int )(Runtime.getRuntime().maxMemory() / 1024);
		//use 1/8th of the available memory for this memory cache
		cacheSize = maxMemory / 6;
		
//		mPhotoWallMemCache = new LruCache<Integer, Bitmap>(cacheSize)
//		{
//			@Override
//			protected int sizeOf(Integer key, Bitmap bitmap) {
////				Log.e("imageUtil", "key = " + key + " size: " + bitmap.getRowBytes()*bitmap.getHeight()/1024);
//				return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
//			}
//		};
				
		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize);
//		{
//			@Override
//			protected int sizeOf(Integer key, Bitmap bitmap) {
//				//the cache size will be measured in kilobytes 
//				return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
////				return bitmap.getByteCount() / 1024;
//			}
//		};
		
		Log.e("imageUtil", "memory size = " + maxMemory + "cache size = " + cacheSize);

		mHandler = new myHandler();
		fcHandler = null;
		ncHandler = null;
		commentHandler = null;
		searchFriendHandler = null;
	}
	
    
    public static imageUtil getInstance(){
        if(uniqueInstance == null){
            synchronized(imageUtil.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new imageUtil();
                }
            }
        }
        
        return uniqueInstance;       
    }
	
	 public static byte[] String2Bytes(String imgStr) 
    {
        byte[] bytes = null;
        try {
            bytes = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i]<0) {
                    bytes[i] += 256;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bytes;
    }
	 
    public static Bitmap scaleBitmap(Bitmap bm,int newWidth,int newHeight)
    {
        // 原始图像的宽和高
        int width = bm.getWidth();
        int height = bm.getHeight();
        
        // 计算缩放率
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        
        return resizedBitmap;
    }
    
	public static void savePhoto(int uid, Bitmap bmp)
	{		
		//check sd card exist
//		boolean sdCardExist = Environment.getExternalStorageState()
//				.equals( Environment.MEDIA_MOUNTED);
//		if( false==sdCardExist)
//		{
//			Log.e("imageUtil", "sd card not exist");
//			return;
//		}
		
		//when you need to save the image inside your own folder in the sd card
	    File imageFileFolder = new File( APP_PATH );

        if( !imageFileFolder.exists() )
        {
            imageFileFolder.mkdir();
//            Log.i("myevent", "************创建一个目录" + APP_PATH);
        }
        
        imageFileFolder = new File(IMAGE_PATH);

		if( !imageFileFolder.exists() )
		{
			imageFileFolder.mkdir();
//			Log.i("myevent", "************创建一个目录" + IMAGE_PATH);
		}
		
		FileOutputStream out = null;
		File imageFileName = new File(imageFileFolder, uid+".jpg");
		try
		{
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			out = null;
//			Log.i("myevent", "************保存了一张图片" + uid);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * precondition:
	 * 		fileExist return true;
	 * @param uid
	 * @return
	 */
	public static Bitmap getLocalBitmapBy(int uid)
	{
		Bitmap bitmap = null;
		
		bitmap = BitmapFactory.decodeFile( IMAGE_PATH + uid + ".jpg" );
	
		return bitmap;
	}
	
	public static boolean fileExist(int uid)
	{
		File file = new File( IMAGE_PATH + uid + ".jpg" );
		return file.exists();
	}
	
//	//map
//	public boolean fileExistInMap(int uid)
//	{
//		return imageMap.containsKey(uid) || fileExist(uid);
//	}
//	
//	public Bitmap getBitmapInMap(int uid)
//	{
//		Bitmap bitmap = null;
//		
//		if( imageMap.containsKey(uid) )	
//		{
//			bitmap = imageMap.get(uid);
//			Log.e("imageUtil", "in map: " + uid);
//		}			
//		else if( fileExist(uid) )
//		{
//			bitmap = getLocalBitmapBy(uid);
//			putBitmapInMap(uid, bitmap);
//			Log.e("imageUtil", "in local: " + uid);
//		}
//		
//		return bitmap;
//	}
//	
//	public void putBitmapInMap(int uid, Bitmap bitmap)
//	{
//		imageMap.put(uid, bitmap);
//	}
	
	//LruCache
	public void addBitmapToMemoryCache(int uid, Bitmap bitmap) {
		//if image not in cache, add to cache
		if( mMemoryCache.get(uid)==null )
		{
			mMemoryCache.put(uid, bitmap);
//			Log.e("imageUtil", "put an image: " + uid);
		}
	}

	/**
	 * precondition:
	 * 		imageExistInCache return true;
	 * @param uid
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(int uid) 
	{	
//		Log.e("imageUtil", "get an image: " + uid);
		return mMemoryCache.get(uid);
	}
	
	//image in cache or not
	public boolean imageExistInCache( int uid )
	{
//		Log.e("imageUtil", "want to get image: " + uid);
		if( mMemoryCache.get(uid)==null ) 
		{
			if( fileExist(uid)  )
			{
				Bitmap bitmap = getLocalBitmapBy(uid);
				if( bitmap!=null ) {
					mMemoryCache.put(uid, bitmap);
					return true;
				}
			}
				
			return false;
		}
		else 
			return true;
	}
	
	public Bitmap getAvatar(int uid)
	{
	    if (imageExistInCache(uid))
	    {
	        return getBitmapFromMemCache(uid);
	    }
	    else {
	        retrieve_avatar(uid);
	        return null;
	    }
	}
	
	private void retrieve_avatar(int uid)
    {
        JSONObject params = new JSONObject();
        try
        {
            params.put("id", uid);
            params.put("operation", 0);
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, mHandler);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	private void writeAvatar(String str)
    {
        JSONObject returnJson;
        try {
            returnJson = new JSONObject(str );
            if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
            {
                String returnStr = returnJson.getString("avatar");  
                byte[] temp = String2Bytes(returnStr);
                if(temp!=null)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                    savePhoto(returnJson.getInt("id"), bitmap);
                    notifyChanged();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
	public void registerHandler(Handler handler, String className)
	{
	    if (className.equals("FriendCenter")) {
	        this.fcHandler = handler;
	    }
	    else if (className.equals("NotificationCenter")) {
	        this.ncHandler = handler;
	    }
	    else if (className.equals("CommentPage")) {
	        this.commentHandler = handler;
	    }
	    else if (className.equals("searchFriend")) {
	        this.searchFriendHandler = handler;
	    }
	}
	
	private void notifyChanged()
	{
	    if (null != fcHandler)
	    {
	        Message msg = fcHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
	        msg.sendToTarget();
	    }
	    if (null != ncHandler)
	    {
	        Message msg = ncHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
            msg.sendToTarget();
	    }
	    if (null != commentHandler)
	    {
	        Message msg = commentHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
            msg.sendToTarget();
	    }
	    if (null != searchFriendHandler)
	    {
	        Message msg = searchFriendHandler.obtainMessage(FriendCenter.MSG_WHAT_ON_UPDATE_AVATAR);
            msg.sendToTarget();
	    }
	}
	
	public void unregisterHandler(String className) {
	    if (className.equals("FriendCenter")) {
	        fcHandler = null;
	    }
	    else if (className.equals("NotificationCenter")) {
            this.ncHandler = null;
        }
	    else if (className.equals("CommentPage")) {
	        this.commentHandler = null;
	    }
	    else if (className.equals("searchFriend")) {
	        this.searchFriendHandler = null;
	    }
	}
	
	public void changeCacheImage(int uid)
	{
	    if (null != mMemoryCache.get(uid)) {
	        mMemoryCache.put(uid, getLocalBitmapBy(uid));
	    }
	}
	
	//-----------------------------------------------------------------------
	//计算图片的缩放值
	public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) 
	{
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    
	    Log.e("imageUitl", "insample size = " + inSampleSize);
	    return inSampleSize;
	}
	
	// 根据路径获得图片并获取小尺寸图片，返回bitmap用于显示（还没进行质量压缩）
	public Bitmap getSmallBitmap(String filePath) 
	{
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

	        // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, 240, 400);

	        // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	//质量压缩
	public Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1000>25 && options>10) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
        }  
        
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
	
	/**
	 * photo wall : save photo to file
	 * directory: PHOTO_WALL_PATH
	 * @param photoId
	 * @param bmp
	 */
	public void photoWallsavePhoto(final int photoId, final Bitmap bmp)
	{		
		new Thread()
		{
			public void run() {
//				if( mPhotoWallMemCache.get(photoId) ==null )
//				{
//					mPhotoWallMemCache.put(photoId, bmp);
//					Log.i("TAG", "save " + photoId + " to cache");
//				}
				
				//check sd card exist
				boolean sdCardExist = Environment.getExternalStorageState()
						.equals( Environment.MEDIA_MOUNTED);
				if( false==sdCardExist)
				{
					Log.e("imageUtil", "sd card not exist");
					return;
				}
				//when you need to save the image inside your own folder in the sd card
			    File imageFileFolder = new File( APP_PATH );
		        if( !imageFileFolder.exists() )      
		            imageFileFolder.mkdir();       
		        
		        imageFileFolder = new File(PHOTO_WALL_PATH);
				if( !imageFileFolder.exists() )
					imageFileFolder.mkdir();

				FileOutputStream out = null;
				File imageFileName = new File(imageFileFolder, photoId+".jpg");
				try
				{
					out = new FileOutputStream(imageFileName);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					out = null;
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}.start();
		
	}
	
	/**
	 * photo wall: get image by photo id
	 * @param photoId
	 * @return bitmap in cache or in file
	 */
	public Bitmap photoWallGetImage( int photoId )
	{
//		Bitmap bitmap = mPhotoWallMemCache.get( photoId );
		
		//if not in cache
//		if( null!=bitmap )
//		{
//			Log.i("TAG", "in cache---------");
//		}
//		else
//		{
			//if exist in file
//			File file = new File( PHOTO_WALL_PATH + photoId + ".jpg" );
//			if( file.exists() )
//			{
//				Log.i("TAG", "in file---------");
//				bitmap = BitmapFactory.decodeFile( PHOTO_WALL_PATH + photoId + ".jpg" );			
//				mPhotoWallMemCache.put(photoId, bitmap);	
//				Log.i("TAG", "save " + photoId + " to cache" + ". size = " + mPhotoWallMemCache.size());
//			}
//		}
			
//		return bitmap;
		File file = new File( PHOTO_WALL_PATH + photoId + ".jpg" );
		Bitmap bitmap = null;
		if( file.exists() )
		{
			bitmap = BitmapFactory.decodeFile( PHOTO_WALL_PATH + photoId + ".jpg" );		
		}
		
		return bitmap;
	}
	
	/**
	 * delete image file
	 * @author chunk
	 *
	 */
	public void photoWallDeleteImg( final String filePath)
	{
		new Thread()
		{
			public void run() {
				File file = new File( filePath );
				
				if( file.exists() )
				{
					try{
						new File(filePath ).delete();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}
	
    public class myHandler extends Handler
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
            case OperationCode.GET_AVATAR:
                final String final_mes = msg.obj.toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    writeAvatar(final_mes);
                    }
                }).start();
                break;
            default: 
                break;
            }
        }
    }
}
