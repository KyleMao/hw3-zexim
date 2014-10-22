package edu.cmu.lti.f14.hw3.hw3_zexim.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_zexim.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
    }

  }

  /**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   * 
   * @param doc
   *          input text
   * @return a list of tokens.
   */

  List<String> tokenize0(String doc) {
    List<String> res = new ArrayList<String>();

    for (String s : doc.split("\\s+"))
      res.add(s);
    return res;
  }

  /**
   * Create the sparse term frequency vector for each document.
   * 
   * @param jcas
   * @param doc   A document object, where the text is parsed into a term vector.
   */

  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();
    List<String> tokenStrings = tokenize0(docText);
    // A hash maps is used to count the frequencies for each term in the document
    Map<String, Integer> termFrequency = new HashMap<String, Integer>();
    for (String aTokenString : tokenStrings) {
      if (termFrequency.containsKey(aTokenString)) {
        Integer frequency = termFrequency.get(aTokenString);
        frequency++;
        termFrequency.put(aTokenString, frequency);
      } else {
        termFrequency.put(aTokenString, 1);
      }
    }

    // Make the token list and add it to the document object
    List<Token> tokenList = new ArrayList<Token>();
    for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
      Token aToken = new Token(jcas);
      aToken.setText(entry.getKey());
      aToken.setFrequency(entry.getValue());
      tokenList.add(aToken);
    }

    doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokenList));
  }

}
