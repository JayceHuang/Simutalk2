package com.ztspeech.simutalk2.qa.data;

import com.ztspeech.simutalk2.data.DataObject;

import cn.ac.ia.directtrans.json.QuestionInfo;


public class QuestionDataItem extends DataObject{
	
	public static final int TYPE_ASK = 1;
	public static final int TYPE_SOLVE = 2;
	
	public QuestionInfo info = new QuestionInfo();
	
	public void setInfo(QuestionInfo data ){
		
		this.id = data.id;
		this.name = data.senderName;
		this.info.text = data.text;
		
		info.setInfo(data);
		
	}
	
}