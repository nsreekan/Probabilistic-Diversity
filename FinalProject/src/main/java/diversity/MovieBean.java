package diversity;
/**
 * This class is a wrapper for the movie bean
 * @author SatNam621
 *
 */
public class MovieBean {
	
    
    Integer movieId;
    String movieName;
    boolean isUnknown;
    boolean isAction;
    boolean isAdventure;
    boolean isAnimation;
    boolean isChildrens;
    boolean isComedy;
    boolean isCrime;
    boolean isDocumentary;
    boolean isDrama;
    boolean isFantasy;
    boolean isFilmNoir;
    boolean isHorror;
    boolean isMusical;
    boolean isMystery;
    boolean isRomance;
    boolean isSciFi;
    boolean isThriller;
    boolean isWar;
    boolean isWestern;
    
    public MovieBean( Integer movieId,
    String movieName,
    String isUnknown,
    String isAction,
    String isAdventure,
    String isAnimation,
    String isChildrens,
    String isComedy,
    String isCrime,
    String isDocumentary,
    String isDrama,
    String isFantasy,
    String isFilmNoir,
    String isHorror,
    String isMusical,
    String isMystery,
    String isRomance,
    String isSciFi,
    String isThriller,
    String isWar,
    String isWestern){
    	this.movieId = movieId;
    	this.movieName = movieName;
    	this.isUnknown = convertToBoolean(isUnknown);
    	this.isAction = convertToBoolean(isAction);
    	this.isAdventure= convertToBoolean(isAdventure);
    	this.isAnimation = convertToBoolean(isAnimation);
    	this.isChildrens = convertToBoolean(isChildrens);
    	this.isComedy = convertToBoolean(isComedy);
    	this.isCrime = convertToBoolean(isCrime);
    	this.isDocumentary = convertToBoolean(isDocumentary);
    	this.isDrama = convertToBoolean(isDrama);
    	this.isFantasy = convertToBoolean(isFantasy);
    	this.isFilmNoir = convertToBoolean(isFilmNoir);
    	this.isHorror = convertToBoolean(isHorror);
    	this.isMusical = convertToBoolean(isMusical);
    	this.isMystery = convertToBoolean(isMystery);
    	this.isRomance = convertToBoolean(isRomance);
    	this.isSciFi = convertToBoolean(isSciFi);
    	this.isThriller = convertToBoolean(isThriller);
    	this.isWar = convertToBoolean(isWar);
    	this.isWestern = convertToBoolean(isWestern);    	
    }
    
    private boolean convertToBoolean(String value) {
        boolean returnValue = false;
        if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || 
            "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
            returnValue = true;
        return returnValue;
    }
    protected long[] getArray(MovieBean mb){
    	long[] array = new long[19];
    	if(mb.isUnknown) array[0]=1;
		if(mb.isAction) array[1]=1;
		if(mb.isAdventure) array[2]=1;
		if(mb.isAnimation) array[3]=1;
		if(mb.isChildrens) array[4]=1;
		if(mb.isComedy) array[5]=1;
		if(mb.isCrime) array[6]=1;
		if(mb.isDocumentary) array[7]=1;
		if(mb.isDrama) array[8]=1;
		if(mb.isFantasy) array[9]=1;
		if(mb.isFilmNoir) array[10]=1;
		if(mb.isHorror) array[11]=1;
		if(mb.isMusical) array[12]=1;
		if(mb.isMystery) array[13]=1;
		if(mb.isRomance) array[14]=1;
		if(mb.isSciFi) array[15]=1;
		if(mb.isThriller) array[16]=1;
		if(mb.isWar) array[17]=1;
		if(mb.isWestern) array[18]=1;
		return array;
    }
   
}
