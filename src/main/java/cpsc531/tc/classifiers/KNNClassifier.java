package cpsc531.tc.classifiers;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cpsc531.tc.features.VectorSpaceModel;
import cpsc531.tc.utils.PrettyPrinter;

public class KNNClassifier {
	
	private VectorSpaceModel vsm;
	public KNNClassifier(VectorSpaceModel _vsm){
		vsm = _vsm;
	}
	
	public String classify(double[] vector, int K) throws Exception{
		HashMap<String,Double> simMap = new HashMap<String,Double>();//<DocName, Similarity> map
		for(int i = 0; i < vsm.getColumnDimension(); i++){
			double sim = cosineSimilarity(vector, vsm.getFeatureVector(i));
			simMap.put(vsm.getDocName(i), sim);
		}
		
		ByValueComparator bvc = new ByValueComparator(simMap);
		TreeMap<String,Double> sortedSimMap = new TreeMap<String,Double>(bvc);
		sortedSimMap.putAll(simMap);getClass();
		
		PrettyPrinter.prettyPrintPortsOfMap("sortedSimMap", sortedSimMap ,  new PrintWriter(System.out, true),0,20);
		
		System.out.println("..............");

		Map<String,Double> cateSimMap = new TreeMap<String,Double>();//
		double count = 0;
		double tempSim;
		
		Set<Map.Entry<String, Double>> simMapSet = sortedSimMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = simMapSet.iterator(); it.hasNext();){
			Map.Entry<String, Double> entry = it.next();
			count++;
			String categoryName = vsm.getCategory(entry.getKey());
			if(cateSimMap.containsKey(categoryName)){
				tempSim = cateSimMap.get(categoryName);
				cateSimMap.put(categoryName, tempSim + entry.getValue());
			}
			else cateSimMap.put(categoryName, entry.getValue());
			if (count > K) break;
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



