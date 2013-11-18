package com.rtg.makovm.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rtg.makovm.R;

public class RomListAdapter extends BaseAdapter
{
	private static Comparator<File> sDateComparator = new Comparator<File>()
	{

		@Override
		public int compare(File lhs, File rhs)
		{
			return (rhs.lastModified() < lhs.lastModified()) ? -1 : 1;
		}
	};

	private static class ViewHolder
	{
		TextView name;
		TextView size;
		TextView date;
	}

	private final List<File> mRoms = new ArrayList<File>();

	@Override
	public int getCount()
	{
		return mRoms.size();
	}

	@Override
	public File getItem(int pos)
	{
		return mRoms.get(pos);
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder vh = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_rom, null);
			vh = new ViewHolder();
			vh.name = (TextView) convertView.findViewById(R.id.RomListItem_Name);
			vh.size = (TextView) convertView.findViewById(R.id.RomListItem_Size);
			vh.date = (TextView) convertView.findViewById(R.id.RomListItem_Date);
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}

		File rom = getItem(position);
		vh.name.setText(rom.getName());
		vh.size.setText(String.format("%.2f Kbytes", rom.length() / 1024.0f));
		vh.date.setText(rom.lastModified() + "");

		return convertView;
	}

	public void addRoms(File[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			mRoms.add(values[i]);
		}

		// Order by date
		Collections.sort(mRoms, sDateComparator);

		// Tell the ListView to update
		notifyDataSetChanged();

	}

	public void clear()
	{
		mRoms.clear();
	}
}