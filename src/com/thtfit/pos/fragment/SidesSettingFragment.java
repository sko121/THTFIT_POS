package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thtfit.pos.R;
import com.thtfit.pos.util.OptionList;

public class SidesSettingFragment extends ListFragment {
	private static final String TAG = "SidesSettingFragment";
	private static final int TYPE_SETTING = 1;

	private ArrayAdapter<String> adapter;
	private List<String> data;
	private FragmentManager manager;
	private FragmentTransaction transaction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		data = new ArrayList<String>();
		OptionList optionList = new OptionList();
		optionList.parse(TYPE_SETTING);

		data = optionList.getList();

		manager = getFragmentManager();
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		setListAdapter(adapter);

	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.d(TAG, "onListItemClick");
		super.onListItemClick(parent, v, position, id);
		String str = adapter.getItem(position);
		transaction = manager.beginTransaction();

		switch (position) {
		case 0:
			
			break;
		case 1:
			
			break;
		case 2:

			break;
		default:
			break;
		}
/*		ReportTotalSalesFragment generalFragment = new ReportTotalSalesFragment();
		Bundle bundle = new Bundle();
		bundle.putString("id", str);
		generalFragment.setArguments(bundle);
		transaction.replace(R.id.setting_general, generalFragment, "detail");
		transaction.commit();*/
	}
}
