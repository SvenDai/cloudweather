/**
 *@author daifeng
 *2016-2-23 下午10:27:52
 *CloudWeather
 */
package com.cloudweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.cloudweather.app.model.City;
import com.cloudweather.app.model.County;
import com.cloudweather.app.model.Province;

import android.R.id;
import android.R.string;
import android.app.ApplicationErrorReport.CrashInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author daifeng
 *
 */
public class CloudWeatherDB {
	
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "cloud_weather";
	/**
	 * 数据库版本
	 */
	public static final int DB_VERSION = 1;
	
	private static CloudWeatherDB cloudWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造函数
	 */
	private CloudWeatherDB(Context context){
		CloudWeatherOpenHelper dbHelper = new CloudWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 获取db的实例
	 */
	public synchronized static CloudWeatherDB getInstance(Context context){
		if(cloudWeatherDB == null){
			cloudWeatherDB = new CloudWeatherDB(context);
		}
		return cloudWeatherDB;
	}
	
	/**
	 * 将province的实例存储进入数据库
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}
	}
	
	/**
	 * 从数据库中读取province信息
	 */
	public List<Province> loadProvinces(){
		List<Province> provincesList = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				provincesList.add(province);
			}while(cursor.moveToNext());
		}
		return provincesList;
	}
	
	/**
	 * 将city的实例存入数据库
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insert("City", null, contentValues);
		}
	}
	
	/**
	 * 从数据库中取出city的信息
	 */
	public List<City> loadCities(int provinceId){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ? ", new String[]{String.valueOf(provinceId)},
				null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				cityList.add(city);
			}while(cursor.moveToNext());
		}
		return cityList;
	}
	
	/**
	 * 将county的实例存入数据库
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			db.insert("country", null, contentValues);
		}
	}
	
	/**
	 * 读取数据库中的county实例
	 */
	public List<County> loadCounties(int cityId){
		List<County> cityList = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ? ", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county =new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				cityList.add(county);
			} while (cursor.moveToNext());
		}
		return cityList;
	}
	
}
