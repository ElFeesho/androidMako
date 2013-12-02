package com.rtg.makovm.utils;

import java.io.File;

import android.os.Environment;

public class MakoDirectories
{
	public final static File MAKO_DIRECTORY = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Mako");
	public final static File MAKO_LIBS_DIRECTORY = new File(MAKO_DIRECTORY.getAbsolutePath()+File.separator+"Libs");
}
