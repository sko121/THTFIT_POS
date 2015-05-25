package com.thtfit.pos.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.LockActivity;
import com.thtfit.pos.activity.LockSetupActivity;
import com.thtfit.pos.activity.LoginActivity;
import com.thtfit.pos.activity.ManageActivity;
import com.thtfit.pos.activity.ReportActivity;
import com.thtfit.pos.activity.SearchActivity;
import com.thtfit.pos.activity.ShopingActivity;
import com.thtfit.pos.service.PosApplication;

public class MainFragment extends Fragment implements OnClickListener {

	private View mView;
	private ImageButton imageButton_login;
	private ImageButton imageButton_shop;
	private ImageButton imageButton_report;
	private ImageButton imageButton_manage;
	private ImageButton imageButton_search;
	private ImageButton imageButton_setting;
	
	PosApplication application = new PosApplication();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_main, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
	}

	private void setupView() {
		imageButton_login = (ImageButton) mView
				.findViewById(R.id.chooseMenu_login_imageButton);
		imageButton_shop = (ImageButton) mView
				.findViewById(R.id.chooseMenu_shop_imageButto);
		imageButton_report = (ImageButton) mView
				.findViewById(R.id.chooseMenu_report_imageButton);
		imageButton_manage = (ImageButton) mView
				.findViewById(R.id.chooseMenu_manage_imageButton);
		imageButton_search = (ImageButton) mView
				.findViewById(R.id.chooseMenu_search_imageButton);
		imageButton_setting = (ImageButton) mView
				.findViewById(R.id.chooseMenu_setting_imageButton);
		
		
		imageButton_login.setOnClickListener(this);
		imageButton_shop.setOnClickListener(this);
		imageButton_report.setOnClickListener(this);
		imageButton_manage.setOnClickListener(this);
		imageButton_search.setOnClickListener(this);
		imageButton_setting.setOnClickListener(this);
		
		

	}

	public void onClick(View v) {
		if (v.getId() == R.id.chooseMenu_login_imageButton) {
			
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivityForResult(intent, 0);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
		}
		if (v.getId() == R.id.chooseMenu_shop_imageButto && loginCheck()) {
			
			Intent intent = new Intent();
			intent.setClass(getActivity(), ShopingActivity.class);
			startActivityForResult(intent, 0);
			
	/*		getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);*/
		}
		if (v.getId() == R.id.chooseMenu_report_imageButton && loginCheck() && lockCheck()) {
			application.setIsVerification(true);
			Intent intent = new Intent();
			intent.setClass(getActivity(), ReportActivity.class);
			startActivityForResult(intent, 0);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
		}
		if (v.getId() == R.id.chooseMenu_manage_imageButton && loginCheck() && lockCheck()) {
			application.setIsVerification(true);
			Intent intent = new Intent();
			intent.setClass(getActivity(), ManageActivity.class);
			startActivityForResult(intent, 0);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
		}
		if (v.getId() == R.id.chooseMenu_search_imageButton && loginCheck() && lockCheck()) {
			application.setIsVerification(true);
			Intent intent = new Intent();
			intent.setClass(getActivity(), SearchActivity.class);
			startActivityForResult(intent, 0);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
		}
		if (v.getId() == R.id.chooseMenu_setting_imageButton && loginCheck() && lockCheck()) {
			application.setIsVerification(true);
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

			/*
			 * Intent intent = new Intent(); intent.setClass(getActivity(),
			 * SettingActivity.class); startActivityForResult(intent, 0);
			 * getActivity().overridePendingTransition(R.anim.push_left_in,
			 * R.anim.push_left_out);
			 */
		}

	}

	

	public boolean loginCheck() {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"login", Context.MODE_PRIVATE);
		int loginStatus = preferences.getInt("loginStatus", 0);
		if (loginStatus == 0) {
			ShowMSG("您尚未登录，请登录后进入");
			return false;
		}
		return true;
	}
	
	
	public boolean lockCheck() {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				PosApplication.LOCK, Context.MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY,
				null);
		if(patternString == null){
			Intent intent = new Intent(getActivity(), LockSetupActivity.class);
			startActivity(intent);
			return false;
		}
		return true;
		
	}

	public void ShowMSG(CharSequence msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

}
