package cn.ac.ia.directtrans.json;

public class JsonQuestion extends JsonRequest {
	
	/**
	 * 
	 */
	public static final String ASK 		= "ask";
	/**
	 * 
	 */
	public static final String SOLVE 	= "solve";
	/**
	 * 
	 */
	public static final String MARK 	= "mark";
	/**
	 * 放弃解答
	 */
	public static final String GIVE_UP 	= "give_up";
	/**
	 * 
	 */
	public static final String ASK_LIST	= "ask_list";
	/**
	 * 捞一个问题
	 */
	public static final String OBTAIN 	= "obtain";
	/**
	 * 
	 */
	public static final String GET_ASK  = "get_ask";
	
	public String cmd = "";
	public long  id = 0;
	public int owner = 0;
	public String type = "";
	public String text = "";
	public String vId = "";
	public int vLen = 0;
	
	public JsonQuestion(){
		function = QUESTION;
	}	
}
