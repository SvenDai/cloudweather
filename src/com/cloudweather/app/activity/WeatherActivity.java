/**
 *@author daifeng
 *2016-2-27 ����5:56:50
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
	//������ʾ��������
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
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
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
		//��ʼ���ؼ�
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
		
		//�õ��ؼ�������
		String countyCode = getIntent().getStringExtra("county_code");
		//�õ��ؼ�����ȥ��ѯ����
		if(!TextUtils.isEmpty(countyCode)){
			publishTimeText.setText("ͬ����...");
			weatherLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			// ��ѯ�ؼ���������
			queryWeatherCode(countyCode);
		}else{
			//û���ؼ�ֱ����ʾ������Ϣ
			showWeather();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * ʵ���л����������������ֶ�ˢ��
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
			publishTimeText.setText("ͬ����...");
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
	 *��ѯ���Ŷ�Ӧ���������� 
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFomServer(address, "county");
	}
	
	/**
	 * ��ѯ�������Ŷ�Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode){
		String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
		String httpArg = "cityid="+weatherCode;
		String address = httpUrl+"?"+httpArg;
		queryFomServer(address, "weatherCode");
	}
	
	/**
	 * ���ݴ���ĵ�ַ�����ͣ����������ѯ�������Ż���������Ϣ
	 */
	private void queryFomServer(final String address, final String type){
		WeatherRequestHttpUtil.sendWeatherHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if("county".equals(type)){
					//�ӷ������ķ���ֵ�н�������������
					String [] array = response.split("\\|");
					if(array != null && array.length == 2){
						String weatherCode = array[1];
						//����������ȥ��ѯ����
						Log.d("MSG", weatherCode);
						queryWeatherInfo(weatherCode);
					}
					
				}else if("weatherCode".equals(type)){
					//������������ص�������Ϣ
					HandleResponseUtility.handleWeatherResponse(WeatherActivity.this, 
							response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//��ʾ������Ϣ
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
						publishTimeText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϰ�
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishTimeText.setText("����"+prefs.getString("publish_time", "")+"����");
		weatherDespText.setText(prefs.getString("weather", ""));
		templText.setText(prefs.getString("low_temp", "")+"��");
		temphText.setText(prefs.getString("high_temp", "")+"��");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		//��������AutoUpdateService����
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

}
