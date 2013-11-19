package com.rtg.makovm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ButtonView extends View
{
	private static final int BUTTON_RADIUS = 96;
	private static final int BUTTON_DIAMETER = BUTTON_RADIUS*2;

	private final static Paint sPaint = new Paint();
	private final static Paint sFillPaint = new Paint();

	public interface ButtonViewListener
	{
		public void buttonViewDown(ButtonView aButtonView);
		public void buttonViewUp(ButtonView aButtonView);
	}

	static
	{
		sPaint.setStyle(Paint.Style.STROKE);
		sPaint.setColor(0xffffffff);
		sFillPaint.setColor(0xff0044ff);
	}

	private boolean mDown;

	private ButtonViewListener mListener = new ButtonViewListener()
	{
		@Override
		public void buttonViewUp(ButtonView aButtonView) {}

		@Override
		public void buttonViewDown(ButtonView aButtonView) {}
	};

	public ButtonView(Context aContext, AttributeSet aAttrs, int aDefStyle)
	{
		super(aContext, aAttrs, aDefStyle);
		setClickable(true);
	}

	public ButtonView(Context aContext, AttributeSet aAttrs)
	{
		this(aContext, aAttrs, 0);
	}

	public ButtonView(Context aContext)
	{
		this(aContext, null, 0);
	}

	@Override
	protected void onMeasure(int aWidthMeasureSpec, int aHeightMeasureSpec)
	{
		setMeasuredDimension(BUTTON_DIAMETER, BUTTON_DIAMETER);
	}

	public void setListener(ButtonViewListener aListener)
	{
		mListener = aListener;
	}

	@Override
	protected void onDraw(Canvas aCanvas)
	{
		super.onDraw(aCanvas);
		if(mDown)
		{
			aCanvas.drawCircle(BUTTON_RADIUS, BUTTON_RADIUS, BUTTON_RADIUS, sFillPaint);
		}

		aCanvas.drawCircle(BUTTON_RADIUS, BUTTON_RADIUS, BUTTON_RADIUS, sPaint);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent aEvent)
	{
		if(aEvent.getAction() == MotionEvent.ACTION_DOWN)
		{
			mListener.buttonViewDown(this);
			mDown = true;
		}
		else if(aEvent.getAction() == MotionEvent.ACTION_UP)
		{
			mListener.buttonViewUp(this);
			mDown = false;
		}
		invalidate();
		return super.dispatchTouchEvent(aEvent);
	}
}
