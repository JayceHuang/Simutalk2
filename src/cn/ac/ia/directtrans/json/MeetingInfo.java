package cn.ac.ia.directtrans.json;

import java.util.ArrayList;

public class MeetingInfo{
	
	public static final int TYPE_FRIEND = 2;
	public static final int TYPE_SYSTEM = 0;
	public static final int TYPE_MEETING = 3;
	
	public String name;
	public long   owner;
	public long   id;
	public int type;
	
	public ArrayList<UserState> items = new ArrayList<UserState>();
	
}
