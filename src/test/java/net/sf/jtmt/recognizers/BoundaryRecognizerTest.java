package net.sf.jtmt.recognizers;

import java.util.LinkedList;
import java.util.List;

import net.sf.jtmt.tokenizers.SentenceTokenizer;
import net.sf.jtmt.tokenizers.Token;
import net.sf.jtmt.tokenizers.WordTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for Boundary Recognizer.
 * @author Sujit Pal
 * @version $Revision$
 */
public class BoundaryRecognizerTest {

  private final Log log = LogFactory.getLog(getClass());
  
  private static BoundaryRecognizer recognizer;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    recognizer = new BoundaryRecognizer();
    recognizer.init();
  }

  @Test
  public void testRecognizeBoundaries() throws Exception {
    String paragraph = "Jaguar will sell its new XJ-6 model in the U.S. for " +
      "a small fortune :-). Expect to pay around USD 120ks. Custom options can " +
      "set you back another few 10,000 dollars. For details, go to " +
      "<a href=\"http://www.jaguar.com/sales\" alt=\"Click here\">Jaguar Sales</a> " +
      "or contact xj-6@jaguar.com.";
    SentenceTokenizer sentenceTokenizer = new SentenceTokenizer();
    sentenceTokenizer.setText(paragraph);
    WordTokenizer wordTokenizer = new WordTokenizer();
    List<Token> tokens = new LinkedList<Token>();
    String sentence = null;
    while ((sentence = sentenceTokenizer.nextSentence()) != null) {
      wordTokenizer.setText(sentence);
      Token token = null;
      while ((token = wordTokenizer.nextToken()) != null) {
        tokens.add(token);
      }
      List<Token> recognizedTokens = recognizer.recognize(tokens);
      for (Token recognizedToken: recognizedTokens) {
        log.debug("token=" + recognizedToken.getValue() + " [" + recognizedToken.getType() + "]");
      }
      tokens.clear();
    }
  }
}