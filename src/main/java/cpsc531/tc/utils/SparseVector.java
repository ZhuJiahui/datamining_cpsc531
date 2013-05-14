package cpsc531.tc.utils;

import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVector;

public class SparseVector extends OpenMapRealVector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SparseVector(RealVector v){
		super(v);
	}
	
	public double dotProduct(double v[]){
		double res = 0;
		for(OpenMapSparseIterator it = (OpenMapSparseIterator) sparseIterator();it.hasNext();){
			Entry entry = it.next(); 
			int index = entry.getIndex();
			if(index < v.length)
				res += entry.getValue() * v[index];
       }
		//System.out.printf("computation times:%d%n", count);
        return res;
	}
}
