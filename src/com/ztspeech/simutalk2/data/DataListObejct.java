package com.ztspeech.simutalk2.data;

import java.util.ArrayList;


public class DataListObejct extends DataObject{
	

	protected ArrayList<DataObject> items = new ArrayList<DataObject>(); 
	
	public DataObject get(int n){
		
		if( n < 0){
			return null;
		}
		
		if(n >= items.size()){
			return null;
		}
		DataObject ret = items.get(n); 
		return ret;
	}
	
	public boolean set(int index, DataObject data){
		
		if( index < 0){
			return false;
		}
		
		if(index >= items.size()){
			return false;
		}
		items.set(index, data);

		return true;
	}
	
	
	public int size(){
		
		return items.size();
	}
	
	
	public void clear() {

		items.clear();
	}
	
	public void add(DataObject data) {
		
		items.add(data);
    }   
	
    public boolean delete(long id) {   
    	
    	for(int i=0; i < items.size(); i ++) {
    		
    		DataObject data = items.get(i);
    		if(data.id == id){
    			items.remove(i);
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    protected DataObject findById(long id) {   
    	
    	for(int i=0; i < items.size(); i ++) {
    		
    		DataObject data = items.get(i);
    		if(data.id == id){
    			return data;
    		}
    	}
    	
    	return null;
    }
}
