package cpsc531.tc.classifiers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.OpenMapRealVector;

import cpsc531.tc.features.VectorSpaceModel;

/**
 * KNN Classifier
 * @author shaofenchen
 *
 */
public class KNNClassifier extends TextClassifier{

	private int K; //KNN's K
	public KNNClassifier(VectorSpaceModel _vsm, int k){
		super(_vsm);
		K = k;
	}
	
	@Override
	public String classify(double[] vector) throws Exception{
		HashMap<String,Double> simMap = new HashMap<String,Double>();//<DocName, Similarity> map
		for(int i = 0; i < vsm.getColumnDimension(); i++){
			double sim = cosineSimilarity(vector, vsm.getFeatureVector(i));
			simMap.put(vsm.getDocName(i), sim);
		}
		
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
					vAbs += v2[i] *v2[i]; }
		}
		uAbs = Math.sqrt(uAbs);
		vAbs = Math.sqrt(vAbs);
		return mul / (uAbs * vAbs);
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



