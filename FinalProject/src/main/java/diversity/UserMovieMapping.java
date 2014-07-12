package diversity;
/**
 * This provides as a wrapper for user and user rated movies
 * @author SatNam621
 *
 */
public class UserMovieMapping {
	
	Integer movieId;
	Integer rating;
	Integer userId;
	
	UserMovieMapping(Integer movieId, Integer rating, Integer userId){
		this.movieId = movieId;
		this.rating = rating;
		this.userId = userId;
	}
	
	

}
