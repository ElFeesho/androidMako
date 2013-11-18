package com.rtg.makovm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor
{
	private final InputStream mZipStream;
	private final File mOutputDirectory;

	public ZipExtractor(InputStream aZipStream, String aOutputDirectory) throws IOException
	{
		mZipStream = aZipStream;
		mOutputDirectory = new File(aOutputDirectory);
		mOutputDirectory.mkdir();
	}

	public boolean extract()
	{
		ZipInputStream zIn = new ZipInputStream(mZipStream);
		ZipEntry entry;
		try
		{
			while ((entry = zIn.getNextEntry()) != null)
			{
				extract(zIn, entry);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			try
			{
				zIn.close();
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
			}
			return false;
		}

		try
		{
			zIn.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void extract(ZipInputStream aZIn, ZipEntry aEntry)
	{
		if (aEntry.isDirectory())
		{
			new File(mOutputDirectory +File.separator+ aEntry.getName()).mkdirs();
		}
		else
		{
			byte[] buffer = new byte[4096];
			try
			{
				FileOutputStream fout = new FileOutputStream(mOutputDirectory + File.separator + aEntry.getName());
				int read = 0;
				while ((read = aZIn.read(buffer, 0, 4096)) != -1)
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
