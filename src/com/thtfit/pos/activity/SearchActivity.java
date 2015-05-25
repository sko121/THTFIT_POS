package com.thtfit.pos.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.adapter.SearchListAdapter;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.model.Product;

public class SearchActivity extends FragmentActivity{
	private EditText searchEditText;
	private Button searchButton;
	private ListView searchListView;
	private SearchListAdapter adapter;
	
	private DBContror dbcon;
	private static List<Product> mylist;
	
	private String searchName = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		
		searchEditText = (EditText) findViewById(R.id.search_editText_input);
		searchButton = (Button) findViewById(R.id.search_button);
		searchListView = (ListView) findViewById(R.id.search_listView);
		
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchName = searchEditText.getText().toString().trim();
				if("".equals(searchName)){
					showMSG("搜索内容不为空");
					return;
				}
				
				dbcon = new DBContror(SearchActivity.this);
				mylist = new ArrayList<Product>();
				mylist.addAll(dbcon.queryAllItemByName(searchName));
				
				try {
					if (mylist.size() != 0) {
						adapter = new SearchListAdapter(mylist, SearchActivity.this);// 自定义适配器
						searchListView.setAdapter(adapter);

						searchListView.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
								showMSG("点击了"+mylist.get(position).getName());
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	public void showMSG(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}
}
