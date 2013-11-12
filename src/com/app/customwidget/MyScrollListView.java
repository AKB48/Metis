package com.app.customwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

public class MyScrollListView extends ListView{

	private int motionLastX;
	private int motionLastY;
	private int listTopPosition;//列表正数第listTopPosition位置
	private int listBottomPosition; //列表倒数第listTopPosition位置
	public MyScrollListView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @author WJL
	 * 初始话自定义listview，包括正数第listTopPosition个位置为最前的位置
	 * 和倒数第listBottomPosition个位置为最后的位置
	 */
	private void init() {
		// TODO Auto-generated method stub
		listTopPosition = 0;
		listBottomPosition = 1;
		setVerticalScrollBarEnabled(false);
		
		setDivider( new ColorDrawable( 0xffe0f0f0 ) );
		setDividerHeight(15);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		final int motionCurrentY = (int)ev.getY();
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
//			Log.i("MyScrollListView","ACTION_DOWN");
			motionLastY = motionCurrentY;
			myScrollListViewListner.onMotionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
//			Log.i("MyScrollListView","ACTION_MOVE");
			
			final int childCount = getChildCount();//当前页面可见的child的个数
			if(childCount == 0) return super.onTouchEvent(ev);
			
			int firstItemTop = getChildAt(0).getTop();
			int lastItemBottom = getChildAt(childCount-1).getBottom();
			
			float listBegin = getListPaddingTop();
			float listEnd = getHeight() - getListPaddingBottom();
			
			int totalItemCount = getAdapter().getCount() - listBottomPosition;
			int deltaY = motionCurrentY - motionLastY;
			int firstVisiblePosition = getFirstVisiblePosition();
//			Log.i("MyScrollListView", "dy = "+deltaY);
			myScrollListViewListner.onMotionMove(ev, deltaY);
			
			if(firstVisiblePosition <= listTopPosition && firstItemTop >= listBegin && deltaY > 0)
			{
//				Log.i("MyScrollListView", "---------------Pull Down");
				myScrollListViewListner.onListViewTopAndPullDown(deltaY);
				motionLastY = motionCurrentY;
			}
			
			if(firstVisiblePosition + childCount >= totalItemCount && lastItemBottom <= listEnd && deltaY < 0)
			{
//				Log.i("MyScrollListView", "---------------Pull Up");
				myScrollListViewListner.onListViewBottomAndPullUp(deltaY);
				motionLastY = motionCurrentY;
			}

			break;
		case MotionEvent.ACTION_UP:
//			Log.i("MyScrollListView","ACTION_UP");
			motionLastY = motionCurrentY;
			myScrollListViewListner.onMotionUp(ev);
			break;
			default: break;
		}
		
		return super.onTouchEvent(ev);
		
	}
	
	/**
	 * @author WJL
	 * 设置MyScrollListView的监听器，暂时为空
	 */
	onScrollListViewListener myScrollListViewListner = new onScrollListViewListener() {
		
		@Override
		public boolean onMotionUp(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionMove(MotionEvent ev, int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionDown(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewTopAndPullDown(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewBottomAndPullUp(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	/**
	 * @author WJL
	 * 将myScrollListViewListener设置成具体实现的listener
	 * 
	 * @param listener 具体实现的listener
	 */
	public void setOnScrollListViewListener(onScrollListViewListener listener)
	{
		myScrollListViewListner = listener;
	}
	
	/**
	 * @author WJL
	 * MyScrollListView 的监听器接口
	 */
	public interface onScrollListViewListener
	{
		/**
		 * @author WJL
		 * 从顶部下拉的时候触发
		 * @param deltaY
		 * @return
		 */
		boolean onListViewTopAndPullDown(int deltaY);
		
		/**
		 * @author WJL
		 * 从底部上拉的时候触发
		 * @param deltaY
		 * @return
		 */
		boolean onListViewBottomAndPullUp(int deltaY);
		
		/**
		 * @author WJL
		 * 当在屏幕按下的时候触发
		 * @param ev
		 * @return
		 */
		boolean onMotionDown(MotionEvent ev);
		
		/**
		 * @author WJL
		 * 当在屏幕上滑动的时候触发
		 * @param ev
		 * @param deltaY
		 * @return
		 */
		boolean onMotionMove(MotionEvent ev, int deltaY);
		
		/**
		 * @author WJL
		 * 当从屏幕上抬起的时候触发
		 * @param ev
		 * @return
		 */
		boolean onMotionUp(MotionEvent ev);
	}

}
