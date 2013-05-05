package cpsc531.tc.features;

import java.util.Map;

import net.sf.jtmt.indexers.matrix.VectorGenerator;

/**
 * A simple trained model wrapping all the necessary element
 * @author shaofenchen
 *
 */
public class VectorSpaceModel {
	private VectorGenerator VG;
	private Map<String, String> docCateMap;//<documentName, CategoryName> map
	
	public VectorSpaceModel(VectorGenerator _VG, Map<String, String> _docCateMap){
		VG = _VG;
		docCateMap = _docCateMap;
	}
	
	public int getColumnDimension(){
		return VG.getMatrix().getColumnDimension();
	}
	
	public int getRowDimension(){
		return VG.getMatrix().getRowDimension();
	}
	
	public double[] getFeatureVector(int index){
		return VG.getMatrix().getColumn(index);
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
	
}





