package diversity;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author SatNam621
 *
 */
public class MovieDataPreProcessor {
  
	LinkedHashMap<Integer,MovieBean> mMovieMap = new LinkedHashMap<Integer,MovieBean>();
	LinkedHashMap<Integer,ArrayList<UserMovieMapping>> userIdMovieMap = new LinkedHashMap<Integer,ArrayList<UserMovieMapping>>();
	
	String mFilePath;
	public MovieDataPreProcessor(String filePath){
		mFilePath = filePath;
	}
	protected void parseUserDataFile(){
		if(null == mFilePath || mFilePath.isEmpty()){
			System.out.println("Invalid Path -- Path not set");
			System.exit(1);
		}		
	}
	
	protected LinkedHashMap<Integer,MovieBean> getMovieMap(){
		return mMovieMap;
	}
	
	protected LinkedHashMap<Integer,ArrayList<UserMovieMapping>> getUserMovieMap(){
		return userIdMovieMap;
	}
	
	protected  void readDataFiles() {
		File folder = new File(mFilePath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				try {
					Charset charset = Charset.forName("UTF-8");
					if(file.getName().endsWith("u.data")){
						List<String> content = FileUtils.readLines(file,StandardCharsets.UTF_8);
						parseUserFile(content);						
					}
				    if(file.getName().endsWith("u.item")){
				    	List<String> content = FileUtils.readLines(file,StandardCharsets.UTF_8);
				    	parseMovieFile(content);
				    }
					
				} catch (IOException e) {

					e.printStackTrace();
				}

			} 
		}
	}
	private void parseMovieFile(List<String> contents) {
		if(contents != null && !contents.isEmpty()){		
			for(String movieLine:contents){				
				String [] line = movieLine.split("\\|");
				if(line != null && line.length>0){
					Integer movieId = Integer.parseInt(line[0]);	
					MovieBean mb = new MovieBean(movieId,line[1],line[5],line[6],line[7],
							line[8],line[9],line[10],line[11],line[12],line[13],line[14],line[15],
							line[16],line[17],line[18],line[19],line[20],line[21],line[22],line[23]);

					mMovieMap.put(movieId,mb);
				}
			}
		}
	}
	private void parseUserFile(List<String> contents) {
       if(contents != null && !contents.isEmpty()){		
          for(String userLine:contents){
        	  String [] line = userLine.split("\t");
        	  if(line != null && line.length>0){
        		  Integer userId = Integer.parseInt(line[0]);
        		  Integer movieId = Integer.parseInt(line[1]);
        		  Integer rating = Integer.parseInt(line[2]);
        		  UserMovieMapping mm = new UserMovieMapping(movieId,rating,userId);
        		  
        		  if(userIdMovieMap.containsKey(userId)){
        			  ArrayList<UserMovieMapping> userMovie = userIdMovieMap.get(userId);        			  
        			  userMovie.add(mm);
        			  userIdMovieMap.put(userId, userMovie);
        		  } 
        		  else {
        			  ArrayList<UserMovieMapping> userMovie = new ArrayList<UserMovieMapping>();
        			  userMovie.add(mm);
        			  userIdMovieMap.put(userId, userMovie);
        		  }
        	  }
          }
       }
	}
	
}
