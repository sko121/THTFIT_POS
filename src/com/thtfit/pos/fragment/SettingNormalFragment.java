package com.thtfit.pos.fragment;


import com.thtfit.pos.R;
import com.thtfit.pos.activity.LockSetupActivity;
import com.thtfit.pos.activity.ManageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class SettingNormalFragment extends Fragment implements OnClickListener
{
	private View mView;
	private LinearLayout accountsGesture;
	private LinearLayout accountsChangePassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_manage_accounts, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		accountsGesture = (LinearLayout) mView.findViewById(R.id.accounts_gesture);		
		accountsGesture.setOnClickListener(this);
		
		accountsChangePassword = (LinearLayout) mView.findViewById(R.id.accounts_change_password);
		accountsChangePassword.setOnClickListener(this);
				
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.accounts_gesture:
				Log.d("niotong", "account_gesture going to do!");
				Intent intent = new Intent(getActivity(), LockSetupActivity.class);
				startActivity(intent);

				break;
			case R.id.accounts_change_password:
				Log.d("niotong", "change_password going to do!");
				Fragment fragment = new ChangePasswordFragment();
				if (getActivity() == null)
				{
					return;
				}
				if (getActivity() instanceof ManageActivity)
				{
					ManageActivity activity = (ManageActivity) getActivity();
					activity.switchContent(fragment);
				}
				break;

			default:
				break;
		}

	}

}
