/**
 * 
 */
package ecommerceDiversity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

/**
 * This class classifies and recommends relevant items in the list and also 
 * induce diversity in recommendation lists.
 * @author SatNam621
 *
 */
public class BayesianRelevanceClassifier {
	
	LinkedHashMap <String,ArrayList<String>> itemUserMatrixRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,ArrayList<String>> itemUserMatrixNonRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,String> itemDetails = 
			new LinkedHashMap<String,String>();
	LinkedHashMap <String,String> userDetails = 
			new LinkedHashMap<String,String>();
	LinkedHashMap <String,ArrayList<String>> userItemMatrix = 
			new LinkedHashMap<String,ArrayList<String>>();
	
	public BayesianRelevanceClassifier(LinkedHashMap <String,ArrayList<String>> itemUserMatrixRelevant,
			LinkedHashMap <String,ArrayList<String>> itemUserMatrixNonRelevant,	LinkedHashMap <String,String> itemDetails,
			LinkedHashMap <String,String> userDetails, LinkedHashMap<String, ArrayList<String>> userItemMatrix ){
			this.itemUserMatrixRelevant = itemUserMatrixRelevant;
			this.itemUserMatrixNonRelevant = itemUserMatrixNonRelevant;
			this.userDetails = userDetails;
			this.itemDetails = itemDetails;
			this.userItemMatrix =userItemMatrix;
	}
	
	protected List<Entry<String,ItemWrapper>> getMostRelevantItems(String userId){
		// for each item compute the relevancy for that user
		// items are those that are not already rated by the user
		// P(Item1="Like"|user1="U1")= P(User=u1|Item1=likes)* P(User=u2|Item1=likes) * P(Item1=likes)
		List<Entry<String,ItemWrapper>> itemRanking = null;
		if(userItemMatrix.containsKey(userId)){
			ArrayList<String> itemsRatedByUser = userItemMatrix.get(userId);
			Set<String> allItems = itemDetails.keySet();
			Set<String> intersect = new LinkedHashSet<String>(allItems);
			Set<String> allItemDup = new LinkedHashSet<String>(allItems);
			intersect.retainAll(itemsRatedByUser);
			itemsRatedByUser.contains("B00004W64W");
			allItemDup.removeAll(intersect);
			allItemDup.contains("B00004W64W");
			TreeMap<String,ItemWrapper> itemScore = getScoresForMostLikelyItem(allItemDup);
			itemScore.containsKey("B00004W64W");
			itemRanking = entriesSortedByValues(itemScore);
			
			
		}
		
		return itemRanking;
		
	}

