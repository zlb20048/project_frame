/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.OrderDetailAction;

/**
 * @author Administrator
 * 
 */
public class OrderDetailActivity extends BaseActivity {

	private final static String TAG = OrderDetailActivity.class.getSimpleName();

	private String orderNo = null;

	private LinearLayout item = null;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int retCode = (Integer) map.get("retCode");
				String retMsg = (String) map.get("retMsg");
				if (retCode == 0) {
					order_number_content.setText(map.get("orderNo").toString());
					order_state.setText(map.get("statusName") + ","
							+ map.get("payStatusName").toString());
					name.setText(map.get("receiver").toString());
					address.setText(map.get("province").toString()
							+ map.get("city").toString()
							+ map.get("fullAddress").toString());
					total.setText(map.get("total").toString());
					List<Map<String, String>> items = (List<Map<String, String>>) map
							.get("items");
					addView(items);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							OrderDetailActivity.this);
					builder.setMessage(retMsg);
					builder.setPositiveButton(R.string.sure_btn_str, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		}

	};

	private void addView(List<Map<String, String>> items) {
		item.removeAllViews();
		for (Map<String, String> map : items) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.order_detail_item, null,
					false);
			TextView t1 = (TextView) view.findViewById(R.id.tilte);
			TextView t2 = (TextView) view.findViewById(R.id.number);
			TextView t3 = (TextView) view.findViewById(R.id.price);
			t1.setText(map.get("productName"));
			t2.setText(map.get("amount"));
			t3.setText(map.get("price"));

			ImageView image = (ImageView) view.findViewById(R.id.image);
			Bitmap bitmap = GetServerImageProcessor.getInstance()
					.getGridItemImage(OrderDetailActivity.this, null,
							(String) map.get("productCode"), null);
			image.setImageBitmap(bitmap);
			item.addView(view);
		}
	}

	private TextView order_number_content;
	private TextView order_state;
	private TextView name;
	private TextView address;
	private TextView total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail_activity);
		showTop("订单详情");
		Bundle bundle = getIntent().getExtras();
		orderNo = bundle.getString("orderNo");
		getOrderDetailMessage();

		order_number_content = (TextView) findViewById(R.id.order_number_content);
		order_state = (TextView) findViewById(R.id.order_state);
		name = (TextView) findViewById(R.id.name);
		address = (TextView) findViewById(R.id.address);
		total = (TextView) findViewById(R.id.total);
		item = (LinearLayout) findViewById(R.id.item);
	}

	private void getOrderDetailMessage() {
		showProgessDialog();
		OrderDetailAction action = new OrderDetailAction(handler);
		JSONObject object = new JSONObject();
		try {
			object.put("orderNo", orderNo);
			object.put("userId", Variable.id);
			action.getMessage("?method=queryOrderDetail&params="
					+ object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
