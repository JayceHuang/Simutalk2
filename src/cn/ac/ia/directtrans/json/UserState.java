package cn.ac.ia.directtrans.json;

import java.util.Date;

public class UserState{
	
	public Date		time;
	public int	 	id = 0;	
	public String 	name = "";
	public String   photo = "";
	public int	 	score = 0;
	public int 		level = 0;	
	public int		solve = 0;
	public int		ask = 0;
	public String   distance = null;
	public boolean  online = false;
	

	public void setInfo(UserState info){
		if( info == null){
			return;
		}
		
		// name 		= info.name;
		photo		= info.photo;
		id 			= info.id;
		time		= info.time;
		score 		= info.score;
		level 		= info.level;
		solve 		= info.solve;
		ask 		= info.ask;
		online 		= info.online;	
		distance    = info.distance;
	}
}
