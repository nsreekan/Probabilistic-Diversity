package diversity;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.distance.CosineDistanceMeasure;


public class StandardRecommender {
	
	protected void generateItemBasedRecommendations(MovieDataPreProcessor mdpp){
		try {
			DataModel model = new FileDataModel(new File("../data/udata.csv"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
			UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
			List<RecommendedItem> recommendations = recommender.recommend(196, 20);
			LinkedHashMap<Integer, MovieBean> movieDB = mdpp.mMovieMap;
			StringBuilder builder = new StringBuilder();
			builder.append("********** User recommended movies ************\n");
			builder.append("List of Recommended Movies Based on Item Based Similarity - Pearson\n");
			for (RecommendedItem recommendation : recommendations) {
			  long movieId = recommendation.getItemID();movieDB.containsKey(1643);
			  builder.append(movieDB.get((int)movieId).movieName+"\n");
			  
			}
			IRStatistics stats = null;
			RecommenderIRStatsEvaluator evaluator =
					new GenericRecommenderIRStatsEvaluator();
			RecommenderBuilder recoBuilder = new RecommenderBuilder() {
				public Recommender buildRecommender(DataModel testModel) {
					ItemSimilarity itemSimilarity;
					 ItemBasedRecommender recommender = null;
					
						itemSimilarity = new LogLikelihoodSimilarity(testModel);
						recommender = new GenericItemBasedRecommender(testModel, itemSimilarity);
		            
					return recommender;
				}
			};	
			stats = evaluator.evaluate(recoBuilder, null, model, null, 20,0.1,0.3);
			builder.append("Evaluation\n");
			builder.append("Evaluation at rank 20---****---");
			builder.append("Precision @20"+stats.getPrecision()+"\n");
			builder.append("Recall @20"+stats.getRecall()+"\n");
			builder.append("F1 measure @20 "+stats.getF1Measure()+"\n");
			builder.append("Fall out at @20 "+stats.getFallOut()+"\n");
			stats = evaluator.evaluate(recoBuilder, null, model, null, 1,0.1,0.7);
			builder.append("Evaluation\n");
			builder.append("Evaluation at rank 20---****---");
			builder.append("Precision @1"+stats.getPrecision()+"\n");
			builder.append("Recall @1"+stats.getRecall()+"\n");
			builder.append("F1 measure @1 "+stats.getF1Measure()+"\n");
			builder.append("Fall out at @1 "+stats.getFallOut()+"\n");
			try {
				FileUtils.writeStringToFile(new File("C:\\temp\\Output_DM\\PR_MLens_Standard_Item.txt"), builder.toString(),true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch (IOException e) {

			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
