package com.ztspeech.simutalk2.qa.data;

import com.ztspeech.simutalk2.data.DataObject;
import com.ztspeech.simutalk2.data.MsgDataList;

import cn.ac.ia.directtrans.json.QuestionInfo;


public class QuestionData extends DataObject{
	

	public QuestionInfo ask = new QuestionInfo();
	public QuestionInfo solve = new QuestionInfo();
	public boolean look_over = true;
	public MsgDataList msg;
	public static final int TYPE_QUESTION = 1;
	public static final int TYPE_MSG = 3;

	public boolean changed = false;
	public int state = 0;

	public boolean isSolved(){

		if(solve.id > 0){
			return true;
		}
		
		return false;
	}
	
	public long getTime(){
		
		if(solve.id > 0){
			return solve.time.getTime();
		}
		
		return ask.time.getTime();
	}
}