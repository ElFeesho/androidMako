package com.rtg.makovm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Grep
{
	public static File grep(String aPin, File[] aHayStack)
	{
		for(int i = 0; i<aHayStack.length; i++)
		{
			if(grep(aPin, aHayStack[i]))
			{
				return aHayStack[i];
			}
		}

		return null;
	}

	private static boolean grep(String aPin, File aFile)
	{
		// We use the attack of reading by lines, rather than plain old chunks
		// of data.
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aFile)));
			while(br.ready())
			{
				String aLine = br.readLine();
				if(aLine.length()<aPin.length())
				{
					continue;
				}

				if(aLine.substring(0, aPin.length()-1).compareTo(aPin) == 0)
				{
					// We have a potential match!
					// This is more specific to an implementation we need ... i.e.
					// We only look for ': main' right now. But we need to make sure the next
					// character is a carriage return, or a space.
					char finalCharacter = aLine.substring(aPin.length()-1).charAt(0);
					if(finalCharacter == ' ' || finalCharacter == '\r' || finalCharacter == '\n')
					{
						return true;
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
