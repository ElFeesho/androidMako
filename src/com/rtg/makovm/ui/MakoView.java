package com.rtg.makovm.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import com.rtg.makovm.MakoConstants;
import com.rtg.makovm.MakoVM;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class MakoView extends SurfaceView
{

	private static final String TAG = "MakoView";
	private MakoVM mVm;

	private float mScale;

	public interface MakoViewListener
	{
		public void makoViewStartLoading();

		public void makoViewFinishLoading();

		public void makoViewLoadError();
	}

	private MakoViewListener mListener = null;
	private boolean mRendering = false;
	private final Semaphore mRenderingSemaphore = new Semaphore(1);
	private final Semaphore mMakoSemaphore = new Semaphore(1);

	private final Thread mMakoThread = new Thread()
	{
		@Override
		public synchronized void run()
		{
			while (mVm != null)
			{
				try
				{
					mMakoSemaphore.acquire();
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				mVm.run();
				mRenderingSemaphore.release();
			}
		}
	};

	private final Thread mRenderingThread = new Thread()
	{
		@Override
		public synchronized void run()
		{
			while (mRendering)
			{
				Canvas c = getHolder().lockCanvas();
				if(c == null)
				{
					return;
				}

				c.save();
				c.scale(mScale, mScale);
				try
				{
					mRenderingSemaphore.acquire();
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				c.drawBitmap(mVm.p, 0, 320, 0, 0, 320, 240, false, null);
				mMakoSemaphore.release();
				c.restore();
				getHolder().unlockCanvasAndPost(c);
			}
		}
	};

	public void setListener(MakoViewListener listener)
	{
		mListener = listener;
	}

	private class RomLoadTask extends AsyncTask<String, Void, Boolean>
	{
		private int[] mLoadedRom = null;
		private int[] mLoadedSave;

		@Override
		protected void onPreExecute()
		{
			if (mListener != null)
			{
				mListener.makoViewStartLoading();
			}
		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			// Only can load one rom at a time... which makes sense
			try
			{
				mLoadedRom = loadRom(new FileInputStream(params[0]), null);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				mLoadedSave = loadRom(new FileInputStream(params[0] + ".sav"), null);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (mListener != null)
			{
				if (!result)
				{
					mListener.makoViewLoadError();
				}
				else
				{
					mVm = new MakoVM(mLoadedSave != null ? mLoadedSave : mLoadedRom);
					mRendering = true;
					try
					{
						mRenderingSemaphore.acquire();
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mRenderingThread.start();
					mMakoThread.start();
				}
				mListener.makoViewFinishLoading();

			}
		}
	}

	public MakoView(Context c, AttributeSet a)
	{
		this(c, a, 0);
	}

	public MakoView(Context aCtx, AttributeSet attrs, int style)
	{
		super(aCtx, attrs, style);
		getHolder().addCallback(new Callback()
		{

			@Override
			public void surfaceDestroyed(SurfaceHolder aHolder)
			{
				mRendering = false;
			}

			@Override
			public void surfaceCreated(SurfaceHolder aHolder)
			{

			}

			@Override
			public void surfaceChanged(SurfaceHolder aHolder, int aFormat, int aWidth, int aHeight)
			{

			}
		});
	}

	public void setRom(String filename)
	{
		mVm = null;
		new RomLoadTask().execute(filename);
	}

	private static int[] loadRom(InputStream i, int[] prev)
	{
		try
		{
			int romSize = i.available();

			// Allocate a byteBuffer, this will be used later to convert the
			// bytes
			// into an integer array
			ByteBuffer buffer = ByteBuffer.allocate(romSize);
			buffer.clear();
			byte[] page = new byte[4096];
			int read = 0;
			int totalRead = 0;
			while (i.available() > 0)
			{
				read = i.read(page, 0, 4096);
				totalRead += read;
				buffer.put(page, 0, read);
			}
			int[] rom = new int[totalRead / 4];
			buffer.rewind();
			IntBuffer intBuf = buffer.asIntBuffer();
			intBuf.get(rom);
			buffer.clear();
			i.close();
			Log.i(TAG, "Restored from save file!");
			return rom;
		}
		catch (IOException ioe)
		{
			Log.e(TAG, "Unable to load rom!");
			return prev;
		}
	}

	public void setKeys(int mask)
	{
		mVm.keys |= mask;
	}

	public void unsetKeys(int mask)
	{
		mVm.keys -= (mVm.keys & mask);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		int k = event.getKeyCode();
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (k == KeyEvent.KEYCODE_DEL)
			{
				keyPressed(8);
			}
			else if (k == KeyEvent.KEYCODE_ENTER)
			{
				keyPressed(10);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_UP)
			{
				setKeys(MakoConstants.KEY_UP);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_DOWN)
			{
				setKeys(MakoConstants.KEY_DN);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_LEFT)
			{
				setKeys(MakoConstants.KEY_LF);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				setKeys(MakoConstants.KEY_RT);
			}
			else if (k == KeyEvent.KEYCODE_C && event.isCtrlPressed())
			{
				keyPressed(3);
			}
			else if (event.getUnicodeChar() < 128 && event.getUnicodeChar() > 31)
			{
				keyPressed(event.getUnicodeChar());
			}
		}
		else if (event.getAction() == KeyEvent.ACTION_UP)
		{
			if (k == KeyEvent.KEYCODE_DPAD_UP)
			{
				unsetKeys(MakoConstants.KEY_UP);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_DOWN)
			{
				unsetKeys(MakoConstants.KEY_DN);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_LEFT)
			{
				unsetKeys(MakoConstants.KEY_LF);
			}
			else if (k == KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				unsetKeys(MakoConstants.KEY_RT);
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		heightMeasureSpec = MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) / 4) * 3, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh)
	{
		super.onSizeChanged(w, h, ow, oh);
		mScale = getResources().getDisplayMetrics().widthPixels / 320.0f;
	}

	@Override
	protected void onWindowVisibilityChanged(int aVisibility)
	{
		super.onWindowVisibilityChanged(aVisibility);
		if (aVisibility != View.VISIBLE)
		{
			mRendering = false;
		}
	}

	public void keyPressed(int charAt)
	{
		mVm.keyQueue.add(charAt);
	}

	public void keyReleased(int charAt)
	{
		// Nothing?
	}

	public MakoVM getMakoVm()
	{
		return mVm;
	}
}
