package com.lucene;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

import java.io.File;
import java.io.IOException;

public class LuceneSearch
{
	private QueryParser qp;
	private IndexSearcher is;
	private IndexReader dir;
	private Query q;

	public LuceneSearch(String indexDir) throws IOException
	{
		/*directory creation and initialization to store index*/
		IndexReader dir = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
		is = new IndexSearcher(dir);

	}

	/*return the best documents*/
	public TopDocs search(String searchQuery) throws IOException,ParseException
	{
		String[] type = searchQuery.split(":");
		if((type.length == 2) && (type[0].toString() == "title")){
			String query = Porterstemmer.applyPorterStemmer(type[1]);
			Analyzer analyze = new StandardAnalyzer(Version.LUCENE_47);
			qp = new QueryParser(Version.LUCENE_47,LuceneConstants.TITLE,analyze);
			q = qp.parse(query);
			return is.search(q,LuceneConstants.MAX_SEARCH);
		}
		else{
			String query = Porterstemmer.applyPorterStemmer(searchQuery);
			Analyzer analyze = new StandardAnalyzer(Version.LUCENE_47);
			qp = new QueryParser(Version.LUCENE_47,LuceneConstants.CONTENTS,analyze);
			q = qp.parse(query);
			return is.search(q,LuceneConstants.MAX_SEARCH);
		}
	}

	public Document getDoc(ScoreDoc score) throws CorruptIndexException,IOException
	{
	   return is.doc(score.doc);	
	}
	
	/*close searcher*/
	public void close() throws IOException
	{
		dir.close();
	}
}