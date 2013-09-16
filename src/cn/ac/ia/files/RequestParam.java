package cn.ac.ia.files;

public class RequestParam {

	public static final String USER_ID = "uid";
	public static final String FILE_ID = "fid";
	public static final String VISITOR = "vt";
	public static final String APP = "app";
	public static final String TYPE = "t";
	public static final String FILE_TYPE_VOICE = "sbx";
	public static final String FILE_TYPE_PHOTO = "png";
	
	public String fileId;
	public String userId;
	public String app;
	public String type;
	
	public RequestParam( String fileId, String userId, String app,String t){
		this.fileId = fileId;
		this.userId = userId;
		this.app = app;
		this.type = t;
	}
}

