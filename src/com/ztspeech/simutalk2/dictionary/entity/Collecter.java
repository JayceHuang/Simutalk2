package com.ztspeech.simutalk2.dictionary.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Collecter implements Parcelable{

	private Integer id;
	private Integer childId;
	private String text1;
	private String text2;
	private String dateTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getChildId() {
		return childId;
	}
	public void setChildId(Integer childId) {
		this.childId = childId;
	}
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(childId);
		dest.writeString(text1);
		dest.writeString(text2);
		dest.writeString(dateTime);
		
	}
	public static final Parcelable.Creator<Collecter> CREATOR = new Parcelable.Creator<Collecter>(){

		@Override
		public Collecter createFromParcel(Parcel source) {
			Collecter collecter = new Collecter();
			collecter.setId(source.readInt());
			collecter.setChildId(source.readInt());
			collecter.setText1(source.readString());
			collecter.setText2(source.readString());
			collecter.setDateTime(source.readString());
			return collecter;
		}

		@Override
		public Collecter[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Collecter[size];
		}
		
	};
}
