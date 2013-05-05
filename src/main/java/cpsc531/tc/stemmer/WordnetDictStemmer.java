package cpsc531.tc.stemmer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import net.sf.jtmt.tokenizers.TokenType;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * A stemmer using a dictionary strategy
 * @author shaofenchen
 *
 */
public class WordnetDictStemmer implements IStemmer {

	private IDictionary dictionary;
	private WordnetStemmer stemmer;
	private List<POS> allowablePartsOfSpeech = Arrays.asList(new POS[] {
			POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB });

	public WordnetDictStemmer(String _dictDirectory) throws Exception {
		this.dictionary = new Dictionary(new URL("file", null, _dictDirectory));
		dictionary.open();
		stemmer = new WordnetStemmer(dictionary);
	}

	@Override
	public void init() throws Exception {

	}

	@Override
	public String stemWord(String word) throws Exception {
        for (POS allowablePartOfSpeech : allowablePartsOfSpeech) {
        	
        	List<String> stems = null;
        	try {
        		stems = stemmer.findStems(word, allowablePartOfSpeech);
        	} catch (IllegalArgumentException e){
        		//System.out.printf("illegalArgumentException caught for word '%s'%n",word);
        		break;
        	}
    	   if(stems != null && !stems.isEmpty()){
    		   IIndexWord indexWord = dictionary.getIndexWord(stems.get(0), allowablePartOfSpeech);
    		   if(indexWord!=null){
    			   return indexWord.getLemma();
    		   }
    		   break;
    		}
    	}
		return null;
	}

}
