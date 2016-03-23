/**
 *@author daifeng
 *2016-2-27 下午5:56:50
 *CloudWeather
 */
package com.cloudweather.app.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.cloudweather.app.R;
import com.cloudweather.app.service.AutoUpdateService;
import com.cloudweather.app.util.HandleResponseUtility;
import com.cloudweather.app.util.HttpCallbackListener;
import com.cloudweather.app.util.MovingPictureView;
import com.cloudweather.app.util.WeatherRequestHttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	//天气状态
	private int WEATHER_FLAGE = 10;
	
	/**
	 * 用于显示动态显示界面
	 * *********************************************************
	 */
	private RelativeLayout today_yubao; //背景
	//加载数据，更新界面
    public static ForecastHandler forecastHandler ; 
	//切换图片定时器、handler、图片标号
	public static Timer weather_timer;
	private Day_Lei_Handler day_lei_handler;
	private Night_Qing_Handler night_qing_handler;
	private Day_Rain_Handler Day_Rain_Handler;
	private Day_Snow_Handler Day_Snow_Handler;
	private Day_RainSnow_Handler Day_RainSnow_Handler;
	private Day_Wu_Handler Day_Wu_Handler;
	//平移的图片
	private MovingPictureView 
	w1_move1,w1_move2,w1_move3,w1_move4,w1_move5,
	w2_move1,w2_move2,w2_move3,w2_move4,w2_move5,
	w3_move1,w3_move2,w3_move3,w3_move4,w3_move5,
	w4_move1,w4_move2,w4_move3,w4_move4,w4_move5,
	w5_move1,w5_move2,w5_move3,w5_move4,w5_move5,
	w6_move1,w6_move2,w6_move3,w6_move4,w6_move5,
	w7_move1,w7_move2,w7_move3,w7_move4,w7_move5;
	private ImageView m1,m2,m3,m4,m5,m6,m7,m8,m9,m10;
	public static int imgIndex;
	//平移的图片所在布局
	private RelativeLayout weather_move1,weather_move2,weather_move3,weather_move4,weather_move5
	,weather_move6,weather_move7,weather_move8,weather_move9,weather_move10; 
	//切换的图片所在布局
	private RelativeLayout  weather_qing, weather_day_duoyun, weather_day_yin,weather_night_yin,
							weather_wu,weather_mai,weather_sha;
	
	//private TextView content;
	//示意
	//private int nowindex=10; //第一个天气默认序号是10
	//private TextView nowweather;
	//private LinearLayout forecast_shili_area;
	
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
		
		/**
		 * 动态加载的view
		 * **********************************************************
		 */
        MovingPictureView.isRuning = true;
        today_yubao = (RelativeLayout)findViewById(R.id.today_yubao);
        //content = (TextView)findViewById(R.id.content);
        //forecast_shili_area = (LinearLayout)findViewById(R.id.forecast_shili_area);
        
		weather_qing = (RelativeLayout)findViewById(R.id.weather_qing);
		weather_day_duoyun = (RelativeLayout)findViewById(R.id.weather_day_duoyun);
		weather_day_yin = (RelativeLayout)findViewById(R.id.weather_day_yin);
		weather_night_yin = (RelativeLayout)findViewById(R.id.weather_night_yin);
		weather_wu = (RelativeLayout)findViewById(R.id.weather_wu);
		weather_mai = (RelativeLayout)findViewById(R.id.weather_mai);
		weather_sha = (RelativeLayout)findViewById(R.id.weather_sha);
		
		//将所有要用到的平移的图片加载好，根据当前天气，选择显示哪些图片，
		//我在这里只为weather_qing何weather_day_yin这两个天气添加了平移图片，你可以为其他的天气加上你需要的图片，然后好用.
		w1_move1 = new MovingPictureView(this, R.drawable.yjjc_h_a3,-300,10,40);
		w1_move2 = new MovingPictureView(this, R.drawable.yjjc_h_a3,250,10,40);
		w1_move3 = new MovingPictureView(this, R.drawable.yjjc_h_a4,480,40,40);
		weather_qing.removeAllViews();
		weather_qing.addView(w1_move1);
		weather_qing.addView(w1_move2);
		weather_qing.addView(w1_move3);
		
		w3_move1 = new MovingPictureView(this, R.drawable.yjjc_h_d2,-250,0,30);
		w3_move2 = new MovingPictureView(this, R.drawable.yjjc_h_d3,180,60,40);
		weather_day_yin.addView(w3_move1);
		weather_day_yin.addView(w3_move2);
	
		weather_move1 = (RelativeLayout)findViewById(R.id.weather_move1);
		weather_move2 = (RelativeLayout)findViewById(R.id.weather_move2);
		weather_move3 = (RelativeLayout)findViewById(R.id.weather_move3);
		weather_move4 = (RelativeLayout)findViewById(R.id.weather_move4);
		weather_move5 = (RelativeLayout)findViewById(R.id.weather_move5);
		weather_move6 = (RelativeLayout)findViewById(R.id.weather_move6);
		weather_move7 = (RelativeLayout)findViewById(R.id.weather_move7);
		weather_move8 = (RelativeLayout)findViewById(R.id.weather_move8);
		weather_move9 = (RelativeLayout)findViewById(R.id.weather_move9);
		weather_move10 = (RelativeLayout)findViewById(R.id.weather_move10);
		m1 = (ImageView)findViewById(R.id.m1);
		m2 = (ImageView)findViewById(R.id.m2);
		m3 = (ImageView)findViewById(R.id.m3);
		m4 = (ImageView)findViewById(R.id.m4);
		m5 = (ImageView)findViewById(R.id.m5);
		m6 = (ImageView)findViewById(R.id.m6);
		m7 = (ImageView)findViewById(R.id.m7);
		m8 = (ImageView)findViewById(R.id.m8);
		m9 = (ImageView)findViewById(R.id.m9);
		m10 = (ImageView)findViewById(R.id.m10);
		day_lei_handler = new Day_Lei_Handler(this);
		night_qing_handler = new Night_Qing_Handler(this);
		Day_Rain_Handler = new Day_Rain_Handler(this);
		Day_Snow_Handler = new Day_Snow_Handler(this);
		Day_RainSnow_Handler = new Day_RainSnow_Handler(this);
		Day_Wu_Handler = new Day_Wu_Handler(this);
		
		forecastHandler = new ForecastHandler();
		
		//***********************************************************
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
		//根据天气情况加载动态背景图片
		if(prefs.getString("weather", "").equals("晴")){
			WEATHER_FLAGE = 10;
		}else if(prefs.getString("weather", "").equals("多云")){
			WEATHER_FLAGE = 11;
		}else if(prefs.getString("weather", "").equals("阴")){
			WEATHER_FLAGE = 12;
		}
		else if(prefs.getString("weather", "").equals("小雨") || 
				prefs.getString("weather", "").equals("中雨") || 
				prefs.getString("weather", "").equals("大雨") ||
				prefs.getString("weather", "").equals("阵雨") ||
				prefs.getString("weather", "").equals("大到暴雨")){
			WEATHER_FLAGE = 21;
		}
		//发送通知，根据天气信息更改界面
		Message msg = new Message();
        Bundle b = new Bundle();
        msg.what=WEATHER_FLAGE;
        Log.d("MSG.WHAT",WEATHER_FLAGE+"sssssss");
        msg.setData(b);
        forecastHandler.sendMessage(msg);
		//激活启动AutoUpdateService服务
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	/**
	 * 处理动态数据
	 * ************************************************************
	 */
	//加载预报数据
    class ForecastHandler extends Handler {       
        //接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //示意处理----------
            switch (msg.what) {
            case 10:
            	//nowweather.setText("晴天");
            	day_qing();
				return;
			case 11:
				//nowweather.setText("多云");
				day_duoyun();
				return;					
			case 12:
				//nowweather.setText("阴天");
				day_yin();
				return;
			case 13:
				//nowweather.setText("夜·阴");
				night_yin();
				return;
			case 14:
				//nowweather.setText("大雾");
				day_wu();
				return;
			case 15:
				//nowweather.setText("雾霾");
				day_mai();
				return;					
			case 16:
				//nowweather.setText("风沙");
				day_sha();
				return;
			case 17:
				//nowweather.setText("下雪");
				day_snow();
				return;
			case 18:
				//nowweather.setText("雨·雪");
				day_rainsnow();
				return;
			case 19:
				//nowweather.setText("雷电");
				day_lei();
				return;					
			case 20:
				//nowweather.setText("夜·晴");
				night_qing();
				return;
			case 21:
				//nowweather.setText("下雨");
				day_rain();
				return;				
			default:
				break;
			}
            //示意处理----------
        }
    }
    
  //10白天_晴
    public void day_qing(){
		wordBlack();
		showweather("day_qing");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_a1);
		if(!w1_move1.isstarted){
			new Thread(w1_move1).start();//每一个移动的图片都是一个线程
			new Thread(w1_move2).start();
			new Thread(w1_move3).start();
		}
    } 
    
    //11白天_多云
    public void day_duoyun(){
    	wordBlack();
    	showweather("day_duoyun");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_c1);
		if(!w1_move1.isstarted){
			new Thread(w1_move1).start();//这里的天气用了上一个天气的图片，也可以根据自己需要用想要的图片
			new Thread(w1_move2).start();
			new Thread(w1_move3).start();
		}
