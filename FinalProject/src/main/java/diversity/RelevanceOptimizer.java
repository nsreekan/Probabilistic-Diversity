package diversity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

/**
 * 
 */

/**
 * This class optimizes the relevant results by retrieving atleast one relevant result
 * and introduces diversity by number of relevant results 
 * @author SatNam621
 *
 */
public class RelevanceOptimizer {

    /**
     * getDiversifiedResults - Get diversified results using the Bayesian classifier 
     * @param bc - reference for the bayesian classifier
     * @param mdpp - movie data pre processor
     * @param k - diversity parameter
     * @param n - training set size
     */
	protected static void getDiversifiedResults(BayesianClassifier bc,MovieDataPreProcessor mdpp, int k, int n){
		System.out.println("Inducing Diversity****************\n");
		System.out.println("How many Relevant results "+k+"\n");
		System.out.println("How many Ranked results "+n+"\n");

		Set<Integer> userIds = mdpp.userIdMovieMap.keySet();
		Integer userId = 196;
		int it =0;
		int ph = k;
		System.out.println("****User Likes*****\n");
		Set <Integer> liked = new LinkedHashSet<Integer>();
		for(UserMovieMapping movieId :mdpp.userIdMovieMap.get(userId)){
			if(movieId.rating >=3.0){
				liked.add(movieId.movieId);
			}
			System.out.println(mdpp.mMovieMap.get(movieId.movieId).movieName+"\n");
		}
		
		Set<Integer> diversifiedResults = new LinkedHashSet<Integer>();
		while(it <n){
			List<Integer> movieIdsFoundRelevant = new ArrayList<Integer>();
			List<Entry<Integer,MovieWrapper>> movieRankingsPRP = bc.getTopPRPMovieRanking(mdpp.getMovieMap(),mdpp.getUserMovieMap(),userId);
			// select the top PRP ranked movies
			bc.evaluatePerformance(it,k,n);

			movieIdsFoundRelevant = getRelevantMovies(movieIdsFoundRelevant,k,n,movieRankingsPRP);

			// with the top result movie ID which is most likely - retrieve it
			// remove it from user liked and give it a rating of 1 to make it non relevant
			if(k == 1){
				ArrayList<UserMovieMapping> userRatedMovies = mdpp.userIdMovieMap.get(userId);
				
				for(Integer movie : movieIdsFoundRelevant){
					userRatedMovies.add(new UserMovieMapping(movie,1,userId));

				}
				mdpp.userIdMovieMap.put(userId, userRatedMovies);
			}else if(k == n){
				ArrayList<UserMovieMapping> userRatedMovies = mdpp.userIdMovieMap.get(userId);
				
				for(Integer movie : movieIdsFoundRelevant){
					userRatedMovies.add(new UserMovieMapping(movie,5,userId));

				}
				bc.userIdMovieMap.put(userId, userRatedMovies);
			}else {
				if(ph>0){
					ArrayList<UserMovieMapping> userRatedMovies = mdpp.userIdMovieMap.get(userId);
					
					for(Integer movie : movieIdsFoundRelevant){
						userRatedMovies.add(new UserMovieMapping(movie,5,userId));
	
					}
					bc.userIdMovieMap.put(userId, userRatedMovies);
					ph--;
				}else {
					ArrayList<UserMovieMapping> userRatedMovies = mdpp.userIdMovieMap.get(userId);
					
					for(Integer movie : movieIdsFoundRelevant){
						userRatedMovies.add(new UserMovieMapping(movie,1,userId));

					}
					bc.userIdMovieMap.put(userId, userRatedMovies);
				}
			}	
			it++;
			if(! movieIdsFoundRelevant.isEmpty()){
				diversifiedResults.add(movieIdsFoundRelevant.get(0));
			}
		}
		
		
		double averageILS = bc.averageIntraListSimilarity(diversifiedResults, liked);
		System.out.println("Average Intra list Sim "+ averageILS);
		System.out.println("The diversified list of movie recommendations are \n");
		
		for(Integer movie : diversifiedResults){
			MovieBean m = mdpp.mMovieMap.get(movie);
			System.out.println("Movie name is "+ m.movieName);
		}
	}

	private static List<Integer>  getRelevantMovies(List<Integer> movieIdsFoundRelevant,
			int k, int n, List<Entry<Integer, MovieWrapper>> movieRankingsPRP) {
		for(Entry<Integer,MovieWrapper> result : movieRankingsPRP){
			
				if(result.getValue().label.equals("R")){

					movieIdsFoundRelevant.add(result.getValue().movieId);
					break;
				}
				else {
					continue;
				}
			
		}
		return movieIdsFoundRelevant;
	}
	
	private static List<Integer>  getRelevantMovies(List<Integer> movieIdsFoundRelevant,
			int n, List<Entry<Integer, MovieWrapper>> movieRankingsPRP) {
			for(Entry<Integer,MovieWrapper> result : movieRankingsPRP){
				if(result.getValue().label.equals("R")){
					movieIdsFoundRelevant.add(result.getValue().movieId);
					if(movieIdsFoundRelevant.size() == n){
						break;
					}	
				}
				else {
					continue;
				}

			}
		return movieIdsFoundRelevant;
	} 
    
	/**
	 * getPRPResults classifies the movie data set as relevant or non relevant
	 * @param bc the reference for the bayesian classifier
	 * @param mdpp the reference for the movie data pre processor
	 * @param provides the index for output files
	 */
	public static void getPRPResults(BayesianClassifier bc,
			MovieDataPreProcessor mdpp,int i) {
		System.out.println("Results from PRP ****************\n");
		Set<Integer> userIds = mdpp.userIdMovieMap.keySet();
		Integer userId = 196;
		int it =0;		
		System.out.println("****User Likes*****\n");
		List<Integer> movieIdsFoundRelevant = new ArrayList<Integer>();
		Set<Integer> likes = new LinkedHashSet<Integer>();
		for(UserMovieMapping movieId :mdpp.userIdMovieMap.get(userId)){
			
			System.out.println(mdpp.mMovieMap.get(movieId.movieId).movieName+"\n");
			if(movieId.rating>=3){
				if(movieId.rating>=3){
					likes.add(movieId.movieId);
				}
			}
		}
		
		
		List<Entry<Integer,MovieWrapper>> movieRankingsPRP = bc.getTopPRPMovieRanking(mdpp.getMovieMap(),mdpp.getUserMovieMap(),userId);
		// select the top PRP ranked movies
		bc.evaluatePerformance(it,0,i);

		movieIdsFoundRelevant = getRelevantMovies(movieIdsFoundRelevant,40,movieRankingsPRP);
		Set<Integer> result = new LinkedHashSet<Integer>();
		System.out.println("The most likely recommendations are \n");
		for(Integer movie : movieIdsFoundRelevant){
			MovieBean m = mdpp.mMovieMap.get(movie);
			System.out.println("Movie name is "+ m.movieName);
			result.add(m.movieId);
		}
		// compute the average list similarity for the recommended results
		double averageILS = bc.averageIntraListSimilarity(result, likes);
		System.out.println("ILS - PRP "+ averageILS);
	}
}
