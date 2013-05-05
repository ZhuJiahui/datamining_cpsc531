package cpsc531.tc;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.math.linear.RealMatrix;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import cpsc531.tc.classifiers.KNNClassifier;
import cpsc531.tc.features.TestSetIdfIndexer;
import cpsc531.tc.features.VectorSpaceModel;
import cpsc531.tc.utils.DocumentCorpus;
import cpsc531.tc.utils.PrettyPrinter;

public class TextCategorization {

	private static VectorGenerator trainVG;
	// private Map<String,Reader> documents;
	private static DocumentCorpus trainDocuments;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		long estimatedTime;
		long stopTime;
		
		trainVG = new VectorGenerator();
		trainDocuments = new DocumentCorpus("src/test/resources/data/articles4");
		System.out.println("======Load document======");
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);
		
		trainVG.generateVector(trainDocuments.getDocuments());
		//documents = null;
		System.out.println("======stem document======");
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);

	    IdfIndexer indexer = new IdfIndexer();
	    RealMatrix idfMatrix = indexer.transform(trainVG.getMatrix());
	    
		System.out.println("======IDF======");
		estimatedTime = System.nanoTime() - stopTime;
		stopTime = System.nanoTime();
		System.out.println(estimatedTime);	    
	    
		 PrettyPrinter.prettyPrintPartsOfMatrix("Occurences", idfMatrix,
		 trainVG.getDocumentNames(), trainVG.getWords(), new
		 PrintWriter(System.out, true), 3000,3200, 0,20);
		// prettyPrintMap("Category map",documents.getCategoryMap(),new
		// PrintWriter(System.out, true));
		System.out.println("======Total documents======");
		System.out.println(trainVG.getWords().length);
		System.out.println(trainDocuments.getCorpusSize());

		//Process test data set
		System.out.println("======Process test data set======");
		stopTime = System.nanoTime();
		VectorGenerator testVG = new VectorGenerator();
		DocumentCorpus testDocuments = new DocumentCorpus("src/test/resources/data/test2");
		

		
		testVG.generateVector(testDocuments.getDocuments());
		//documents = null;
		TestSetIdfIndexer testIndexer = new TestSetIdfIndexer(testVG.getWordList(),trainVG.getWordList(),indexer.getDFRawCounts(),trainVG.getDocumentNames().length);
	    
		testIndexer.transform(testVG.getMatrix());
		RealMatrix testIdfMatrix = testIndexer.getTransformedMatrix();

		PrettyPrinter.prettyPrintPartsOfMatrix("test doc idf", testIdfMatrix,
				 testVG.getDocumentNames(), trainVG.getWords(), new
				 PrintWriter(System.out, true), 1000,1200, 0,20);
		 
		 System.out.println("======KNN Classifier======");
		 VectorSpaceModel vsm = new VectorSpaceModel(trainVG, trainDocuments.getDocCateMap());
		 KNNClassifier knn = new KNNClassifier(vsm);
		 int K = 20;
		 String cate = null;
		 for(int i = 0; i < testIdfMatrix.getColumnDimension(); i++){
			 cate = knn.classify(testIdfMatrix.getColumn(i), K);
			 System.out.printf("doc:%s, category:%s%n", testVG.getDocumentName(i), cate);
		 }
		
		System.out.println("======Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		System.out.println(estimatedTime);

	}

}
