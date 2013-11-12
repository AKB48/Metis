package com.app.customwidget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;

public class LetterSidebar extends View{

    public LetterSidebar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public LetterSidebar(Context context)
    {
        super(context);
    }

    public LetterSidebar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    
    private String[] letterTable =
        {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    private int choose = -1;
    private Paint paint = new Paint();
    private boolean showBkg = false;
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        if (showBkg)
        {
            canvas.drawColor(Color.parseColor("#ffaaaaaa"));
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letterTable.length;
        for (int i = 0; i < letterTable.length; i++)
        {
            paint.setTextSize(15) ;
            paint.setColor(Color.BLACK);
            //paint.setTypeface(Typeface.DEFAULT_BOLD);          
            paint.setAntiAlias(true);
            float xPos = width / 2 - paint.measureText(letterTable[i]) / 2;
            float yPos = singleHeight * i + singleHeight + paint.measureText(letterTable[i]) / 2;
            
            if (i == choose)
            {
                paint.setColor(Color.GRAY);
                float radius = width/2;
                if (width > singleHeight)
                    radius = singleHeight/2;
                canvas.drawCircle(width/2, singleHeight*i + singleHeight, radius, paint);
                //canvas.drawRect(0, singleHeight * i, width, singleHeight * (i+1), paint);
                paint.setColor(Color.WHITE);
                paint.setFakeBoldText(true);
            }
          
            canvas.drawText(letterTable[i], xPos, yPos, paint);
            paint.reset();
        }

    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * letterTable.length);

        switch (action)
        {
        case MotionEvent.ACTION_DOWN:
            showBkg = true;
            if (oldChoose != c && listener != null)
            {
                if (c >= 0 && c < letterTable.length)
                {
                    listener.onTouchingLetterChanged(letterTable[c]);
                    choose = c;
                    invalidate();
                }
            }

            break;
        case MotionEvent.ACTION_MOVE:
            if (oldChoose != c && listener != null)
            {
                if (c >= 0 && c < letterTable.length)
                {
                    listener.onTouchingLetterChanged(letterTable[c]);
                    choose = c;
                    invalidate();
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            showBkg = false;
            if (listener != null)
            {
                if (c >= 0 && c < letterTable.length)
                {
                    listener.onTouchingLetterChanged(letterTable[c]);
                    choose = c;
                    invalidate();
                }
            }
            
            break;
        default:
            break;
        }
        return true;
    }
    
    
    public void OnScrollChangedLetter(int c)
    {
        int oldChoose = choose;
        if (oldChoose != c && this.onTouchingLetterChangedListener != null)
        {   
            if (c >= 0 && c < letterTable.length)
            {
                //this.onTouchingLetterChangedListener.onTouchingLetterChanged(letterTable[c]);
                choose = c;
                invalidate();
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
    {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }
    
    public interface OnTouchingLetterChangedListener
    {
        public void onTouchingLetterChanged(String s);
    }
}
