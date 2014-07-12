package diversity;

/**
 * This class wraps the movie id and the rating provided by the user
 * @author SatNam621
 *
 */
public class MovieWrapper {

	 Integer movieId;
	 Double score;
	 String label;
	 
	 protected MovieWrapper(Integer movieId, double score, String label){
		 this.movieId = movieId;
		 this.score = score;
		 this.label = label;
	 }
}
