package cn.ac.ia.directtrans.json;

public class JsonPostImage extends JsonRequest {
	
	public byte[] photo = null;
	
	public JsonPostImage(){
		function = SEND_IMG;
	}	
}
