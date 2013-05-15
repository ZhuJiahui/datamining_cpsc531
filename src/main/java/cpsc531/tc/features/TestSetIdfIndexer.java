package cpsc531.tc.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import net.sf.jtmt.indexers.matrix.IdfIndexer;

import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Calculate the TF/IDF matrix of test set, which need special processing
 * @author shaofenchen
 *
 */
public class TestSetIdfIndexer extends IdfIndexer {

	private ArrayList<Double> trainDocFreq;
	private int trainDocNum; // number of training documents
	private int trainWordsetSize; //number of wordset in training data
	private ArrayList<Integer> testTrainWordMap;
	private RealMatrix transMatrix; //a matrix with the same number of rows(words) as the training matrix

	public TestSetIdfIndexer(ArrayList<String> _testWordList,
			ArrayList<String> _trainWordList, ArrayList<Double> docFreq,
			int docNum) {
		trainDocFreq = docFreq;
		trainDocNum = docNum;
		trainWordsetSize = _trainWordList.size();
		testTrainWordMap = new ArrayList<Integer>(_testWordList.size());
		
		System.out.println("......_trainWordList........%n");
		System.out.printf("size:%d,",_trainWordList.size());
		
		for (int i = 0; i < _testWordList.size(); i++) {
			testTrainWordMap.add(i,
					_trainWordList.indexOf(_testWordList.get(i)));
		}
	}
	public TestSetIdfIndexer(ArrayList<String> _testWordList,
			ArrayList<String> _trainWordList, String dfFileStr,
			int docNum) throws Exception {
		
		File dfFile = new File(dfFileStr);
		BufferedReader dfFileBR = new BufferedReader(new FileReader(dfFile));
		String line;
		ArrayList<Double> df = new ArrayList<Double>();
		while((line = dfFileBR.readLine()) != null){
			
			line = line.trim();
			df.add(Double.valueOf(line));
		}
		dfFileBR.close();
		
		//this(_testWordList, _trainWordList, df, docNum);
		
		trainDocFreq = df;
		trainDocNum = docNum;
		trainWordsetSize = _trainWordList.size();
		testTrainWordMap = new ArrayList<Integer>(_testWordList.size());
		
		System.out.println("......_trainWordList........%n");
		System.out.printf("size:%d,",_trainWordList.size());
		
		for (int i = 0; i < _testWordList.size(); i++) {
			testTrainWordMap.add(i,
					_trainWordList.indexOf(_testWordList.get(i)));
		}
	}

	@Override
//	public RealMatrix transform(RealMatrix matrix) {
//		transMatrix = new OpenMapRealMatrix(trainWordsetSize, matrix.getColumnDimension());
//		System.out.println(".......size of transMatrix.......");
//		System.out.printf("row:%d, col:%d%n",transMatrix.getRowDimension(),transMatrix.getColumnDimension());
//		
//		for (int i = 0; i < transMatrix.getRowDimension(); i++) {
//			int testWordIdx = testTrainWordMap.indexOf(i);
//			if(testWordIdx!=-1){
//				for (int j = 0; j < transMatrix.getColumnDimension(); j++) {
//					double matrixElement = matrix.getEntry(testWordIdx, j);
//					if (matrixElement > 0.0D) {
//						transMatrix.setEntry(i, j, matrixElement);
//					}
//				}
//			}
//		}
//		normalizeMatrix(transMatrix);
//		//normalizeMatrix(matrix);		
//		return transMatrix;
//	}
	public RealMatrix transform(RealMatrix matrix) {
		calculateRawDF(matrix);
		double n = trainDocNum + matrix.getColumnDimension();
		double dm;
		for (int j = 0; j < matrix.getColumnDimension(); j++) {
			for (int i = 0; i < matrix.getRowDimension(); i++) {
				double matrixElement = matrix.getEntry(i, j);
				if (matrixElement > 0.0D) {
					int k = testTrainWordMap.get(i);
					if (k > -1) {
						dm = trainDocFreq.get(k) + dfRawCounts.get(i);
					} else
						dm = dfRawCounts.get(i);
					matrix.setEntry(i, j, matrix.getEntry(i, j) * ( 1 + Math.log(n / dm)));
				}
			}
		}
		
		transMatrix = new OpenMapRealMatrix(trainWordsetSize, matrix.getColumnDimension());
		System.out.println(".......size of transMatrix.......");
		System.out.printf("row:%d, col:%d%n",transMatrix.getRowDimension(),transMatrix.getColumnDimension());
		
		for (int i = 0; i < transMatrix.getRowDimension(); i++) {
			int testWordIdx = testTrainWordMap.indexOf(i);
			if(testWordIdx!=-1){
				for (int j = 0; j < transMatrix.getColumnDimension(); j++) {
					double matrixElement = matrix.getEntry(testWordIdx, j);
					if (matrixElement > 0.0D) {
						transMatrix.setEntry(i, j, matrixElement);
					}
				}
			}
		}
		normalizeMatrix(transMatrix);
		//normalizeMatrix(matrix);		
		return transMatrix;
	}
	
	public RealMatrix getTransformedMatrix(){
		return transMatrix;
	}

}
