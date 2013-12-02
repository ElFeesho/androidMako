package com.rtg.makovm.ui;

import java.io.File;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter
{
	/** The directory that the user is currently browsing */
	private File mDir = new File(Environment.getExternalStorageDirectory().getPath());

	/** A list of the files in the current directory (mDir) */
	private File[] mCurrentDir = new File[] {};

	public void setCurrentDir(File aDir)
	{
		mDir = aDir;
		mCurrentDir = mDir.listFiles();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
		}
		File file = getItem(pos);
		((TextView)convertView).setText(file.getName() +(file.isDirectory()?"/":" "+file.length()+ " bytes"));

		return convertView;
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