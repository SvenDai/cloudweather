/**
 *@author daifeng
 *2016-2-28 ����8:13:31
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
		//8Сʱ��ִ�й㲥�������յ��㲥���ٴε��÷����������
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
	}

}
