package com.lucene;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Porterstemmer
{
	public static String applyPorterStemmer(File input) throws IOException 
	{
		PorterStemmer ps = new PorterStemmer();
		String output = "";
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = "";
		while((line = br.readLine()) != null)
		{
			String[] words = line.split(" ");
			for(int i = 0; i< words.length; i++)
			{
				ps.setCurrent(words[i]);
		        ps.stem();
		        output = output + " "+ ps.getCurrent();
			}
			output = output + "\n";
		}
		br.close();
		return output;
		
	}
	public static String applyPorterStemmer(String query) throws IOException 
	{
		PorterStemmer ps = new PorterStemmer();
		String output = "";
		String words[] = query.split(" ");
		for(int i=0; i<words.length; i++){
				ps.setCurrent(words[i]);
		        ps.stem();
		        output = output + " "+ ps.getCurrent();
		}
		return output;
		
	}
}