package com.ztspeech.simutalk2.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {

	public String SDPATH;
	private static FileUtils instanse;
	private boolean isSDCardExist = false;
	public static FileUtils getInstanse()
    {
    	if(instanse == null){
    		instanse = new FileUtils();
    	}
    	return instanse;
    }
	
	public FileUtils() {

		// 得到当前外部存储设备的目录
		String status = Environment.getExternalStorageState();  
		isSDCardExist = status.equals(Environment.MEDIA_MOUNTED);
		if( isSDCardExist) {
			SDPATH = Environment.getExternalStorageDirectory() + "/";
		}
	}

	// 在SD卡上创建文件
	public File createSDFile(String fileName) throws IOException {
		File file = null;
		if(isSDCardExist){
			file = new File(SDPATH + fileName);
			file.createNewFile();
		}
		return file;

	}

	// 在SD卡上创建目录

	public File createSDDir(String dirName) {
		File dir = null;
		if(isSDCardExist){
			dir = new File(SDPATH + dirName);	
			dir.mkdir();
		}
		return dir;

	}
	
	public File openSDFile(String fileName){
		File file = null;
		if(isSDCardExist){
			file = new File(SDPATH + fileName);
		}
		return file;
	}
	

	// 判断SD卡上的目录是否存在

	public boolean isFileExist(String fileName) {
		File file = null;
		if(isSDCardExist){
			file = new File(SDPATH + fileName);
			return file.exists();
		}
		
		return false;
	}

	// 将InputStream里面的数据写入到SD卡中去

	public File writeSD(String path, String fileName, InputStream input) {

		File file = null;

		OutputStream output = null;

		try {

			// 调用创建SD卡目录方法

			createSDDir(path);

			// 调用创建SD卡文件的方法

			file = createSDFile(path + fileName);

			// 创建文件输出流对象

			output = new FileOutputStream(file);

			// 4个字节的读取

			byte buffer[] = new byte[4 * 1024];

			// 当文件的内容不为空的时候就停止输出

			while (input.read(buffer) != -1) {

				output.write(buffer);

			}

			output.flush();

		} catch (Exception e) {

			e.getMessage();

		} finally {

			try {

				output.close();

			} catch (IOException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();

			}
		}

		return file;
	}

}
