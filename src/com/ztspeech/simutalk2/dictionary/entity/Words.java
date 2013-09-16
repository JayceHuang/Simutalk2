package com.ztspeech.simutalk2.dictionary.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Words implements Parcelable{

	private Integer wordsId;
	private Integer childID;
	private String chinese;
	private String english;
	public Integer getWordsId() {
		return wordsId;
	}
	public void setWordsId(Integer wordsId) {
		this.wordsId = wordsId;
	}

	public Integer getChildID() {
		return childID;
	}
	public void setChildID(Integer childID) {
		this.childID = childID;
	}
	public String getChinese() {
		return chinese;
	}
	public void setChinese(String chinese) {
		this.chinese = chinese;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public Integer getWordsHeat() {
		return wordsHeat;
	}
	public void setWordsHeat(Integer wordsHeat) {
		this.wordsHeat = wordsHeat;
	}
	private Integer wordsHeat;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(wordsId);
		dest.writeInt(childID);
		dest.writeString(chinese);
		dest.writeString(english);
		dest.writeInt(wordsHeat);
	}
	public static final Parcelable.Creator<Words> CREATOR = new Parcelable.Creator<Words>(){

		@Override
		public Words createFromParcel(Parcel source) {
			Words word = new Words();
			word.setWordsId(source.readInt());
			word.setChildID(source.readInt());
			word.setChinese(source.readString());
			word.setEnglish(source.readString());
			word.setWordsHeat(source.readInt());
			return word;
		}

		@Override
		public Words[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Words[size];
		}
		
	};
}
