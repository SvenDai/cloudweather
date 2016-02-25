package com.cloudweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
/**
 * 
 * @author fdai
 * 封装网络请求
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
					//TODO 加入key 访问服务器数据
					//connection.addRequestProperty(field, newValue);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while((line =reader.readLine()) != null){
						response.append(line);
						
					}
					//返回成功时回调 onFinish函数
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					//异常时回调onError函数
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
