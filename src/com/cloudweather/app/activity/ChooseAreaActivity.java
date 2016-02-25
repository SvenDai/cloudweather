/**
 *@author daifeng
 *2016-2-25 ����4:08:30
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
	
	private ProgressDialog progressDialog;  //��ʾ����
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter; //
	private CloudWeatherDB cloudWeatherDB;
	private List<String> dataList = new ArrayList<String>(); //���ʡ��������
	
	//ʡ�б�
	private List<Province> provincesList;
	//���б�
	private List<City> citiesList;
	//���б�
	private List<County> countiesList;
	//ѡ�е�ʡ��
	private Province selectedpProvince;
	//ѡ�е���
	private City selectedCity;
	//ѡ�еļ���
	//private int selectedLevel;
	//��ǰ�ļ���
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
		//��ȡ���ݿ����ʵ��
		cloudWeatherDB = CloudWeatherDB.getInstance(this);
		Log.d("MSG","HH "+cloudWeatherDB);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if(currentLevel == LEVEL_PROVINCE){
						selectedpProvince = provincesList.get(position);
						//�����м�����
						queryCities();
					}else if(currentLevel == LEVEL_CITY){
						selectedCity = citiesList.get(position);
						//�����ؼ�����
						queryCounty();
					}
			}
			
		});
		//����ʡ������
		queryProvinces();
	}
	
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ���鲻���ڴӷ�������ȡ
	 */
	private void queryProvinces(){
		provincesList = cloudWeatherDB.loadProvinces();//�����ݿ��ѯ����
		if(provincesList.size() >0){
			dataList.clear();
			for(Province province : provincesList){
				dataList.add(province.getProvinceName());
			}
			//����ʡ���б�
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			//�ӷ��������� 
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * ��ѯʡ����������У����ȴ����ݿ���ң��Ҳ�����ȥ�������ϲ�ѯ
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
			//�ӷ������ϲ�ѯ 
			queryFromServer(selectedpProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * ��ѯ���ڵ������ص���Ϣ���������ݿ⡣û�в�ѯ����ȥ�������ϲ�ѯ
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
			// �ӷ�������ѯ 
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * ���ݴ����ʡ���ش�������ƣ����������ϲ���ʡ��������
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//��ʾ���صĶԻ���
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
					//ͨ��runOnUiThread()�������ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();//�رնԻ���
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
				//�ص����̴߳���
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();//�رնԻ���
						Toast.makeText(ChooseAreaActivity.this, "δ�ҵ���ַ��Ϣ", 
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private  void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���Ժ�...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * �رնԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Back�������ݵ�ǰ�ļ����жϣ���ʱ������һ���б������˳�
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
