package cpsc531.tc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

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
	static DocumentCorpus trainDocuments;
	static DocumentCorpus testDocuments;
	static IStemmer stemmer;
	static TextClassifier classifier;
	static ArrayList<Double> dfList;
	
	public static void init(String wordnetDictPath, String matrixFilePath, String wordListFilePath, String dfFilePath) throws Exception{
		stemmer = new WordnetDictStemmer(wordnetDictPath);
		VectorSpaceModel vsm_rebuided= new VectorSpaceModel(matrixFilePath, wordListFilePath);
		classifier = new KNNClassifier(vsm_rebuided, 20);
		dfList = loadDfFile(dfFilePath);
	}
	
	public static String classify(String title, String content) throws Exception{
		testDocuments = new DocumentCorpus();
		testDocuments.addDocument(title, content);
		VectorGenerator testVG = new VectorGenerator();
		testVG.setStemmer(stemmer);
		testVG.generateVector(testDocuments.getDocuments());
		TestSetIdfIndexer testIndexer = new TestSetIdfIndexer(
				testVG.getWordList(), classifier.getVSM().getWordList(),
				dfList, classifier.getVSM().getColumnDimension());
		RealMatrix testIdfMatrix= testIndexer.transform(testVG.getMatrix());
		
		String cate =  classifier.classify(testIdfMatrix.getColumn(0));
		System.out.println(cate);
		return cate;
		
	}
	
	public static ArrayList<Double> loadDfFile(String dfFileStr) throws NumberFormatException, IOException{

		File dfFile = new File(dfFileStr);
		BufferedReader dfFileBR = new BufferedReader(new FileReader(dfFile));
		String line;
		ArrayList<Double> df = new ArrayList<Double>();
		while((line = dfFileBR.readLine()) != null){
			
			line = line.trim();
			df.add(Double.valueOf(line));
		}
		dfFileBR.close();

		return df;
	}
	
	private static void generateModelFiles(String trainFilesRootDir) throws Exception{
		start();
		String matrixFile = "C:/Users/shaofenchen/workspace2/data/matrix.txt"; 
		String wordListFile = "C:/Users/shaofenchen/workspace2/data/wordList.txt";
		String docFreFile = "C:/Users/shaofenchen/workspace2/data/df.txt";
		trainDocuments = new DocumentCorpus(trainFilesRootDir);
		VectorGenerator trainVG = new VectorGenerator();

		trainVG.setStemmer(stemmer);

		trainVG.generateVector(trainDocuments.getDocuments());

		IdfIndexer indexer = new IdfIndexer();
		RealMatrix idfMatrix = indexer.transform(trainVG.getMatrix());

		VectorSpaceModel vsm = new VectorSpaceModel(trainVG,
				trainDocuments.getDocCateMap(),
				trainDocuments.getCateDocListMap(), idfMatrix);
		
		indexer.outputRawDFCount(docFreFile);
		vsm.outputModel(matrixFile, wordListFile);
	}
	
	private static void testModelFiles()throws Exception, IOException {
		String matrixFile = "C:/Users/shaofenchen/workspace2/data/matrix.txt"; 
		String wordListFile = "C:/Users/shaofenchen/workspace2/data/wordList.txt";
		String dfFileStr = "C:/Users/shaofenchen/workspace2/data/df.txt";
		
		VectorGenerator testVG = new VectorGenerator();
		testVG.setStemmer(stemmer);
		testVG.generateVector(testDocuments.getDocuments());

		VectorSpaceModel vsm_rebuided= new VectorSpaceModel(matrixFile, wordListFile);
	
		TestSetIdfIndexer testIndexer = new TestSetIdfIndexer(
				testVG.getWordList(), vsm_rebuided.getWordList(),
				dfFileStr, vsm_rebuided.getColumnDimension());
		
		RealMatrix testIdfMatrix= testIndexer.transform(testVG.getMatrix());
		
		KNNClassifier knn = new KNNClassifier(vsm_rebuided, 20);
		runAndPrintConfusionMatrix(testVG, testDocuments, testIdfMatrix, knn);
	}
	
	public static void testClassfier() throws Exception{
		String wordNetPath = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
		String matrixPath = "C:/Users/shaofenchen/workspace2/data/matrix.txt"; 
		String wordListPath = "C:/Users/shaofenchen/workspace2/data/wordList.txt";
		String dfFilePath = "C:/Users/shaofenchen/workspace2/data/df.txt";
		init(wordNetPath,matrixPath,wordListPath,dfFilePath);
		String title = "test";
		String content = "Earlier this week I had the opportunity to sit with six other people from the NYC technology scene and talk to NYC Council Speaker Christine Quinn and a few members of her staff. Charlie O'Donnell organized the event to help Speaker Quinn engage with the startup scene and hear from us directly what the city can do, if anything, to help foster startups, tech, and innovation here in NYC. The two major themes that emerged from the discussion were: more on the ground interaction between city government and the tech community and that NYC has a major shortage of engineering talent. The resulting talk on the perceived programmer shortage in NYC is the inspiration for this post. Ask any startup in NYC (or anyone that needs a programmer for that matter) and most will agree that their biggest problem is finding programmers to hire. This was definitely the most prominent topic in the discussion with Speaker Quinn and her staff. The question then became: how do we solve this problem? How can the city government help? The group converged on education as the method to create more programming talent in the city (there wasn't much talk about what can be done to entice already qualified developers to move here). I agree completely that education is what is ultimately needed. An idea that I thought was spot on was required computer science classes for grade school students. We require math and physical sciences. Computers are just as ingrained and important in our society at this point as math and hard sciences. We need something more than teaching kids MS Word, Excel, and Powerpoint. That's like teaching kids how to use a match to light a fire and expecting them to be able to explain the second law of thermodynamics. However, that doesn't solve the immediate need. It's more of a 20 year plan. The group's idea to address the immediate need for programmers was to put together secondary classes to give training to those currently unemployed people that have interest in picking up new skills. The city can help immensely by providing space and letting those in need know about the availability of these classes. The thinking goes that people could pick up mobile application development or Ruby on Rails in a period of 3 to 6 months and then jump into the NYC startups that are so desperate for these skill sets. I contend that: first, there are more available engineers in this city than hiring startups claim, and second, this probably won't solve the programmer need to the satisfaction of those NYC startups that claim to be so desperate. Hence the myth that there are no available programmers in NYC and the truth that the best programmers are hard to find. But there is hope to fix this, let me explain... First, my claim that there are more engineers in this city than hiring startups claim. It's not that startups can't find anyone. I'm sure that startups with open positions came across resumes and even interview some candidates. It's just that they end up throwing out most of the resumes they see and giving the \"no hire\" stamp on most of the candidates they interview. I've been guilty of this myself. I've interviewed many candidates and given far more \"no hire\" than \"hire\" recommendations. I've also looked through a stack of resumes and thrown out a significant portion of them. This comes naturally from a startup's desire to only hire the best. This means that while startups are all screaming to the rafters about the shortage of programmers, there are programmers in this city that are available for hire. But there is hope... Second, my claim that secondary education in mobile app development or Ruby on Rails won't solve the programmer shortage to the satisfaction of NYC startups. This follows naturally from my first point: startups only want to hire the best. If they can't find the best they say that there are no \"qualified candidates\" out there. While I'm sure that some of the basics of web development or mobile apps can be taught in six months, the student won't be the best. Becoming the best takes years of education, personal study, practice, and work experience. Someone with no previous programming experience that picks up development over the course of six months will still be incredibly green. They will either not pass the resume filter for hiring or will get hung up on questions in the technical interview. In either case the result is the same: no hire and startups still can't find qualified candidates. But there is hope... The hurdles around retraining engineers that may not have your specific skill set and working with very junior programmers can be overcome. First, startups need to readjust their expectations. You don't need to have the best in everything. You don't even need a programmer that has your specific skill requirement in their resume. The key is to do pair programming with senior people paired up with junior people (or someone picking up something new). Within six months of full time pairing the junior people will have picked up so much that they'll be well on their way to being ready to mentor a new junior hire themselves. Getting students from these programming courses around the resume filter is also possible. The combination of two things will go a long way: require the students to create two or more projects and have the students put those projects up on Github. The two projects will give them invaluable experience. Further, they should be required to refactor as they go based on feedback from code review done by experienced developers. Requiring them to put the code on Github gives multiple advantages. First, they learn how to use source control as they write code. Second, the code reviews done by other developers can be done right there on the site and associated with each commit. Third, they'll have projects of reference that can be used to show prospective employers what exactly their classes taught them. For startups to get past the hurdle of hiring junior people they'll need to do a couple of things. First, embrace pair programming as a great method for bringing on junior developers and making them a key part of your team from the beginning (they'll be mid-level and senior before you know it). Second, readjust your expectations. You don't really need the most bad-ass developer you can find. If you did, you'd do whatever it took to poach them from their current job. Mid-level and senior developers paired with junior developers will do just fine for most startup's needs. If you can't find the best, senior, or mid-level developers to pair with junior hires, look to a consulting company. Pivotal Labs (they have a great new office here in NYC) is regularly brought in to pair with developers already in a startup. They do a great job of bringing in solid engineering process and bringing junior developers up to speed quickly (and they're not the only game in town). Finally, if you can't afford a consulting company or hiring senior developers, you'll need to do one of two things. Roll up your own sleeves and pick up some books and a keyboard, or be ok with the fact that you may only be able to convince someone without experience to work on your hot new startup idea (it's how I got started). There is definitely hope for the engineering shortage in NYC. It requires bringing on inexperienced people or those in need of retraining (C++ or Java to dynamic language anyone?) and helping them achieve their potential while letting them help you get your startup built. I'd like to thank Speaker Quinn, Sarah, and David from her staff for taking the time to talk to us. I hope that we'll come up with some great ideas and collaboration in the future to make innovation a permanent home here in NYC. What ideas do you have to increase the amount of talent here in the city? Update: My friend Brody Berg wondered why I didn't write about the \"talent vortex\" in NYC that is the financial industry. I actually originally meant to, but simply forgot. So here is why I think the competition from the NYC financial market helps fuel the myth that it's impossible to find great talent in NYC. First, the finance industry generally pays their programmers quite well. Startups that are desperate for these people will just have to get used to it and offer competitive compensation packages. It's kind of like trying to hire programmers in Redmond or Bellevue, WA in the 90's. Microsoft was a huge talent vortex and offered larger salaries and better benefits. The same could have been said for Google from 2003-2008. You either get competitive or find people that these companies have overlooked. Which brings me to my second point: there are plenty of programmers to hire that the finance industry won't even look twice at. The intersection of the programmers you want and those that work in finance may not be as large as you think. Financial firms want people from name brand schools (preferably Ivy) with high GPAs (3.5 or greater is common). If you don't want to compete on compensation, look for programmers that don't fit their mold. Self-taught (and specially those with secondary education like this article talks about) are the kinds of programmers that will rarely get consideration from a financial firm. Also, the financial industry wants Java, dotNet, and C++ programmers. Chances are that if you're a startup, this isn't what you're after. Further, the finance industry has laid off quite a few programmers in the last three years. These are those experienced engineers that don't have the skill set that matches yours. They know Java, but they don't know Rails and Javascript. However, these are generally smart people that can pick it up very quickly on the job. The easiest way to find a Rails programmer right now is to poach from another community and teach them Rails. Competing with the financial firms is tough, but it's certainly doable.";
		classify(title, content);
	}
	
	
	public static void main(String[] args) throws Exception {
		String trainTestFilesRootDir, testFilesRootDir, trainFilesRootDir;
		trainTestFilesRootDir = "C:/Users/shaofenchen/workspace2/data/articles2";
		trainFilesRootDir = "C:/Users/shaofenchen/workspace2/data/articles4";
		testFilesRootDir = "C:/Users/shaofenchen/workspace2/data/test2";
		
		
		if(args.length > 0){
			stemmer = new WordnetDictStemmer(args[0]);
		}else 
			stemmer = new WordnetDictStemmer("C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		if(args.length == 2){
			trainTestFilesRootDir = args[1];
			runBatchTest(trainTestFilesRootDir);
		}
		else if(args.length == 3){
			trainFilesRootDir = args[1];
			testFilesRootDir = args[2];
			runOnce(trainFilesRootDir,testFilesRootDir);
		}else{
			//runBatchTest(trainTestFilesRootDir);
			//runOnce(trainFilesRootDir, testFilesRootDir);
			generateModelFiles(trainFilesRootDir);
		}
		
	}
	
	private static void runBatchTest(String trainTestFilesRootDir) throws IOException, Exception{
		double startPercent = 0, endPercent = 0;
		for(int i = 1; i < 10; i++){
			start();
			endPercent = 0.1D * (double)i;
			trainDocuments = new DocumentCorpus(trainTestFilesRootDir, startPercent, endPercent);
			testDocuments = new DocumentCorpus(trainTestFilesRootDir, endPercent, endPercent+ 0.1D);
			runTest();
		}
		
	}
	
	private static void runOnce(String trainFilesRootDir, String testFilesRootDir) throws Exception{
		start();
		trainDocuments = new DocumentCorpus(trainFilesRootDir, 1);
		testDocuments = new DocumentCorpus(testFilesRootDir, 1);
		testModelFiles();
	}
	

	private static void runTest() throws Exception, IOException {
		
		VectorGenerator trainVG = new VectorGenerator();
		VectorGenerator testVG = new VectorGenerator();
	
		trainVG.setStemmer(stemmer);
		testVG.setStemmer(stemmer);
		
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
