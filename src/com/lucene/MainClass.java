package com.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.util.logging.*;

public class MainClass 
{
	private String dataDir;
	private String indexDir;
	private int IndexNum;
	
	public MainClass(String indexDir){
		this.indexDir = indexDir;
	}
	public void setDataDir(String dataDir){
		this.dataDir = dataDir;
	}
	
	public void Index() throws IOException
	{
		LuceneIndex index = new LuceneIndex(indexDir);
		long start = System.currentTimeMillis();
		IndexNum = index.doIndex(dataDir, new Filefilter());
		long end = System.currentTimeMillis();
		System.out.println(IndexNum+" Indexing files,time taken: "+(end-start)+" ms");	
		index.close();
	}
	public void search(String searchQuery) throws IOException, ParseException
	{
		int rank = 1;
		LuceneSearch si = new LuceneSearch(indexDir);
		long startTime = System.currentTimeMillis();
	    TopDocs hits = si.search(searchQuery);
	    long endTime = System.currentTimeMillis();
	    System.out.println(hits.totalHits + " documents found. Time taken:" + (endTime - startTime) +" ms");
	    for(ScoreDoc scoreDoc : hits.scoreDocs) 
	    {
	    	
	    	Document doc = si.getDoc(scoreDoc);
	    	System.out.println("---------------------------------------");
	    	System.out.println("File: "+ doc.get(LuceneConstants.FILE_NAME));
	    	System.out.println("Title: "+ doc.get(LuceneConstants.TITLE));
	    	System.out.println("Rank: "+ rank);
	    	System.out.println("Score: "+ scoreDoc.score);
	    	System.out.println("Path: "+ doc.get(LuceneConstants.FILE_PATH));
	    	System.out.println("Last Modified: "+ doc.get(LuceneConstants.FILE_LAST_MODIFIED));
	    	System.out.println("---------------------------------------");
	    	//System.out.println("Contents: "+ doc.get(LuceneConstants.CONTENTS));
	    	rank++;
	    }
	}	
	
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		final Logger LOGGER = Logger.getLogger( MainClass.class.getName() );
		int choice;
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the directory path for index creation");
		String inDir = in.next();
		MainClass main = new MainClass(inDir);
		for(;;){
			System.out.println("Enter your option");
			System.out.println("1:Indexing  2: Searching");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			choice =Integer.parseInt(br.readLine());
			switch(choice){
				case 1: System.out.println("Enter the directory path of files");
						String dataDir = in.next();
						main.setDataDir(dataDir);
						try{
						main.Index();
						}catch(IOException e){
							LOGGER.log(Level.FINE,e.toString(),e);
						}
						break;
						 
				case 2: System.out.println("Enether the query to search");
						String query = in.next();
						try{
							main.search(query);
							
						}catch(IOException | ParseException e){
							LOGGER.log(Level.FINE,e.toString(),e);
						}
						break;
				default: System.out.println("Choose any of the above options");
						System.exit(0);
			}
		}

	}
}
