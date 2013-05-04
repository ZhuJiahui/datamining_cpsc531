package cpsc531.tc.features;

import java.util.ArrayList;

import net.sf.jtmt.indexers.matrix.IdfIndexer;
import net.sf.jtmt.indexers.matrix.VectorGenerator;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.math.linear.RealMatrix;

public class TestSetIdfIndexer extends IdfIndexer {

	private ArrayList<Double> trainDocFreq;
	private double trainSize; // number of training documents
	private ArrayList<Integer> testTrainWordMap;

	public TestSetIdfIndexer(ArrayList<String> _testWordList,
			ArrayList<String> _trainWordList, ArrayList<Double> docFreq,
			double docNum) {
		trainDocFreq = docFreq;
		trainSize = docNum;
		testTrainWordMap = new ArrayList<Integer>(_testWordList.size());
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
		normalizeMatrix(matrix);
		return matrix;
	}

}