//		new Thread(w2_move1).start();
//		new Thread(w2_move2).start();
//		new Thread(w2_move3).start();
//		new Thread(w2_move4).start();
//		new Thread(w2_move5).start();	
    }
    //12阴天
    public void day_yin(){
    	wordWhite();
    	showweather("day_yin");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_d1);
		if(!w3_move1.isstarted){
			new Thread(w3_move1).start();
			new Thread(w3_move2).start();
			
		}
    }
    //13夜晚阴天
    public void night_yin(){
    	wordWhite();
    	showweather("night_yin");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_l1);
//		new Thread(w4_move1).start();
//		new Thread(w4_move2).start();
    }
    //14大雾
    public void day_wu(){
		wordBlack();
		showweather("day_wu");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_i1);
//		new Thread(w5_move1).start();
//		new Thread(w5_move2).start();
    }
    //15霾
    public void day_mai(){
		wordBlack();
		showweather("day_mai");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_j1);
    }
    //16风沙
    public void day_sha(){
		wordBlack();
		showweather("day_sha");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_k1);
    }
    //17雪
    public void day_snow(){
    	wordBlack();
    	showweather("other");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_g1);
		m1.setImageResource(R.drawable.yjjc_h_g2);
		m2.setImageResource(R.drawable.yjjc_h_g3);
		m3.setImageResource(R.drawable.yjjc_h_g4);
		m4.setImageResource(R.drawable.yjjc_h_g5);
    	Day_Snow_Timer chage = new Day_Snow_Timer();
 		Thread chageimg = new Thread(chage);
 		chageimg.start();
    }
    //18雨夹雪
    public void day_rainsnow(){
    	wordWhite();
    	showweather("other");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_h1);
		m1.setImageResource(R.drawable.yjjc_h_h2);
		m2.setImageResource(R.drawable.yjjc_h_h3);
		m3.setImageResource(R.drawable.yjjc_h_h4);
    	Day_RainSnow_Timer chage = new Day_RainSnow_Timer();
 		Thread chageimg = new Thread(chage);
 		chageimg.start();
    }
    
    //19雷雨
    public void day_lei(){
    	wordWhite();
    	showweather("other");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_f1);
		m1.setImageResource(R.drawable.yjjc_h_f2);
		m2.setImageResource(R.drawable.yjjc_h_f3);
		m3.setImageResource(R.drawable.yjjc_h_f4);
		m4.setImageResource(R.drawable.yjjc_h_f5);
		m5.setImageResource(R.drawable.yjjc_h_f6);
		m6.setImageResource(R.drawable.yjjc_h_f7);
		m7.setImageResource(R.drawable.yjjc_h_f8);
    	Day_Lei_Timer chage = new Day_Lei_Timer();
 		Thread chageimg = new Thread(chage);
 		chageimg.start();
    } 
    //20夜晚_晴
    public void night_qing(){
    	wordWhite();
    	showweather("other");
		today_yubao.setBackgroundResource(R.drawable.yjjc_h_b1);
		m1.setImageResource(R.drawable.yjjc_h_b2);
		m2.setImageResource(R.drawable.yjjc_h_b3);
		m3.setImageResource(R.drawable.yjjc_h_b4);
		m4.setImageResource(R.drawable.yjjc_h_b5);
		m5.setImageResource(R.drawable.yjjc_h_b6);
    	Night_Qing_Timer chage = new Night_Qing_Timer();
 		Thread chageimg = new Thread(chage);
 		chageimg.start();
    }
    //21雨
    public void day_rain(){
    		wordWhite();
    		showweather("other");
			today_yubao.setBackgroundResource(R.drawable.yjjc_h_e1);
			m1.setImageResource(R.drawable.yjjc_h_e2);
			m2.setImageResource(R.drawable.yjjc_h_e3);
			m3.setImageResource(R.drawable.yjjc_h_e4);
			m4.setImageResource(R.drawable.yjjc_h_e5);
	 		Day_Rain_Timer chage = new Day_Rain_Timer();
	 		Thread chageimg = new Thread(chage);
	 		chageimg.start();
    }
    
    class Day_Rain_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 3){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.Day_Rain_Handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 300); 
		}
	}
    
    class Night_Qing_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 4){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.night_qing_handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 1*500);
		}
	}

    class Day_Wu_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 4){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.Day_Wu_Handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 1*500);
		}
	}

	class Day_Lei_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 15){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.day_lei_handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 1*200);
		}
	}

    class Day_Snow_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 3){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.Day_Snow_Handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 300); 
		}
	}

    class Day_RainSnow_Timer implements Runnable{
		@Override
		public void run(){
			if(WeatherActivity.weather_timer != null){
				WeatherActivity.weather_timer.cancel();
				System.gc();
			}
			WeatherActivity.weather_timer = new Timer();
            TimerTask t = new TimerTask() {
        		@Override
        		public void run() {
    	            if(WeatherActivity.imgIndex > 2){
    	            	WeatherActivity.imgIndex = 0;
    	            }
    	            Message msg = new Message();
    	            Bundle b = new Bundle();
    	            b.putString("index", String.valueOf(WeatherActivity.imgIndex));
    	            msg.setData(b);
    	            WeatherActivity.imgIndex += 1;
    	            WeatherActivity.this.Day_RainSnow_Handler.sendMessage(msg);
        		}
        	};
        	WeatherActivity.weather_timer.schedule(t, 0, 300); 
		}
	}
    
    class Day_Snow_Handler extends Handler{
    	private Activity context;
        public Day_Snow_Handler() {
        }
        public Day_Snow_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else if(msgindex == 3){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move4.setVisibility(View.VISIBLE);
            }else{// if(msgindex == 4){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }
        }
    }

    class Day_Wu_Handler extends Handler{
    	private Activity context;
        public Day_Wu_Handler() {
        }
        public Day_Wu_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move5.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else if(msgindex == 3){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move4.setVisibility(View.VISIBLE);
            }else if(msgindex == 4){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move5.setVisibility(View.VISIBLE);
            }else{
            	
            }
        }
    }

    class Day_RainSnow_Handler extends Handler{
    	private Activity context;
        public Day_RainSnow_Handler() {
        }
        public Day_RainSnow_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else{
            	
            }
        }
    }

    class Day_Rain_Handler extends Handler{
    	private Activity context;
        public Day_Rain_Handler() {
        }
        public Day_Rain_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else if(msgindex == 3){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move4.setVisibility(View.VISIBLE);
            }else{// if(msgindex == 4){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }
        }
    }

    class Night_Qing_Handler extends Handler{
    	private Activity context;
        public Night_Qing_Handler() {
        }
        public Night_Qing_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move5.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else if(msgindex == 3){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move4.setVisibility(View.VISIBLE);
            }else if(msgindex == 4){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move5.setVisibility(View.VISIBLE);
            }else{	//if(msgindex == 5){
            	weather_move5.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }
        }
    }
	
    class Day_Lei_Handler extends Handler{
    	private Activity context;
        public Day_Lei_Handler() {
        }
        public Day_Lei_Handler(Activity context) {
        	this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 更新UI
            int msgindex = 20;
            Bundle b = msg.getData();
            if(b.getString("index")!=null){
            	msgindex = Integer.parseInt(b.getString("index"));
            }
            if(msgindex == 0){
            	weather_move7.setVisibility(View.INVISIBLE);
            	weather_move1.setVisibility(View.VISIBLE);
            }else if(msgindex == 1){
            	weather_move1.setVisibility(View.INVISIBLE);
            	weather_move2.setVisibility(View.VISIBLE);
            }else if(msgindex == 2){
            	weather_move2.setVisibility(View.INVISIBLE);
            	weather_move3.setVisibility(View.VISIBLE);
            }else if(msgindex == 3){
            	weather_move3.setVisibility(View.INVISIBLE);
            	weather_move4.setVisibility(View.VISIBLE);
            }else if(msgindex == 4){
            	weather_move4.setVisibility(View.INVISIBLE);
            	weather_move5.setVisibility(View.VISIBLE);
            }else if(msgindex == 5){
            	weather_move5.setVisibility(View.INVISIBLE);
            	weather_move6.setVisibility(View.VISIBLE);
            }else if(msgindex == 6){
            	weather_move6.setVisibility(View.INVISIBLE);
            	weather_move7.setVisibility(View.VISIBLE);
            }else{
            	weather_move7.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    //黑字
    public void wordBlack(){
    	int color = this.getResources().getColor(R.color.Myblack);
    	publishTimeText.setTextColor(color);
    	currentDateText.setTextColor(color);
    } 
    //白字
    public void wordWhite(){
    	int color = this.getResources().getColor(R.color.MyWhite);
    	publishTimeText.setTextColor(color);
    	currentDateText.setTextColor(color);
    }
    
  //显示某一天气,显示帧天气传参数other。
    public void showweather(String weather){
    	initWeatherLayout();
    	if(weather.equals("day_qing")){
    		weather_qing.setVisibility(View.VISIBLE);
    	}else if(weather.equals("day_duoyun")){
    		weather_qing.setVisibility(View.VISIBLE);//没有为weather_day_duoyun添加图片，所以暂时用的是day_qing天气中的图片。你可以自己在create方法中加载，在这里就可以显示了
    		weather_day_duoyun.setVisibility(View.VISIBLE);
    	}else if(weather.equals("day_yin")){
    		weather_day_yin.setVisibility(View.VISIBLE);
    	}else if(weather.equals("night_yin")){
    		weather_night_yin.setVisibility(View.VISIBLE);
    	}else if(weather.equals("day_wu")){
    		weather_wu.setVisibility(View.VISIBLE);
    	}else if(weather.equals("day_mai")){
    		weather_mai.setVisibility(View.VISIBLE);
    	}else if(weather.equals("day_sha")){
    		weather_sha.setVisibility(View.VISIBLE);
    	}else{
    		
    	}
    }
    //初始化天气布局
    public void initWeatherLayout(){
		if(WeatherActivity.weather_timer != null){
			WeatherActivity.weather_timer.cancel();
		}
		weather_qing.setVisibility(View.INVISIBLE);
		weather_day_duoyun.setVisibility(View.INVISIBLE);
		weather_day_yin.setVisibility(View.INVISIBLE);
		weather_night_yin.setVisibility(View.INVISIBLE);
		weather_wu.setVisibility(View.INVISIBLE);
		weather_mai.setVisibility(View.INVISIBLE);
		weather_sha.setVisibility(View.INVISIBLE);
		weather_move1.setVisibility(View.INVISIBLE);
		weather_move2.setVisibility(View.INVISIBLE);
		weather_move3.setVisibility(View.INVISIBLE);
		weather_move4.setVisibility(View.INVISIBLE);
		weather_move5.setVisibility(View.INVISIBLE);
		weather_move6.setVisibility(View.INVISIBLE);
		weather_move7.setVisibility(View.INVISIBLE);
		weather_move8.setVisibility(View.INVISIBLE);
		weather_move9.setVisibility(View.INVISIBLE);
		weather_move10.setVisibility(View.INVISIBLE);
    }
}
