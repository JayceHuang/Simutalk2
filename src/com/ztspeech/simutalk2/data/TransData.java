package com.ztspeech.simutalk2.data;

public class TransData {
	
	public class Define {
		public static final String TRANS_CH2EN = "CH2EN";
		public static final String TRANS_EN2CH = "EN2CH";
		public static final String FROM = "from";
		public static final String TO = "to";
		public static final String TYPE = "type";
	};
	public String from = "";
	public String to = "";
	public String type = "";
	public int id = 0;
	
	public TransData(int id, String t,String from, String to){
		
		this.from = from;
		this.to = to;
		this.type = t;
		this.id = id;
	}
}