package com.app.customwidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.app.customwidget.MyScrollListView.onScrollListViewListener;
import com.example.slidingmenudemo.R;

import android.R.color;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PullUpDownView extends LinearLayout implements onScrollListViewListener{

	private static final float PULL_DISTANCE_LIMIT = 50;	// The critical value of pull distance. If pull distance
															// doesn't reach the value, we will ignore the pull action
	private static final float AUTO_INCREMENT = 10;			// A unit height used for the change of the height
	private static final int DEFAULT_HEADER_HEIGHT = 110;	// The default height of the header
	
	private static final int MSG_WHAT_ON_LOAD_DATA = -1;	//msg.what: Before loading data
	private static final int MSG_WHAT_LOAD_DATA_DONE = -2;	//msg.what: Loading data has been done 
	private static final int MSG_WHAT_ON_REFRESH = -3;		//msg.what: Before refreshing
	private static final int MSG_WHAT_REFRESH_DONE = -4;	//msg.what: Refreshing has been done
	private static final int MSG_WHAT_GET_MORE_DONE = -5;	//msg.what: Getting more data has been done
	private static final int MSG_WHAT_SET_HEADER_HEIGHT = -6;	//msg.what: set the height of the header
	
	// Header states
	private static final int HEADER_STATE_IDLE = 0;	// State of idle
	private static final int HEADER_STATE_OVER_HEIGHT = 1;	// State of over the default height
	private static final int HEADER_STATE_NOT_OVER_HEIGHT = 2;	// State of not over the default height
	
	private int headerIncrement;		// Record the current height of the header
	private int headerState = HEADER_STATE_IDLE;	// Initialize the state of the header
	private float motionDownY;		// Record the Y-coordinate when touch action is DOWN
	
	private boolean isDown;		// If touch action is DOWN or not
	private boolean isRefreshing;	// If is Refreshing or not
	private boolean isGettingMore;	// If is Getting more data or not
	private boolean isPullBackDone; // If executing operation of pull back or not
	
	// Something about views
	private View headerView;
	private LayoutParams headerViewParams;	
	private TextView headerViewDateView;
	private TextView headerTextView;
	private ImageView headerArrowView;
	private View headerLoadingView;
	private View footerView;
	private TextView footerTextView;
	private View footerLoadingView;
	private MyScrollListView myListView;
	
	private onPullListener myOnPullListener;
	private RotateAnimation rotate0To180Animation;	// Animation that rotate from 0 degree to 180 degree
	private RotateAnimation rotate180To0Animation;	// Animation that rotate from 180 degree to 0 degree

	private myHandler viewHandler = new myHandler();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	
	/**
	 * @author WJL
	 * An interface to handle the pull events
	 */
	public interface onPullListener 
	{
		/**
		 * @author WJL
		 * 
		 */
		void Refresh();
		
		/**
		 * @author WJL
		 * 
		 */
		void GetMore();
	}
	
	/**
	 * @author WJL
	 * a Constructor of PullUpDownView
	 * @param context
	 */
	public PullUpDownView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initHeaderAndFooterAndListView(context);
	}
	
	/**
	 * @author WJL
	 * a Constructor of PullUpDownView
	 * @param context
	 * @param attrs
	 */
	public PullUpDownView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initHeaderAndFooterAndListView(context);
	}
	
	/**
	 * @author WJL
	 * Notify before loading data
	 */
	public void notifyOnLoadData()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_ON_LOAD_DATA);
	}
	
	/**
	 * @author WJL
	 * Notify if loading data is done
	 */
	public void notifyLoadDataDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_LOAD_DATA_DONE);
	}
	
	/**
	 * @author WJL
	 * Notify if refreshing is done
	 */
	public void notifyRefreshDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_REFRESH_DONE);
	}
	
	/**
	 * @author WJL
	 * Notify if getting more data is done
	 */
	public void notifyGetMoreDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_GET_MORE_DONE);
	}
	
	/**
	 * @author WJL
	 * Set myOnPullListener
	 * @param listener The definite listener
	 */
	public void setOnPullListener(onPullListener listener)
	{
		myOnPullListener = listener;
	}
	
	/**
	 * @author WJL
	 * 
	 * @return Return the ListView, which is instance of MyScrollListView
	 */
	public ListView getListView()
	{
		return myListView;
	}
	
	/**
	 * @author WJL
	 * Initialization of header, footer and listview
	 * @param context
	 */
	private void initHeaderAndFooterAndListView(Context context)
	{
		setOrientation(LinearLayout.VERTICAL);
		
		headerView = LayoutInflater.from(context).inflate(R.layout.pulldown_header, null);
		headerViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(headerView, 0, headerViewParams);
		
		headerTextView = (TextView)headerView.findViewById(R.id.pulldown_header_text);
		headerLoadingView = headerView.findViewById(R.id.pulldown_header_loading);
		headerArrowView = (ImageView)headerView.findViewById(R.id.pulldown_header_arrow);
		headerViewDateView = (TextView)headerView.findViewById(R.id.pulldown_header_date);
		
		footerView = LayoutInflater.from(context).inflate(R.layout.pulldown_footer, null);
		footerTextView = (TextView)footerView.findViewById(R.id.pulldown_footer_text);
		footerLoadingView = footerView.findViewById(R.id.pulldown_footer_loading);
		footerLoadingView.setVisibility(View.VISIBLE);
		footerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isGettingMore)
				{
					isGettingMore = true;
					footerLoadingView.setVisibility(View.VISIBLE);
					myOnPullListener.GetMore();
				}
			}
		});
		
		rotate0To180Animation = new RotateAnimation(0, 180, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate0To180Animation.setDuration(250);
		rotate0To180Animation.setFillAfter(true);
		
		rotate180To0Animation = new RotateAnimation(180, 0, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate180To0Animation.setDuration(250);
		rotate180To0Animation.setFillAfter(true);
		
		myListView = new MyScrollListView(context);
		myListView.setOnScrollListViewListener(this);
		myListView.setCacheColorHint(color.white);
		addView(myListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		PullUpDownView.LayoutParams params = (PullUpDownView.LayoutParams) myListView.getLayoutParams();
		params.setMargins(8, 8, 8, 0);
		myListView.setLayoutParams(params);
		this.setBackgroundColor( 0xffe0f0f0 );
		
		myOnPullListener = new onPullListener() {
			
			@Override
			public void Refresh() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void GetMore() {
				// TODO Auto-generated method stub
				
			}
		};
		
	}
	
	/**
	 * @author WJL
	 * Check the header state 
	 */
	private void checkHeaderViewState()
	{
		if(headerViewParams.height >= DEFAULT_HEADER_HEIGHT) // If the pull down distance is enough
		{
			if(headerState == HEADER_STATE_OVER_HEIGHT) return;
			headerState = HEADER_STATE_OVER_HEIGHT;
			headerTextView.setText("松开可以刷新");
			headerArrowView.startAnimation(rotate0To180Animation);
		}
		else	// If the pull down distance is not enough
		{
			if(headerState == HEADER_STATE_NOT_OVER_HEIGHT || headerState == HEADER_STATE_IDLE) return;
			headerState = HEADER_STATE_NOT_OVER_HEIGHT;
			headerTextView.setText("下拉可以刷新");
			headerArrowView.startAnimation(rotate180To0Animation);
		}
	}
	
	/**
	 * @author WJL
	 * Set the height of the header
	 * @param height	Value of the new height of the header
	 */
	private void setHeaderHeight(int height)
	{
		headerIncrement = height;
		headerViewParams.height = height;
		headerView.setLayoutParams(headerViewParams);
	}
	
	/**
	 * @author WJL
	 * The TimerTask that realize the action of hiding the header,
	 * under the circumstance of pulling down distance is not enough
	 */
	private class HideHeaderTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isDown)	// When is not under a touch event
			{
				headerIncrement -= AUTO_INCREMENT;	// The current height of the header is slowing toward 0
				if(headerIncrement < 0)			// If the current height of the header is less than 0
				{
					headerIncrement = 0;	// Reset height of the header to 0
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
					cancel();	// The task is done
				}
				else	// Set height of the header and continue reduce the height of the header
				{
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
				}
			}
			else
			{
				cancel();
			}
		}
		
	}
	
	/**
	 * @author WJL
	 * The TimeTask that realize the action of showing the header, 
	 * under the circumstance of pulling down too much
	 */
	private class ShowHeaderTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isDown)	// When is not under a touch event
			{
				headerIncrement -= AUTO_INCREMENT;		// The current height of the header is slowing toward the default height
				if(headerIncrement <= DEFAULT_HEADER_HEIGHT)			// If current height of the header is less than the default height
				{
					headerIncrement = DEFAULT_HEADER_HEIGHT;			// Reset the height of the header
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
					if(!isRefreshing)			// Judge if is handling refreshing. If not, do the refreshing
					{
						isRefreshing = true;
						viewHandler.sendEmptyMessage(MSG_WHAT_ON_REFRESH);
					}
					cancel();
				}
				else
				{
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
				}
			}
			else
			{
				cancel();
			}
		}
		
	}
	
	/**
	 * @author WJL
	 * Show the footerView
	 */
	private void showFooterView()
	{
		if(myListView.getFooterViewsCount() == 0 && isFillScreen())
		{
			myListView.addFooterView(footerView);
			myListView.setAdapter(myListView.getAdapter());
		}
	}
	
	/**
	 * @author WJL
	 * Judge if the visible items are full of the listview
	 * @return
	 */
	private boolean isFillScreen()////////
	{
//		int childCount = myListView.getChildCount();
		int firstVisiblePosition = myListView.getFirstVisiblePosition();
		int lastVisiblePosition = myListView.getLastVisiblePosition() - myListView.getFooterViewsCount();
		int visibleItemsCount = lastVisiblePosition - firstVisiblePosition + 1;
		int totalItemsCount = myListView.getCount() - myListView.getFooterViewsCount();
//		Log.i("PDV","visibleCount: "+visibleItemsCount+", totalCount: "+ totalItemsCount);
//		int lastVisiblePositionBottom = footerView.getBottom();
//		int listEnd = myListView.getHeight() - myListView.getPaddingBottom();
//		if(lastVisiblePositionBottom <= listEnd) return true;
		if(visibleItemsCount < totalItemsCount) return true;
//		int visibleLastItem = lastVisiblePosition + 1;
//		int lastItem = myListView.getCount() - myListView.getFooterViewsCount();
//		if(visibleLastItem < lastItem) return false;
		else return false;
	}
	
	/**
	 * @author WJL
	 * Custom handler
	 */
	private class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case MSG_WHAT_ON_LOAD_DATA:
				footerLoadingView.setVisibility(VISIBLE);
				footerTextView.setText("加载中...");
				break;
			case MSG_WHAT_LOAD_DATA_DONE:
				headerViewParams.height = 0;
				headerLoadingView.setVisibility(View.GONE);
				headerTextView.setText("下拉可以刷新");
				headerViewDateView.setText("更新于: "+ dateFormat.format(new Date(System.currentTimeMillis())));
				headerViewDateView.setVisibility(View.VISIBLE);
				headerArrowView.setVisibility(View.VISIBLE);
				showFooterView();
				break;
			case MSG_WHAT_ON_REFRESH:
				headerArrowView.clearAnimation();
				headerArrowView.setVisibility(View.INVISIBLE);
				headerLoadingView.setVisibility(View.VISIBLE);
				myOnPullListener.Refresh();
				break;
			case MSG_WHAT_REFRESH_DONE:
				isRefreshing = false;
				headerState = HEADER_STATE_IDLE;
				headerArrowView.setVisibility(View.VISIBLE);
				headerLoadingView.setVisibility(View.GONE);
				headerTextView.setText("下拉可以刷新");
				headerViewDateView.setText("更新于: "+ dateFormat.format(new Date(System.currentTimeMillis())));
				setHeaderHeight(0);
				showFooterView();
				break;
			case MSG_WHAT_GET_MORE_DONE:
				isGettingMore = false;
				footerTextView.setText("暂时没有更多了~");
				footerLoadingView.setVisibility(View.GONE);
				break;
			case MSG_WHAT_SET_HEADER_HEIGHT:
				setHeaderHeight(headerIncrement);
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	
	@Override
	public boolean onListViewTopAndPullDown(int deltaY) {
		// TODO Auto-generated method stub
		if(isRefreshing || myListView.getCount() - myListView.getFooterViewsCount() == 0) return false;
		int absDeltaY = Math.abs(deltaY);
		final int i = (int) Math.ceil((double)absDeltaY / 2);
		headerIncrement += i;
		if(headerIncrement > 0)
		{
			setHeaderHeight(headerIncrement);
			checkHeaderViewState();
		}
		return true;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int deltaY) {
		// TODO Auto-generated method stub
		if(isGettingMore) return false;
		if(isFillScreen())
		{
			isGettingMore = true;
			footerLoadingView.setVisibility(View.VISIBLE);
			footerTextView.setText("加载中...");
			myOnPullListener.GetMore();
		}
		return true;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev) {
		// TODO Auto-generated method stub
		isDown = true;
		isPullBackDone = false;
		motionDownY = ev.getY();
		return false;
	}

	@Override
	public boolean onMotionMove(MotionEvent ev, int deltaY) {
		// TODO Auto-generated method stub
		if(isPullBackDone) return true;
		
		int absMotionY = (int) Math.abs(ev.getY() - motionDownY);
		if(absMotionY < PULL_DISTANCE_LIMIT) return true;
		
		int absDeltaY = Math.abs(deltaY);
		int i = (int) Math.ceil((double)absDeltaY/2);
		
		if(headerViewParams.height > 0 && deltaY < 0)
		{
			headerIncrement -= i;
			if(headerIncrement > 0)
			{
				setHeaderHeight(headerIncrement);
				checkHeaderViewState();
			}
			else
			{
				headerState = HEADER_STATE_IDLE;
				headerIncrement = 0;
				setHeaderHeight(headerIncrement);
				isPullBackDone = true;				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev) {
		// TODO Auto-generated method stub
		isDown = false;
		
		if( headerIncrement > 0)
		{
			int deltaY = headerIncrement - DEFAULT_HEADER_HEIGHT;
			Timer timer = new Timer();
			if(deltaY < 0)
			{
				timer.schedule(new HideHeaderTimerTask(), 0, 20);
			}
			else
			{
				timer.schedule(new ShowHeaderTimerTask(), 0, 20);
			}
			return true;
		}
		return false;
	}

}
