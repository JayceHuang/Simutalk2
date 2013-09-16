package com.ztspeech.simutalk2.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DoStream {

	private static final int READ_BUFFER = 1024;

	public static ByteArrayInputStream getCopy(InputStream input) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[READ_BUFFER];
		int nRead = 0;		
		
		try {
			while(true){
				nRead = input.read(buffer);
				if( nRead <= 0){					
					break;
				}
				
				out.write(buffer, 0, nRead);
			}
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		
		return null;
	}

}
