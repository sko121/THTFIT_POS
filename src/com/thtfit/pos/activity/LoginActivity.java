package com.thtfit.pos.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.Config;
import android.util.Log;

public class LoginActivity extends Activity implements OnClickListener {

	private String TAG = "LoginActivity";

	private EditText edit_loginName;
	private EditText edit_password;
	private Button button_login;
	private Button button_logoutManager;
	private Button button_switchManager;

	private BroadcastReceiver receiver_Login;

	int loginStatus;
	int loginStatusManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		edit_loginName = (EditText) findViewById(R.id.loginname_edit);
		edit_password = (EditText) findViewById(R.id.password_edit);
		button_login = (Button) findViewById(R.id.signin_button);
		button_logoutManager = (Button) findViewById(R.id.logout_manager_button);
		button_switchManager = (Button) findViewById(R.id.switch_manager_button);

		edit_loginName.setText(Config.get("loginName"));
		edit_password.setText(Config.get("passWord"));

		SharedPreferences preferences = getSharedPreferences("login",
				Context.MODE_PRIVATE);
		loginStatus = preferences.getInt("loginStatus", 0);
		loginStatusManager = preferences.getInt("loginStatusManager", 0);// 店长登录状态

		// 判断店长是否登录
		if (0 == loginStatusManager) {
			// onStop();

			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("初次使用，请先登录管理帐号");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}

		if (2 == loginStatus) {
			// onStop();

			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("您已登录！");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							LoginActivity.this.finish();
						}
					});
			builder.setNeutralButton("切换帐号",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							edit_loginName.setText(null);
							edit_password.setText(null);
							logoutManager();
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("退出帐号",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							edit_loginName.setText(null);
							edit_password.setText(null);
							Config.addSaveOption("loginName", "");
							Config.addSaveOption("passWord", "");
							Config.save();

							SharedPreferences preferences = getSharedPreferences(
									"login", Context.MODE_PRIVATE);
							Editor editor = preferences.edit();
							editor.putInt("loginStatus", 0);
							editor.commit();
							finish();
						}
					});
			builder.create().show();
		}

		button_login.setOnClickListener(this);
		button_logoutManager.setOnClickListener(this);
		button_switchManager.setOnClickListener(this);

		receiver_Login = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent
						.getAction()
						.equals("com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA")) {
					final String loginResult = intent
							.getStringExtra("loginResult");
					if (loginResult.equals("success")) {

						CustomDialog.Builder builder = new CustomDialog.Builder(
								LoginActivity.this);
						builder.setTitle("登录提示");
						builder.setMessage("登录成功");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										finish();
									}
								});
						builder.create().show();

					} else {

						CustomDialog.Builder builder = new CustomDialog.Builder(
								LoginActivity.this);
						builder.setTitle("登录提示");
						builder.setMessage(loginResult);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.create().show();

					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				"com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
		try {
			registerReceiver(receiver_Login, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * String myAction = getIntent().getAction(); if (myAction == null ||
		 * !myAction.equals("LoginOut")) { login(); }
		 */
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signin_button:

			login();

			break;
		case R.id.logout_manager_button:

			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("确定登出管理员帐号？");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							logoutManager();
							dialog.dismiss();
							LoginActivity.this.finish();
						}
					});

			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();

			showLock();
			
			break;
		case R.id.switch_manager_button:

			CustomDialog.Builder builder2 = new CustomDialog.Builder(this);
			builder2.setMessage("已登录管理员帐号将会注销");
			builder2.setTitle("提示");
			builder2.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							logoutManager();
							dialog.dismiss();
						}
					});

			builder2.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder2.create().show();

			showLock();

			break;
		default:
			break;
		}

	}

	private void logoutManager() {
		SharedPreferences preferences = getSharedPreferences("login",
				Context.MODE_PRIVATE);
		preferences.edit().clear().commit();
		
		Config.clearOption();

/*		Config.addSaveOption("loginNameManager", "");
		Config.addSaveOption("passWordManager", "");

		Config.addSaveOption("loginName", "");
		Config.addSaveOption("passWord", "");
		Config.save();
		*/

	}

	private void login() {

		if (edit_loginName.getText().toString().equals("")) {
			edit_loginName.requestFocus();
			ShowMSG("请输入用户名称！");	
		} else if (edit_password.getText().toString().equals("")) {
			edit_password.requestFocus();
			ShowMSG("请输入密码！");
		} else {

			String loginName = loginStatusManager == 0 ? edit_loginName
					.getText().toString() : edit_loginName.getText().toString();
			String passWord = edit_password.getText().toString();

			// 发送后台登录请求
			Intent tmpIntent = new Intent(
					"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
			tmpIntent.putExtra("requestAction", "LoginActivity.login");
			tmpIntent
					.putExtra("responseFilter",
							"com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
			tmpIntent.putExtra("loginName", loginName);
			tmpIntent.putExtra("passWord", passWord);
			sendBroadcast(tmpIntent);
			
		}
	}

	public void showAlertDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage("这个就是自定义的提示框");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 设置你的操作事项
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();

	}

	public void showLock() {
		SharedPreferences preferences = getSharedPreferences(
				PosApplication.LOCK, MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY,
				null);
		if (patternString != null) {
			Intent intent = new Intent(this, LockActivity.class);
			intent.putExtra("TAG", TAG);
			startActivity(intent);
			return;
		}
	}

	public void ShowMSG(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// 销毁对象
	public void onDestroy() {
		try {
			unregisterReceiver(receiver_Login);
		} catch (Exception e) {
		}
		super.onDestroy();
	}

}
