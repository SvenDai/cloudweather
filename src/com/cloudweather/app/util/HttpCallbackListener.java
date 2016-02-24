package com.cloudweather.app.util;

public interface HttpCallbackListener {
	/**
	 * 成功回调接口
	 */
	void onFinish(String response);
	/**
	 * 失败、异常回调接口
	 */
	void onError(Exception e);
}
