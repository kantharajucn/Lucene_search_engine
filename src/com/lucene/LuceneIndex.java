package com.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

public class LuceneIndex 
{
	/*Class variables */
	private IndexWriter indexWriter;
	
	/*Constructor*/
	public LuceneIndex(String indexDir) throws IOException
	{
		Directory dir = FSDirectory.open(new File(indexDir));
		Analyzer analyze = new StandardAnalyzer(Version.LUCENE_47);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_47,analyze);
		indexWriter = new IndexWriter(dir,conf);
		indexWriter.deleteAll();
	}
	
	/*Parsing html file */
	public String parseHTML(File file,String type) throws IOException{
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(file, "UTF-8");
		String htmlContent = htmlDoc.select(type).toString();
		return htmlContent;
	}
	
	/*method to get files from folder*/
	private Document getFile(File file) throws IOException
	{
		Document doc = new Document();
		Field filename,filepath,lastmodifiedDate,title = null,filecontent;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String modifiedDate = sdf.format(file.lastModified());
		filename = new Field(LuceneConstants.FILE_NAME,file.getName(),TextField.TYPE_STORED);
		filepath = new Field(LuceneConstants.FILE_PATH,file.getParent(),TextField.TYPE_STORED);
		lastmodifiedDate = new Field(LuceneConstants.FILE_LAST_MODIFIED,modifiedDate,TextField.TYPE_STORED);
		if(file.getName().toLowerCase().endsWith("html")){
			String htmltitle = Porterstemmer.applyPorterStemmer(parseHTML(file,"title"));
			String body = Porterstemmer.applyPorterStemmer(parseHTML(file,"body"));
			title = new Field(LuceneConstants.TITLE,htmltitle,TextField.TYPE_STORED);
			filecontent = new Field(LuceneConstants.CONTENTS,body,TextField.TYPE_STORED);
			doc.add(filename);
		    doc.add(filepath);
		    doc.add(lastmodifiedDate);
		    doc.add(title);
			doc.add(filecontent);
			return doc;
			
		}
		else{
			String contents = Porterstemmer.applyPorterStemmer(file);
			filecontent = new Field(LuceneConstants.CONTENTS,contents,TextField.TYPE_STORED);
			/*add the above indexes to the Document object*/
			doc.add(filename);
			doc.add(filepath);
			doc.add(lastmodifiedDate);
			doc.add(filecontent);
			return doc;
		}
	}

	/*Method for closing the index writer*/
	public void close() throws CorruptIndexException, IOException
	{
		indexWriter.close();
	}

	/*method for indexing*/
	private void FileIndex(File file)  throws IOException
	{
		Document doc = getFile(file);
		System.out.println(file.getCanonicalPath());
		indexWriter.addDocument(doc);
	}
	
	public int doIndex(String dirpath, FileFilter filter) throws IOException
	{
		
		File[] files = new File(dirpath).listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				doIndex(file.getAbsolutePath(),filter);
			}
			else if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead())
			{
				FileIndex(file);
			}
		}
		return indexWriter.numDocs();
	}
}
