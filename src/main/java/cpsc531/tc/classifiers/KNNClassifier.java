package cpsc531.tc.classifiers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.OpenMapRealVector;

import cpsc531.tc.features.VectorSpaceModel;

/**
 * KNN Classifier
 * @author shaofenchen
 *
 */

public class KNNClassifier extends TextClassifier{
	private long startTime;
	private long elaspedTime;
	private long stopTime;

	private int K; //KNN's K
	private double[] sumAbs;
	
	public KNNClassifier(VectorSpaceModel _vsm, int k){
		super(_vsm);
		startTime = System.nanoTime();
		K = k;
		int rows = vsm.getRowDimension();
		int cols = vsm.getColumnDimension();
		sumAbs = new double[cols];
		for(int i =0; i < cols; i++){
			sumAbs[i] = 0D;
			for(int j = 0; j < rows; j++){
				double temp = vsm.getMatrix().getEntry(j, i);
				if(temp > 0)
					sumAbs[i] += (temp*temp);
				
			}
			sumAbs[i] = Math.sqrt(sumAbs[i]);
		}
	}
	
	private void benchMark(String legend) {
		System.out.printf("======%s======%n", legend);
		System.out.println("******Elasped time******");
		elaspedTime = System.nanoTime() - stopTime;
		stopTime = System.nanoTime();
		System.out.printf("%8.4fs%n",(double)elaspedTime/1000000000D);
		
		System.out.println("======Total Elasped time======");
		elaspedTime = System.nanoTime() - startTime;
		System.out.printf("%8.4fs%n",(double)elaspedTime/1000000000D);
	}
	
	@Override
	public String classify(double[] vector) throws Exception{
		HashMap<String,Double> simMap = new HashMap<String,Double>();//<DocName, Similarity> map
		double norm = 0D;
		for(int i = 0; i < vector.length; i++){
			if(vector[i]>0){
				norm += (vector[i] *vector[i]);
			}
		}
		norm = Math.sqrt(norm);
		//benchMark("compute denominator");
		OpenMapRealMatrix testDocMatrix = new OpenMapRealMatrix(1, vector.length);
		for(int i = 0; i < vector.length; i++){
			if(vector[i]>0)
				testDocMatrix.setEntry(0, i, vector[i]);
		}
		OpenMapRealMatrix simMatrix = (OpenMapRealMatrix) testDocMatrix.multiply(vsm.getMatrix());

		for(int i = 0; i < vsm.getColumnDimension(); i++){
			double denominator = norm*sumAbs[i];
			double similarity = simMatrix.getEntry(0, i)/denominator;
			simMap.put(vsm.getDocName(i), similarity);
			//double sim = cosineSimilarity(vector, vsm.getFeatureVector(i), denominator);
			//System.out.printf("Sim1==:%f%n", similarity);
			//System.out.printf("Sim2==:%f%n", sim);
			//double sim = cosineSimilarity(vector, vsm.getFeatureVector(i));
			
		}
		
		//benchMark("Similarities");
		
		//ByValueComparator bvc = new ByValueComparator(simMap);
		//TreeMap<String,Double> sortedSimMap = new TreeMap<String,Double>(bvc);
		//sortedSimMap.putAll(simMap);
		
		//PrettyPrinter.prettyPrintPortsOfMap("sortedSimMap", sortedSimMap ,  new PrintWriter(System.out, true),0,20);
		//System.out.println("..............");

		Map<String,Double> cateSimMap = new HashMap<String,Double>();//
		//double count = 0;
		double tempSim;
		
		Set<Map.Entry<String, Double>> simMapSet = simMap.entrySet();
		Set<String> maxSimsDoc = new HashSet<String>();
		for(int i = 0; i < K; i++){
			Map.Entry<String, Double> max = null;
			boolean initFlag = true;
			for(Iterator<Map.Entry<String, Double>> it = simMapSet.iterator(); it.hasNext();){
				Map.Entry<String, Double> entry = it.next();
				if(maxSimsDoc.contains(entry.getKey()))continue;
				if(initFlag){
					max = entry;
					initFlag = false;
					continue;
				}else if(max.getValue() < entry.getValue()){
					max = entry;
				}
			}
			maxSimsDoc.add(max.getKey());

				//count++;
			String categoryName = vsm.getCategory(max.getKey());
			if(cateSimMap.containsKey(categoryName)){
				tempSim = cateSimMap.get(categoryName);
				cateSimMap.put(categoryName, tempSim + max.getValue());
			}
			else cateSimMap.put(categoryName, max.getValue());
			//if (count > K) break;
		}
		
		double maxSim = 0;
		String bestCate = null;
		Set<Map.Entry<String, Double>> cateSimMapSet = cateSimMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = cateSimMapSet.iterator(); it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			if(me.getValue()> maxSim){
				bestCate = me.getKey();
				maxSim = me.getValue();
			}
		}
		//benchMark("Max similarity and best cate");
		return bestCate;
	}
	
	private double cosineSimilarity(OpenMapRealVector v1, OpenMapRealVector v2){
		double mul = 0, uAbs = 0, vAbs = 0;
		mul = v1.dotProduct(v2);
		uAbs = v1.getNorm();
		vAbs = v2.getNorm();
		return mul/(uAbs * vAbs);
		
	}
	private double cosineSimilarity(double[] v1,double[] v2) throws Exception{
		double mul = 0, uAbs = 0, vAbs = 0;
		if(v1.length != v2.length){
			throw new RuntimeException("Failed to compute CosineSimilarity. Lengths of 2 vectors did not equal");
		}
		for(int i = 0; i < v1.length; i++){
			if(v1[i]>0 && v2[i]>0){
				mul += v1[i] * v2[i];
				uAbs += v1[i] *v1[i];
				vAbs += v2[i] *v2[i]; 
			}else{ 
				if(v1[i]>0)
					uAbs += v1[i] *v1[i];
				if(v2[i]>0)
					vAbs += v2[i] *v2[i]; 
				}
		}
//		System.out.printf("Dominator_pre:%f%n", uAbs);
//		System.out.printf("Dominator_pos:%f%n", vAbs);
		uAbs = Math.sqrt(uAbs*vAbs);
		//vAbs = Math.sqrt(vAbs);
		return mul / uAbs;
	}
	
	private double cosineSimilarity(double[] v1,double[] v2, double denominator) throws Exception{
		double mul = 0;
		if(v1.length != v2.length){
			throw new RuntimeException("Failed to compute CosineSimilarity. Lengths of 2 vectors did not equal");
		}
		for(int i = 0; i < v1.length; i++){
			//if(v1[i]>0 && v2[i]>0){
				mul += v1[i] * v2[i];
			//}
		}
		System.out.printf("Multi==:%f%n", mul);
		return mul / denominator;
	}
	
	static class ByValueComparator implements Comparator<Object> {
		HashMap<String, Double> base_map;

		public ByValueComparator(HashMap<String, Double> disMap) {
			this.base_map = disMap;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			String arg0 = o1.toString();
			String arg1 = o2.toString();
			if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
				return 0;
			}
			if (base_map.get(arg0) < base_map.get(arg1)) {
				return 1;
			} else if (base_map.get(arg0) == base_map.get(arg1)) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}



