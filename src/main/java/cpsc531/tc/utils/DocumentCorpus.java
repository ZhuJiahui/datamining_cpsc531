package cpsc531.tc.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.RealMatrix;

/**
 * A simple in-memory document corpus, load all the documents as string into 
 * memory, accompany with their parent directory as their category
 * @author Shaofen Chen 
 * @version 
 */

public class DocumentCorpus {
	
	private Map<String, Reader> documents;
	private Map<String, String> docCategoryMap;

	/**
	 * @param rootDir The path of the top directory. All the documents under its 
	 * direct child folders will be loaded recursively.Direct child folders will be 
	 * treated as categories with the folder name as the category names. 
	 * @throws IOException
	 */
	public DocumentCorpus(String rootDir) throws IOException{
	    File dir_articles = new File(rootDir);
	    String entries[] = dir_articles.list();
		documents = new LinkedHashMap<String,Reader>();
		docCategoryMap = new HashedMap<String, String>();
	    for(String entry : entries){
	    	File article = new File(dir_articles,entry);
	    	if(article.isDirectory()){
	    		recursiveLoad(article, article.getName() );
	    	}
	    	if(article.isFile()){
	    		documents.put(entry, new StringReader(FileUtils.readFileToString(article)));
	    	}
	    }
	}
	
	public Map<String, Reader> getDocuments(){
		return documents;
	}
	
	public Map<String, String> getDocCateMap(){
		return docCategoryMap;
	}
	
	/**
	 * Return total number of documents in corpus
	 * @return Total number of documents in corpus
	 */
	public int getCorpusSize(){
		return documents.size();
	}

	
	/**
	 * Load all the files under a folder recursively, and associate the files with the category
	 * in the docCategoryMap
	 * @param dir The directory
	 * @param category All the files under dir folder will be marked as this category
	 * @throws IOException 
	 */
	private void recursiveLoad(File dir, String category) throws IOException{
	    String entries[] = dir.list();
	    for(String entry : entries){
	    	File article = new File(dir,entry);
	    	if(article.isDirectory()){
	    		recursiveLoad(article, category);
	    	}
	    	if(article.isFile()){
	    		String docName = entry +"_" + category;
	    		docCategoryMap.put(docName, category);
	    		documents.put(docName, new StringReader(FileUtils.readFileToString(article)));
	    	}
	    }
	}

	
}
