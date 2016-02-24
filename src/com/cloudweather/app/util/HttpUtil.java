package com.cloudweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
/**
 * 
 * @author fdai
 * ��װ��������
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpsURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpsURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//TODO ����key ���ʷ���������
					//connection.addRequestProperty(field, newValue);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while((line =reader.readLine()) != null){
						response.append(line);
					}
					//���سɹ�ʱ�ص� onFinish����
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					//�쳣ʱ�ص�onError����
					if(listener != null){
						listener.onError(e);
					}
				} finally{
					if(connection != null){
						connection.disconnect();
					}
				} 
			}
		}).start();
	}
}