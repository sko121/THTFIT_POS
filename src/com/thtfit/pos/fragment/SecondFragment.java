package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.ShopingActivity;
import com.thtfit.pos.adapter.MainGridAdapter;
import com.thtfit.pos.adapter.MainPagerAdapter;
import com.thtfit.pos.conn.hql.DataLoader;
import com.thtfit.pos.conn.hql.DataLoader.OnCompletedListener;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.widget.FooterView;
import com.thtfit.pos.util.widget.MainGridView;

public class SecondFragment extends Fragment implements OnCompletedListener,
OnClickListener {
	private String LOG_TAG = "FirstFragment";
	private View mView;
	private MainPagerAdapter mPagerAdapter;

	// private ScrollView scrollView;
	private MainGridView myGridView;
	private MainGridAdapter mainGridAdapter;
	private DataLoader loader;
	private List<Product> mylist = new ArrayList<Product>();

	public int page = 0;
	public final static int PAGE_SIZE = 12; // 每次加载10个item
	private boolean isLoadFinished;
	private HashMap<String, String> loaderMap = new HashMap<String, String>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_mainview_grid,
					container, false);
			myGridView = (MainGridView) mView
					.findViewById(R.id.main_gridView_grid);
		}
		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null) {
			parent.removeView(mView);
		}

		init();
		
		loaderMap.put("page", page + "");
		loaderMap.put("page_size", PAGE_SIZE + "");
		loaderMap.put("typeId", String.valueOf(getTypeId()));

		loader = new DataLoader(getActivity());
		loader.setOnCompletedListerner(this);
		loader.startLoading(loaderMap);

		return mView;
	}

	private void init() {

		mainGridAdapter = new MainGridAdapter(mylist, getActivity());// 自定义适配器
		myGridView.setAdapter(mainGridAdapter);

		myGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)
							&& !isLoadFinished
							&& mainGridAdapter.getFooterView().getStatus() != FooterView.LOADING) {
						loadMoreData();

					}
				}
			}
		});

		myGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				TotalFragment.getInstance(getActivity()).updateList(
						mylist.get(position));
			}
		});
	

	}

	private void loadMoreData() {
		if (loader != null) {

			page = page + 1;
			loaderMap.put("page", page + "");
			if (mainGridAdapter != null) {
				mainGridAdapter.setFooterViewStatus(FooterView.LOADING);
			}
			loader.startLoading(loaderMap);
		}
	}
	
	public Integer getTypeId(){
		mPagerAdapter = new MainPagerAdapter(getActivity(), getActivity()
				.getSupportFragmentManager());
		return mPagerAdapter.getPageTitleId(ShopingActivity.currentItem);
	}

	public int getConversionDip(float dpValue) {
		final float scale = this.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static SecondFragment getInstance(Bundle bundle) {
		SecondFragment firstFragment = new SecondFragment();
		firstFragment.setArguments(bundle);
		return firstFragment;
	}

	public void showMSG(String content) {
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}


	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.foot_view_layout:
			if (mainGridAdapter != null
					&& mainGridAdapter.getFooterView().getStatus() == FooterView.MORE) {
				loadMoreData();
			}
			break;
		case R.id.footview_button:
			loadMoreData();
			break;
		}

	}

	@Override
	public void onCompletedSucceed(List<Product> l) {

		// 在添加数据之前删除最后的伪造item
		if (mainGridAdapter.isFooterViewEnable()) {
			mylist.remove(mylist.get(mylist.size() - 1));
		}

		// 分页加载
		if (l.size() < PAGE_SIZE) {
			// 如果加载出来的数目小于指定条数，可视为已全部加载完成
			isLoadFinished = true;
			mylist.addAll(l);
			mainGridAdapter.setFootreViewEnable(false);
			mainGridAdapter.notifyDataSetChanged();
		} else {
			// 还有数据可加载。
			mylist.addAll(l);
			// 伪造一个空项来构造一个footerview;
			mylist.add(null);
			mainGridAdapter.setFootreViewEnable(true);
			mainGridAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onCompletedFailed(String str) {
		Toast.makeText(getActivity(), "出错啦！", Toast.LENGTH_LONG).show();
	}

	@Override
	public void getCount(int count) {
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mylist.clear();
	}

}
