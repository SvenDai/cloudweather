/**
 *@author daifeng
 *2016-2-28 下午8:13:31
 *CloudWeather
 */
package com.cloudweather.app.receiver;

import com.cloudweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author daifeng
 *
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		//8小时后执行广播接收器收到广播，再次调用服务更新天气
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
	}

}
