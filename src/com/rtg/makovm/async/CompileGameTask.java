package com.rtg.makovm.async;

import java.io.File;

import android.os.AsyncTask;

import com.rtg.makovm.Maker;

public class CompileGameTask extends AsyncTask<File, Void, Boolean>
{
	private final File mOutputDir;

	public CompileGameTask(File aOutputDirectory)
	{
		mOutputDir = aOutputDirectory;
	}

	@Override
	protected Boolean doInBackground(File... aParams)
	{
		File targetFile = aParams[0];
		if(!targetFile.exists())
		{
			return false;
		}

		return Maker.compile(targetFile.getAbsolutePath(), getOutputFilePath(targetFile));
	}

	private String getOutputFilePath(File targetFile)
	{
		return mOutputDir.getPath()+File.separator+trimFileExtension(targetFile);
	}

	private String trimFileExtension(File targetFile)
	{
		return targetFile.getName().substring(0, targetFile.getName().lastIndexOf("."));
	}

}
