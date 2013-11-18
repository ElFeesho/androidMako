package com.rtg.makovm.async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class LibraryInflationTask extends AsyncTask<Void, Void, Void>
{
	private final Context mContext;

	public LibraryInflationTask(Context aContext)
	{
		mContext = aContext;
	}

	@Override
	protected Void doInBackground(Void... aParams)
	{
		try
		{
			ZipInputStream zIn = new ZipInputStream(mContext.getAssets().open("lib.zip"));
			ZipEntry entry;
			while ((entry = zIn.getNextEntry()) != null)
			{
				extract(zIn, entry);
			}
			zIn.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void extract(ZipInputStream aZIn, ZipEntry aEntry)
	{
		String outputDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Mako/Libs/";
		if(aEntry.isDirectory())
		{
			new File(outputDir+aEntry.getName()).mkdirs();
		}
		else
		{
			byte[] buffer = new byte[4096];
			try
			{
				FileOutputStream fout = new FileOutputStream(outputDir+aEntry.getName());
				int read = 0;
				while((read = aZIn.read(buffer, 0, 4096))!=-1)
				{
					fout.write(buffer, 0, read);
				}
				fout.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
