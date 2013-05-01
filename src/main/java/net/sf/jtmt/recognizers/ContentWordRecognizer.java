package net.sf.jtmt.recognizers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jtmt.tokenizers.Token;
import net.sf.jtmt.tokenizers.TokenType;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * Recognizes content words (noun, verb, adjective, and adverb) from a
 * List of Token objects. Only TokenType.WORD tokens are considered in
 * this recognizer, and are converted to TokenType.CONTENT_WORD. Words
 * are looked up against the WordNet dictionary.
 * @author Sujit Pal
 * @version $Revision$
 */
public class ContentWordRecognizer implements IRecognizer {

  private IDictionary dictionary;
  private IStemmer stemmer;
  private List<POS> allowablePartsOfSpeech = Arrays.asList(new POS[] {
    POS.NOUN, POS.VERB});
  
  public void init() throws Exception {
    this.dictionary = new Dictionary(new URL("file", null, "C:\\Program Files (x86)\\WordNet\\2.1\\dict"));
    dictionary.open();
    stemmer = new WordnetStemmer(dictionary);
  }

  public List<Token> recognize(List<Token> tokens) {
    List<Token> outputTokens = new ArrayList<Token>();
    //IIndexWord testWord = dictionary.getIndexWord("functions", POS.NOUN);
//    if(testWord !=null){
//    	  System.out.println("none empty");
//      }
	   List<String> stemss = stemmer.findStems("2-vectors", POS.NOUN);
	   if(!stemss.isEmpty()){
		   for(int i =0; i<stemss.size(); i++){
			   System.out.println(stemss.get(i));}
	   }
    for (Token token : tokens) {
      Token outputToken = new Token(token.getValue(), token.getType());
      //System.out.printf("word:%s type:%s\n",token.getValue(),token.getType().toString());
      
      if (token.getType() == TokenType.WORD) {
        String word = token.getValue();
        for (POS allowablePartOfSpeech : allowablePartsOfSpeech) {
    	   List<String> stems = stemmer.findStems(word, allowablePartOfSpeech);
    	   if(!stems.isEmpty()){
    		   outputToken.setType(TokenType.CONTENT_WORD);
    		   break;
    	   }
//          IIndexWord indexWord = dictionary.getIndexWord(word, allowablePartOfSpeech);
//          if (indexWord != null) {
//            outputToken.setType(TokenType.CONTENT_WORD);
//            break;
//          }
        }
      }
      outputTokens.add(outputToken);
    }
    return outputTokens;
  }
}
