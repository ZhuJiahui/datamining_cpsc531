package net.sf.jtmt.recognizers;

import java.util.ArrayList;
import java.util.List;

import net.sf.jtmt.tokenizers.Token;
import net.sf.jtmt.tokenizers.TokenType;
import cpsc531.tc.stemmer.IStemmer;
//import edu.mit.jwi.morph.IStemmer;

/**
 * Recognizes content words (noun, verb, adjective, and adverb) from a List of
 * Token objects. Only TokenType.WORD tokens are considered in this recognizer,
 * and are converted to TokenType.CONTENT_WORD. Words are looked up against the
 * WordNet dictionary.
 * 
 * @author Sujit Pal
 * @version $Revision$
 */
public class ContentWordRecognizer implements IRecognizer {

	private IStemmer stemmer;
	
	public ContentWordRecognizer(IStemmer _stemmer){
		stemmer = _stemmer;
	}
	public void init() throws Exception {
	}

	public List<Token> recognize(List<Token> tokens) throws Exception {
    List<Token> outputTokens = new ArrayList<Token>();
    for (Token token : tokens) {
      Token outputToken = new Token(token.getValue(), token.getType());
     
      if (token.getType() == TokenType.WORD) {          
          String word = token.getValue();

//        //System.out.printf("word:%s type:%s\n",outputToken.getValue(),outputToken.getType().toString());
    	String stemmedWord = stemmer.stemWord(word);
    	if(stemmedWord !=null){
    		outputToken.setValue(stemmedWord);
    		outputToken.setType(TokenType.CONTENT_WORD);
    	}
      }
      outputTokens.add(outputToken);
    }
    return outputTokens;
  }
}
