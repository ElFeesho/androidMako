package com.rtg.makovm;

import java.io.File;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.rtg.makovm.async.LibraryInflationTask;
import com.rtg.makovm.async.RomFinderTask;
import com.rtg.makovm.ui.RomListAdapter;

public class RomChooserActivity extends ListActivity
{
	private final static String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	private final static String STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	private final static String MAKO_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Mako";
	private static final String MAKO_LIB_DIR = MAKO_DIR + File.separator + "Libs";

	private TextView mEmpty = null;

	private final RomListAdapter mRomAdapter = new RomListAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rom_chooser);
		mEmpty = (TextView) findViewById(android.R.id.empty);

		getListView().setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> listView, View item, int pos, long id)
			{
				startActivity(new Intent(RomChooserActivity.this, Makoid.class).putExtra(Makoid.EXTRA_ROM_FILE, mRomAdapter.getItem(pos).getAbsolutePath()));
				finish();
			}
		});

		// ListActivity takes care of everything else
		setListAdapter(mRomAdapter);
		initialiseDirectories();

		findRoms();
	}

	private void findRoms()
	{
		mRomAdapter.clear();
		new RomFinderTask()
		{
			@Override
			protected void onProgressUpdate(File... values)
			{
				mRomAdapter.addRoms(values);
			}

			@Override
			protected void onPostExecute(Boolean found)
			{
				if (!found)
				{
					mEmpty.setText("Please download or compile roms to play.");
				}
				else
				{
					mRomAdapter.notifyDataSetChanged();
				}
			}
		}.execute(MAKO_DIR, STORAGE_DIR, DOWNLOAD_DIR);
	}

	private void initialiseDirectories()
	{
		// Because we are kind, we'll create the MAKO_DIR for the user
		new File(MAKO_DIR).mkdir();
		new File(MAKO_LIB_DIR).mkdir();

		if(!new File(MAKO_LIB_DIR+File.separator+"lib").exists())
		{
			extractLibrariesFromAssets();
		}
	}

	private void extractLibrariesFromAssets()
	{
		final ProgressDialog pdlg = ProgressDialog.show(this, null, "Uncompressing libs");
		new LibraryInflationTask(this)
		{
			@Override
			protected void onPreExecute()
			{
				//...
			}

			@Override
			protected void onPostExecute(Void result)
			{
				pdlg.dismiss();
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.rom_chooser, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.compile)
		{
			startActivity(new Intent(this, CompileActivity.class));
		}
		else if(item.getItemId() == R.id.refresh)
		{
			findRoms();
		}
		return super.onOptionsItemSelected(item);
	}
}
