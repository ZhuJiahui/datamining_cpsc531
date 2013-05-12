package net.sf.jtmt.indexers.matrix;

import java.util.ArrayList;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Reduces the weight of words which are commonly found (ie in more documents).
 * The factor by which it is reduced is chosen from the book as: f(m) = 1 +
 * log(N/d(m)) where N = total number of docs in collection d(m) = number of
 * docs containing word m so where a word is more frequent (ie d(m) is high,
 * f(m) would be low.
 * 
 * @author Sujit Pal
 * @version $Revision: 44 $
 */
public class IdfIndexer implements Transformer<RealMatrix, RealMatrix> {

	protected ArrayList<Double> dfRawCounts = new ArrayList<Double>();
	// the raw doc frequency of terms

	public RealMatrix transform(RealMatrix matrix) {
		// Phase 1: apply IDF weight to the raw word frequencies
		RealMatrix resultMatrix = new OpenMapRealMatrix(matrix.getRowDimension(),
				matrix.getColumnDimension());
		calculateRawDF(matrix);
		int n = matrix.getColumnDimension();
		for (int j = 0; j < matrix.getColumnDimension(); j++) {
			for (int i = 0; i < matrix.getRowDimension(); i++) {
				double matrixElement = matrix.getEntry(i, j);
				if (matrixElement > 0.0D) {
					double dm = dfRawCounts.get(i);
					// matrix.setEntry(i, j, matrix.getEntry(i,j) * (1 +
					// Math.log(n) - Math.log(dm)));
					resultMatrix.setEntry(i, j,
							matrix.getEntry(i, j) * (Math.log(n / (dm + 1))));
				}
			}
		}
		// Phase 2: normalize the word scores for a single document
		normalizeMatrix(resultMatrix);
		return resultMatrix;
	}

	protected void normalizeMatrix(RealMatrix matrix) {
		for (int j = 0; j < matrix.getColumnDimension(); j++) {
			double sum = sum(matrix.getSubMatrix(0,
					matrix.getRowDimension() - 1, j, j));
			for (int i = 0; i < matrix.getRowDimension(); i++) {
				if (sum > 0.0D) {
					matrix.setEntry(i, j, (matrix.getEntry(i, j) / sum));
				} else {
					matrix.setEntry(i, j, 0.0D);
				}
			}
		}
	}

	public ArrayList<Double> getDFRawCounts() {
		return dfRawCounts;
	};

	private double sum(RealMatrix colMatrix) {
		double sum = 0.0D;
		for (int i = 0; i < colMatrix.getRowDimension(); i++) {
			sum += colMatrix.getEntry(i, 0);
		}
		return sum;
	}

	protected void calculateRawDF(RealMatrix matrix) {
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			double dm = countDocsWithWord(matrix.getSubMatrix(i, i, 0,
					matrix.getColumnDimension() - 1));
			dfRawCounts.add(dm);
		}
	}

	private double countDocsWithWord(RealMatrix rowMatrix) {
		double numDocs = 0.0D;
		for (int j = 0; j < rowMatrix.getColumnDimension(); j++) {
			if (rowMatrix.getEntry(0, j) > 0.0D) {
				numDocs++;
			}
		}
		return numDocs;
	}
}
