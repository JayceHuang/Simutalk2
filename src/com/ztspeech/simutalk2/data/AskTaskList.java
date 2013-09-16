package com.ztspeech.simutalk2.data;

import java.util.ArrayList;

import cn.ac.ia.directtrans.json.AskTask;

public class AskTaskList {

	private ArrayList<AskTask> items = new ArrayList<AskTask>();
	private boolean mIsChanged = true;
	

	
	public int getTaskCount(){
		synchronized(this){
			return items.size();
		}
	}
	
	public void AddTask(AskTask obj){
		
		synchronized(this){
			items.add(obj);
		}
		mIsChanged = true;
	}
	
	public void setTaskList(AskTaskList list){
		
		synchronized(this){
			int nSize = list.items.size();
			items.clear();			
			for(int i=0; i< nSize; i ++){
				items.add(list.items.get(i));
			}
		}
		mIsChanged = true;
	}
	
	public void clear(){
		
		synchronized(this){
			items.clear();
		}		
		mIsChanged = true;

	}
	
	public void delTask(AskTask obj){
		synchronized(this){
			items.remove(obj);
		}
		mIsChanged = true;
	}
	
	public void delTask(long id){
		
		synchronized(this){
			int nSize = items.size();
			for(int i=0; i<nSize; i ++){
				AskTask task = items.get(i);
				if(task.id == id){
					items.remove(i);
					break;
				}
			}
		}
		mIsChanged = true;
	}

	public void deleteTopTask(){
		
		synchronized(this){
			int nSize = items.size();
			if(nSize > 0){
				items.remove(0);
			}
		}
		mIsChanged = true;
	}
	

	public boolean isChanged() {

		return mIsChanged;
	}
	
	public void setChanged(boolean bChanged){
		
		mIsChanged = bChanged;
	}

	public AskTask putTaskId() {
		synchronized(this){
			if(items.size() > 0){
				AskTask task = items.get(0);
				return task;
			}
		}
		return null;
	}
	
}
