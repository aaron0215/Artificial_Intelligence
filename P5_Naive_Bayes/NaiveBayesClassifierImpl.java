import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

  //THESE VARIABLES ARE OPTIONAL TO USE, but HashMaps will make your life much, much easier on this assignment.

  // number of training instances with the label
  private int numTragedy;
  private int numComedy;
  private int numHistory;
  
  //dictionaries of form word:frequency that store the number of times word w has been seen in documents of type label
  //for example, comedyCounts["mirth"] should store the total number of "mirth" tokens that appear in comedy documents
  private HashMap<String, Integer> tragedyCounts = new HashMap<String, Integer>();
  private HashMap<String, Integer> comedyCounts = new HashMap<String, Integer>();
  private HashMap<String, Integer> historyCounts = new HashMap<String, Integer>();

  //prior probabilities, ie. P(T), P(C), and P(H)
  //use the training set for the numerator and denominator
  private double tragedyPrior;
  private double comedyPrior;
  private double historyPrior;

  //total number of word TOKENS for each type of document in the training set, ie. the sum of the length of all documents with a given label
  private int tTokenSum;
  private int cTokenSum;
  private int hTokenSum;

  //full vocabulary, update in training, cardinality is necessary for smoothing
  private HashSet<String> vocabulary = new HashSet<String> ();


  /**
   * Trains the classifier with the provided training data
   * Should iterate through the training instances, and, for each word in the documents, update the variables above appropriately.
   * The dictionary of frequencies and prior probabilites can then be used at classification time.
   */
  @Override
  public void train(Instance[] trainingData) {
    // TODO : Implement
    
    // create a new hashmap
    HashMap<String,Integer> hash = null;

    for (Instance ins : trainingData) {
      if (ins.label == Label.TRAGEDY) {
        numTragedy++;
        tTokenSum += ins.words.length;
        hash = tragedyCounts;
      }

      if (ins.label == Label.COMEDY) {
        numComedy++;
        cTokenSum += ins.words.length;
        hash = comedyCounts;
      }
      
      if (ins.label == Label.HISTORY) {
        numHistory++;
        hTokenSum += ins.words.length;
        hash = historyCounts;
      }
      
      // add the word to the hashmap
      for (String word : ins.words) {
        
        if (hash.containsKey(word)) {
          hash.put(word, hash.get(word) + 1);
        } else {
          hash.put(word,1);
        }
        // add all the unique word to the vocabulary
        if (!vocabulary.contains(word)){
        vocabulary.add(word);
        }
      }
    }
  }

  /*
   * Prints out the number of documents for each label
   * A sanity check method
   */
  public void documents_per_label_count(){
    // TODO : Implement
    System.out.println(Label.TRAGEDY.toString() + "=" + numTragedy);
    System.out.println(Label.COMEDY.toString() + "=" + numComedy);
    System.out.println(Label.HISTORY.toString() + "=" + numHistory);
  }

  /*
   * Prints out the number of words for each label
	Another sanity check method
   */
  public void words_per_label_count(){
    // TODO : Implement
    System.out.println(Label.TRAGEDY.toString() + "=" +  tTokenSum);
    System.out.println(Label.COMEDY.toString() + "=" +  cTokenSum);
    System.out.println(Label.HISTORY.toString() + "=" +  hTokenSum);

  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
   */
  @Override
  public double p_l(Label label) {
    // TODO : Implement
    double total = (double) numTragedy + numComedy + numHistory;
    double labelPrior = 0.0;

    if (label == Label.TRAGEDY) {
      tragedyPrior = numTragedy / total;
      labelPrior = tragedyPrior;
    }

    if (label == Label.COMEDY) {
      comedyPrior = numComedy / total;
      labelPrior =  comedyPrior;
    }
    
    if (label == Label.HISTORY) {
      historyPrior = numHistory / total;
      labelPrior = historyPrior;
    }
   
    return labelPrior;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|COMEDY) or
   * P(word|HISTORY)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
    // TODO : Implement
    double conProb = 0.0;
    double delta = 0.00001;
    
    if (label == Label.TRAGEDY) {
      conProb = (c_w_l(word, label) + delta) / (vocabulary.size()*delta + tTokenSum); 
    }
    
    if (label == Label.COMEDY) {
      conProb = (c_w_l(word, label) + delta) / (vocabulary.size()*delta + cTokenSum);
    }
    
    if (label == Label.HISTORY) {
      conProb = (c_w_l(word, label) + delta) / (vocabulary.size()*delta + hTokenSum);
    }

    return conProb;
  }
  
  /**
   * Return the total number of w tokens (ie number of times “and” appears)
   * across all documents with label l in the training set.
   * 
   */
  private double c_w_l(String word, Label label) {
    double result = 0.0;
    if (label == Label.TRAGEDY) {
     if (tragedyCounts.containsKey(word)) {
       result = tragedyCounts.get(word);
     } else {
       result = 0.0;
     }
    }

    if (label == Label.COMEDY) {
      if (comedyCounts.containsKey(word)) {
        result = comedyCounts.get(word);
      } else {
        result = 0.0;
      }
    }
    
    if (label == Label.HISTORY) {
      if (historyCounts.containsKey(word)) {
        result = historyCounts.get(word);
      } else {
        result = 0.0;
      }
    }
  
    return result;
  }

  /**
   * Classifies a document as either a Comedy, History, or Tragedy.
   * Break ties in favor of labels with higher prior probabilities.
   */
  @Override
  public Label classify(Instance ins) {

    // TODO : Implement
    //Initialize sum probabilities for each label
    //For each word w in document ins
    //compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
    //add to appropriate sum
    //Return the Label of the maximal sum probability
    
    double tProbSum = Math.log(p_l(Label.TRAGEDY));
    double cProbSum = Math.log(p_l(Label.COMEDY));
    double hProbSum = Math.log(p_l(Label.TRAGEDY));
    Label result = null;
    
    for (String word : ins.words) {
      tProbSum += Math.log(p_w_given_l(word,Label.TRAGEDY));
      cProbSum += Math.log(p_w_given_l(word,Label.COMEDY));
      hProbSum += Math.log(p_w_given_l(word,Label.HISTORY));
    }
    
    if (tProbSum > cProbSum && tProbSum > hProbSum) {
      result = Label.TRAGEDY;
    }
    if (cProbSum > tProbSum && cProbSum > hProbSum) {
      result = Label.COMEDY;
    }

    if (hProbSum > tProbSum && hProbSum > cProbSum) {
      result = Label.HISTORY;
    }
    return result; 
  }
}
