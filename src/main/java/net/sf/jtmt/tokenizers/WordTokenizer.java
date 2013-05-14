package net.sf.jtmt.tokenizers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * Tokenize a block of text into its component words. This is a simple wrapper
 * over BreakIterator.
 * @author Sujit Pal
 * @version $Revision$
 */
public class WordTokenizer {
  
  //private final Log log = LogFactory.getLog(getClass());

  @SuppressWarnings("unchecked")
  private final static Map<Integer,TokenType> RULE_ENTITY_MAP = 
    ArrayUtils.toMap(new Object[][] {
      {new Integer(0), TokenType.UNKNOWN},
      {new Integer(100), TokenType.NUMBER},
      {new Integer(200), TokenType.WORD},
      {new Integer(500), TokenType.ABBREVIATION},
      {new Integer(501), TokenType.WORD},
      {new Integer(502), TokenType.INTERNET},
      {new Integer(503), TokenType.INTERNET},
      {new Integer(504), TokenType.MARKUP},
      {new Integer(505), TokenType.EMOTICON},
      {new Integer(506), TokenType.INTERNET},
      {new Integer(507), TokenType.INTERNET}
  });
  
  private String text;
  private int index = 0;
  private RuleBasedBreakIterator breakIterator;
  
  public WordTokenizer() throws Exception {
    this("rules/word_break_rules.txt");
  }

  public WordTokenizer(String rulesfile) throws Exception {
    super();
    String rules = getTextFile(rulesfile);
    //this.breakIterator = new RuleBasedBreakIterator(FileUtils.readFileToString(new File(rulesfile), "UTF-8"));
    this.breakIterator = new RuleBasedBreakIterator(rules);
  }
  
  public void setText(String text) {
    this.text = text;
    this.breakIterator.setText(text);
    this.index = 0;
  }
  
  public Token nextToken() throws Exception {
    for (;;) {
      int end = breakIterator.next();
      if (end == BreakIterator.DONE) {
        return null;
      }
      String nextWord = text.substring(index, end);
//      log.debug("next=" + nextWord + "[" + breakIterator.getRuleStatus() + "]");
      index = end;
      return new Token(nextWord, RULE_ENTITY_MAP.get(breakIterator.getRuleStatus()));
    }
  }
  
  private static String getTextFile(String path) {
  	InputStream inStream= WordTokenizer.class.getResourceAsStream(path);
  	String inList = "";
  	if (inStream != null) {
  		BufferedReader in= new BufferedReader(new InputStreamReader(inStream));
  		String nextLine;
  		try {   // Whole try-catch is a bit of a waste, but needed to satisfy readLine 
  			while( (nextLine= in.readLine()) != null) {
  				inList = inList + "\n" + nextLine;
  			}
  		} catch (IOException ex) {
  			System.out.println("something bad happened!");
  			System.exit(0);
  		}
  		return inList;
  	} else {
  		System.err.println("Couldn't find file: " + path);
  		return null;
  	}
  }
}
