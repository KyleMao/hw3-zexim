package edu.cmu.lti.f14.hw3.hw3_zexim.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.*;
import edu.cmu.lti.f14.hw3.hw3_zexim.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /** Name of configuration parameter that must be set to the path of the output file. **/
  public static final String PARAM_OUTPUT_FILE = "OutputFile";

  /** Query id number **/
  public ArrayList<Integer> qIdList;

  /** Query and text relevant values **/
  public ArrayList<Integer> relList;

  /** Document texts **/
  public ArrayList<String> textList;

  /** Term frequencies **/
  public ArrayList<Map<String, Integer>> termList;

  /** Map of query id and indices in lists **/
  public Map<Integer, Integer> indexMap;

  /** Map from query id to ReportEntry **/
  public Map<Integer, ReportEntry> scoreMap;

  private class ReportEntry {
    private int index;

    private double answerScore;

    private int rank;

    private List<Double> allScores;

    public ReportEntry() {
      allScores = new ArrayList<Double>();
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public double getAnswerScore() {
      return answerScore;
    }

    public void setAnswerScore(double score) {
      answerScore = score;
    }

    public int getRank() {
      return rank;
    }

    public void setRank(int rank) {
      this.rank = rank;
    }

    public void addScore(double score) {
      allScores.add(score);
    }

    public List<Double> getAllScores() {
      return allScores;
    }
  }

  @Override
  public void initialize() throws ResourceInitializationException {

    qIdList = new ArrayList<Integer>();
    relList = new ArrayList<Integer>();
    textList = new ArrayList<String>();
    termList = new ArrayList<Map<String, Integer>>();
    indexMap = new HashMap<Integer, Integer>();
    scoreMap = new TreeMap<Integer, ReportEntry>();
  }

  /**
   * 1. Construct the global word dictionary 2. Keep the word frequency for each sentence
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator<Annotation> it = jcas.getAnnotationIndex(Document.type).iterator();

    if (it.hasNext()) {
      Document doc = (Document) it.next();

      // Make sure that your previous annotators have populated this in CAS
      FSList fsTokenList = doc.getTokenList();
      ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
      Map<String, Integer> docVector = new HashMap<String, Integer>(tokenList.size());
      for (Token token : tokenList) {
        docVector.put(token.getText(), token.getFrequency());
      }

      qIdList.add(doc.getQueryID());
      relList.add(doc.getRelevanceValue());
      textList.add(doc.getText());
      termList.add(docVector);
      if (doc.getRelevanceValue() == 99) {
        indexMap.put(doc.getQueryID(), qIdList.size() - 1);
      }
    }
  }

  /**
   * 1. Compute Cosine Similarity and rank the retrieved sentences 2. Compute the MRR metric 3.
   * Generate report.txt
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    // Compute the cosine similarity measure
    for (int i = 0; i < qIdList.size(); i++) {
      if (relList.get(i) != 99) {
        int queryId = qIdList.get(i);
        double cosine_similarity = computeCosineSimilarity(termList.get(indexMap.get(queryId)),
                termList.get(i));
        if (!scoreMap.containsKey(queryId)) {
          scoreMap.put(queryId, new ReportEntry());
        }

        ReportEntry entry = scoreMap.get(queryId);
        if (relList.get(i) == 1) {
          entry.setIndex(i);
          entry.setAnswerScore(cosine_similarity);
          entry.addScore(cosine_similarity);
        } else {
          entry.addScore(cosine_similarity);
        }
      }
    }

    // Compute the rank of retrieved sentences
    for (ReportEntry entry : scoreMap.values()) {
      int rank = 1;
      double answerScore = entry.getAnswerScore();

      for (Iterator<Double> iterator = entry.getAllScores().iterator(); iterator.hasNext();) {
        Double score = iterator.next();
        // Here is the tie break
        if (score.compareTo(answerScore) > 0) {
          rank++;
        }
      }

      entry.setRank(rank);
    }

    // Compute the metric:: mean reciprocal rank
    double metric_mrr = compute_mrr();
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);

    // Generate the report file
    generateReport(metric_mrr);
  }

  private void generateReport(double mrr) {
    BufferedWriter writer = null;
    try {
      File outputFile = new File(((String) getConfigParameterValue(PARAM_OUTPUT_FILE)).trim());
      if (!outputFile.exists()) {
        outputFile.createNewFile();
      }
      writer = new BufferedWriter(new FileWriter(outputFile, false));
      for (Entry<Integer, ReportEntry> entry : scoreMap.entrySet()) {
        String outString = String.format("consine=%.4f\trank=%d\tqid=%d\trel=1\t%s\n", entry
                .getValue().getAnswerScore(), entry.getValue().getRank(), entry.getKey(), textList
                .get(entry.getValue().getIndex()));
        writer.write(outString);
      }
      writer.write(String.format("MRR=%.4f\n", mrr));
      writer.close();
    } catch (IOException e) {
      System.out.println("Creating report file failed!");
    }
  }

  /**
   * 
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;
    double query_norm = 0.0;
    double doc_norm = 0.0;

    // Compute cosine similarity between two sentences
    for (Integer term_frequency : queryVector.values()) {
      query_norm += term_frequency * term_frequency;
    }
    query_norm = Math.sqrt(query_norm);

    for (Integer term_frequency : docVector.values()) {
      doc_norm += term_frequency * term_frequency;
    }
    doc_norm = Math.sqrt(doc_norm);

    Set<String> matchTerms = new HashSet<String>(queryVector.keySet());
    matchTerms.retainAll(docVector.keySet());
    for (String term : matchTerms) {
      cosine_similarity += queryVector.get(term) * docVector.get(term);
    }

    cosine_similarity /= (query_norm * doc_norm);

    return cosine_similarity;
  }

  /**
   * 
   * @return mrr
   */
  private double compute_mrr() {
    double metric_mrr = 0.0;

    // Compute Mean Reciprocal Rank (MRR) of the text collection
    for (ReportEntry entry : scoreMap.values()) {
      metric_mrr += (double) entry.getRank() / entry.getAllScores().size();
    }
    metric_mrr /= scoreMap.size();

    return metric_mrr;
  }

}