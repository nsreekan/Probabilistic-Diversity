package diversity;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class is designed for the Amazon data set to provide useful recommendations
 * to the user based on ratings.
 * @author SatNam621
 *
 */
public class DiversityBuilder { 

	/**
	 * This method starts the execution of classifying Movie data set 
	 * as relevant or non relevant
	 * @param args file name
	 */
	public static void main(String[] args) { 
		DiversityBuilder builder = new DiversityBuilder();
		String filePath = builder.readFileNameFromArgs(args);
		MovieDataPreProcessor mdpp = new MovieDataPreProcessor(filePath);
		mdpp.readDataFiles();
		BayesianClassifier bc = new BayesianClassifier();
		// hard wired the executions - to reduce the output generations
		int[] k = new int[]{10};
		int[] l = new int[]{20};
		for(int x: k){
			for (int y :l){
				RelevanceOptimizer.getDiversifiedResults(bc,mdpp, x,y);
			}
		}
				
		RelevanceOptimizer.getPRPResults(bc,mdpp, 30);
		
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
