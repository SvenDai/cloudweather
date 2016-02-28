/**
 *@author daifeng
 *2016-2-28 下午8:06:35
 *CloudWeather
 */
package com.cloudweather.app.service;

import com.cloudweather.app.receiver.AutoUpdateReceiver;
import com.cloudweather.app.util.HandleResponseUtility;
import com.cloudweather.app.util.HttpCallbackListener;
import com.cloudweather.app.util.WeatherRequestHttpUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * @author daifeng
 *
 */
public class AutoUpdateService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//开启子线程更新天气
				updateWeather();
			}
		});
		//启动服务，每隔8个小时，发出广播
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour = 8*60*60* 1000;
		long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
		Intent i =new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	//更新天气
	private void updateWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		//组装请求地址
		String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
		String httpArg = "cityid="+weatherCode;
		String address = httpUrl+"?"+httpArg;
		//发送http请求
		WeatherRequestHttpUtil.sendWeatherHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				//成功返回数据，调用handle解析并存储天气信息数据
				HandleResponseUtility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
