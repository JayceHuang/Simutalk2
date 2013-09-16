package com.ztspeech.simutalk2.trans.speak;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class AudioClip implements OnCompletionListener {

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		release();		
		super.finalize();
	}
	private MediaPlayer mPlayer = null;
	private Context mContext = null;
	private int mResid = 0;
	private boolean mPlaying =false;
	private IOnCompletionListener mOnCompletionListener = null;
	public interface IOnCompletionListener{
		public void audioClipCompletion();
	}
	
	public void setOnCompletionListener(IOnCompletionListener listener){
		this.mOnCompletionListener = listener;
	}
	
	public AudioClip(Context context, int resid){
		mContext = context;
		mResid =  resid;
		createPlayer();
	}
	public boolean play(){
		
		if( mPlayer == null){
			return false;
		}
		
		if(mPlaying == true){
			return false;			
		}
		
		 try {
			 mPlayer.start(); 
			 mPlaying = true;
			 return true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return false;
	}
	
	private boolean createPlayer(){

		release();
		mPlayer = MediaPlayer.create(mContext, mResid);
		if( mPlayer != null){
			try {
				//mPalyer.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
			mPlayer.setOnCompletionListener(this);
			return true;
		}
		
		return false;
	}
	
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mPlaying = false;
		
		if(mOnCompletionListener != null){
			mOnCompletionListener.audioClipCompletion();
			
		}
	}
	
	public void release() {
		// TODO Auto-generated method stub
		if( mPlayer != null){
			mPlayer.release();
			mPlayer = null;
		}
	}
}
