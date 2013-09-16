package com.ztspeech.simutalk2.data;

/**
 * 保存全局共享数据
 * @author kjzhang
 *
 */
public class GlobalData {


	private static AskTaskList fish ;
	private static AskTaskList ask;	
	
	/**
	 * 解答问题列表
	 */
	public static AskTaskList getAskInstance(){

		if(ask == null){
			ask = new AskTaskList();
		}
		return ask;
	}
	/**
	 * 捞问题列表
	 */
	public static AskTaskList getFishInstance(){

		if(fish == null){
			fish = new AskTaskList();
		}
		return fish;
	}
}
