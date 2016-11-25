package com.lucene;

import java.io.File;
import java.io.FileFilter;

/*method and class for filtering only text and html files*/
public class Filefilter implements FileFilter
{
	public boolean accept(File path)
	{
		if((path.getName().toLowerCase().endsWith(".txt")) || (path.getName().toLowerCase().endsWith(".html")))
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
}
