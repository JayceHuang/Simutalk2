package cn.ac.ia.directtrans.json;

public class JsonUserConfig  extends Json{
	
	public boolean localeTTS = false;
	public float TTSSpeed = 0.7f;
	public boolean onlyRecoginze = false;
	public int fontSize = 2;
	public boolean ttsGender = false; 
	public String photo = "";
	public boolean translatetalk = true;
	/**
	 * ����¼����ť¼��
	 */
	public boolean lClickRecord = true;
	/**
	 * ֱ�Ӵ򿪿������
	 */
	public int openTrans = 0;
	
	/**
	 * �����̨����
	 */
	public boolean bRun = true;	
	
	/**
	 * ���������ϢId
	 * 2012.11.22
	 */	
	public long maxMsg = 0;
	public int id = 0;
}
