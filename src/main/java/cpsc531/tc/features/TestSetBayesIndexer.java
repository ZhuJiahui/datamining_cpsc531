package cpsc531.tc.features;

import java.util.ArrayList;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

public class TestSetBayesIndexer implements Transformer<RealMatrix, RealMatrix> {
	private ArrayList<Integer> testTrainWordMap;
	private int trainWordsetSize;
	//private RealMatrix transMatrix; //a matrix with the same number of rows(words) as the training matrix
	
	public TestSetBayesIndexer(ArrayList<String> _testWordList,
			ArrayList<String> _trainWordList){
		trainWordsetSize = _trainWordList.size();
		testTrainWordMap = new ArrayList<Integer>(_testWordList.size());
		for (int i = 0; i < _testWordList.size(); i++) {
			testTrainWordMap.add(i,
					_trainWordList.indexOf(_testWordList.get(i)));
		}
	}
	
	@Override
	public RealMatrix transform(RealMatrix matrix) {
		RealMatrix transMatrix = new OpenMapRealMatrix(trainWordsetSize, matrix.getColumnDimension());
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
		
		return transMatrix;
	}

}
