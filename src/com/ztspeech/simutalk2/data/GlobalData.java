package com.ztspeech.simutalk2.data;

/**
 * ����ȫ�ֹ�������
 * @author kjzhang
 *
 */
public class GlobalData {


	private static AskTaskList fish ;
	private static AskTaskList ask;	
	
	/**
	 * ��������б�
	 */
	public static AskTaskList getAskInstance(){

		if(ask == null){
			ask = new AskTaskList();
		}
		return ask;
	}
	/**
	 * �������б�
	 */
	public static AskTaskList getFishInstance(){

		if(fish == null){
			fish = new AskTaskList();
		}
		return fish;
	}
}
