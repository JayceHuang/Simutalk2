package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonEditLinkman extends JsonRequest {
	
	public static final String CMD_ADD 			= "add";
	public static final String CMD_DELETE 		= "del";
	public static final String CMD_INVITE		= "invite";
	public static final String CMD_INVITE_RET	= "invite_ret";

	
	public String   cmd;
	public String 	email;
	public long 	id;
	public String 	name;
	
	public JsonEditLinkman(){
		function = EDIT_LINKMAN;
	}
	
	public void setInviteLinkman(long id){
		cmd = CMD_INVITE;
		this.id = id;
	}
	
	public void setAddLinkman(long id, String email, String name){
		cmd = CMD_ADD;
		this.email = email;
		this.name = name;
		this.id = id;
	}
	
	public void setDeleteLinkman(long linkId){
		cmd = CMD_DELETE;
		this.id = linkId;
	}
	
	public static JsonEditLinkman fromJson(String json) {

		Gson gson = new Gson();
		JsonEditLinkman user = gson.fromJson(json,JsonEditLinkman.class);		
		return user;
	}

	public void setInviteRespond(long linkmanId) {
		// TODO Auto-generated method stub
		cmd = CMD_INVITE_RET;
		this.id = linkmanId;
	}
}
