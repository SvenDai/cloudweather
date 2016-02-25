/**
 *@author daifeng
 *2016-2-25 下午4:08:30
 *CloudWeather
 */
package com.cloudweather.app.activity;


import java.util.ArrayList;
import java.util.List;

import com.cloudweather.app.R;
import com.cloudweather.app.R.id;
import com.cloudweather.app.db.CloudWeatherDB;
import com.cloudweather.app.model.City;
import com.cloudweather.app.model.County;
import com.cloudweather.app.model.Province;
import com.cloudweather.app.util.HandleResponseUtility;
import com.cloudweather.app.util.HttpCallbackListener;
import com.cloudweather.app.util.HttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author daifeng
 *
 */
public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;  //显示进度
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter; //
	private CloudWeatherDB cloudWeatherDB;
	private List<String> dataList = new ArrayList<String>(); //存放省市县数据
	
	//省列表
	private List<Province> provincesList;
	//市列表
	private List<City> citiesList;
	//县列表
	private List<County> countiesList;
	//选中的省份
	private Province selectedpProvince;
	//选中的市
	private City selectedCity;
	//选中的级别
	//private int selectedLevel;
	//当前的级别
	private int currentLevel;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleView = (TextView)findViewById(R.id.title_text);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		//获取数据库对象实例
		cloudWeatherDB = CloudWeatherDB.getInstance(this);
		Log.d("MSG","HH "+cloudWeatherDB);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if(currentLevel == LEVEL_PROVINCE){
						selectedpProvince = provincesList.get(position);
						//加载市级数据
						queryCities();
					}else if(currentLevel == LEVEL_CITY){
						selectedCity = citiesList.get(position);
						//加载县级数据
						queryCounty();
					}
			}
			
		});
		//加载省份数据
		queryProvinces();
	}
	
	/**
	 * 查询全国所有的省，优先从数据库查询，查不到在从服务器上取
	 */
	private void queryProvinces(){
		provincesList = cloudWeatherDB.loadProvinces();//从数据库查询数据
		if(provincesList.size() >0){
			dataList.clear();
			for(Province province : provincesList){
				dataList.add(province.getProvinceName());
			}
			//更新省份列表
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			//从服务器上找 
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * 查询省下面的所有市，优先从数据库查找，找不到再去服务器上查询
	 */
	private void queryCities(){
		citiesList = cloudWeatherDB.loadCities(selectedpProvince.getId());
		if(citiesList.size() > 0){
			dataList.clear();
			for(City city : citiesList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedpProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			//从服务器上查询 
			queryFromServer(selectedpProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询市内的所有县的信息，优先数据库。没有查询到再去服务器上查询
	 */
	private void queryCounty(){
		countiesList = cloudWeatherDB.loadCounties(selectedCity.getId());
		if(countiesList.size() >0){
			dataList.clear();
			for(County county : countiesList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else {
			// 从服务器查询 
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * 根据传入的省市县代码和名称，到服务器上查找省市县数据
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//显示加载的对话框
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				Log.d("MSG", response );
				if("province".equals(type)){
					result = HandleResponseUtility.handleProvincesResponse(cloudWeatherDB, 
							response);
				}else if("city".equals(type)){
					result = HandleResponseUtility.handleCitiesResponse(cloudWeatherDB, 
							response, selectedpProvince.getId());
				}else if("county".equals(type)){
					result = HandleResponseUtility.handleCountiesResponse(cloudWeatherDB, 
							response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法返回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();//关闭对话框
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//回到主线程处理
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();//关闭对话框
						Toast.makeText(ChooseAreaActivity.this, "未找到地址信息", 
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private  void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("请稍后...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back键，根据当前的级别判断，此时返回哪一级列表，或者退出
	 */
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
