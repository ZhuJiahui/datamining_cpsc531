package cpsc531.tc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.RealMatrix;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import cpsc531.tc.features.TestSetIdfIndexer;
import cpsc531.tc.utils.DocumentCorpus;

public class TextCategorization {

	private static VectorGenerator vectorGenerator;
	// private Map<String,Reader> documents;
	private static DocumentCorpus documents;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		long estimatedTime;
		long stopTime;
		
		vectorGenerator = new VectorGenerator();
		documents = new DocumentCorpus("src/test/resources/data/articles3");
		System.out.println("======Load document======");
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);
		
		vectorGenerator.generateVector(documents.getDocuments());
		//documents = null;
		System.out.println("======stem document======");
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);

	    IdfIndexer indexer = new IdfIndexer();
	    RealMatrix idfMatrix = indexer.transform(vectorGenerator.getMatrix());
	    
		System.out.println("======IDF======");
		estimatedTime = System.nanoTime() - stopTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);	    
	    
		 prettyPrintPartsOfMatrix("Occurences", idfMatrix,
		 vectorGenerator.getDocumentNames(), vectorGenerator.getWords(), new
		 PrintWriter(System.out, true), 3000,3200, 0,20);
		// prettyPrintMap("Category map",documents.getCategoryMap(),new
		// PrintWriter(System.out, true));
		System.out.println("======Total documents======");
		System.out.println(vectorGenerator.getWords().length);
		System.out.println(documents.getCorpusSize());
		System.out.println(documents.getCategoryMap().size());
		System.out.println(vectorGenerator.getMatrix().getColumnDimension());
		System.out.println(vectorGenerator.getMatrix().getRowDimension());
		
		//Process test data set
		System.out.println("======Process test data set======");
		stopTime = System.nanoTime();
		VectorGenerator testVG = new VectorGenerator();
		DocumentCorpus testDC = new DocumentCorpus("src/test/resources/data/test");
		

		
		testVG.generateVector(testDC.getDocuments());
		//documents = null;
		TestSetIdfIndexer testIndexer = new TestSetIdfIndexer(testVG.getWordList(),vectorGenerator.getWordList(),indexer.getDFRawCounts(),vectorGenerator.getDocumentNames().length);
	    
		testIndexer.transform(testVG.getMatrix());
		RealMatrix testIdfMatrix = testIndexer.getTransformedMatrix();

		 prettyPrintPartsOfMatrix("test doc idf", testIdfMatrix,
				 testVG.getDocumentNames(), vectorGenerator.getWords(), new
				 PrintWriter(System.out, true), 1000,1200, 0,20);
	    

		
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		System.out.println(estimatedTime);

	}

	private static void prettyPrintMatrix(String legend, RealMatrix matrix,
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
	
	private static void prettyPrintPartsOfMatrix(String legend, RealMatrix matrix,
			String[] documentNames, String[] words, PrintWriter writer, int startRow, int endRow, int startCol,int endCol) {
		writer.printf("=== %s ===%n", legend);
		writer.printf("%15s", " ");
		for (int i = 0; i < endCol  && i < documentNames.length ; i++) {
			if(i > startCol)
				writer.printf("%8s", documentNames[i]);
		}
		writer.println();
		for (int i = 0;  i < words.length && i < endRow; i++) {
			if(i > startRow){
				writer.printf("%15s", words[i]);
				for (int j = 0; j < endCol  && j < documentNames.length; j++) {
					if(j > startCol)
						writer.printf("%8.4f", matrix.getEntry(i, j));
				}
				writer.println();
			}
		}
		writer.flush();
	}

	private static void prettyPrintMap(String legend, Map<String,String> m, PrintWriter writer) {
		writer.printf("=== %s ===%n", legend);
		Set<Map.Entry<String, String>> setView = m.entrySet();
		for (Iterator<Map.Entry<String, String>> it = setView.iterator(); it
				.hasNext();) {
			Map.Entry<String, String> me = it.next();
			writer.printf("Document:%s, Category:%s%n", me.getKey(),
					me.getValue());
		}

	}

}
