package cpsc531.tc.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math.linear.RealMatrix;

import net.sf.jtmt.indexers.matrix.VectorGenerator;

/**
 * A simple trained model wrapping all the necessary element
 * @author shaofenchen
 *
 */
public class VectorSpaceModel {
	private VectorGenerator VG;
	private Map<String, String> docCateMap;//<documentName, CategoryName> map
	private Map<String, int[]> cateDocListMap; //<category, documents Index List> map
	private RealMatrix matrix;
	
	public VectorSpaceModel(VectorGenerator _VG, Map<String, String> _docCateMap, Map<String, int[]> _cateDocListMap,RealMatrix _m){
		VG = _VG;
		docCateMap = _docCateMap;
		cateDocListMap = _cateDocListMap;
		matrix = _m;
	}
	
	public int getColumnDimension(){
		return matrix.getColumnDimension();
	}
	
	public int getRowDimension(){
		return matrix.getRowDimension();
	}
	
	public double[] getFeatureVector(int index){
		return matrix.getColumn(index);
	}
	
	public String getDocName(int index){
		return VG.getDocumentNameList().get(index);
	}
	
	public String getCategory(String docName){
		return docCateMap.get(docName);
	}
	public String getCategory(int index){
		return docCateMap.get(VG.getDocumentName(index));
	}
	
	public Map<String, int[]>getCateDocListMap(){
		return cateDocListMap;
	}
	public RealMatrix getMatrix(){
		return VG.getMatrix();
	}
	public ArrayList<String> getCategoreisList(){
		SortedSet<String> cateNames = new TreeSet<String>();
		Set<Map.Entry<String, String>> cateSet = docCateMap.entrySet();
		for(Iterator<Map.Entry<String, String>> it = cateSet.iterator(); it.hasNext();){
			Map.Entry<String, String> entry = it.next();
			cateNames.add(entry.getValue());
		}
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(cateNames);
		return list;
	}
}





