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

import cpsc531.tc.utils.PorterStemmer;

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
    POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB});
  
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
// test code 1:
//	   List<String> stemss = stemmer.findStems("2-vectors", POS.ADJECTIVE);
//	   if(!stemss.isEmpty()){
//		   IIndexWord wordtest = dictionary.getIndexWord(stemss.get(0), POS.NOUN);
//		   if (wordtest== null)
//		   {
//			   System.out.println("not null");
//		   }
//		   for(int i =0; i<stemss.size(); i++){
//			   System.out.println(stemss.get(i));}
//	   }
//	   else System.out.println("sssss");
    
    for (Token token : tokens) {
      Token outputToken = new Token(token.getValue(), token.getType());
     
      if (token.getType() == TokenType.WORD) {
    	  //System.out.printf("word:%s type:%s\n",token.getValue(),token.getType().toString());
          
          String word = token.getValue();
       
//        String stem = Stemmer.stemWord(word);
//        if(!stem.isEmpty()){
//        	outputToken.setValue(stem);
//        	outputToken.setType(TokenType.CONTENT_WORD);
//        }

//        //System.out.printf("word:%s type:%s\n",outputToken.getValue(),outputToken.getType().toString());
    	  
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
    			   outputToken.setValue(indexWord.getLemma());
    		   }else outputToken.setValue(stems.get(0));
    		   outputToken.setType(TokenType.CONTENT_WORD);
    		   break;
    		   }
    	   }
      }
      outputTokens.add(outputToken);
    }
    return outputTokens;
  }
}
