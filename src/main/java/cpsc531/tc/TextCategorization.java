package cpsc531.tc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import cpsc531.tc.classifiers.KNNClassifier;
import cpsc531.tc.classifiers.NaiveBayesClassifier;
import cpsc531.tc.classifiers.TextClassifier;
import cpsc531.tc.features.TestSetBayesIndexer;
import cpsc531.tc.features.TestSetIdfIndexer;
import cpsc531.tc.features.VectorSpaceModel;
import cpsc531.tc.stemmer.IStemmer;
import cpsc531.tc.stemmer.WordnetDictStemmer;
import cpsc531.tc.utils.DocumentCorpus;
import cpsc531.tc.utils.PrettyPrinter;

/**
 * Main entrance of this Text Categorization program
 * 
 * @author shaofenchen
 * 
 */
public class TextCategorization {

	static long startTime;
	static long estimatedTime;
	static long stopTime;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		String trainTestFilesRootDir = "src/test/resources/data/articles2";
		String wordNetDictPath = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
		double startPercent = 0,endPercent = 0.1;
		
		
		for(int i = 1; i < 10; i++){
			start();
			runTest(trainTestFilesRootDir, wordNetDictPath, startPercent, endPercent * (double)i);
		}

	}

	private static void runTest(String trainTestFilesRootDir,
			String wordNetDictPath, double startPercent, double endPercent)
			throws Exception, IOException {
		DocumentCorpus trainDocuments, testDocuments;
		IStemmer stemmer = new WordnetDictStemmer(wordNetDictPath);
		
		VectorGenerator trainVG, testVG;
		trainVG = new VectorGenerator();
		testVG = new VectorGenerator();
		trainVG.setStemmer(stemmer);
		testVG.setStemmer(stemmer);
		trainDocuments = new DocumentCorpus(trainTestFilesRootDir, startPercent, endPercent);
		testDocuments = new DocumentCorpus(trainTestFilesRootDir, endPercent, endPercent+ 0.1D);
		
		benchMark("Load document");

		trainVG.generateVector(trainDocuments.getDocuments());
		testVG.generateVector(testDocuments.getDocuments());
		
		benchMark("Stem documents");

		IdfIndexer indexer = new IdfIndexer();
		RealMatrix idfMatrix = indexer.transform(trainVG.getMatrix());

		benchMark("IDF");

//		PrettyPrinter.prettyPrintPartsOfMatrix("Occurences", idfMatrix,
//				trainVG.getDocumentNames(), trainVG.getWords(),
//				new PrintWriter(System.out, true), 3000, 3200, 0, 20);
		// prettyPrintMap("Category map",documents.getCategoryMap(),new PrintWriter(System.out, true));
		System.out.println("======Total documents======");
		System.out.println(trainVG.getWords().length);
		System.out.println(trainDocuments.getCorpusSize());

		
		System.out.println("======KNN Classifier======");		
		// documents = null;
		TestSetIdfIndexer testIndexer = new TestSetIdfIndexer(
				testVG.getWordList(), trainVG.getWordList(),
				indexer.getDFRawCounts(), trainVG.getDocumentNames().length);
		RealMatrix testIdfMatrix= testIndexer.transform(testVG.getMatrix());
		
//		PrettyPrinter.prettyPrintPartsOfMatrix("test doc idf", testIdfMatrix,
//				testVG.getDocumentNames(), trainVG.getWords(), new PrintWriter(
//						System.out, true), 1000, 1200, 0, 20);

		
		VectorSpaceModel vsm = new VectorSpaceModel(trainVG,
				trainDocuments.getDocCateMap(),
				trainDocuments.getCateDocListMap(), idfMatrix);
		KNNClassifier knn = new KNNClassifier(vsm, 20);
		runAndPrintConfusionMatrix(testVG, testDocuments, testIdfMatrix, knn);
		benchMark("KNN Classifier");
		
		
		System.out.println("======Native Bayes Classifier======");
		TestSetBayesIndexer testBayesIndexer = new TestSetBayesIndexer(testVG.getWordList(), trainVG.getWordList());
		RealMatrix testBayesMatrix= testBayesIndexer.transform(testVG.getMatrix());
//		PrettyPrinter.prettyPrintPartsOfMatrix("test doc bayes", testBayesMatrix,
//				testVG.getDocumentNames(), trainVG.getWords(), new PrintWriter(
//						System.out, true), 1000, 1200, 0, 20);
		
		VectorSpaceModel vsmb = new VectorSpaceModel(trainVG,
				trainDocuments.getDocCateMap(), trainDocuments.getCateDocListMap(), trainVG.getMatrix());
		NaiveBayesClassifier bayes = new NaiveBayesClassifier(vsmb);
		runAndPrintConfusionMatrix(testVG, testDocuments, testBayesMatrix, bayes);
		benchMark("Native Bayes Classifier");
	}

	private static void start() {
		startTime = System.nanoTime();
		stopTime = System.nanoTime();
	}

	private static void benchMark(String legend) {
		System.out.printf("======%s======%n", legend);
		System.out.println("******Elasped time******");
		estimatedTime = System.nanoTime() - stopTime;
		stopTime = System.nanoTime();
		System.out.printf("%8.4fs%n",(double)estimatedTime/1000000000D);
		
		System.out.println("======Total Elasped time======");
		estimatedTime = System.nanoTime() - startTime;
		System.out.printf("%8.4fs%n",(double)estimatedTime/1000000000D);
	}

	private static void runAndPrintConfusionMatrix(VectorGenerator testVG,
			DocumentCorpus testDocuments, RealMatrix testIdfMatrix,
			TextClassifier knn) throws Exception {
		
		String predictedCate = null;
		double correctCount = 0;
		
		ArrayList<String> trainCategories = knn.getVSM().getCategoreisList();
		ArrayList<String> testCategories = testDocuments.getCategoreisList();
		RealMatrix confuseMatrix = new OpenMapRealMatrix(testCategories.size(),
				trainCategories.size());

		// SortedSet<String> resultCategories = new TreeSet<String>();
		for (int i = 0; i < testIdfMatrix.getColumnDimension(); i++) {
			String actualCate = testDocuments.getDocCateMap().get(
					testVG.getDocumentName(i));// test
			predictedCate = knn.classify(testIdfMatrix.getColumn(i));// train
			if (actualCate.equals(predictedCate))
				correctCount++;
			int row = testCategories.indexOf(actualCate);
			int col = trainCategories.indexOf(predictedCate);
			double temp = confuseMatrix.getEntry(row, col);
			confuseMatrix.setEntry(row, col, temp + 1);
			//System.out.printf("doc:%s, category:%s%n", testVG.getDocumentName(i), predictedCate);
		}

		PrettyPrinter.prettyPrintMatrix("ConfusionMatrix", confuseMatrix,
				trainCategories.toArray(new String[trainCategories.size()]),
				testCategories.toArray(new String[testCategories.size()]),
				new PrintWriter(System.out, true));
		System.out.printf("Accuracy rate:%8.4f%n", correctCount / testIdfMatrix.getColumnDimension());
	}

}
