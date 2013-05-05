package cpsc531.tc.stemmer;

public interface IStemmer {
	public void init() throws Exception;
	public String stemWord(String in) throws Exception;
}
