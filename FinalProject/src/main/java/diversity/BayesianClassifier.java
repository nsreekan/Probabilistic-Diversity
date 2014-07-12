package diversity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap; 
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 * Bayesian Classifier classifies data using the Naive Bayes classifier to 
 * classify a movie as relevant or non relevant
 * @author SatNam621
 *
 */
public class BayesianClassifier {
	
	/*
	 * Initializing the movie map data sets
	 */
	LinkedHashMap<Integer,MovieBean> mMovieMap = new LinkedHashMap<Integer,MovieBean>();
	LinkedHashMap<Integer,ArrayList<UserMovieMapping>> userIdMovieMap = new LinkedHashMap<Integer,ArrayList<UserMovieMapping>>();


    /**
     * getTopPRPMovieRanking method generates the top movies for a user
     * by using Probability Ranking Principle.
     * @param movieMap contains the movie details
     * @param userMovieMap contains the user and user rated movies
     * @param userId user id for which recommendations have to be provided
     * @return
     */
	public List<Entry<Integer,MovieWrapper>> getTopPRPMovieRanking(
			LinkedHashMap<Integer, MovieBean> movieMap,
			LinkedHashMap<Integer, ArrayList<UserMovieMapping>> userMovieMap, Integer userId) {
		this.mMovieMap = movieMap;
		this.userIdMovieMap = userMovieMap;
		List<Entry<Integer,MovieWrapper>> movieRanking = null;
		movieRanking = computeTrainingSetForUser(userId);
		return movieRanking;
	}

	/**
	 * computeTrainingSetForUser computes the training set for the user
	 * @param userId - for which recommendations have to provided
	 * @return list of movie recommendations
	 */
	private List<Entry<Integer,MovieWrapper>>  computeTrainingSetForUser(Integer userId) {
		ArrayList<UserMovieMapping> userWatchedMovies = userIdMovieMap.get(userId);
		ArrayList<UserMovieMapping> relevantSet = new ArrayList<UserMovieMapping>();
		ArrayList<UserMovieMapping> nonRelevantSet = new ArrayList<UserMovieMapping>();
        LinkedHashSet<MovieBean> userRatedMovies = new LinkedHashSet<MovieBean>();
		for(UserMovieMapping movie : userWatchedMovies){
			int rating = movie.rating;
			
			if(rating >=3){
				relevantSet.add(movie);
			}else 
			{
				nonRelevantSet.add(movie);
			}
			userRatedMovies.add(mMovieMap.get(movie.movieId));
		}
		double relevantPrior = (double)relevantSet.size()/(double)userWatchedMovies.size();
		double nonRelevantPrior = (double)nonRelevantSet.size()/(double)userWatchedMovies.size();

		LinkedHashMap<String, ArrayList<Double>> classConditionals = trainDataSetForSingleUser(relevantPrior, 
				nonRelevantPrior,userWatchedMovies,userId,
				relevantSet,nonRelevantSet);
		 		
		LinkedHashSet<MovieBean> testSet = getTestSetForSingleUser(userRatedMovies);
		
		TreeMap<Integer,MovieWrapper> results = computeFirstRankPRP(testSet, classConditionals,relevantPrior, 
				nonRelevantPrior);
		// 	sort by values and return the highest ranking item as the 1st Relevant Item;
		
		List<Entry<Integer,MovieWrapper>> movieRanking = entriesSortedByValues(results);
		 
		return movieRanking;
	}

