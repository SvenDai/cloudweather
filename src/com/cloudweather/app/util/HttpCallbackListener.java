package com.cloudweather.app.util;

public interface HttpCallbackListener {
	/**
	 * �ɹ��ص��ӿ�
	 */
	void onFinish(String response);
	/**
	 * ʧ�ܡ��쳣�ص��ӿ�
	 */
	void onError(Exception e);
}
