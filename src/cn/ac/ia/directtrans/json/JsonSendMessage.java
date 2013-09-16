package cn.ac.ia.directtrans.json;

public class JsonSendMessage extends JsonRequest {
	

	
	public long  linkId = 0;
	public int  vLen = 0;
	public String vId = "";
	public String text = "";
	public String cmd = "";

	public JsonSendMessage(){
		function = SEND_MSG;
	}	
}
