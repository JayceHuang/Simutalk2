package com.ztspeech.simutalk2.qa;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TransDataBase;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;

import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;


public class MsgService extends Service {

	private ActivityManager activityManager;
	private String packageName = "";
    private boolean isStop = false;
    private ProcessMessage mMessageLoop;
    
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
	public void startThread() {
		
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE); 
        packageName = this.getPackageName(); 
//        System.out.println("��������");
        
        new Thread() { 
            public void run() { 
                try { 
                	boolean state = false;
                    while (!isStop) {
                        Thread.sleep(1000); 
                        
                        if (isAppOnForeground()) { 
                          //  Log.v(TAG, "ǰ̨����");
                        	// hideNotification();
                        	if(state == false){
                        		state = true;
                        		ProcessMessage.setActiveState(true);                        		
                        	}
                        } else { 
                          //  Log.v(TAG, "��̨����");
                          //  showNotification();
                        	if(state){
                        		state = false;
                        		ProcessMessage.setActiveState(false);                        		
                        	}
                        } 
                    } 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            } 
        }.start(); 
    }
    
	public void hideNotification(){
		   NotificationManager notificationManager = (
	                NotificationManager)getSystemService(
	                        android.content.Context.NOTIFICATION_SERVICE);
	        
	        notificationManager.cancel(0);
	 }
	 
	@Override
	public void onStart(Intent intent, int startId) {
		if(mMessageLoop==null){
			mMessageLoop = ProcessMessage.getInstance();
			mMessageLoop.set(this, getString(R.string.host_ip));
		}
		if(!mMessageLoop.getIsRunning()){
			mMessageLoop.start();
		}
		super.onStart(intent, startId);
	}
	
    @Override
	public void onCreate() {

		super.onCreate();
		
		startThread();
		if(mMessageLoop==null){
			mMessageLoop = ProcessMessage.getInstance();
			mMessageLoop.set(this, getString(R.string.host_ip));
		}
	}

	/**
     * �����Ƿ���ǰ̨����
     * @return
     */
    public boolean isAppOnForeground() { 
        // Returns a list of application processes that are running on the device 
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE); 
        packageName = this.getPackageName(); 
        
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
        if (appProcesses == null) return false; 
        
        for (RunningAppProcessInfo appProcess : appProcesses) { 
            // The name of the process that this object is associated with. 
            if (appProcess.processName.equals(packageName) 
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) { 
                return true; 
            } 
        } 
        
        return false; 
    } 
    
    @Override
    public void onDestroy() {

        super.onDestroy();
        isStop = true;
        mMessageLoop.stop();
        System.out.println("��ֹ����");
    }
    
    
    // ��ʾNotification
    public void showNotification() {
        // ����һ��NotificationManager������
        NotificationManager notificationManager = (
                NotificationManager)getSystemService(
                        android.content.Context.NOTIFICATION_SERVICE);
        
        // ����Notification�ĸ�������
        Notification notification = new Notification(
                R.drawable.ic_launcher, getString(R.string.app_name), 
                System.currentTimeMillis());
        // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        // ������Զ����Notification
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000;
                
        // ����֪ͨ���¼���Ϣ
        CharSequence contentTitle = getString(R.string.app_name); // ֪ͨ������
        CharSequence contentText = getString(R.string.app_name) + getString(R.string.app_run_tip); // ֪ͨ������
        
        Intent intent = new Intent(MainActivity.getInstance(),MainActivity.getInstance().getClass());
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(
        		MainActivity.getInstance(), 0, 
        		intent,PendingIntent.FLAG_UPDATE_CURRENT);
       
        notification.setLatestEventInfo(MainActivity.getInstance(),
        		contentTitle, contentText, contentIntent);
        // ��Notification���ݸ�NotificationManager
        notificationManager.notify(0, notification);
    }
 
	 

}
