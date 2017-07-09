import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels 2 labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues;


  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    // build the tree
    this.root = buildTree(train.instances,this.attributes,null, majorityClass(train.instances));
  }

  /**
   * Build a decision tree
   * 
   * @param instances: the list of instances
   * @param attributes: list of attributes 
   * @param parentAttributeValue: the parent attribute value
   * @param defaultLabel: 
   */

  private DecTreeNode buildTree(List<Instance> instances, 
      List<String> attributes, 
      String parentAttributeValue,
      String defaultLabel) {

    // if instances empty, return default label
    if(instances.isEmpty()) {
      return new DecTreeNode(defaultLabel, null, parentAttributeValue, true);
    }

    // if instances all have the sample label then return the label
    if(allSameLabel(instances)) {

      return new DecTreeNode(instances.get(0).label,null, parentAttributeValue, true);

    }

    // if no attribute left, return majority-class of examples
    if(attributes.isEmpty()) {
      return new DecTreeNode(majorityClass(instances), null, parentAttributeValue, true);
    }

    String bestAttr = bestAttribute(instances, attributes);

    // create tree with the best attribute
    DecTreeNode tree = new DecTreeNode(majorityClass(instances), bestAttr, parentAttributeValue, false);

    // for each value in the best attribute
    for (String attrValue : this.attributeValues.get(bestAttr)) {

      List<Instance> subSetInstances = new ArrayList<Instance>();

      int attrIndex = getAttributeIndex(bestAttr);

      // save the instances with the attribute value to a sub set of instances
      for (Instance instance : instances) {
        if(instance.attributes.get(attrIndex).equals(attrValue)) {

          subSetInstances.add(instance);

        }
      }
      // remove the best attribute from the list
      // store as a sub set of attribute
      List <String> subAttrs = new ArrayList<String>(attributes);
      subAttrs.remove(bestAttr);

      tree.addChild(buildTree(subSetInstances, subAttrs, attrValue, majorityClass(instances)));

    }

    return tree;
  }

  /**
   * find the best attribute with maximum information gain
   * @param instances: the list of instances
   * @param attributes: list of attributes 
   */
  private String bestAttribute(List<Instance> instances, List<String> attributes) {
    // TODO Auto-generated method stub
    String bestAttr = attributes.get(0);
    double maxInfoGain = Double.MIN_VALUE;

    for (String attr : attributes) {
      // current information gain
      double currInfoGain =  getEntropy (instances) - getConEntropy(instances, attr);

      // if the current information gain is bigger than the maximum info gain
      if (currInfoGain > maxInfoGain) {
        maxInfoGain = currInfoGain;
        bestAttr = attr;
      }
    }

    return bestAttr;


  }

  /**
   * find the majority class of the instances
   * @param instances: the list of instances
   */

  private String majorityClass(List<Instance> instances) {

    // use array to store the number of labels for each label (binary label)
    int [] numLabels = new int[2];

    for (Instance ins : instances) {
      numLabels[getLabelIndex(ins.label)]++;
    }

    // return the majority vote
    if (numLabels[0] >= numLabels[1]) {
      return labels.get(0);
    } else {
      return labels.get(1);
    }

  }

  /**
   * check if the instances have the same label
   * @param instances: the list of instances
   */
  private boolean allSameLabel(List<Instance> instances) {
    // TODO Auto-generated method stub

    String initLabel = instances.get(0).label;

    for (Instance ins :instances) {

      if(!initLabel.equals(ins.label)) {
        return false;
      }
    }

    return true;

  }

  /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * 
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    // build the tree
    this.root = buildTree(train.instances,this.attributes,null, majorityClass(train.instances));

    prune (root, tune);

  }

  /**
   * Pruning method
   * 
   * @param tune: the tuning set
   */
  private void prune(DecTreeNode initTree,  DataSet tune) {

    // repeat until no more improvement
    boolean isBetter = true;
    
    while (isBetter) {

      double initAccuracy = getAccuracy(tune);
      double maxAccuracy = 0.0;
      // initialize the new tree
      DecTreeNode newTree = initTree;

      // use BFS to traverse all the tree nodes
      // for every internal node N in T
      Queue <DecTreeNode> queue = new LinkedList<DecTreeNode>();
      queue.add(initTree);
      // find the subtree with max accuracy
      
      while (!queue.isEmpty()) {
        DecTreeNode currNode = queue.poll();
        if (currNode.terminal) {
          continue;
        }
        
        // the current node children
        List<DecTreeNode> currChildren = currNode.children;

        // Prune the children of the current node
        // current node becomes the new leaf node
        currNode.children = null;
        currNode.terminal = true;
        double currAccuracy = getAccuracy(tune);

        // if the current accuracy larger than the max accuracy
        // set the current node as the new tree
        if (currAccuracy > maxAccuracy) {
          maxAccuracy = currAccuracy;
          newTree = currNode;
        }

        // Add the children of the current node to the queue.
        for (DecTreeNode child : currChildren) {
          queue.add(child);
        }

        // reset the current node and set it as non-leaf node
        currNode.children = currChildren;
        currNode.terminal = false;
      }

      // check if there is improvement
      if (maxAccuracy >= initAccuracy) {
        newTree.children = null;
        newTree.terminal = true;
      } else {
        isBetter = false;
      }
    }
  }

  @Override
  public String classify(Instance instance) {

    // TODO: add code here
    DecTreeNode curr = this.root;

    while (!curr.terminal) {

      // get the index of the attribute of the current node
      int attrIndex = getAttributeIndex(curr.attribute);
      // get the attribute value of the current attribute
      String attrValue = instance.attributes.get(attrIndex);
      // get the index of the attribute value
      int attrValueIndex = getAttributeValueIndex(curr.attribute, attrValue);
      // get the leaf node
      curr = curr.children.get(attrValueIndex);

    }

    return curr.label;

  }

  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here

    for (int i = 0; i < attributes.size(); i++) {

      double infoGain = getEntropy(train.instances) - getConEntropy(train.instances, attributes.get(i));
      System.out.print( attributes.get(i)  + " ");
      System.out.format("%.5f\n", infoGain);

    }
  }


  /**
   * get the entropy of the attributes
   * @param instances: the list of instances
   */
  private double getEntropy(List<Instance> instances) {
    // TODO Auto-generated method stub

    // use an array to store the number of labels for each instance 
    int [] numLabels = new int[labels.size()];

    for (Instance instance : instances) {
      numLabels[getLabelIndex(instance.label)]++;

    }
    double entropy = 0.0; 
    for (int numLabel : numLabels){

      double prob = (double) numLabel / instances.size();
      entropy += -prob * (Math.log(prob)/Math.log(2));

    }

    return entropy;
  }

  /**
   * get the conditional entropy of the attributes 
   * @param instances: the list of instances
   * @param attr: the attribute
   */
  private double getConEntropy(List<Instance> instances, String attr) {

    int numAttrValue = attributeValues.get(attr).size();
    int [][] numLabels = new int [numAttrValue][labels.size()];
    int attrIndex = getAttributeIndex(attr);

    for (Instance instance: instances) {
      int attrValueIndex = getAttributeValueIndex(attr, instance.attributes.get(attrIndex));
      numLabels[attrValueIndex][getLabelIndex(instance.label)]++;
    }


    // calculate specific conditional entropy
    double totalConEntropy = 0.0;

    for (int i = 0; i < numAttrValue; i++) {
      int numValue = 0;

      for (int j = 0; j < labels.size(); j++) {
        numValue += numLabels[i][j];
      }

      double conEntropy = 0.0;
      for (int j = 0; j < labels.size(); j++) {
        if (numLabels[i][j] == 0)  {
          continue;
        }

        double prob = (double)  numLabels[i][j] / numValue;
        conEntropy += -prob * (Math.log(prob)/Math.log(2));
      }
      totalConEntropy += (double) numValue/instances.size() *conEntropy;
    }
    return totalConEntropy;
  }


  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {

    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }


  /**
   * Returns the accuracy of the decision tree on a given DataSet.
   */
  @Override
  public double getAccuracy(DataSet ds){
    //TODO, compute accuracy

    int correct = 0; 
    int incorrect = 0;
    for (Instance instance : ds.instances){
      if (instance.label.equals(classify(instance))) {
        correct++;
      }
      else {
        incorrect++;
      }
    }
    double accuracy = (double) (correct / (double) (correct + incorrect));
    return accuracy;

  }

}
