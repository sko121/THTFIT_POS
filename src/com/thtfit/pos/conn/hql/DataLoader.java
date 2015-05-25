package com.thtfit.pos.conn.hql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.thtfit.pos.model.Product;

public class DataLoader {
	private Context mContext;
	private OnCompletedListener l;

	private DBContror dbcon;

	public DataLoader(Context mContext) {
		this.mContext = mContext;
	}

	public void setOnCompletedListerner(OnCompletedListener mL) {
		l = mL;
	}

	@SuppressWarnings("unchecked")
	public void startLoading(HashMap<String, String> mParams) {
		if (mParams != null) {
			LoadTask task = new LoadTask();
			task.execute(mParams);
		}
	}

	private class LoadTask extends
			AsyncTask<HashMap<String, String>, Void, List<Product>> {

		@Override
		protected List<Product> doInBackground(HashMap<String, String>... params) {
			List<Product> mylist = null;
			try {
				Thread.sleep(250);
				int page = Integer.parseInt(params[0].get("page"));//页面
				int page_size = Integer.parseInt(params[0].get("page_size"));//页面数量
				String typeId = params[0].get("typeId");//类别名称
				mylist = new ArrayList<Product>();
				
				
				System.out.println("typeId========"+typeId);

				dbcon = new DBContror(mContext);
				
				List<Product> tmpList = new ArrayList<Product>();
				tmpList = dbcon.queryPageByType(typeId, page, page_size);
				
				if (tmpList.size() > 0) {
					mylist.addAll(tmpList);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			return mylist;
		}

		@Override
		protected void onPostExecute(List<Product> ret) {

			if (ret == null) {
				l.onCompletedFailed("--------faild");
			} else {
				l.onCompletedSucceed(ret);
			}

		}
	}

	public interface OnCompletedListener {
		public void onCompletedSucceed(List<Product> list);

		public void onCompletedFailed(String str);

		public void getCount(int count);

	}
}