package net.sf.jtmt.indexers.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.LsiIndexer;
import net.sf.jtmt.indexers.matrix.TfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.DirectoryWalker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import cpsc531.tc.utils.DocumentCorpus;

/**
 * Test class for generating term/document matrices using various methods.
 * @author Sujit Pal
 * @version $Revision: 55 $
 */
public class IndexersTest {

  private VectorGenerator vectorGenerator;
  //private Map<String,Reader> documents;
  private DocumentCorpus documents;
  
  @Before
  public void setUp() throws Exception {
    vectorGenerator = new VectorGenerator();
//    vectorGenerator.setDataSource(new DriverManagerDataSource(
//      "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/tmdb", "root", "orange"));
    
//    documents = new LinkedHashMap<String,Reader>();
//    File dir_articles = new File("src/test/resources/data/articles");
//    String entries[] = dir_articles.list();
//    for(String entry : entries){
//    	File article = new File(dir_articles,entry);
//    	if(article.isFile()){
//    		documents.put(entry, new StringReader(FileUtils.readFileToString(article)));
//    	}
//    }
    
    documents = new DocumentCorpus("src/test/resources/data/articles");
    
    
    
//    BufferedReader reader = new BufferedReader(
//      new FileReader("src/test/resources/data/indexing_sample_data.txt"));
//    String line = null;
//    while ((line = reader.readLine()) != null) {
//      String[] docTitleParts = StringUtils.split(line, ";");
//      documents.put(docTitleParts[0], new StringReader(docTitleParts[1]));
//    }
    
    
  }
  
  @Test
  public void testVectorGeneration() throws Exception {
	  
    vectorGenerator.generateVector(documents.getDocuments());
    prettyPrintMatrix("Occurences", vectorGenerator.getMatrix(), vectorGenerator.getDocumentNames(), vectorGenerator.getWords(), new PrintWriter(System.out, true));
    prettyPrintMap("Category map",documents.getDocCateMap(),new PrintWriter(System.out, true));
  }
//  
  @Test
  public void testTfIndexer() throws Exception {
    vectorGenerator.generateVector(documents.getDocuments());
    TfIndexer indexer = new TfIndexer();
    RealMatrix tfMatrix = indexer.transform(vectorGenerator.getMatrix());
    prettyPrintMatrix("Term Frequency", tfMatrix, 
      vectorGenerator.getDocumentNames(), vectorGenerator.getWords(), 
      new PrintWriter(System.out, true));
  }
  
  @Test
  public void testIdfIndexer() throws Exception {
    vectorGenerator.generateVector(documents.getDocuments());
    IdfIndexer indexer = new IdfIndexer();
    RealMatrix idfMatrix = indexer.transform(vectorGenerator.getMatrix());
    prettyPrintMatrix("Inverse Document Frequency", idfMatrix,
      vectorGenerator.getDocumentNames(), vectorGenerator.getWords(),
      new PrintWriter(System.out, true));
  }
  
  @Test
  public void testLsiIndexer() throws Exception {
    vectorGenerator.generateVector(documents.getDocuments());
    LsiIndexer indexer = new LsiIndexer();
    RealMatrix lsiMatrix = indexer.transform(vectorGenerator.getMatrix());
    prettyPrintMatrix("Latent Semantic (LSI)", lsiMatrix,
      vectorGenerator.getDocumentNames(), vectorGenerator.getWords(),
      new PrintWriter(System.out, true));
  }
  
  private void prettyPrintMatrix(String legend, RealMatrix matrix, 
      String[] documentNames, String[] words, PrintWriter writer) {
    writer.printf("=== %s ===%n", legend);
    writer.printf("%15s", " ");
    for (int i = 0; i < documentNames.length; i++) {
      writer.printf("%8s", documentNames[i]);
    }
    writer.println();
    for (int i = 0; i < words.length; i++) {
      writer.printf("%15s", words[i]);
      for (int j = 0; j < documentNames.length; j++) {
        writer.printf("%8.4f", matrix.getEntry(i, j));
      }
      writer.println();
    }
    writer.flush();
  }
  
  private void prettyPrintMap(String legend, Map m, PrintWriter writer){
	  writer.printf("=== %s ===%n", legend);
	  Set<Map.Entry<String, String>> setView = m.entrySet();
		for(Iterator<Map.Entry<String,String>> it = setView.iterator(); it.hasNext();){
			Map.Entry<String, String> me = it.next();
			writer.printf("Document:%s, Category:%s%n", me.getKey(), me.getValue());
		}
	  
  }
}
