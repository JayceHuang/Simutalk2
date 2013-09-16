package cn.ac.ia.directtrans.json;


public class JsonSetUserInfo extends JsonRequest{


	public static final String SET_PHOTO = "set_photo";
	public static final String SET_NAME = "set_name";		

	public String value = "";
	public String name = "";
	
	public JsonSetUserInfo(){
		function = SET_USER;
	}	
	
	public void setUserName(String v){
		name = SET_NAME;
		value = v;
	}
	public void setUserPhoto(String v){
		name = SET_PHOTO;
		value = v;		
	}
	
}