	/**
	 * computeFirstRankPRP - use the first result as the relevant item computed by
	 * PRP principle
	 * @param testSet - test set from where recommendations have to be provided
	 * @param classConditionals - class conditionals for which 
	 * @param relevantPrior - relevant set priori
	 * @param nonRelevantPrior - non relevant priori
	 * @return all the results from PRP principle
	 */
	private TreeMap<Integer,MovieWrapper> computeFirstRankPRP(LinkedHashSet<MovieBean> testSet,
			LinkedHashMap<String, ArrayList<Double>> classConditionals,double relevantPrior, 
			double nonRelevantPrior) {
		TreeMap<Integer,MovieWrapper> movieRankingPRP = new TreeMap<Integer,MovieWrapper>();
		ArrayList<Double> relevantClassConditionals = classConditionals.get("relevant");
		ArrayList<Double> nonRelevantClassConditionals = classConditionals.get("nonRelevant");
		 
		for(MovieBean mb :testSet){
			double probRelevant =1.0; double probNonRelevant =1.0;
			probRelevant *=  (mb.isUnknown)? (relevantClassConditionals.get(0)): (1-relevantClassConditionals.get(0));
			probRelevant *=  (mb.isAction)? (relevantClassConditionals.get(1)): (1-relevantClassConditionals.get(1));
			probRelevant *=  (mb.isAdventure)? (relevantClassConditionals.get(2)): (1-relevantClassConditionals.get(2));
			probRelevant *=  (mb.isAnimation)? (relevantClassConditionals.get(3)): (1-relevantClassConditionals.get(3));
			probRelevant *=  (mb.isChildrens)? (relevantClassConditionals.get(4)): (1-relevantClassConditionals.get(4));
			probRelevant *=  (mb.isComedy)? (relevantClassConditionals.get(5)): (1-relevantClassConditionals.get(5));
			probRelevant *=  (mb.isCrime)? (relevantClassConditionals.get(6)): (1-relevantClassConditionals.get(6));
			probRelevant *=  (mb.isDocumentary)? (relevantClassConditionals.get(7)): (1-relevantClassConditionals.get(7));
			probRelevant *=  (mb.isDrama)? (relevantClassConditionals.get(8)): (1-relevantClassConditionals.get(8));
			probRelevant *=  (mb.isFantasy)? (relevantClassConditionals.get(9)): (1-relevantClassConditionals.get(9));
			probRelevant *=  (mb.isFilmNoir)? (relevantClassConditionals.get(10)): (1-relevantClassConditionals.get(10));
			probRelevant *=  (mb.isHorror)? (relevantClassConditionals.get(11)): (1-relevantClassConditionals.get(11));
			probRelevant *=  (mb.isMusical)? (relevantClassConditionals.get(12)): (1-relevantClassConditionals.get(12));
			probRelevant *=  (mb.isMystery)? (relevantClassConditionals.get(13)): (1-relevantClassConditionals.get(13));
			probRelevant *=  (mb.isRomance)? (relevantClassConditionals.get(14)): (1-relevantClassConditionals.get(14));
			probRelevant *=  (mb.isSciFi)? (relevantClassConditionals.get(15)): (1-relevantClassConditionals.get(15));
			probRelevant *=  (mb.isThriller)? (relevantClassConditionals.get(16)): (1-relevantClassConditionals.get(16));
			probRelevant *=  (mb.isWar)? (relevantClassConditionals.get(17)): (1-relevantClassConditionals.get(17));
			probRelevant *=  (mb.isWestern)? (relevantClassConditionals.get(18)): (1-relevantClassConditionals.get(18));
			probRelevant *= (relevantPrior);

			probNonRelevant *=  (mb.isUnknown)? (nonRelevantClassConditionals.get(0)): (1-nonRelevantClassConditionals.get(0));
			probNonRelevant *=  (mb.isAction)? (nonRelevantClassConditionals.get(1)): (1-nonRelevantClassConditionals.get(1));
			probNonRelevant *=  (mb.isAdventure)? (nonRelevantClassConditionals.get(2)): (1-nonRelevantClassConditionals.get(2));
			probNonRelevant *=  (mb.isAnimation)? (nonRelevantClassConditionals.get(3)): (1-nonRelevantClassConditionals.get(3));
			probNonRelevant *=  (mb.isChildrens)? (nonRelevantClassConditionals.get(4)): (1-nonRelevantClassConditionals.get(4));
			probNonRelevant *=  (mb.isComedy)? (nonRelevantClassConditionals.get(5)): (1-nonRelevantClassConditionals.get(5));
			probNonRelevant *=  (mb.isCrime)? (nonRelevantClassConditionals.get(6)): (1-nonRelevantClassConditionals.get(6));
			probNonRelevant *=  (mb.isDocumentary)? (nonRelevantClassConditionals.get(7)): (1-nonRelevantClassConditionals.get(7));
			probNonRelevant *=  (mb.isDrama)? (nonRelevantClassConditionals.get(8)): (1-nonRelevantClassConditionals.get(8));
			probNonRelevant *=  (mb.isFantasy)? (nonRelevantClassConditionals.get(9)): (1-nonRelevantClassConditionals.get(9));
			probNonRelevant *=  (mb.isFilmNoir)? (nonRelevantClassConditionals.get(10)): (1-nonRelevantClassConditionals.get(10));
			probNonRelevant *=  (mb.isHorror)? (nonRelevantClassConditionals.get(11)): (1-nonRelevantClassConditionals.get(11));			
			probNonRelevant *=  (mb.isMusical)? (nonRelevantClassConditionals.get(12)): (1-nonRelevantClassConditionals.get(12));
			probNonRelevant *=  (mb.isMystery)? (nonRelevantClassConditionals.get(13)): (1-nonRelevantClassConditionals.get(13));
			probNonRelevant *=  (mb.isRomance)? (nonRelevantClassConditionals.get(14)): (1-nonRelevantClassConditionals.get(14));
			probNonRelevant *=  (mb.isSciFi)? (nonRelevantClassConditionals.get(15)): (1-nonRelevantClassConditionals.get(15));
			probNonRelevant *=  (mb.isThriller)? (nonRelevantClassConditionals.get(16)): (1-nonRelevantClassConditionals.get(16));
			probNonRelevant *=  (mb.isWar)? (nonRelevantClassConditionals.get(17)): (1-nonRelevantClassConditionals.get(17));
			probNonRelevant *=  (mb.isWestern)? (nonRelevantClassConditionals.get(18)): (1-nonRelevantClassConditionals.get(18));
			probNonRelevant *= (nonRelevantPrior);
            
			if(probRelevant > probNonRelevant){
				movieRankingPRP.put(mb.movieId, new MovieWrapper(mb.movieId,probRelevant,"R"));
			}
			else {
				movieRankingPRP.put(mb.movieId, new MovieWrapper(mb.movieId,probRelevant,"NR"));
			}

		}
		
		return movieRankingPRP;
	}
	
