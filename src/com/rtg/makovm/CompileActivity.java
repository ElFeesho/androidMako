package com.rtg.makovm;

import java.io.File;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.rtg.makovm.async.CompileGameTask;
import com.rtg.makovm.async.UnzipSourceTask;
import com.rtg.makovm.ui.ErrorDialogFragment;
import com.rtg.makovm.ui.FileAdapter;
import com.rtg.makovm.ui.ProgressDialogFragment;
import com.rtg.makovm.utils.MakoDirectories;

public class CompileActivity extends FragmentActivity
{
	private final FileAdapter mFileAdapter = new FileAdapter();

	private final static String TAG_ERROR = "edlg";
	private final static String TAG_PROGRESS = "pdlg";

	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);
		setContentView(R.layout.rom_chooser);
		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(mFileAdapter);

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
				else if(selectedFile.getName().toLowerCase(Locale.getDefault()).endsWith(".fs"))
				{
					compileTargetFile(selectedFile);
				}
				else if(selectedFile.getName().toLowerCase(Locale.getDefault()).endsWith(".zip"))
				{
					showProgressDialog(null, "Unzipping "+selectedFile.getName());
					new UnzipSourceTask(){
						@Override
						protected void onPostExecute(File result)
						{
							dismissDialog(TAG_PROGRESS);
							compileTargetFile(result);
						}
					}.execute(selectedFile);
				}
				else
				{
					showErrorDialog("Unsupported Format", "Only .zip & .fs files are supported");
				}
			}
		});
	}

	private void compileTargetFile(File selectedFile)
	{
		showProgressDialog(null, "Compiling "+selectedFile.getName());
		new CompileGameTask(MakoDirectories.MAKO_DIRECTORY)
		{
			@Override
			protected void onPostExecute(Boolean res)
			{
				dismissDialog(TAG_PROGRESS);

				if(res)
				{
					Toast.makeText(CompileActivity.this, "Finished compiling", Toast.LENGTH_LONG).show();
				}
				else
				{
					showErrorDialog("Failed Compilation", "It was not possible to compile the selected game.");
				}
			}
		}
		.execute(selectedFile);
	}

	private void showErrorDialog(String aTitle, String aMessage)
	{
		dismissDialog(TAG_ERROR);
		ErrorDialogFragment.createErrorDialogFragment(aTitle, aMessage).show(getSupportFragmentManager(), TAG_ERROR);
	}

	private void showProgressDialog(String aTitle, String aMessage)
	{
		dismissDialog(TAG_PROGRESS);
		ProgressDialogFragment.newProgressDialog(aTitle, aMessage).show(getSupportFragmentManager(), TAG_PROGRESS);
	}

	private void dismissDialog(String tag)
	{
		if(getSupportFragmentManager().findFragmentByTag(tag)!=null)
		{
			((DialogFragment)getSupportFragmentManager().findFragmentByTag(tag)).dismiss();
		}
	}

}
