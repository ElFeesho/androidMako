package com.rtg.makovm;

import java.io.File;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rtg.makovm.utils.GameZipExtractor;

public class CompileActivity extends Activity
{
	private File mDir = new File("/sdcard");

	private class FileAdapter extends BaseAdapter
	{

		private File[] mCurrentDir = new File[] {};

		public void setCurrentDir(File aDir)
		{
			mDir = aDir;
			mCurrentDir = mDir.listFiles();
			notifyDataSetChanged();
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			if(arg1 == null)
			{
				arg1 = LayoutInflater.from(arg2.getContext()).inflate(android.R.layout.simple_list_item_1, null);
			}
			File file = getItem(arg0);
			((TextView)arg1).setText(file.getName() +(file.isDirectory()?"/":" "+file.length()+ " bytes"));

			return arg1;
		}

		@Override
		public long getItemId(int arg0)
		{
			return 0;
		}

		@Override
		public File getItem(int arg0)
		{
			return arg0 == 0 ? new File(mDir, "..") : mCurrentDir[arg0-1];
		}

		@Override
		public int getCount()
		{
			return mCurrentDir.length+1;
		}
	};

	private final FileAdapter mFileAdapter = new FileAdapter();

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);
		setContentView(R.layout.rom_chooser);
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(mFileAdapter);
		mFileAdapter.setCurrentDir(mDir);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				File selectedFile = mFileAdapter.getItem(arg2);
				if(selectedFile.isDirectory())
				{
					mFileAdapter.setCurrentDir(selectedFile);
				}
				else if(selectedFile.getName().endsWith(".fs"))
				{
					new AsyncTask<File, Void, Void>()
					{
						@Override
						protected Void doInBackground(File... p1)
						{
							Maker.compile(p1[0].getAbsolutePath(), "/sdcard/Mako/"+p1[0].getName().substring(0, p1[0].getName().lastIndexOf(".")));
							return null;
						}

						@Override
						protected void onPostExecute(Void res)
						{
							Toast.makeText(CompileActivity.this, "Finished compiling", Toast.LENGTH_LONG).show();
						}

					}.execute(selectedFile);
				}
				else if(selectedFile.getName().endsWith(".zip"))
				{
					new AsyncTask<File, Void, Void>()
					{
						@Override
						protected Void doInBackground(File... p1)
						{
							GameZipExtractor gameZipExtractor = new GameZipExtractor(p1[0].getAbsolutePath());
							String gameEntryPoint = gameZipExtractor.findGameEntryPoint();
							if(gameEntryPoint!=null)
							{
								Maker.compile(gameEntryPoint, Environment.getExternalStorageDirectory()+File.separator+"Mako"+File.separator+p1[0].getName().substring(0, p1[0].getName().lastIndexOf(".")));
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void res)
						{
							Toast.makeText(CompileActivity.this, "Finished compiling", Toast.LENGTH_LONG).show();
						}

					}.execute(selectedFile);
				}
				else
				{
					Toast.makeText(CompileActivity.this, "Only .zip & .fs files are supported", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
