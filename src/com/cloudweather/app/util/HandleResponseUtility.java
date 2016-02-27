/**
 *@author daifeng
 *2016-2-25 ����2:17:16
 *CloudWeather
 */
package com.cloudweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.cloudweather.app.db.CloudWeatherDB;
import com.cloudweather.app.model.City;
import com.cloudweather.app.model.County;
import com.cloudweather.app.model.Province;

/**
 * @author daifeng
 *
 */
public class HandleResponseUtility {
	
	/**
	 * �������洢���������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(CloudWeatherDB cloudWeatherDB,
			String response){
		if(!TextUtils.isEmpty(response)){
			String [] allprovinces = response.split(",");
			if(allprovinces != null && allprovinces.length > 0){
				for(String provincecn:allprovinces){
					String [] pcodename = provincecn.split("\\|");
					Province province = new Province();
					province.setProvinceCode(pcodename[0]);
					province.setProvinceName(pcodename[1]);
					//���������������ݴ洢�����ݿ�
					cloudWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������洢���������ص��м�����
	 */
	public synchronized static boolean handleCitiesResponse(CloudWeatherDB cloudWeatherDB,
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String [] allCitise = response.split(",");
			if(allCitise != null && allCitise.length >0){
				for(String citisecn:allCitise){
					String [] ccodename = citisecn.split("\\|");
					City city = new City();
					city.setCityCode(ccodename[0]);
					city.setCityName(ccodename[1]);
					city.setProvinceId(provinceId);
					cloudWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������洢���������ص��ؼ�����
	 */
	public synchronized static boolean handleCountiesResponse(CloudWeatherDB cloudWeatherDB,
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String [] allCounties = response.split(",");
			if(allCounties != null && allCounties.length >0){
				for(String countiescn:allCounties){
					String [] cocodename = countiescn.split("\\|");
					County county = new County();
					county.setCountyCode(cocodename[0]);
					county.setCountyName(cocodename[1]);
					county.setCityId(cityId);
					cloudWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��������������json������
	 */
	public static void handleWeatherResponse(Context context, String response){
		
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("retData");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("citycode");
			String publishTime = weatherInfo.getString("time");
			String weather = weatherInfo.getString("weather");
			String lowTemp = weatherInfo.getString("l_tmp");
			String highTemp = weatherInfo.getString("h_tmp");
			//���д洢
			saveWeatherInfo(context, cityName, weatherCode, publishTime, weather, 
					lowTemp, highTemp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �����������ɺ��������Ϣ���ݣ���sharedpreferences
	 */
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, 
			String publishTime, String weather, String lowTemp, String highTemp){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("publish_time", publishTime);
		editor.putString("weather", weather);
		editor.putString("low_temp", lowTemp);
		editor.putString("high_temp", highTemp);
		editor.putString("current_date", simpleDateFormat.format(new Date()));
		editor.commit();
	}
}
