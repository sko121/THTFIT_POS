package com.thtfit.pos.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.thtfit.pos.R;
import com.thtfit.pos.model.CustomDialog;

public class ManageAddSaleFragment extends Fragment implements OnClickListener {
	private View mView;
	private EditText edit_loginname;
	private EditText edit_password;
	private EditText edit_reinputPassword;
	private Button bt_signin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_manage_addsale, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		edit_loginname = (EditText) mView.findViewById(R.id.loginname_edit);
		edit_password = (EditText) mView.findViewById(R.id.password_edit);
		edit_reinputPassword = (EditText) mView.findViewById(R.id.reinputpassword_edit);
		bt_signin = (Button) mView.findViewById(R.id.signin_button);
		bt_signin.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
			case R.id.signin_button:
				Log.d("niotong",edit_loginname.getText().toString());
				Log.d("niotong",edit_password.getText().toString());
				Log.d("niotong",edit_reinputPassword.getText().toString());
				if(edit_password.getText().toString().equals(edit_reinputPassword.getText().toString()))
				{
					Log.d("niotong","password input ok!");
				}
				else{
					CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
					builder.setMessage("你的密码输入不一致，请重新输入");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									edit_password.setText(null);
									edit_reinputPassword.setText(null);
								}
							});
					builder.create().show();
				}
				break;
			default:
				break;
		}

	}

}
