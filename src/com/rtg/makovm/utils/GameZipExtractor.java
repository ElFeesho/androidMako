package com.rtg.makovm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Environment;

public class GameZipExtractor
{
	private ZipExtractor mExtractor;
	private final File mOutput;

	public GameZipExtractor(String aGameZip)
	{
		mOutput = new File(Environment.getExternalStorageDirectory()+File.separator+"Mako"+File.separator+"Temp");
		mOutput.delete();
		mOutput.mkdirs();
		try
		{
			mExtractor = new ZipExtractor(new FileInputStream(aGameZip), mOutput.getAbsolutePath());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public String findGameEntryPoint()
	{
		if(mExtractor.extract())
		{
			// Game extracted successful (round of applause)
			// Now find the entry point filename
			return findGameEntryPoint(mOutput);
		}
		return null;
	}

	private String findGameEntryPoint(File aOutput)
	{
		File[] gameFiles = aOutput.listFiles();
		for(int i = 0; i<gameFiles.length; i++)
		{
			if(gameFiles[i].isDirectory())
			{
				String mainFile = findGameEntryPoint(gameFiles[i]);
				if(mainFile!=null)
				{
					return mainFile;
				}
			}
			else
			{
				File mainFile = Grep.grep(": main", gameFiles);
				if(mainFile!=null)
				{
					return mainFile.getAbsolutePath();
				}
			}
		}
		return null;
	}

	public void cleanup()
	{
		mOutput.delete();
	}
}
