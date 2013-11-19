package com.rtg.makovm;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View
{
	public static final int DIR_UP = 0;
	public static final int DIR_DOWN = 1;
	public static final int DIR_LEFT = 2;
	public static final int DIR_RIGHT = 3;

    public interface JoystickListener
	{
		public void joystickAxisOn(int dir);
		public void joystickAxisOff(int dir);
	}

	private boolean mLeft, mRight, mUp, mDown;

    private final Paint PAINT = new Paint();

	private final int stickWidth = 90;
	private final int stickRadius = 150;

	private float mX;
	private float mY;

	private JoystickListener mListener = new JoystickListener()
	{

		@Override
		public void joystickAxisOn(int dir) {}

		@Override
		public void joystickAxisOff(int dir) {}

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
		setMeasuredDimension(stickRadius*2, stickRadius*2);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawCircle(stickRadius, stickRadius, stickRadius, PAINT);
		canvas.drawCircle(mX+stickRadius, mY+stickRadius, stickWidth, PAINT);
	}

	@Override
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
		mX = Math.min(Math.abs(mX), stickRadius-stickWidth);
		mY = Math.min(Math.abs(mY), stickRadius-stickWidth);

		mX = (float) Math.cos(angle)*mX;
		mY = (float) Math.sin(angle)*mY;

		calculateCallbacks();
	}

	private void calculateCallbacks()
	{
        if(mX > stickWidth/2)
		{
        	if(!mRight)
        	{
				mListener.joystickAxisOn(DIR_RIGHT);
				mRight = true;
        	}
		}
		else if(mX < -stickWidth/2)
		{
			if(!mLeft)
			{
				mListener.joystickAxisOn(DIR_LEFT);
				mLeft = true;
			}
		}
		else
		{
			if(mLeft)
			{
				mListener.joystickAxisOff(DIR_LEFT);
				mLeft = false;
			}
			else if(mRight)
			{
				mListener.joystickAxisOff(DIR_RIGHT);
				mRight = false;
			}
		}

		if(mY > stickWidth/2)
		{
			if(!mDown)
			{
				mListener.joystickAxisOn(DIR_DOWN);
				mDown = true;
			}
		}
		else if(mY < -stickWidth/2)
		{
			if(!mUp)
			{
				mListener.joystickAxisOn(DIR_UP);
				mUp = true;
			}
		}
		else
		{
			if(mUp)
			{
				mListener.joystickAxisOff(DIR_UP);
				mUp = false;
			}
			else if(mDown)
			{
				mListener.joystickAxisOff(DIR_DOWN);
				mDown = false;
			}
		}
	}
}
