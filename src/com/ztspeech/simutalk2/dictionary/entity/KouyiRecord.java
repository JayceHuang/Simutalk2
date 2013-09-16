package com.ztspeech.simutalk2.dictionary.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class KouyiRecord implements Parcelable{

	private Integer recordId;
	private String said;
	private String translated;
	private String dateTime;
	
	private String id;
	private String type;
	private String comment;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getRecordId() {
		return recordId;
	}
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	public String getSaid() {
		return said;
	}
	public void setSaid(String said) {
		this.said = said;
	}
	public String getTranslated() {
		return translated;
	}
	public void setTranslated(String translated) {
		this.translated = translated;
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
		dest.writeInt(recordId);
		dest.writeString(said);
		dest.writeString(translated);
		dest.writeString(dateTime);
		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(comment);
		
	}
	public static final Parcelable.Creator<KouyiRecord> CREATOR = new Parcelable.Creator<KouyiRecord>(){

		@Override
		public KouyiRecord createFromParcel(Parcel source) {
			KouyiRecord kouyi = new KouyiRecord();
			kouyi.setRecordId(source.readInt());
			kouyi.setSaid(source.readString());
			kouyi.setTranslated(source.readString());
			kouyi.setDateTime(source.readString());
			kouyi.setId(source.readString());
			kouyi.setType(source.readString());
			kouyi.setComment(source.readString());
			return kouyi;
		}

		@Override
		public KouyiRecord[] newArray(int size) {
			// TODO Auto-generated method stub
			return new KouyiRecord[size];
		}
	};
}
