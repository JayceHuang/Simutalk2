package com.ztspeech.simutalk2.qa.message;

import android.app.AlertDialog;
import android.content.Context;

public class TipDialog {

	public static void show( Context context, String title, String msg, String btn){
	    new  AlertDialog.Builder(context)  		 
	    .setTitle(title)  		   
	    .setMessage(msg )  		   
	    .setPositiveButton(btn,  null )  		   
	    .show();  
	}
}
