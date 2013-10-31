/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.xwtec.util.download.ImageObj;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.ProductTypeAction;
import com.xwtec.yycoin.widget.MyViewBinder;
import com.xwtec.yycoin.widget.PullToRefreshListView.OnRefreshListener;

/**
 * @author liuzixiang
 * 
 */
public class ProductListActivity extends BaseActivity {

	private final static String TAG = ProductListActivity.class.getSimpleName();

	private LinkedList<String> mListItems;

	private ListView listview = null;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	SimpleAdapter adapter;

	ProductTypeAction action;

	private int currnetPage = 1;

	private String purl = "";

	/**
	 * 按钮组
	 */
	private RadioGroup rg = null;

	/**
	 * 当前的搜索类型
	 */
	private int type = 1;

	/**
	 * 下载图片,然后刷新界面
	 */
	private Handler imagehandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ImageObj image = (ImageObj) msg.obj;
			if (msg.what == Constant.DOWNLOAD_IMAGE_FINISH) {
				Log.v(TAG, "DOWNLOAD_IMAGE_FINISH..." + image.getName());
				Bitmap bm = BitmapFactory.decodeFile(Constant.FILE_PATH
						+ image.getName());
				if (bm == null) {
					bm = BitmapFactory.decodeResource(getResources(),
							R.drawable.image_default);
				}
				for (Map<String, Object> map : list) {
					String id = (String) map.get("bid");
					if (image.getName().equals("list_item_" + id + ".jpg")) {
						map.put("bitmap", bm);
					}
				}
			}
			adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}

	};

	private Handler newHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				list = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				if (null != tempList && !tempList.isEmpty()) {
					for (Map<String, Object> map : tempList) {
						String id = (String) map.get("bid");
						String imageUrl = (String) map.get("img");
						Bitmap bitmap = GetServerImageProcessor.getInstance()
								.getListItemImage(ProductListActivity.this,
										Constant.IMAGE_URL + imageUrl, id,
										imagehandler);
						map.put("bitmap", bitmap);
						list.add(map);
					}
				}
				Collections.sort(list, new Comparator<Map<String, Object>>() {

					@Override
					public int compare(Map<String, Object> lhs,
							Map<String, Object> rhs) {
						return lhs.get("bid").toString()
								.compareTo(rhs.get("bid").toString());
					}
				});
				adapter = new SimpleAdapter(ProductListActivity.this, list,
						R.layout.single_item, new String[] { "name", "bitmap",
								"des" }, new int[] { R.id.text, R.id.image,
								R.id.des });
				adapter.setViewBinder(new MyViewBinder());
				listview.setAdapter(adapter);
//				listview.onRefreshComplete();
				adapter.notifyDataSetChanged();
				break;
			case Constant.NO_DATA:
//				listview.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};

	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				if (null != tempList && !tempList.isEmpty()) {
					for (Map<String, Object> map : tempList) {
						String id = (String) map.get("bid");
						String imageUrl = (String) map.get("headurl");
						Bitmap bitmap = GetServerImageProcessor.getInstance()
								.getListItemImage(ProductListActivity.this,
										Constant.IMAGE_URL + imageUrl, id,
										imagehandler);
						map.put("bitmap", bitmap);
						list.add(map);
					}
				}
				adapter = new SimpleAdapter(ProductListActivity.this, list,
						R.layout.single_item, new String[] { "title", "bitmap",
								"desc" }, new int[] { R.id.text, R.id.image,
								R.id.des });
				adapter.setViewBinder(new MyViewBinder());
				listview.setAdapter(adapter);
//				listview.onRefreshComplete();
				adapter.notifyDataSetChanged();
				break;
			case Constant.NO_DATA:
//				listview.onRefreshComplete();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate...");
		setContentView(R.layout.product_list);

		// Bundle bundle = getIntent().getExtras();
		// String pid = bundle.getString("pid");
		// final String purl = bundle.getString("purl");
		// final String title = bundle.getString("title");
		// showTop(title);
		showDailer();
		purl = "basedataList.jsp?type=" + type;

		Log.v(TAG, "purl = " + purl);
		showProgessDialog();
		action = new ProductTypeAction(newHandler);
		action.getMessage(purl);

		rg = (RadioGroup) findViewById(R.id.topLayout);

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.check1:
					type = 1;
					break;
				case R.id.check2:
					type = 2;
					break;
				case R.id.check3:
					type = 3;
					break;
				}
				purl = "basedataList.jsp?type=" + type;
				action.getMessage(purl);
			}
		});

		listview = (ListView) findViewById(R.id.list);

//		listview.setOnRefreshListener(new OnRefreshListener() {
//			@Override
//			public void onRefresh() {
//				action.getMessage(purl);
//			}
//		});

		adapter = new SimpleAdapter(ProductListActivity.this, list,
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
				Map<String, Object> map = list.get(position);
				String name = map.get("name").toString();
				bundle.putBoolean("isSearch", true);
				switch (type) {
				case 1:
					// 材质
					bundle.putString("text_4_value", name);
					break;
				case 2:
					// 题材
					bundle.putString("text_3_value", name);
					break;
				case 3:
					// 价格
					String[] str = name.split("-");
					bundle.putString("text_2_1_value", str[0]);
					bundle.putString("text_2_2_value", str[1]);
					break;
				default:
					break;
				}
				// Map<String, Object> map = list.get(position - 1);
				// String aurl = map.get("aurl").toString();
				// String bid = map.get("bid").toString();
				// bundle.putString("aurl", aurl);
				// bundle.putString("bid", bid);
				intent.putExtras(bundle);
				intent.setAction(GridActivity.class.getName());
				startActivity(intent);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				// 跳转到主界面
				RadioButton rb = (RadioButton) BottomTabAcitivity.radioGroup
						.getChildAt(0);
				rb.setChecked(true);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
