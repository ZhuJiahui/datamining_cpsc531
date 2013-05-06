package cpsc531.tc.classifiers;

import cpsc531.tc.features.VectorSpaceModel;

public abstract class TextClassifier {
	protected VectorSpaceModel vsm;
	
	public abstract String classify(double[] vector) throws Exception;
	
	public TextClassifier(VectorSpaceModel _vsm){
		vsm = _vsm;
	}
	
	public VectorSpaceModel getVSM(){
		return vsm;
	}
}
