package com.ztspeech.simutalk2.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ztspeech.simutalk2.data.FileUtils;


public class HttpDownloader {
	
	
	public interface IHttpDownloadLisenter {
		public void onHttpDownloadString(String sText);
	}
	
	private IHttpDownloadLisenter mLisenter = null;
	private String mDownloadUrl = "";
	private  Runnable mThreadRun = new Runnable(){
		
    	public void run() {
    		String sb;
    		synchronized(this){
    			sb = download(mDownloadUrl);
    		}
			if(mLisenter != null){
				mLisenter.onHttpDownloadString(sb);
			}
    	}
	};
	
	public void download(String sUrl, IHttpDownloadLisenter lisenter){
		mDownloadUrl = sUrl;
		mLisenter = lisenter;
		Thread t = new Thread(mThreadRun);
		t.start();
	}
	
    // 根据url下载文件，前提是这个文件是文本文件，函数的返回值是文件中的内容  
  
    public String download(String urlStr) {  
  
       StringBuffer sb = new StringBuffer();
       HttpURLConnection urlConn = null;
       String line = null;   
       
       try {  
  
           // 创建url      	   
           URL url = new URL(urlStr);  
      
           // 创建http    
           urlConn = (HttpURLConnection) url.openConnection();  
           urlConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
           urlConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);      
           // 读取数据  
      
           InputStream inputStream = urlConn.getInputStream();             
           BufferedReader  br = new BufferedReader(new InputStreamReader(inputStream));  
           
           while ((line = br.readLine()) != null) {  
  
              // 将这些字符添加到缓冲区的末端    
              sb.append(line + "/r/n");    
           }    
  
           // 关闭流    
           br.close();  
  
       } catch (MalformedURLException e) {    
           e.printStackTrace();  
  
       } catch (IOException e) {  
           e.printStackTrace();    
       }  
  
       if(urlConn != null){
    	   urlConn.disconnect();
    	   urlConn = null;
       }
       
       return sb.toString();    
    }  
  
  
    public InputStream getOnLineInputStream(String urlStr) {  
    	  
    	HttpURLConnection urlConn = null;
        try {  
   
            URL url = new URL(urlStr);  
            
            // 创建http    
            urlConn = (HttpURLConnection) url.openConnection();  
            urlConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
            urlConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);      
            // 读取数据  
       
            InputStream inputStream = urlConn.getInputStream();  

            // 使用IO流读取数据  
        	// BufferedInputStream
            return new BufferedInputStream(inputStream);  
      
        } catch (MalformedURLException e) {  
   
            // TODO Auto-generated catch block  
   
            e.printStackTrace();  
   
        } catch (IOException e) {  
 
            e.printStackTrace();  
   
        }  
   
        return null;     
     }  
   
    // 下载任意格式的文件  
  
    //1是文件已经存在，-1是文件下载失败，0是下载成功  
  
    public int downFile(String urlStr, String path, String fileName) {  
  
       InputStream inputStream = null;  
  
       FileUtils fileUtils = new FileUtils();  
  
       if ((fileUtils.isFileExist(path + fileName))) {  
  
           return 1;  
  
       } else {  
  
    	   HttpURLConnection urlConn = null;
           try {  
  
               URL url = new URL(urlStr);  
               
               // 创建http    
               urlConn = (HttpURLConnection) url.openConnection();  
               urlConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
               urlConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);      
               // 读取数据  
          
               inputStream = urlConn.getInputStream();  
  
               File resultFile = fileUtils  
  
                     .writeSD(path, fileName, inputStream);  
  
               urlConn.disconnect();
               urlConn = null;
               if (resultFile == null) {  
            	  
                  return -1;  
               }  
           } catch (MalformedURLException e) {  
              e.printStackTrace();  
  
           } catch (IOException e) {  
              e.printStackTrace();  
  
           } finally {  
  
              try {  
            	  if(urlConn != null){
            		  urlConn.disconnect();
            		  urlConn = null;
            	  }
                  inputStream.close();  
  
              } catch (IOException e) {  
  
                  // TODO Auto-generated catch block  
  
                  e.printStackTrace();  
              }  
           }  
       }  
       return 0;   
    }  
  
 }
