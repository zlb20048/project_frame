/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xwtec.util.tool.Constant;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.ProductAction;
import com.xwtec.yycoin.widget.PullToRefreshListView;
import com.xwtec.yycoin.widget.PullToRefreshListView.OnRefreshListener;

/**
 * @author liuzixiang
 * 
 */
public class ProductActivity extends BaseActivity {

	private final static String TAG = ProductActivity.class.getSimpleName();

	private LinkedList<String> mListItems;

	private PullToRefreshListView listview = null;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				if (null != tempList && !tempList.isEmpty()) {
					for (Map<String, Object> map : tempList) {
						list.add(map);
					}
				}
				adapter = new SimpleAdapter(ProductActivity.this, list,
						R.layout.single_item, new String[] { "title" },
						new int[] { R.id.text });
				listview.setAdapter(adapter);
				listview.onRefreshComplete();
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	SimpleAdapter adapter;

	ProductAction action;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_activity);

		action = new ProductAction(handler);
		action.getMessage("boardList.jsp");

		listview = (PullToRefreshListView) findViewById(R.id.list);

		listview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				action.getMessage("boardList.jsp");
			}
		});

		adapter = new SimpleAdapter(ProductActivity.this, list,
				R.layout.single_item, new String[] { "title" },
				new int[] { R.id.text });
		listview.setAdapter(adapter);
		// 请求数据
		listview.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击事件，页面跳转
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				Log.v(TAG, "list.size() = " + list.size());
				Map<String, Object> map = list.get(position - 1);
				String purl = map.get("purl").toString();
				String bid = map.get("bid").toString();
				String title = map.get("title").toString();
				bundle.putString("purl", purl);
				bundle.putString("bid", bid);
				bundle.putString("title", title);
				intent.putExtras(bundle);
				intent.setAction(ProductListActivity.class.getName());
				startActivity(intent);
			}
		});
	}
}