	/**
	 * makeConfusionMatrix develops the confusion matrix for evaluating the performance of
	 * classfiers on Amazon data set
	 * @param itemRanking 
	 * @param userItemMatrix
	 * @param userItemMatrixRelevant
	 * @param k
	 * @param q
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	protected double[] makeConfusionMatrix(List<Entry<String, ItemWrapper>> itemRanking, LinkedHashMap<String, 
			ArrayList<String>> userItemMatrix, LinkedHashMap<String, ArrayList<String>> userItemMatrixRelevant, 
			int k,int q, String userId) throws IOException {
		double matrix[] = new double[5];
		int TP = 0,TN=0, FP=0,FN=0,n=0;
		ArrayList<String> al=  userItemMatrix.get(userId);
		n += al.size();
		TreeMap<String, ItemWrapper> scores= getScoresForMostLikelyItem(new LinkedHashSet<String>(al));
		Collection<ItemWrapper> results= scores.values();
		ArrayList<String> relevantForUser = userItemMatrixRelevant.get(userId);
		for(ItemWrapper item: results){				
			if(item.label.equals("R")){			
				if(relevantForUser != null && relevantForUser.contains(item.itemId)){
					TP++;
				}else if(relevantForUser != null && !relevantForUser.contains(item.itemId)){
					FP++;
				}
			} else if(item.label.equals("NR")){
				if(relevantForUser != null && !relevantForUser.contains(item.itemId)){
					TN++;
				}else if(relevantForUser != null && relevantForUser.contains(item.itemId)){
					FN++;
				}
			}
		}

		matrix[0]= ((double)TP/(TP+FN));//recall
		matrix[1] = ((double)TP/(TP+FP));//precision
		matrix[2] = (((double)(2*matrix[0]*matrix[1]))/(matrix[0]+matrix[1]));//precision
		matrix[3] = ((double)(TP+TN))/n;// accuracy
		matrix[4] = ((double)FP/(FP+TN)); // fall out

		
		try {
			FileUtils.writeStringToFile(new File("C:\\temp\\Output_DM\\AMZ\\PR_amzk="+k+"n="+q+".txt"), ""+n+"\t"+matrix[0]+"\t"+matrix[1]+"\t"+matrix[2]+"\t"+ matrix[3]+"\t"+matrix[4]+"\t"+"\n",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matrix;
	}


	/**
     * Uses the PRP principle to retrieve the most likely class for the item 
     * by classfiying it as relevant or non relevant and choosing the highest
     * scoring class
     * @param allItems
     * @return list of items ranked by relevance
     */
	private TreeMap<String, ItemWrapper> getScoresForMostLikelyItem(
			Set<String> allItems) {
		TreeMap<String,ItemWrapper> itemScore = new TreeMap<String,ItemWrapper>();
		for(String eachItem:allItems){
			
			if(!itemUserMatrixRelevant.containsKey(eachItem) || !itemUserMatrixNonRelevant.containsKey(eachItem)){
				continue;
			}
			double priorRelevant = calculatePrior("Relevant",eachItem);
			double priorNonRelevant = calculatePrior("NonRelevant",eachItem);
			
			Double probRelevant =1.0, probNonRelevant=1.0;
			double size = itemUserMatrixRelevant.get(eachItem).size();
			if(itemUserMatrixRelevant.containsKey(eachItem)){
				
				ArrayList<String> userWhoRatedThisItem = itemUserMatrixRelevant.get(eachItem);
				for(String eachUser :userWhoRatedThisItem) {
					probRelevant *= ((double)(1+1)/(double)(size+2));
				}	
				
			}
			if(itemUserMatrixNonRelevant.containsKey(eachItem)){
				
				ArrayList<String> userWhoRatedThisItem = itemUserMatrixNonRelevant.get(eachItem);
				for(String eachUser :userWhoRatedThisItem) {
					probRelevant *= ((double)(0+1)/(double)(size+2));
				}
			}
			
			probRelevant *= (priorRelevant);
			 size = itemUserMatrixNonRelevant.get(eachItem).size();
			if(itemUserMatrixNonRelevant.containsKey(eachItem)){
				
				ArrayList<String> userWhoRatedThisItem = itemUserMatrixNonRelevant.get(eachItem);
				for(String eachUser :userWhoRatedThisItem) {
					probNonRelevant *= ((double)(1+1)/(double)(size+2));
				}
			}
			if(itemUserMatrixRelevant.containsKey(eachItem)){
				ArrayList<String> userWhoRatedThisItem = itemUserMatrixRelevant.get(eachItem);
				for(String eachUser :userWhoRatedThisItem) {
					probNonRelevant *= ((double)(0+1)/(double)(size+2));
				}
			} 
		
			probNonRelevant *= (priorNonRelevant);
		
			
			if(probRelevant >= probNonRelevant){
				//itemScore.put(probRelevant, eachItem);
				ItemWrapper wr = new ItemWrapper(eachItem,probRelevant,"R" );
				itemScore.put(eachItem, wr);
			}else {
				ItemWrapper wr = new ItemWrapper(eachItem,probNonRelevant,"NR" );
				itemScore.put(eachItem, wr);
			}
			
		}
		return itemScore;
	}
	
	/**
	 * Sort ranked items by the scores of highest relevance
	 * @param map
	 * @return entries that are sorted decreasing by their relance
	 */
	static
	List<Entry<String,ItemWrapper>> entriesSortedByValues(Map<String,ItemWrapper> map) {
		List<Entry<String,ItemWrapper>> sortedEntries = new ArrayList<Entry<String,ItemWrapper>>(map.entrySet());
		Collections.sort(sortedEntries, 
				new Comparator<Entry<String,ItemWrapper>>() {
			@Override
			public int compare(Entry<String,ItemWrapper> e1, Entry<String,ItemWrapper> e2) {
				return ((e2.getValue().score).compareTo(e1.getValue().score)); 
			}
		});
		return sortedEntries;
	}

	static double log(double d)
	{
		//return ((double)Math.log(d) / Math.log(2));
		return Math.log10(d);
	}
	
	/**
	 * calculatePrior method computes the prior for the relevant and non relevant classes
	 * needed for the Bayesian classifier
	 * @param classType - relevant prior / non - relevant prior
	 * @param eachItem 
	 * @return priori for the relevant/ non-relevant distribution
	 */
	private double calculatePrior(String classType, String eachItem) {
		double prior=0.0;
		double size = getSizeForItem(eachItem);
		
			
		if(classType.equals("Relevant")){
			if(itemUserMatrixRelevant.containsKey(eachItem)){
				prior = ((itemUserMatrixRelevant.get(eachItem).size())/size);
				
			}
		}else {
			if(itemUserMatrixNonRelevant.containsKey(eachItem)){
				prior = ((itemUserMatrixNonRelevant.get(eachItem).size())/size);
				
			}
		}
		
		return prior;
	}
    /**
     * getSizeForItem get the Total number of relevant or non relavant size
     * for class distribution 
     * @param eachItem
     * @return the size of the items
     */
	private double getSizeForItem(String eachItem) {
		double size=0.0;
		if(itemUserMatrixRelevant.containsKey(eachItem)){
			size += itemUserMatrixRelevant.get(eachItem).size();
		}
		if(itemUserMatrixNonRelevant.containsKey(eachItem)){
			size += itemUserMatrixNonRelevant.get(eachItem).size();
		}
		return size;
		
	}

}
