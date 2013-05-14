package cpsc531.tc.classifiers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVector;

import cpsc531.tc.features.VectorSpaceModel;

public class NaiveBayesClassifier extends TextClassifier {
	private double[] wordsTotalCountsInCate;//1.<total_count>Total sum of all the counts of all words in category Map
	private double[] uniqueWordsCountInCate;//2.<num>,number of unique words in category
	private double[] wordTotalCounts;//3.Word T's count in all categories
	private double wordsTotalCountsAllCategories;//Total sum of all the counts of all words in all categories 
	OpenMapRealMatrix cateWordFreMatrix;
	
	public NaiveBayesClassifier(VectorSpaceModel _vsm){
		super(_vsm);
		init();
	}
	
	private void init(){
		Map<String, int[]> cateDocListMap = vsm.getCateDocListMap();
		Set<Map.Entry<String, int[]>> cateDocListSet = cateDocListMap.entrySet();
		wordsTotalCountsInCate = new double[cateDocListSet.size()];
		uniqueWordsCountInCate = new double[cateDocListSet.size()];
		wordTotalCounts = new double[vsm.getRowDimension()];
		Arrays.fill(wordTotalCounts, 0);
		
		cateWordFreMatrix = new OpenMapRealMatrix(vsm.getRowDimension(), cateDocListSet.size());
		int count = 0;
		for(Iterator<Map.Entry<String, int[]>> it = cateDocListSet.iterator(); it.hasNext();){
			Map.Entry<String, int[]> entry = it.next();
			int[] docIndexList = entry.getValue();
			RealVector wordsFreCountsInCate = new OpenMapRealVector(vsm.getRowDimension());//Zeros vector
			for(int i = 0; i < docIndexList.length; i++){
				wordsFreCountsInCate = 
						wordsFreCountsInCate.add(new OpenMapRealVector(vsm.getMatrix().getColumnVector(docIndexList[i])));
				
			}
			cateWordFreMatrix.setColumnVector(count, wordsFreCountsInCate);
			
			double uniqueWordCount = 0;
			double wordsTotalCounts = 0;
			for(int i = 0; i < vsm.getRowDimension(); i ++){
				double temp = wordsFreCountsInCate.getEntry(i);
				if(temp!=0){
					wordsTotalCounts += temp;
					uniqueWordCount ++;
					wordTotalCounts[i] += temp;
					wordsTotalCountsAllCategories += temp;
				}
			}
			wordsTotalCountsInCate[count] = wordsTotalCounts;
			uniqueWordsCountInCate[count] = uniqueWordCount;
			count ++;
		}
	}
	
	@Override
	public String classify(double[] vector) throws Exception{
		ArrayList<String> cateList = vsm.getCategoreisList();
		BigDecimal maxP = new BigDecimal(0);
		String bestCate = null;
		for(int i = 0; i < wordsTotalCountsInCate.length; i++){
			BigDecimal p = computeCategoryProb(vector, i);
			if(i == 0){
				maxP = p;
				bestCate = cateList.get(i);
				continue;
			}
			if(p.compareTo(maxP) == 1){
				maxP = p;
				bestCate = cateList.get(i);
			}
		}
		return bestCate;
	}

	/**
	 * Compute the probability of assigning a test sample to a specific category
	 * @param testVector The vector expression of a test sample, here use the TF of the test text
	 * @param cateIndex	 The index of the category 
	 * @return
	 * @throws Exception
	 */
	private BigDecimal computeCategoryProb(double[] testVector, int cateIndex) throws Exception {
		BigDecimal probability = new BigDecimal(1);
		double wordNumInCate = wordsTotalCountsInCate[cateIndex];//cateWordsNum.get(trainFile.getName());
		BigDecimal wordNumInCateBD = new BigDecimal(wordNumInCate);
		BigDecimal totalWordsNumBD = new BigDecimal(wordsTotalCountsAllCategories);
		for(int i = 0; i < testVector.length; i++){
			if(testVector[i] != 0){
				double testFileWordNumInCate;
				testFileWordNumInCate = cateWordFreMatrix.getEntry(i, cateIndex);
				BigDecimal testFileWordNumInCateBD = new BigDecimal(testFileWordNumInCate);
				BigDecimal xcProb = (testFileWordNumInCateBD.add(new BigDecimal(1))).divide(totalWordsNumBD.add(wordNumInCateBD),10, BigDecimal.ROUND_CEILING);
				probability = probability.multiply(xcProb);
			}
		}
		BigDecimal res = probability.multiply(wordNumInCateBD.divide(totalWordsNumBD,10, BigDecimal.ROUND_CEILING));
		return res;
	}
}
