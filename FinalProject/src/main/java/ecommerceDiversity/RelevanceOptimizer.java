package ecommerceDiversity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is an optimizes the recommendations for relevance and diversity
 * @author SatNam621
 *
 */
public class RelevanceOptimizer {

	/**
	 * This method returns the diversification of results 
	 * @param itemUserMatrixRelevant
	 * @param itemUserMatrixNonRelevant
	 * @param itemDetails
	 * @param userDetails
	 * @param userItemMatrix
	 * @param classifier
	 * @param userItemMatrixRelevant
	 * @param k parameter for diversity 
	 * @param n total number of results
	 */
	public static void getDiversifiedResults(LinkedHashMap<String, ArrayList<String>> itemUserMatrixRelevant,
			LinkedHashMap<String, ArrayList<String>> itemUserMatrixNonRelevant,
			LinkedHashMap<String, String> itemDetails, 
			LinkedHashMap<String, String> userDetails,
			LinkedHashMap<String, ArrayList<String>> userItemMatrix, BayesianRelevanceClassifier classifier,
			LinkedHashMap <String,ArrayList<String>> userItemMatrixRelevant, int k, int n){

		System.out.println("Inducing Diversity****************\n");
		System.out.println("How many Relevant results "+k+"\n");
		System.out.println("How many Ranked results "+n+"\n");
		
		int it =0;
		int ph = k;
		
		List<String> diversifiedResults = new ArrayList<String>();

		String userId = "A1RCGK44YXNBBB";
		while(it <n){
			List<Entry<String,ItemWrapper>> itemRanking = classifier.getMostRelevantItems(userId);

			try { 
				classifier.makeConfusionMatrix(itemRanking,userItemMatrix, userItemMatrixRelevant,k,n, userId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<String> itemIdsFoundRelevant = new ArrayList<String>();
			itemIdsFoundRelevant = getRelevantItems(itemIdsFoundRelevant,k,n,itemRanking); 


			if(k == 1){
				ArrayList<String> userRatedItems = userItemMatrix.get(userId);
				ArrayList<String> userLikedItems = userItemMatrixRelevant.get(userId);

				for(String item : itemIdsFoundRelevant){
					userRatedItems.add(item);					
					ArrayList<String> nonRel = itemUserMatrixNonRelevant.get(item);
					nonRel.add(userId);
					itemUserMatrixNonRelevant.put(item, nonRel);

				}
				userItemMatrix.put(userId,userRatedItems);

			} else if(k == n){
				ArrayList<String> userRatedItems = userItemMatrix.get(userId);
				ArrayList<String> userLikedItems = userItemMatrixRelevant.get(userId);

				for(String item : itemIdsFoundRelevant){
					userRatedItems.add(item);
					userLikedItems.add(item);
					ArrayList<String> rel = itemUserMatrixRelevant.get(item);
					rel.add(userId);
					itemUserMatrixRelevant.put(item, rel);

				}
				userItemMatrix.put(userId,userRatedItems);
				userItemMatrixRelevant.put(userId, userLikedItems);

			}else {
				if(ph>0){
					ArrayList<String> userRatedItems = userItemMatrix.get(userId);
					ArrayList<String> userLikedItems = userItemMatrixRelevant.get(userId);

					for(String item : itemIdsFoundRelevant){
						userRatedItems.add(item);
						userLikedItems.add(item);
						ArrayList<String> rel = itemUserMatrixRelevant.get(item);
						rel.add(userId);
						itemUserMatrixRelevant.put(item, rel);
					}
					userItemMatrix.put(userId,userRatedItems);
					userItemMatrixRelevant.put(userId, userLikedItems);
					ph--;
				}else {
					ArrayList<String> userRatedItems = userItemMatrix.get(userId);


					for(String item : itemIdsFoundRelevant){
						userRatedItems.add(item);
						ArrayList<String> nonRel = itemUserMatrixNonRelevant.get(item);
						nonRel.add(userId);

					}
					userItemMatrix.put(userId,userRatedItems);
				}
			}
			it++;
			diversifiedResults.add(itemIdsFoundRelevant.get(0));
		}
		
		System.out.println("************** Diversified Results ***********\n");
		System.out.println("User has liked -----------\n");
		ArrayList<String> list = userItemMatrixRelevant.get("A1RCGK44YXNBBB");
		
		for(String itemId : list){
			System.out.println("Item name " + itemDetails.get(itemId));
		}
		System.out.println("System has provided these recommendations-------\n");
		for(String itemId : diversifiedResults){
			System.out.println("Recommended Item name " + itemDetails.get(itemId));
		}

	}

	private static List<String> getRelevantItems(List<String> itemIdsFoundRelevant,
			int k, int n, List<Entry<String, ItemWrapper>> itemRanking) {
		for(Map.Entry<String, ItemWrapper> entry : itemRanking){
			if(entry.getValue().label.equals("R")){
				itemIdsFoundRelevant.add(entry.getValue().itemId);
				break;
			}
			else {
				continue;
			}
		}
		return itemIdsFoundRelevant;
	}
}
