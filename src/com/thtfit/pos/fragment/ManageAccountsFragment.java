package com.thtfit.pos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.LockSetupActivity;

public class ManageAccountsFragment extends Fragment implements OnClickListener {
	private View mView;
	private LinearLayout accountsGesture;

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
				
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accounts_gesture:
			Intent intent = new Intent(getActivity(),LockSetupActivity.class);
			startActivity(intent);
			
			break;

		default:
			break;
		}

	}

}
