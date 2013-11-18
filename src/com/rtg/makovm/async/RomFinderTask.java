package com.rtg.makovm.async;

import java.io.File;
import java.io.FilenameFilter;

import android.os.AsyncTask;

public abstract class RomFinderTask extends AsyncTask<String, File, Boolean>
{

	@Override
	protected Boolean doInBackground(String... params)
	{

		FilenameFilter romFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				return filename.endsWith(".rom") || filename.endsWith(".ROM");
			}
		};

		boolean found = false;
		for (int i = 0; i < params.length; i++)
		{
			File searchDir = new File(params[i]);

			File[] listFiles = searchDir.listFiles(romFilter);
			if (listFiles.length > 0)
			{
				found = true;
			}

			publishProgress(listFiles);
		}
		return found;
	}
}