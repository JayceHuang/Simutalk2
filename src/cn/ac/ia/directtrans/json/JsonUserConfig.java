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
	 * 长按录音按钮录音
	 */
	public boolean lClickRecord = true;
	/**
	 * 直接打开口译界面
	 */
	public int openTrans = 0;
	
	/**
	 * 口译后台运行
	 */
	public boolean bRun = true;	
	
	/**
	 * 本地最大消息Id
	 * 2012.11.22
	 */	
	public long maxMsg = 0;
	public int id = 0;
}
