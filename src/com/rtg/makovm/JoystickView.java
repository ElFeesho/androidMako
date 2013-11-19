package com.rtg.makovm;
import android.view.*;
import android.content.*;
import android.util.*;
import android.graphics.*;

public class JoystickView extends View
{
    public interface JoystickListener
	{
		public void joystickAxisOn(int axis, int dir);
		public void joystickAxisOff(int axis);
	}
	
	private boolean mLeft, mRight, mUp, mDown;

    private final Paint PAINT = new Paint();

	private int stickWidth = 60;
	
	private float mX;
	private float mY;
	
	private JoystickListener mListener = new JoystickListener()
	{

		public void joystickAxisOn(int axis, int dir)
		{
			
		}

		public void joystickAxisOff(int axis)
		{
			
		}
		
	};
	
	public JoystickView(Context actx, AttributeSet attr)
	{
		this(actx, attr, 0);
	}

    public JoystickView(Context aCtx, AttributeSet attr, int defstyle)
	{
		super(aCtx, attr, defstyle);
		setClickable(true);
		PAINT.setColor(0xffffffff);
		PAINT.setStyle(Paint.Style.STROKE);
		PAINT.setTextSize(20);
	}
	
	public void setListener(JoystickListener aListener)
	{
		mListener = aListener;
	}
	
	@Override
	protected void onMeasure(int w, int h)
	{
		setMeasuredDimension(200, 200);
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawCircle(100, 100, 100, PAINT);
		canvas.drawCircle(mX+100, mY+100, stickWidth, PAINT);
		canvas.drawText(mX+":"+mY, 0, 30, PAINT);
	}
	
	public boolean dispatchTouchEvent(MotionEvent e)
	{
		if (e.getAction() == MotionEvent.ACTION_DOWN)
		{
			setStickPos(e.getX()-100, e.getY()-100);
		}
		else if (e.getAction() == MotionEvent.ACTION_MOVE)
		{
			setStickPos(e.getX()-100, e.getY()-100);
		}
		else if (e.getAction() == MotionEvent.ACTION_UP)
		{
			setStickPos(0, 0);
		}
		invalidate();
		return super.dispatchTouchEvent(e);
	}

	private void setStickPos(float x, float y)
	{
		mX = x;
		mY = y;
		double angle = Math.atan2(y, x);
		mX = Math.min(Math.abs(mX), 40);
		mY = Math.min(Math.abs(mY), 40);
		
		mX = (float) Math.cos(angle)*mX;
		mY = (float) Math.sin(angle)*mY;
		
		calculateCallbacks(mX, mY);
	}

	private void calculateCallbacks(float mX, float mY)
	{
        if(mX > 30.0f && !mRight)
		{
			mListener.joystickAxisOn(0, 1);
			mRight = true;
		}
		else if(mX < -30 && !mLeft)
		{
			mListener.joystickAxisOn(0, -1);
			mLeft = true;
		}
		else if(mRight || mLeft && (mX > -30 && mX < 30))
		{
			mRight = mLeft = false;
			mListener.joystickAxisOff(0);
		}
		
		if(mY > 30 && !mUp)
		{
			mListener.joystickAxisOn(1, 1);
			mUp = true;
		}
		else if(mY < -30 && !mDown)
		{
			mListener.joystickAxisOn(1, -1);
			mDown = true;
		}
		else
		{
			mUp = mDown = false;
			mListener.joystickAxisOff(1);
		}
	}
}
