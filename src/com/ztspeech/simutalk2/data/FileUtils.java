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

		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		String status = Environment.getExternalStorageState();  
		isSDCardExist = status.equals(Environment.MEDIA_MOUNTED);
		if( isSDCardExist) {
			SDPATH = Environment.getExternalStorageDirectory() + "/";
		}
	}

	// ��SD���ϴ����ļ�
	public File createSDFile(String fileName) throws IOException {
		File file = null;
		if(isSDCardExist){
			file = new File(SDPATH + fileName);
			file.createNewFile();
		}
		return file;

	}

	// ��SD���ϴ���Ŀ¼

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
	

	// �ж�SD���ϵ�Ŀ¼�Ƿ����

	public boolean isFileExist(String fileName) {
		File file = null;
		if(isSDCardExist){
			file = new File(SDPATH + fileName);
			return file.exists();
		}
		
		return false;
	}

	// ��InputStream���������д�뵽SD����ȥ

	public File writeSD(String path, String fileName, InputStream input) {

		File file = null;

		OutputStream output = null;

		try {

			// ���ô���SD��Ŀ¼����

			createSDDir(path);

			// ���ô���SD���ļ��ķ���

			file = createSDFile(path + fileName);

			// �����ļ����������

			output = new FileOutputStream(file);

			// 4���ֽڵĶ�ȡ

			byte buffer[] = new byte[4 * 1024];

			// ���ļ������ݲ�Ϊ�յ�ʱ���ֹͣ���

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
