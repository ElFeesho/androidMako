package com.rtg.makovm.async;

import java.io.File;

import android.os.AsyncTask;

import com.rtg.makovm.utils.GameZipExtractor;

public class UnzipSourceTask extends AsyncTask<File, Void, File>
{
	@Override
	protected File doInBackground(File... files)
	{
		GameZipExtractor gameZipExtractor = new GameZipExtractor(files[0].getAbsolutePath());
		String filePath = gameZipExtractor.findGameEntryPoint();

		if (filePath != null)
		{
			return new File(filePath);
		}

		return null;
	}

}
