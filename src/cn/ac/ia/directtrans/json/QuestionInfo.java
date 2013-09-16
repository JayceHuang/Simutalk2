package cn.ac.ia.directtrans.json;

import java.util.Date;

public class QuestionInfo{
	
	public static final int STATE_UNSOLVED = 0; 	// ���������� 
	public static final int STATE_DISPENSE = 1; 	// �·�����
	public static final int STATE_MARK 	= 2; 		// ��ȷ������
	public static final int STATE_SOLVED = 100;		// �ѽ������
	public static final int STATE_CLOSE = 101;		// û�н��ֱ�ӹر�

	
	public long	 	id = 0;	
	public int		senderId = 0;
	public String 	senderName = "";
	public String 	text = "";
	public String 	photo = "";
	public Date 	time;
	public int		state = STATE_MARK;
	public String	type = "";
	public String 	vId = "";
	public int		vLen = 0;
	public int 		sId = 0;
	public String sName = "";
	public String sPhoto = "";	
	
//	public void setTime(Date date){
//		time = TimeString.dateToString(date);
//	}
	
	public void setInfo(QuestionInfo data){
		id               = data.id;
		senderId         = data.senderId;
		senderName       = data.senderName;
		text             = data.text;
		time             = data.time;
		type             = data.type;
		vId              = data.vId;
		vLen       		 = data.vLen;
		photo			 = data.photo;
	}
}
