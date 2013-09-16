package com.ztspeech.simutalk2.dictionary.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpConnection {

	public static String request(String data)
			throws Exception {
//		ǰ��������ַ����
		String httpUrl = "";
		String sessionID = "";
//		ǰ�ò��Ե�ַ����
//		String httpUrl = "kO5UMzvG3sfKFVGGnRNYCfOoY7s7P8SLa32J743KA019R1UAJwWx7h57EpvwowlU|Sa4VpeLZ1OnlB9cz11kMdQ==";
		HttpPost request = new HttpPost(httpUrl);//ǰ�õ�ַ����ԭ��
		String str = "";
		try {
			request.setEntity(new ByteArrayEntity(data.getBytes()));
			request.setHeader("Content-Type", "text/plain");
			if(sessionID!=""){
				request.setHeader("sessionId", sessionID);
			}  
//            String host = Proxy.getHost(context);  //?
//            int port = Proxy.getPort(context);   //?
//            HttpClient client;
//            if(host != null){
//            	HttpHost httpHost = new HttpHost(host, port);  
//            	HttpParams httpParams = new BasicHttpParams();  
//            	httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
//            	client = new DefaultHttpClient(httpParams);
//            }else{
//            	client = new DefaultHttpClient();
//            }
			HttpClient client = new DefaultHttpClient();
			//����ʱ 
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);  
			//��ȡ��ʱ
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				str = EntityUtils.toString(response.getEntity());
			} else {
				return "9999";
			}
			request.abort();//�ͷ���Դ
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			// ��ʱ
			return "8888";
		}catch (IOException e) {
			// �����쳣
			return "9999";
		}finally{
			request.abort();//�ͷ���Դ
		}
		return str;
	}
}
