package cn.ac.ia.directtrans.json;

public class UserInfo{
	
	public static class Gender{
		
		public static final String GENDER_F = "Å®";
		public static final String GENDER_M = "ÄÐ";
		public static final int GENDER_DB_F = 0;
		public static final int GENDER_DB_M = 1;
		
		public static String strToDB(String gender){
			
			if(GENDER_M.equals(gender)){
				return "1";
			}
			
			return "0";
		}
		
		public static String dbToStr(int gender){
			
			if(GENDER_DB_M == gender){
				return GENDER_M;
			}
			
			return GENDER_F;		
		}
	}
	
	public int	 	id = 0;	
	public String 	name = "";
	public String 	photo = "";
	public int	 	score = 0;
	public int 		level = 0;	
	public String   distance = null;//¾àÀë
	public boolean  online = false;
	
	public void setInfo(UserInfo info){
		name 		= info.name;
		photo 		= info.photo;
		id 			= info.id;
		score 		= info.score;
		level 		= info.level;
	}
}
