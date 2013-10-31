/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.Util;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.OrderAction;
import com.xwtec.yycoin.info.ProductCarInfo;

/**
 * @author Administrator
 * 
 */
public class CarProductDetailActivity extends BaseActivity {
	private final static String TAG = CarProductDetailActivity.class
			.getSimpleName();

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int retCode = (Integer) map.get("retCode");
				String retMsg = (String) map.get("retMsg");
				if (retCode == 0) {
					ProductCarInfo productCarInfo = new ProductCarInfo();
					for (Map<String, String> maps : infos) {
						if (maps.get("isChecked").equals("true")) {
							String id = maps.get("productCarId");
							productCarInfo.delete(id);
						}
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CarProductDetailActivity.this);
					builder.setMessage("订单成功");
					builder.setPositiveButton(R.string.sure_btn_str,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CarProductDetailActivity.this);
					builder.setMessage(retMsg);
					builder.setPositiveButton(R.string.sure_btn_str, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		}

	};

	private EditText name_edit;

	private EditText phone_edit;

	private EditText province_input;

	private EditText city_input;

	private EditText address_edit;

	private List<Map<String, String>> infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_order_info_activity);
		ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
		TextView topText = (TextView) findViewById(R.id.top);
		topText.setText("填写订单");
		backBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		showDailer();
		name_edit = (EditText) findViewById(R.id.name_edit);
		name_edit.setText(getInfo().getString("name" + Variable.id, ""));
		phone_edit = (EditText) findViewById(R.id.phone_edit);
		phone_edit.setText(getInfo().getString("phone" + Variable.id, ""));
		province_input = (EditText) findViewById(R.id.province_input);
		province_input.setText(getInfo()
				.getString("province" + Variable.id, ""));
		city_input = (EditText) findViewById(R.id.city_input);
		city_input.setText(getInfo().getString("city" + Variable.id, ""));
		address_edit = (EditText) findViewById(R.id.address_edit);
		address_edit.setText(getInfo().getString("address" + Variable.id, ""));

		infos = (List<Map<String, String>>) getIntent().getExtras()
				.getSerializable("info");

		ImageButton rightBtn = (ImageButton) findViewById(R.id.rightBtn);
		rightBtn.setBackgroundResource(R.drawable.go_buy_button);
		rightBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(name_edit.getText())) {
					showSureDialog("请输入姓名");
					return;
				}
				if (TextUtils.isEmpty(phone_edit.getText())) {
					showSureDialog("请输入手机号码");
					return;
				}
				if (!Util.isMobileNO(phone_edit.getText().toString())) {
					showSureDialog("请输入正确的手机号码");
					return;
				}

				if (TextUtils.isEmpty(province_input.getText())) {
					showSureDialog("请输入省");
					return;
				}
				if (TextUtils.isEmpty(city_input.getText())) {
					showSureDialog("请输入市");
					return;
				}
				if (TextUtils.isEmpty(address_edit.getText())) {
					showSureDialog("请输入详细地址");
					return;
				}
				saveInfo();

				showProgessDialog();
				OrderAction orderAction = new OrderAction(handler);
				JSONObject o = new JSONObject();
				try {
					o.put("Activity", 0);
					o.put("description", 0);

					JSONObject distribution = new JSONObject();
					o.put("distribution", distribution);
					distribution.put("carryType", "1");
					distribution.put("city", URLEncoder.encode(city_input
							.getText().toString(), "utf-8"));
					distribution.put("fullAddress", URLEncoder.encode(
							address_edit.getText().toString(), "utf-8"));
					distribution.put("province", URLEncoder.encode(
							province_input.getText().toString(), "utf-8"));
					distribution.put("receiver", URLEncoder.encode(name_edit
							.getText().toString(), "utf-8"));
					distribution.put("receiverMobile", phone_edit.getText());
					JSONObject invoice = new JSONObject();
					o.put("invoice", invoice);
					invoice.put("invoiceHead", URLEncoder.encode("永银", "utf-8"));
					invoice.put("invoiceMoney", 0);
					invoice.put("invoiceName", URLEncoder.encode("永银", "utf-8"));

					JSONArray items = new JSONArray();
					for (Map<String, String> info : infos) {
						if (info.get("isChecked").equals("true")) {
							JSONObject ob = new JSONObject();
							ob.put("amount",
									Integer.parseInt(info.get("count")));
							ob.put("picPath", info.get("image"));
							ob.put("price", Float.parseFloat(info.get("jg")));
							ob.put("productCode", info.get("productno"));
							ob.put("productName", URLEncoder.encode(
									info.get("title"), "utf-8"));
							items.put(ob);
						}
					}
					o.put("items", items);
					o.put("userId", Variable.id);
					orderAction.getMessage("?method=createOrder&params="
							+ o.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});

		TextView total = (TextView) findViewById(R.id.total);
		Bundle bundle = getIntent().getExtras();
		String totalMoney = bundle.getString("totalMoney");
		total.setText(totalMoney + "￥");

		// TextView name = (TextView) findViewById(R.id.name);
		// name.setText(Variable.trueName);
		//
		// TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
		// phoneNumber.setText(Variable.phoneNumber);
		//
		// TextView address = (TextView) findViewById(R.id.address);
		// StringBuffer sb = new StringBuffer();
		// sb.append(Variable.address).append(Variable.detailAddress);
		// address.setText(sb.toString());

		ImageButton edit_btn = (ImageButton) findViewById(R.id.edit_btn);
		edit_btn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void saveInfo() {
		SharedPreferences.Editor sp = getSharedPreferences("info",
				Context.MODE_PRIVATE).edit();
		sp.putString("name" + Variable.id, name_edit.getText().toString());
		sp.putString("phone" + Variable.id, phone_edit.getText().toString());
		sp.putString("province" + Variable.id, province_input.getText()
				.toString());
		sp.putString("city" + Variable.id, city_input.getText().toString());
		sp.putString("address" + Variable.id, address_edit.getText().toString());
		sp.commit();
	}

	private SharedPreferences getInfo() {
		return getSharedPreferences("info", Context.MODE_PRIVATE);
	}

}
