package cpsc531.tc.features;

import java.util.ArrayList;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

public class TestSetIdfIndexer extends IdfIndexer {

	private ArrayList<Double> trainDocFreq;
	private int trainSize; // number of training documents
	private int trainWordsetSize; //number of wordset in training data
	private ArrayList<Integer> testTrainWordMap;
	private RealMatrix transMatrix; //a matrix with the same number of rows(words) as the training matrix

	public TestSetIdfIndexer(ArrayList<String> _testWordList,
			ArrayList<String> _trainWordList, ArrayList<Double> docFreq,
			int docNum) {
		trainDocFreq = docFreq;
		trainSize = docNum;
		trainWordsetSize = _trainWordList.size();
		testTrainWordMap = new ArrayList<Integer>(_testWordList.size());
		
		System.out.println("......_trainWordList........");
		System.out.printf("size:%d,",_trainWordList.size());
		
		for (int i = 0; i < _testWordList.size(); i++) {
			testTrainWordMap.add(i,
					_trainWordList.indexOf(_testWordList.get(i)));
		}
	}

	@Override
	public RealMatrix transform(RealMatrix matrix) {
		calculateRawDF(matrix);
		double n = trainSize + matrix.getColumnDimension();
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
					matrix.setEntry(i, j,
							matrix.getEntry(i, j) * (1 + Math.log(n / dm)));
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
						try{
						transMatrix.setEntry(i, j, matrixElement);
						}
						catch(MatrixIndexException e){
							System.out.println("..............");
							System.out.printf("row:%d, col:%d%n",testWordIdx,j);
							throw(e);
						}
					}
				}
			}
		}
		normalizeMatrix(transMatrix);
		normalizeMatrix(matrix);		
		return matrix;
	}
	
	public RealMatrix getTransformedMatrix(){
		return transMatrix;
	}

}
