/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.OrderMessageAction;
import com.xwtec.yycoin.activity.GridActivity.ViewHolder;
import com.xwtec.yycoin.info.ImageAndText;

/**
 * @author Administrator
 * 
 */
public class OrderActivity extends BaseActivity {

	private final static String TAG = OrderActivity.class.getSimpleName();

	private ListView orderList;

	private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();;

	private MyAdapter mAdapter = null;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int retCode = (Integer) map.get("retCode");
				String retMsg = (String) map.get("retMsg");
				if (retCode == 0) {
					dataList = (List<Map<String, String>>) map.get("obj");
					mAdapter = new MyAdapter();
					orderList.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							OrderActivity.this);
					builder.setMessage(retMsg);
					builder.setPositiveButton(R.string.sure_btn_str, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}

		}

	};

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dataList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder = null;
			if (arg1 == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getLayoutInflater();
				arg1 = inflater.inflate(R.layout.order_layout_item, null);
				holder.imageView = (ImageView) arg1
						.findViewById(R.id.orderImage);
				holder.tv1 = (TextView) arg1
						.findViewById(R.id.orderImageContent);
				holder.tv2 = (TextView) arg1.findViewById(R.id.orderContent);
				holder.tv3 = (TextView) arg1
						.findViewById(R.id.orderCountContent);
				holder.tv4 = (TextView) arg1
						.findViewById(R.id.orderTimeContent);
				holder.tv5 = (TextView) arg1
						.findViewById(R.id.orderStatusContent);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			Map<String, String> map = dataList.get(arg0);
			Bitmap bitmap = GetServerImageProcessor.getInstance()
					.getGridItemImage(OrderActivity.this, null,
							map.get("productCode"), null);
			holder.imageView.setImageBitmap(bitmap);
			holder.tv1.setText(map.get("productName"));
			holder.tv2.setText(map.get("orderNo"));
			holder.tv3.setText(map.get("pay"));
			holder.tv4.setText(map.get("outTime"));
			holder.tv5.setText(map.get("status") + "," + map.get("payStatus"));
			return arg1;
		}

	}

	private class ViewHolder {
		ImageView imageView;
		TextView tv1;
		TextView tv2;
		TextView tv3;
		TextView tv4;
		TextView tv5;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_activity);

		showTop("我的订单");

		orderList = (ListView) findViewById(R.id.orderList);
		orderList.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.setAction(OrderDetailActivity.class.getName());
				Bundle bundle = new Bundle();
				bundle.putString("orderNo", dataList.get(arg2).get("orderNo"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		getOrderList();

	}

	private void getOrderList() {
		showProgessDialog();
		OrderMessageAction action = new OrderMessageAction(handler);
		JSONObject object = new JSONObject();
		try {
			object.put("begin", "");
			object.put("end", "");
			object.put("orderNo", "");
			object.put("productName", "");
			object.put("userId", Variable.id);
			action.getMessage("?method=queryOrderList&params="
					+ object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}