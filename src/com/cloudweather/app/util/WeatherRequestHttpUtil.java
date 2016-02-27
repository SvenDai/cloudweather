/**
 *@author daifeng
 *2016-2-27 下午4:44:23
 *CloudWeather
 */
package com.cloudweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author daifeng
 *获取天气信息
 */
public class WeatherRequestHttpUtil {
	/**
	 * 
	 * @param address 请求地址
	 * @param listener 回调接口
	 * 发送http请求，到服务器取得天气信息
	 */
	public static void sendWeatherHttpRequest(final String address, 
			final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection =null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					//加入申请的apikey
					connection.setRequestProperty("apikey", "b7396ec1d66db924b662bc2174301845");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if(listener !=null){
						//请求成功，回调onFinish函数
						listener.onFinish(response.toString());
					}
				}catch (IOException e) {
					e.printStackTrace();
					if(listener !=null){
						listener.onError(e);
					}
				}finally{
					//断开连接
					if(connection != null){
						connection.disconnect();
					}
				}
				
			}
		}).start();
	}
}
