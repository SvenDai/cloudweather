/**
 *@author daifeng
 *2016-2-25 下午2:17:16
 *CloudWeather
 */
package com.cloudweather.app.util;

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
	 * 解析并存储服务器返回的省级数据
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
					//将解析出来的数据存储到数据库
					cloudWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析并存储服务器返回的市级数据
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
	 * 解析并存储服务器返回的县级数据
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
}
