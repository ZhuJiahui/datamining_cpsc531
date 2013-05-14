package cpsc531.tc.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.jtmt.indexers.matrix.VectorGenerator;

import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

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
	
	public VectorSpaceModel(String matrixFile, String wordListFile) throws Exception{
		docCateMap = new HashedMap<String, String>();//<documentName, CategoryName> map
		cateDocListMap = new TreeMap<String, int[]>();
		VG = new VectorGenerator();
		
		File trainMatrixFile = new File(matrixFile);
		File trainWordListFile = new File(wordListFile);
		
		BufferedReader trainMatrixFileBR = new BufferedReader(new FileReader(trainMatrixFile));
		BufferedReader trainWordListFileBR = new BufferedReader(new FileReader(trainWordListFile));

		String line;
		String [] lineSplits;
		
		int count = 0;
		boolean firstLine = true;
		while((line = trainMatrixFileBR.readLine()) != null){
			lineSplits = line.split(" ");
			if(firstLine){
				firstLine = false;
				if(lineSplits.length==2)
					matrix = new OpenMapRealMatrix(Integer.valueOf(lineSplits[0]), Integer.valueOf(lineSplits[1]));
				else{
					trainWordListFileBR.close();
					trainMatrixFileBR.close();
					throw(new RuntimeException("Format of the matrixFile is not correct!"));
				}
				continue;
			}
			
			docCateMap.put(Integer.toString(count), lineSplits[0]);//doc, category
			VG.addDocumentName(Integer.toString(count));//doc list
			
			if(cateDocListMap.containsKey(lineSplits[0])){
				int[] originDocList = cateDocListMap.get(lineSplits[0]);
				int[] newDocList = Arrays.copyOf(originDocList, originDocList.length + 1);
				newDocList[originDocList.length] = count;
				cateDocListMap.put(lineSplits[0], newDocList);
			}
			
			for(int i = 1; i < lineSplits.length; i = i + 2){
				matrix.setEntry(Integer.valueOf(lineSplits[i]), count, Double.valueOf(lineSplits[i+1])); //word, tfidf
			}
			
			
			count++;
		}
		
		line = "";
		
		while((line = trainWordListFileBR.readLine()) != null){	
			VG.addWord(line.trim());
		}
		
		trainWordListFileBR.close();
		trainMatrixFileBR.close();
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
	
	public ArrayList<String> getWordList(){
		return VG.getWordList();
	}
	public ArrayList<String> getDocumentNameList(){
		return VG.getDocumentNameList();
	}
	
	
	/**
	 * Get the category according to the document index in VG.documentNameList
	 */
	public String getCategory(int index){
		return docCateMap.get(VG.getDocumentName(index));
	}
	
	public Map<String, int[]>getCateDocListMap(){
		return cateDocListMap;
	}
	public RealMatrix getMatrix(){
		return matrix;
	}
	public ArrayList<String> getCategoreisList(){
		SortedSet<String> cateNames = new TreeSet<String>();
		//Set<Map.Entry<String, String>> cateSet = docCateMap.entrySet();
		cateNames.addAll(docCateMap.values());
//		for(Iterator<Map.Entry<String, String>> it = cateSet.iterator(); it.hasNext();){
//			Map.Entry<String, String> entry = it.next();
//			cateNames.add(entry.getValue());
//		}
//		
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(cateNames);
		return list;
	}
	
	/**
	 * Save 2 files for reuse this trained model
	 * @param cateWordTfidfFile Structure for each line of this file:categories wordIndex1 tfidf wordIndex2 tfIdf
	 * @param wordListFile Structure for each line of this file:word
	 * @throws IOException 
	 */
	public void outputModel(String cateWordTfidfFile, String wordListFile) throws IOException{
		FileWriter cateWordTfidfWriter = new FileWriter(cateWordTfidfFile);
		FileWriter wordListWriter = new FileWriter(wordListFile);
		cateWordTfidfWriter.append(Integer.toString(getRowDimension()) +" " + Integer.toString(getColumnDimension()) +"\n");
		cateWordTfidfWriter.flush();
		for(int j = 0; j < getColumnDimension(); j++){//column loop(documents)
			String outputLine = getCategory(j);
			for(int i = 0; i < getRowDimension(); i++){ //row loop(words)
				Double tfIdf = matrix.getEntry(i, j);
				if(!tfIdf.equals(0.0D)){
					outputLine += " " + Integer.toString(i) + " " + tfIdf.toString();
				}
			}
			cateWordTfidfWriter.append(outputLine +"\n");
			cateWordTfidfWriter.flush();
		}
		for(int i = 0; i < getRowDimension(); i++){
			wordListWriter.append(VG.getWord(i) + "\n");
		}
		wordListWriter.flush();
		
		cateWordTfidfWriter.close();
		wordListWriter.close();
	}
	
}



