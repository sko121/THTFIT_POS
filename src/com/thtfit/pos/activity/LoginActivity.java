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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.Config;

public class LoginActivity extends Activity implements OnClickListener
{
	private EditText etLoginName;
	private EditText etPassword;
	private Button btLogin;
	private Button btLogoutManager;
	private Button btSwitchManager;

	int loginStatus;
	int loginStatusManager;

	/* debug */
	private static String TAG = "LoginActivity";
	public static final boolean isDebug = true;
	// public static final boolean isDebug = false;
	public DebugPrint LOG = new DebugPrint(isDebug, TAG);

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals("com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA"))
			{
				final String loginResult = intent.getStringExtra("loginResult");
				if (loginResult.equals("success"))
				{
					CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
					builder.setTitle("登录提示");
					builder.setMessage("登录成功");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							finish();
						}
					});
					builder.create().show();
				}
				else
				{
					CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
					builder.setTitle("登录提示");
					builder.setMessage(loginResult);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		}
	};

	private void registerBroadcasts()
	{
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
		mIntentFilter.setPriority(100);
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		InitView();

		// checkLoginStatus();

		registerBroadcasts();
	}

	private void InitView()
	{
		etLoginName = (EditText) findViewById(R.id.loginname_edit);
		etPassword = (EditText) findViewById(R.id.password_edit);
		btLogin = (Button) findViewById(R.id.signin_button);
		btLogoutManager = (Button) findViewById(R.id.logout_manager_button);
		btSwitchManager = (Button) findViewById(R.id.switch_manager_button);

		btLogin.setOnClickListener(this);
		btLogoutManager.setOnClickListener(this);
		btSwitchManager.setOnClickListener(this);

		etLoginName.setText(Config.get("loginName"));
		etPassword.setText(Config.get("passWord"));
	}

	private void checkLoginStatus()
	{
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		loginStatus = preferences.getInt("loginStatus", 0);
		loginStatusManager = preferences.getInt("loginStatusManager", 0);// 店长登录状态

		// 判断店长是否登录
		if (0 == loginStatusManager)
		{
			// onStop();
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("初次使用，请先登录管理帐号");
			builder.setTitle("提示");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			builder.create().show();
		}

		if (2 == loginStatus)
		{
			// onStop();
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("您已登录！");
			builder.setTitle("提示");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				/* callback from dialog builder. */
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					LoginActivity.this.finish();
				}
			});
			builder.setNeutralButton("切换帐号", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					etLoginName.setText(null);
					etPassword.setText(null);
					logoutManager();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("退出帐号", new android.content.DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					etLoginName.setText(null);
					etPassword.setText(null);
					Config.addSaveOption("loginName", "");
					Config.addSaveOption("passWord", "");
					Config.save();

					SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putInt("loginStatus", 0);
					editor.commit();
					finish();
				}
			});
			builder.create().show();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.signin_button:
				login();
				break;

			case R.id.logout_manager_button:
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("确定登出管理员帐号？");
				builder.setTitle("提示");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						logoutManager();
						dialog.dismiss();
						LoginActivity.this.finish();
					}
				});

				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
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
				builder2.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{

						logoutManager();
						dialog.dismiss();
					}
				});

				builder2.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
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

	private void logoutManager()
	{
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		preferences.edit().clear().commit();

		Config.clearOption();

		/*
		 * Config.addSaveOption("loginNameManager", "");
		 * Config.addSaveOption("passWordManager", "");
		 * 
		 * Config.addSaveOption("loginName", "");
		 * Config.addSaveOption("passWord", ""); Config.save();
		 */
	}

	private void login()
	{
		if (etLoginName.getText().toString().equals(""))
		{
			etLoginName.requestFocus();
			ShowMsg("请输入用户名称！");
		}
		else if (etPassword.getText().toString().equals(""))
		{
			etPassword.requestFocus();
			ShowMsg("请输入密码！");
		}
		else
		{
			String loginName = loginStatusManager == 0 ? etLoginName.getText().toString() : etLoginName.getText()
					.toString();
			String passWord = etPassword.getText().toString();

			// 发送后台登录请求
			Intent tmpIntent = new Intent("com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
			tmpIntent.putExtra("requestAction", "LoginActivity.login");
			tmpIntent.putExtra("responseFilter", "com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
			tmpIntent.putExtra("loginName", loginName);
			tmpIntent.putExtra("passWord", passWord);
			sendBroadcast(tmpIntent);
		}
	}

	public void showAlertDialog()
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage("这个就是自定义的提示框");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置你的操作事项
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public void showLock()
	{
		SharedPreferences preferences = getSharedPreferences(PosApplication.LOCK, MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY, null);
		if (patternString != null)
		{
			Intent intent = new Intent(this, LockActivity.class);
			intent.putExtra("TAG", TAG);
			startActivity(intent);
			return;
		}
	}

	public void ShowMsg(CharSequence msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// 销毁对象
	public void onDestroy()
	{
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
		}
		super.onDestroy();
	}
}
