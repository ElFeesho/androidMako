package com.rtg.makovm;

import java.io.File;

import android.os.Environment;

public class AndroidLangLibraryProvider implements LibraryProvider
{
	@Override
	public File getLibraryDirectory()
	{
		return new File(Environment.getExternalStorageDirectory(), "Mako/Libs");
	}
}
