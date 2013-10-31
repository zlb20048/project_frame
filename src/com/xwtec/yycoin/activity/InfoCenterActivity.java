/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;

/**
 * @author Administrator
 * 
 */
public class InfoCenterActivity extends BaseActivity {

	/**
	 * 当前需要展示的列表
	 */
	private ListView listview;

	/**
	 * Adapter
	 */
	private SimpleAdapter mAdapter;

	/**
	 * 登陆按钮
	 */
	private ImageButton loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_center_activity);
		initView();
		initData();
		showDailer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Variable.isLogin) {
			loginButton.setBackgroundResource(R.drawable.exit);
		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("Item", "我的订单");
		data.add(map);
		map = new HashMap<String, String>();
		map.put("Item", "我的收藏");
		data.add(map);
		map = new HashMap<String, String>();
		map.put("Item", "个人信息");
		data.add(map);
		// map = new HashMap<String, String>();
		// map.put("Item", "活动咨询");
		// data.add(map);
		mAdapter = new SimpleAdapter(this, data, R.layout.info_center_item,
				new String[] { "Item" }, new int[] { R.id.title });
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(listener);
	}

	OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent();
			switch (arg2) {
			case 0:
				if (Variable.isLogin) {
					intent.setAction(OrderActivity.class.getName());
				} else {
					intent.setAction(LoginActivity.class.getName());
				}
				break;
			case 1:
				intent.setAction(GridActivity.class.getName());
				intent.putExtra("isColletion", true);
				break;
			case 2:
				if (Variable.isLogin) {
					intent.setAction(PersonInfoActivity.class.getName());
				} else {
					intent.setAction(LoginActivity.class.getName());
				}
				break;
			}
			startActivity(intent);
		}
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		listview = (ListView) findViewById(R.id.listview);
		loginButton = (ImageButton) findViewById(R.id.login_btn);
		loginButton.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Variable.isLogin) {
					Variable.isLogin = false;
					loginButton.setBackgroundResource(R.drawable.login);
					return;
				}
				Intent intent = new Intent();
				intent.setAction(LoginActivity.class.getName());
				startActivity(intent);
			}
		});
	}

}
