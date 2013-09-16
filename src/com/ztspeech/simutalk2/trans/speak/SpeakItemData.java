package com.ztspeech.simutalk2.trans.speak;

import java.io.InputStream;

import com.ztspeech.simutalk2.data.UserInfo;

public class SpeakItemData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public class Define { 
		
		public static final int SPEAK_TYPE_SPEAK 		= 1;
		public static final int SPEAK_TYPE_TEXT       	= 4;
		public static final int SPEAK_TYPE_TRANS       	= 2;
		public static final int SPEAK_TYPE_FULL       	= 3;
	}

	public static SpeakItemData isFocus = null;
//	public InputStream mPlayStream;	
	
	public String type = UserInfo.getInstanse().s2sType;	
	public int id = 0;

	public String speak = "";
	public String trans = "";
	public String mspeak = "";
	/**
	 * 识别ID
	 */
	public String taskId = "";
	/**
	 * 评价状态
	 */
	public int flag = 3;
	
	public InputStream speakStream;
	public InputStream transStream;
	
	public String languageSpeak = "";
	public String languageTrans = "";
	public String datetime = "";
	public Integer recordId=0;
	public boolean focus = false;

	/**
	 * 是否存在翻译结果
	 * @return
	 */
	public boolean isExistTrans() {		
		if( trans != null){
			if(trans.length() > 0){
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * 是否对翻译结果播放
	 * @return
	 */
	public boolean isTransTTS() {		
		if( false == isExistTrans()){
			return false;
		}
		
		if(UserInfo.S2T_LETTER.equals(type)) {
			return false;
		}
		return true;
	}
	
	public SpeakItemData(){
		
	}	
	
	public SpeakItemData(String nType, String languageSpeak, String sSpeak, InputStream speakStream ,  
			 String languageTrans, String sTrans ,InputStream transStream, boolean focus){
		
		this.speakStream = speakStream;
		this.transStream = transStream;
		this.focus = focus;	
		this.type = nType;		
		this.languageSpeak = languageSpeak;
		this.languageTrans = languageTrans;
		this.speak = sSpeak;
		this.trans = sTrans;
		
	}
	public SpeakItemData(String nType, String languageSpeak, String sSpeak, InputStream speakStream ,  
			 String languageTrans, String sTrans ,InputStream transStream, boolean focus,String datetime){
		
		this.speakStream = speakStream;
		this.transStream = transStream;
		this.focus = focus;	
		this.type = nType;		
		this.languageSpeak = languageSpeak;
		this.languageTrans = languageTrans;
		this.speak = sSpeak;
		this.trans = sTrans;
		this.datetime = datetime;
	}
	public SpeakItemData(String nType, String languageSpeak, String sSpeak,boolean focus){
		
		this.focus = focus;	
		this.type = nType;		
		this.languageSpeak = languageSpeak;
		this.speak = sSpeak;
	}
}
