/**
 *@author daifeng
 *2016-2-27 下午5:56:50
 *CloudWeather
 */
package com.cloudweather.app.activity;

import com.cloudweather.app.R;
import com.cloudweather.app.service.AutoUpdateService;
import com.cloudweather.app.util.HandleResponseUtility;
import com.cloudweather.app.util.HttpCallbackListener;
import com.cloudweather.app.util.WeatherRequestHttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author daifeng
 *
 */
public class WeatherActivity extends Activity implements OnClickListener  {
	
	private LinearLayout weatherLayout;
	//用于显示城市名称
	private TextView cityNameText;
	//
	private TextView publishTimeText;
	//
	private TextView weatherDespText;
	//
	private TextView templText;
	//
	private TextView temphText;
	//
	private TextView currentDateText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_show);
		//初始化控件
		weatherLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText = (TextView)findViewById(R.id.city_name);
		publishTimeText = (TextView) findViewById(R.id.publish_time);
		weatherDespText = (TextView)findViewById(R.id.weather_desp);
		templText = (TextView)findViewById(R.id.temp_l);
		temphText = (TextView)findViewById(R.id.temp_h);
		currentDateText = (TextView)findViewById(R.id.current_date);
		
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		//得到县级的名称
		String countyCode = getIntent().getStringExtra("county_code");
		//得到县级代号去查询天气
		if(!TextUtils.isEmpty(countyCode)){
			publishTimeText.setText("同步中...");
			weatherLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			// 查询县级天气代号
			queryWeatherCode(countyCode);
		}else{
			//没有县级直接显示天气信息
			showWeather();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 实现切换地区天气和天气手动刷新
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh:
			publishTimeText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	/**
	 *查询代号对应的天气代号 
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFomServer(address, "county");
	}
	
	/**
	 * 查询天气代号对应的天气
	 */
	private void queryWeatherInfo(String weatherCode){
		String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
		String httpArg = "cityid="+weatherCode;
		String address = httpUrl+"?"+httpArg;
		queryFomServer(address, "weatherCode");
	}
	
	/**
	 * 根据传入的地址和类型，向服务器查询天气代号或者天气信息
	 */
	private void queryFomServer(final String address, final String type){
		WeatherRequestHttpUtil.sendWeatherHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if("county".equals(type)){
					//从服务器的返回值中解析出天气代号
					String [] array = response.split("\\|");
					if(array != null && array.length == 2){
						String weatherCode = array[1];
						//用天气代号去查询天气
						Log.d("MSG", weatherCode);
						queryWeatherInfo(weatherCode);
					}
					
				}else if("weatherCode".equals(type)){
					//处理服务器返回的天气信息
					HandleResponseUtility.handleWeatherResponse(WeatherActivity.this, 
							response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//显示天气信息
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishTimeText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上啊
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishTimeText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		weatherDespText.setText(prefs.getString("weather", ""));
		templText.setText(prefs.getString("low_temp", "")+"℃");
		temphText.setText(prefs.getString("high_temp", "")+"℃");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		//激活启动AutoUpdateService服务
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

}
