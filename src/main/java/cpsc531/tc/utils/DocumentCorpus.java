package cpsc531.tc.utils;

import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.lang.StringUtils;

/**
 * A simple in-memory document corpus, load all the documents as string into 
 * memory, accompany with their parent directory as their category
 * @author Shaofen Chen 
 * @version 
 */

public class DocumentCorpus {
	
	private Map<String, Reader> documents;//
	private Map<String, String> docCategoryMap; //<document, category>
	private SortedMap<String, int[]> cateDocListMap; //<category, document index list>

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
		cateDocListMap = new TreeMap<String, int[]>();
	    for(String entry : entries){
	    	File article = new File(dir_articles,entry);
	    	if(article.isDirectory()){
	    		int docCountPre = documents.size(); 
	    		int fileCount = recursiveLoad(article, article.getName() );
	    		if(fileCount > 0){
	    			int[] docIndexList = new int[fileCount];
	    			for(int i = 0; i < fileCount; i++)
	    				docIndexList[i] = docCountPre + i;
	    			cateDocListMap.put(article.getName(), docIndexList);
	    		}
	    	}
	    	if(article.isFile()){
	    		//documents.put(entry, new StringReader(FileUtils.readFileToString(article)));
	    	}
	    }
	}
	
	public DocumentCorpus(String rootDir, double startPer, double endPer) throws IOException{
	    File dir_articles = new File(rootDir);
	    String entries[] = dir_articles.list();
		documents = new LinkedHashMap<String,Reader>();
		docCategoryMap = new HashedMap<String, String>();
		cateDocListMap = new TreeMap<String, int[]>();
	    for(String entry : entries){
	    	File article = new File(dir_articles,entry);
	    	if(article.isDirectory()){
	    		int docCountPre = documents.size(); 
	    		int fileCount = loadFiles(article, article.getName(),  startPer, endPer);
	    		if(fileCount > 0){
	    			int[] docIndexList = new int[fileCount];
	    			for(int i = 0; i < fileCount; i++)
	    				docIndexList[i] = docCountPre + i;
	    			cateDocListMap.put(article.getName(), docIndexList);
	    		}
	    	}
	    }
	}
	
	public Map<String, Reader> getDocuments(){
		return documents;
	}
	
	public Map<String, int[]> getCateDocListMap(){
		return cateDocListMap;
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

	private int loadFiles(File dir, String category, double startPer, double endPer) throws IOException{
	    //String entries[] = dir.list();
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		File[] entries = dir.listFiles(directoryFilter);
	    int fileCount = 0;
	    int startIndex, endIndex;
	    //System.out.printf("start:%f, end:%f%n", startPer, endPer);
	    startIndex = (int) Math.ceil(entries.length * startPer);
	    endIndex = (int) Math.ceil(entries.length * endPer);
	    //System.out.printf("length:%d", entries.length);
	    //System.out.printf("start:%d, end:%d%n", startIndex, endIndex);
	    for(int i = startIndex; i < endIndex; i++){
	    	//File article = new File(dir,entry);
    		fileCount ++;
    		String docName = entries[i].getName() +"_" + category;
    		docCategoryMap.put(docName, category);
    		documents.put(docName, new StringReader(FileUtils.readFileToString(entries[i])));
	    	
	    }
	    return fileCount;
	}
	
	/**
	 * Load all the files under a folder recursively, and associate the files with the category
	 * in the docCategoryMap
	 * @param dir The directory
	 * @param category All the files under dir folder will be marked as this category
	 * @throws IOException 
	 */
	private int recursiveLoad(File dir, String category) throws IOException{
	    String entries[] = dir.list();
	    int fileCount = 0;
	    for(String entry : entries){
	    	File article = new File(dir,entry);
	    	if(article.isDirectory()){
	    		fileCount += recursiveLoad(article, category);
	    	}
	    	if(article.isFile()){
	    		fileCount ++;
	    		String docName = entry +"_" + category;
	    		docCategoryMap.put(docName, category);
	    		documents.put(docName, new StringReader(FileUtils.readFileToString(article)));
	    	}
	    }
	    return fileCount;
	}
	
	/**
	 * Return a list of categories  sorted  in ascendent order.
	 * @return
	 */
	public ArrayList<String> getCategoreisList(){
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(cateDocListMap.keySet());
		return list;
	}
//	public ArrayList<String> getCategoreisList(){
//		SortedSet<String> cateNames = new TreeSet<String>();
//		Set<Map.Entry<String, String>> cateSet = docCategoryMap.entrySet();
//		for(Iterator<Map.Entry<String, String>> it = cateSet.iterator(); it.hasNext();){
//			Map.Entry<String, String> entry = it.next();
//			cateNames.add(entry.getValue());
//		}
//		ArrayList<String> list = new ArrayList<String>();
//		list.addAll(cateNames);
//		return list;
//	}
}
