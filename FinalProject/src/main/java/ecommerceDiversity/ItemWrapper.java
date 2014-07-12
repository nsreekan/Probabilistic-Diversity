package ecommerceDiversity;

/**
 * This class encapsulates the Item along with its user rating
 * @author SatNam621
 *
 */
public class ItemWrapper {

	String itemId;
	Double score;
	String label;
	
	protected ItemWrapper(String itemId, Double score,String label){
		this.itemId = itemId;
		this.score = score;
		this.label = label;
	}
}
