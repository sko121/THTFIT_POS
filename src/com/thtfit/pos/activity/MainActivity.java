package com.thtfit.pos.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.thtfit.pos.R;
import com.thtfit.pos.service.POSService;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.POSLog;
import com.thtfit.pos.util.Utils;

public class MainActivity extends FragmentActivity {

	

	private String LOG_TAG = "MainActivity";
	private LocationManager locationManager;
	private Location loc;
	private BroadcastReceiver receiver_Main;
	private POSLog posLog = new POSLog();

	private static final String KEY = "K9uYlOFfCkoG6I7dC13wqYlm";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Log.d(LOG_TAG, "=====start MainActivity=====");

		// 启动后台服务
		Intent serviceIntent = new Intent(MainActivity.this, POSService.class);
		serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(serviceIntent);

		startPushServer();

		new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				postDriveInfo();
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				posLog.setCheckUp(getApplicationContext(),
						"THIS IS TEST DATA TOO!");
				posLog.setGPS(getApplicationContext(), "116.417854,39.921988");
				posLog.setGPS(getApplicationContext(), "116.417854,39.921988");
				posLog.setLogs(getApplicationContext(),
						"THIS IS TEST DATA TOO!");
				posLog.setActionRoutine(getApplicationContext(),
						"THIS IS TEST DATA TOO!");
				posLog.setLogs(getApplicationContext(),
						"THIS IS TEST DATA TOO!");
				posLog.setPayByCard(getApplicationContext(), "HELLO WORLD !");
				posLog.setCheckUp(getApplicationContext(),
						"THIS IS TEST DATA TOO!");
				// postPushInfoFile();
			}
		}.start();

		receiver_Main = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent
						.getAction()
						.equals("com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA")) {
					final String responseResult = intent
							.getStringExtra("responseResult");

					Log.d(LOG_TAG, "=====" + responseResult + "======");
					if ("{'Result':'1'}".equals(responseResult)) {
						Toast.makeText(getApplicationContext(),
								"The background data has been upload",
								Toast.LENGTH_SHORT).show();
					} else if ("{'Result':'2'}".equals(responseResult)) {
						Toast.makeText(getApplicationContext(),
								"The background data has been upload",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"The background to upload data exception",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				"com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA");
		try {
			registerReceiver(receiver_Main, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	// 启动推送接收服务
	public void startPushServer() {
		if (!Utils.isBind(getApplicationContext())) {
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY, KEY);
		}
	}

	private void postPushInfoFile() {
		Date date = new Date();
		String timeNow = new SimpleDateFormat("yyyyMMdd")
				.format(date.getTime());
		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "THTFIT/PushInfo" + File.separator + timeNow
				+ ".json";

		String content = "HELLO WORLD !";

		// 检查上传信息文件
		File infoFile = new File(filePath);
		if (!infoFile.exists()) {
			try {
				infoFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writeByFileWrite(filePath, content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void postDriveInfo() {
		// 发送后台登录请求
		Intent tmpIntent = new Intent(
				"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
		tmpIntent.putExtra("requestAction", "MainActivity.device");
		tmpIntent.putExtra("responseFilter",
				"com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA");
		tmpIntent.putExtra("dName", getIMEI());
		tmpIntent.putExtra("dSysversion", Build.DISPLAY);
		tmpIntent.putExtra("dMac", getMacAddress());
		// tmpIntent.putExtra("dGPS", getLocation(MainActivity.this)[0] + ","
		// + getLocation(MainActivity.this)[1]);
		tmpIntent.putExtra("dGPS", "116.417854,39.921988");
		tmpIntent.putExtra("dClient", "A301");
		sendBroadcast(tmpIntent);

	} 

	/**
	 * 
	 * 向文件写入内容
	 * 
	 * @param destFile
	 * 
	 * @throws IOException
	 */

	public static void writeByFileWrite(String destFile, String content)

	throws IOException {

		File file = new File(destFile);

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write("\r\n" + content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获得设备ID(IMEI)
	public String getIMEI() {

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getDeviceId();
	}

	// 获得MAC
	public String getMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	// 获得位置信息
	public String[] getLocation(Context mContext) {

		String[] location = { "0", "0" };
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean isNetworkConnected = isNetworkConnected(MainActivity.this);
		try {
			if (network || isNetworkConnected) {
				// 使用网络信息获取地理位置
				loc = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				location[0] = loc.getLatitude() + "";
				location[1] = loc.getLongitude() + "";
			} else if (gps) {
				// 使用GPS信息获取地理位置
				loc = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				location[0] = loc.getLatitude() + "";
				location[1] = loc.getLongitude() + "";
			} else {
				Toast.makeText(mContext, "请打开GPS", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "====getLocation exception====" + e.toString());
		}
		return location;

	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		PosApplication application = new PosApplication();
		if (application.getIsVerification()) {
			Intent intent = new Intent(this, LockActivity.class);
			startActivity(intent);
			application.setIsVerification(false);
		}
	}

	// 销毁对象
	public void onDestroy() {
		try {
			unregisterReceiver(receiver_Main);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

}
