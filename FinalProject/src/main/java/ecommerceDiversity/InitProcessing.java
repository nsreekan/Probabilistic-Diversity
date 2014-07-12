package ecommerceDiversity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;


/**
 * @author SatNam621
 *
 */
public class InitProcessing {
	
	LinkedHashMap <String,ArrayList<String>> itemUserMatrixRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,ArrayList<String>> itemUserMatrixNonRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,ArrayList<String>> userItemMatrix = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,ArrayList<String>> userItemMatrixRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,ArrayList<String>> userItemMatrixNonRelevant = 
			new LinkedHashMap<String,ArrayList<String>>();
	LinkedHashMap <String,String> itemDetails = 
			new LinkedHashMap<String,String>();
	LinkedHashMap <String,String> userDetails = 
			new LinkedHashMap<String,String>();
	
	public static void main(String args[]){
		InitProcessing ipc = new InitProcessing();
		String filePath = ipc.readFileNameFromArgs(args);
		ipc.parseDataIntoItemUserMatrix(filePath);		
		BayesianRelevanceClassifier classifier = new BayesianRelevanceClassifier(ipc.itemUserMatrixRelevant,ipc.itemUserMatrixNonRelevant,
				ipc.itemDetails,ipc.userDetails,ipc.userItemMatrix);
		ArrayList<String> mostProbableItems = new ArrayList<String>();
		int[] k = new int[]{1,10,20,30};
		int[] l = new int[]{30};
		for(int x: k){
			for (int y :l){
				RelevanceOptimizer.getDiversifiedResults(ipc.itemUserMatrixRelevant,ipc.itemUserMatrixNonRelevant,
						ipc.itemDetails,ipc.userDetails,ipc.userItemMatrix,classifier,ipc.userItemMatrixRelevant,x,y);
			}
		}
		
		
		
	}
	/**
	 * Parses the user item rating data and stores them into
	 * collections.
	 * @param filePath
	 */
	@SuppressWarnings("unused")
	private void parseDataIntoItemUserMatrix(String filePath) {
		File file = new File(filePath);
		
		int i =0;
		if (file.isFile()) {
			try {
				Charset charset = Charset.forName("UTF-8");				
				LineIterator content = FileUtils.lineIterator(file);				
				String productId = null;
				String productTitle = null;
				String profileName = null;
				String profileId = null;
				Double score = null;
				
				
				while(content.hasNext()){					
					String line = content.nextLine();
					if(line.startsWith("product/productId:")){
						productId = line.split("product/productId:")[1].trim();						
					}
					else if(line.startsWith("product/title:")){
						productTitle = line.split("product/title:")[1].trim();
					}
					else if(line.startsWith("review/userId:")){
						profileId = line.split("review/userId:")[1].trim();
						if(profileId.trim().equals("unknown")){
							profileId = profileId.concat(""+i).trim();
						}
					}
					else if(line.startsWith("review/profileName:")){
						profileName = line.split("review/profileName:")[1].trim();						
					}
					else if(line.startsWith("review/score:")){
						score = Double.valueOf(line.split("review/score:")[1].trim());						
					}
					else if(line.startsWith("review/text:")){
						if(score != null && score > 3.0){
							if(itemUserMatrixRelevant.containsKey(productId)){
								ArrayList<String> userIdForItem = itemUserMatrixRelevant.get(productId);
								if(!userIdForItem.contains(profileId)){
									userIdForItem.add(profileId);
								}
								itemUserMatrixRelevant.put(productId,userIdForItem);

							}else {
								ArrayList<String> userIdForItem = new ArrayList<String>();
								userIdForItem.add(profileId);
								itemUserMatrixRelevant.put(productId, userIdForItem);
							}
							
							if(userItemMatrixRelevant.containsKey(profileId)){
								ArrayList<String> userIdForItem = userItemMatrixRelevant.get(profileId);
								if(!userIdForItem.contains(productId)){
									userIdForItem.add(productId);
								}
								userItemMatrixRelevant.put(profileId,userIdForItem);

							}else {
								ArrayList<String> userIdForItem = new ArrayList<String>();
								userIdForItem.add(productId);
								userItemMatrixRelevant.put(profileId, userIdForItem);
							}
							
						}else {
							if(itemUserMatrixNonRelevant.containsKey(productId)){
								ArrayList<String> userIdForItem = itemUserMatrixNonRelevant.get(productId);
								if(!userIdForItem.contains(profileId)){
									userIdForItem.add(profileId);
								}
								itemUserMatrixNonRelevant.put(productId,userIdForItem);

							}else {
								ArrayList<String> userIdForItem = new ArrayList<String>();
								userIdForItem.add(profileId);
								itemUserMatrixNonRelevant.put(productId, userIdForItem);
							}
						}
						if(userItemMatrix.containsKey(profileId)){
							ArrayList<String> itemForUserId = userItemMatrix.get(profileId);
							if(!itemForUserId.contains(productId)){
								itemForUserId.add(productId);
							}	
							userItemMatrix.put(profileId,itemForUserId);
						}else {
							ArrayList<String> itemForUserId = new ArrayList<String>();
							itemForUserId.add(productId);
							userItemMatrix.put(profileId, itemForUserId);
						}
						i++;
						if(productId.equals("B00006690A")){
							System.out.println(productId);
						}
						itemDetails.put(productId, productTitle);
						userDetails.put(profileId, profileName);
					}
				}
				removeNoise();
				if(itemDetails.containsKey("B00006690A")){
					System.out.println(productId);
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}	
		System.out.println("total size of the itemUserMatrixRelevant is "+ itemUserMatrixRelevant.size());
		System.out.println("total size of the itemUserMatrixNonRelevant is "+ itemUserMatrixNonRelevant.size());
		System.out.println("total size of the userItemMatrix is "+ userItemMatrix.size());
		System.out.println("total size of ratings for all items "+ i);
	}
	
	/**
	 * removeNoise() methods removes inconsistent data and missing values. For 
	 * consistency where are there are reviews relevant and non relevant for
	 * each item are considered.
	 */
	private void removeNoise() {
		Set<String> nonRel  = itemUserMatrixNonRelevant.keySet();
		Set<String> rel  = itemUserMatrixRelevant.keySet();
		Set<String> noise  =  new LinkedHashSet<String>(rel);
		Set<String> iter  =  new LinkedHashSet<String>(rel);
		Set<String> niter  =  new LinkedHashSet<String>(nonRel);
		noise.retainAll(nonRel);
		iter.removeAll(noise);
		niter.removeAll(noise);
		System.out.println(userItemMatrix.size());
		System.out.println(userItemMatrixRelevant.size());
		System.out.println(itemUserMatrixRelevant.size());
		System.out.println(itemUserMatrixNonRelevant.size());
		
		for(String unwanted: iter){
			itemUserMatrixRelevant.remove(unwanted);
			itemUserMatrixNonRelevant.remove(unwanted);
		}
		
		for(Entry<String, ArrayList<String>> entry : userItemMatrix.entrySet()){
			entry.getValue().removeAll(iter);
		}
		for(Entry<String, ArrayList<String>> entry : userItemMatrixRelevant.entrySet()){
			entry.getValue().removeAll(iter);
		}
		
		for(String unwanted: niter){
			itemUserMatrixRelevant.remove(unwanted);
			itemUserMatrixNonRelevant.remove(unwanted);
		}
		
		for(Entry<String, ArrayList<String>> entry : userItemMatrix.entrySet()){
			entry.getValue().removeAll(niter);
		}
		for(Entry<String, ArrayList<String>> entry : userItemMatrixRelevant.entrySet()){
			entry.getValue().removeAll(niter);
		}
		
		System.out.println(userItemMatrix.size());
		System.out.println(userItemMatrixRelevant.size());
		System.out.println(itemUserMatrixRelevant.size());
		System.out.println(itemUserMatrixNonRelevant.size());
		
	    int maxSize = 0; String userid = "";
		for(Entry<String, ArrayList<String>> entry : userItemMatrixRelevant.entrySet()){
			if(entry.getValue().size()>maxSize){
				maxSize = entry.getValue().size();
				userid = entry.getKey();
			}
		}
		System.out.println("Choose user " + userid);
	}

	private String readFileNameFromArgs(String[] args) {
		String filePath = "";
		try{
			if(args != null){
				filePath = args[0];
			}
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
		return filePath;
	}
}
