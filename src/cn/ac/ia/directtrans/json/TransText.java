package cn.ac.ia.directtrans.json;

public class TransText {

	public static class Language {
		public static final String CHINESE = "ch";
		public static final String ENGLISH = "en";
	}
	
	public String language;
	public String chinese;
	public String english;
	
	public void set(String language2, String message) {
		// TODO Auto-generated method stub
		
		this.language = language2;
		chinese = message;
	}
	
	public String getChinese(){
		if(chinese != null) {
			return chinese.replaceAll("`", "'");
		}
		return "";
	}
	
	public void setChinese(String text){
		chinese = text.replaceAll("'", "`");
	}
	
	public String getEnglish(){
		if(english != null) {
			return english.replaceAll("`", "'");
		}
		return "";
	}
	
	public void setEnglish(String text){
		english = text.replaceAll("'", "`");
	}	
	
}