	/**
	 * evaluatePerformance - This method evaluates the performance of the classifier 
	 * @param m : append to file
	 * @param run_num iteration number
	 * @param q : append to file
	 */
	protected void evaluatePerformance(int m, int run_num, int q){
		int TP=0,FP=0,TN=0,FN=0,n=0;
		double matrix[] = new double[5];
		double averageILS = 0.0;
		for(Map.Entry<Integer,ArrayList<UserMovieMapping>> userMovie: userIdMovieMap.entrySet()){
			Integer userId = userMovie.getKey(); 
			ArrayList<UserMovieMapping> watched = userMovie.getValue();
			LinkedHashMap<Integer,UserMovieMapping> rel = new LinkedHashMap<Integer,UserMovieMapping>();
			LinkedHashMap<Integer,UserMovieMapping> nrel = new LinkedHashMap<Integer,UserMovieMapping>();
			LinkedHashSet<MovieBean> userRatedMovies = new LinkedHashSet<MovieBean>();
			Set<Integer> movieInts = new LinkedHashSet<Integer>();
			for(UserMovieMapping umm: watched){
				if(umm.rating >=3.0){
					rel.put(umm.movieId,umm);
				}else {
					nrel.put(umm.movieId,umm);
				}
				userRatedMovies.add(mMovieMap.get(umm.movieId));
				movieInts.add(umm.movieId);
			}
			n += watched.size();
			double relevantPrior = ((double)rel.size())/((double) watched.size());
			double nonRelevantPrior = ((double)nrel.size())/((double) watched.size());
			
			LinkedHashMap<String, ArrayList<Double>> classConditionals = trainDataSetForSingleUser(relevantPrior, 
					nonRelevantPrior,watched,userId,
					new ArrayList(rel.values()),new ArrayList(nrel.values()));
			
			TreeMap<Integer,MovieWrapper> results = computeFirstRankPRP(userRatedMovies, classConditionals,relevantPrior, 
					nonRelevantPrior);
			Set<Integer> recommendedItems = new LinkedHashSet<Integer>();
			for(Map.Entry<Integer, MovieWrapper> result: results.entrySet()){
				MovieWrapper mw = result.getValue();
				if(mw.label.equals("R")){
					recommendedItems.add(mw.movieId);
					if(rel.containsKey(mw.movieId)){
						TP++;
						
					}else {
						FP++;
					}
				}else {
					if(nrel.containsKey(mw.movieId)){
						TN++;
					}else {
						FN++;
					}
					
				}
			}
			matrix[0] += ((double)TP/(TP+FN));//recall
			matrix[1] += ((double)TP/(TP+FP));//precision
			matrix[2] += ((double)(2*((double)TP/(TP+FN))*((double)TP/(TP+FP)))/(((double)TP/(TP+FN))+((double)TP/(TP+FP))));// fscore
			matrix[3] += ((double)(TP+TN))/n;// accuracy
			matrix[4] += ((double)FP/(FP+TN)); // fall out
			
			
		}
		matrix[0] = ((double)matrix[0]/userIdMovieMap.size());
		matrix[1] = ((double)matrix[1]/userIdMovieMap.size());
		matrix[2] = ((double)matrix[2]/userIdMovieMap.size());
		matrix[3] = ((double)matrix[3]/userIdMovieMap.size());
		matrix[4] = ((double)matrix[4]/userIdMovieMap.size());
		try {
			FileUtils.writeStringToFile(new File("C:\\temp\\Output_DM\\PR_MLensk="+run_num+"n="+q+".txt"), ""+n+"\t"+matrix[0]+"\t"+matrix[1]+"\t"+matrix[2]+"\t"+ matrix[3]+"\t"+matrix[4]+"\t"+averageILS+"\n",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** 
	 * Sort values of the recommended results based on score
	 * @param map - map of the recommended results
	 * @return - aall results sorted by ranking 
	 */
	static
	List<Entry<Integer,MovieWrapper>> entriesSortedByValues(Map<Integer,MovieWrapper> map) {
		List<Entry<Integer,MovieWrapper>> sortedEntries = new ArrayList<Entry<Integer,MovieWrapper>>(map.entrySet());
		Collections.sort(sortedEntries, 
				new Comparator<Entry<Integer,MovieWrapper>>() {
			@Override
			public int compare(Entry<Integer,MovieWrapper> e1, Entry<Integer,MovieWrapper> e2)  {
				return ((e2.getValue().score).compareTo(e1.getValue().score)); 
			}
		});
		return sortedEntries;
	}

	static double log(double d)
	{
		//return ((double)Math.(d) / Math.(2));
		return Math.log(d);
	}
	/**
	 * Get the test set for single user
	 * @param userRatedMovies all movies rated by the user
	 * @return list of movies as test set 
	 */
	private LinkedHashSet<MovieBean> getTestSetForSingleUser(LinkedHashSet<MovieBean> userRatedMovies) {
		
		Collection<MovieBean> movieSet = mMovieMap.values();
		LinkedHashSet<MovieBean> testSet = new LinkedHashSet<MovieBean>(mMovieMap.values());
		LinkedHashSet<MovieBean> intersection = new LinkedHashSet<MovieBean>(userRatedMovies);
		intersection.retainAll(movieSet);		
		testSet.removeAll(intersection);
		
		return testSet;
	}
	
	/**
	 * Compute the intra-list similarity within the recommended results.
	 * This measure evaluates the amount of diversity in the results
	 * @param recommendedItems - recommended results
	 * @param watchedMovies - movies watched by the user
	 * @return return the average intra list similarity 
	 */
	protected double averageIntraListSimilarity(Set<Integer> recommendedItems, Set<Integer> watchedMovies){
		double[] simScores = new double[recommendedItems.size() *watchedMovies.size() ];
		int i=0;
		for(Integer rI : recommendedItems){
			MovieBean mb1 = mMovieMap.get(rI);
			long a1 [] = mb1.getArray(mb1);
			for(Integer wm : watchedMovies){
				MovieBean mb2 = mMovieMap.get(wm);
				long [] a2 = mb2.getArray(mb2);
				
			
				int intersectionSize = getIntersectionSize(a1,a2);
				     if (intersectionSize == 0) {
				       return Double.NaN;
				     }
				     
				     int unionSize = a1.length + a2.length - intersectionSize;
				     
				     simScores[i] = (double) intersectionSize / (double) unionSize;
				     i++;
			}
		}
		double sum =0.0;
		for(int j = 0; j < simScores.length;j++){
			sum += simScores[j];
		}
		return (sum/simScores.length);
	}

	protected static int getIntersectionSize(long[] arr1,long[] arr2){
	    int k=0;
	      for (int i=0;i<arr1.length;i++){	          
	              if(arr1[i]!=arr2[i]){
	                   k++;
	          
	          }
	      }
	      return arr1.length-k;
	}

	private LinkedHashMap<String, ArrayList<Double>> trainDataSetForSingleUser(double relevantPrior,
			double nonRelevantPrior,
			ArrayList<UserMovieMapping> userWatchedMovies, Integer userId,
			ArrayList<UserMovieMapping>  relevantCorpus,
			ArrayList<UserMovieMapping>  nonRelevantCorpus) {
		//P(r|d) = P(d|r) * P(r)/P(d)
		// first compute P(d|r) - where d are all the genres

		ArrayList<Double> classConditionalRelevant = computeClassConditionals(relevantCorpus, true);
		ArrayList<Double> classConditionalNonRelevant = computeClassConditionals(nonRelevantCorpus,false);
		LinkedHashMap<String, ArrayList<Double>> classConditionals = new LinkedHashMap <String, ArrayList<Double>>();
		classConditionals.put("relevant",classConditionalRelevant);
		classConditionals.put("nonRelevant",classConditionalNonRelevant);
		return classConditionals;

	}

	/**
	 * computeClassConditionals Computes the class conditionals for the Naive Bayes
	 * classifier. 
	 * @param relevantCorpus - computes the relevant corpus 
	 * @param isRelevant - whether the data set isRelevant
	 * @return all the class conditionals 
	 */
	private ArrayList<Double> computeClassConditionals(
			ArrayList<UserMovieMapping> relevantCorpus, boolean isRelevant) {
		int isUnknown = 0;
		int isAction = 0;
		int isAdventure = 0;
		int isAnimation = 0;
		int isChildrens = 0;
		int isComedy = 0;
		int isCrime = 0;
		int isDocumentary = 0;
		int isDrama = 0;
		int isFantasy = 0;
		int isFilmNoir = 0;
		int isHorror = 0;
		int isMusical = 0;
		int isMystery =0;
		int isRomance = 0;
		int isSciFi = 0;
		int isThriller = 0;
		int isWar = 0;
		int isWestern = 0;
		int relevantCorpusSize = relevantCorpus.size();
		for( UserMovieMapping movie: relevantCorpus){
			MovieBean mb = mMovieMap.get(movie.movieId);
			if(mb.isUnknown) isUnknown++;
			if(mb.isAction) isAction++;
			if(mb.isAdventure) isAdventure++;
			if(mb.isAnimation) isAnimation++;
			if(mb.isChildrens) isChildrens++;
			if(mb.isComedy) isComedy++;
			if(mb.isCrime) isCrime++;
			if(mb.isDocumentary) isDocumentary++;
			if(mb.isDrama) isDrama++;
			if(mb.isFantasy) isFantasy++;
			if(mb.isFilmNoir) isFilmNoir++;
			if(mb.isHorror) isHorror++;
			if(mb.isMusical) isMusical++;
			if(mb.isMystery) isMystery++;
			if(mb.isRomance) isRomance++;
			if(mb.isSciFi) isSciFi++;
			if(mb.isThriller) isThriller++;
			if(mb.isWar) isWar++;
			if(mb.isWestern) isWestern++;
		}
		ArrayList<Double> classConditional = new ArrayList<Double>();
		classConditional.add(((double) (isUnknown+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isAction+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isAdventure+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isAnimation+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isChildrens+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isComedy+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isCrime+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isDocumentary+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isDrama+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isFantasy+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isFilmNoir+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isHorror+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isMusical+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isMystery+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isRomance+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isSciFi+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isThriller+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isWar+1))/(relevantCorpusSize+2));
		classConditional.add(((double) (isWestern+1))/(relevantCorpusSize+2));
		
		

		return classConditional;



	}



}
