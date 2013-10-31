/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.OrderAction;
import com.xwtec.yycoin.action.PriceAction;
import com.xwtec.yycoin.info.ImageAndText;
import com.xwtec.yycoin.info.ProductCarInfo;

/**
 * @author Administrator
 * 
 */
public class CarProductActivity extends BaseActivity {

	private final static String TAG = CarProductActivity.class.getSimpleName();

	private TextView total_money;

	private TextView product_total_money;

	private TextView return_money;

	private CheckBox checkAll;

	private ProductCarInfo productCarInfo;

	private LinearLayout productcontent;

	List<Map<String, String>> info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_product_activity);
		showDailer();
		ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
		TextView topText = (TextView) findViewById(R.id.top);
		topText.setText("填写订单");
		backBtn.setVisibility(View.GONE);
		productCarInfo = new ProductCarInfo();
		ImageButton rightBtn = (ImageButton) findViewById(R.id.rightBtn);
		rightBtn.setBackgroundResource(R.drawable.del_action);
		rightBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				for (Map<String, String> map : info) {
					if (map.get("isChecked").equals("true")) {
						String id = map.get("productCarId");
						productCarInfo.delete(id);
					}
				}
				loadData();
			}
		});

		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
		getPrice();
	}

	private void loadData() {
		info = productCarInfo.queryAllProductInfo();
		Log.v(TAG, "info = " + info.size());
		for (Map<String, String> map : info) {
			map.put("isChecked", "true");
		}
		// addView();
		priceHanlder.sendEmptyMessage(11);
	}

	protected Handler priceHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				for (Map<String, String> map : info) {
					if (Variable.priceMap.containsKey(map.get("productno"))) {
						map.put("jg",
								Variable.priceMap.get(map.get("productno")));
					}
				}
				sendEmptyMessage(11);
				break;
			case 11:
				addView();
				break;
			}
		}
	};

	private void getPrice() {
		try {
			if (Variable.isLogin) {
				PriceAction action = new PriceAction(priceHanlder);
				JSONArray array = new JSONArray();
				for (final Map<String, String> map : info) {

					JSONObject jsonObj = new JSONObject();
					String productNo = map.get("productno");
					if (!TextUtils.isEmpty(productNo)) {
						jsonObj.put("productCode", productNo);
						jsonObj.put("productName",
								URLEncoder.encode(map.get("title"), "utf-8"));
						array.put(jsonObj);
					}
				}
				JSONObject o = new JSONObject();
				o.put("products", array);
				o.put("userId", Variable.id);
				action.getMessage("?method=queryPrice&params=" + o.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void addView() {
		productcontent.removeAllViews();
		for (final Map<String, String> map : info) {

			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater
					.inflate(R.layout.car_product_item, null, false);
			CheckBox check = (CheckBox) view.findViewById(R.id.check);
			check.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if (arg1) {
						map.put("isChecked", "true");
					} else {
						map.put("isChecked", "false");
					}
					setAllCheckedState();
				}
			});

			if (map.get("isChecked").equals("true")) {
				check.setChecked(true);
			} else {
				check.setChecked(false);
			}

			ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setImageBitmap(GetServerImageProcessor.getInstance()
					.getGridItemImage(this, null, map.get("productno"), null));
			TextView titleTextView = (TextView) view.findViewById(R.id.title);
			TextView cz = (TextView) view.findViewById(R.id.cz);
			final TextView countview = (TextView) view.findViewById(R.id.count);
			ImageButton down = (ImageButton) view.findViewById(R.id.down);
			ImageButton up = (ImageButton) view.findViewById(R.id.up);
			String tag = map.get("productCarId");
			down.setTag(tag);
			up.setTag(tag);
			titleTextView.setText(map.get("title"));
			cz.setText(map.get("cz"));
			countview.setText(map.get("count"));

			down.setOnClickListener(new ImageButton.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int count = Integer.valueOf(map.get("count"));
					if (count > 1) {
						count--;
						ProductCarInfo info = new ProductCarInfo();
						info.update(map.get("productno"), count);
						map.put("count", String.valueOf(count));
						countview.setText(map.get("count"));
						caculationTotalMoney();
					}
				}
			});
			up.setOnClickListener(new ImageButton.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int count = Integer.valueOf(map.get("count"));
					count++;
					ProductCarInfo info = new ProductCarInfo();
					info.update(map.get("productno"), count);
					map.put("count", String.valueOf(count));
					countview.setText(map.get("count"));
					caculationTotalMoney();
				}
			});
			productcontent.addView(view);

			TextView price = (TextView) view.findViewById(R.id.price);
			price.setText(map.get("jg"));
		}
		setAllCheckedState();
	}

	private void setAllCheckedState() {
		checkAll.setChecked(true);
		for (final Map<String, String> map : info) {
			Log.v(TAG, "isChecked = " + map.get("isChecked"));
			if (!map.get("isChecked").equals("true")) {
				checkAll.setChecked(false);
				break;
			}
		}
		caculationTotalMoney();
	}

	int totalMoney = 0;

	private void caculationTotalMoney() {
		totalMoney = 0;
		for (final Map<String, String> map : info) {
			if (map.get("isChecked").equals("true")) {
				int count = Integer.valueOf(map.get("count"));
				Float jg = 0f;
				try {
					jg = Float.valueOf(map.get("jg"));
				} catch (Exception e) {
				} finally {
					totalMoney += count * jg;
				}
			}
		}
		total_money.setText(String.valueOf(totalMoney));
		product_total_money.setText(String.valueOf(totalMoney));
		return_money.setText("0");
	}

	private boolean isChecked = true;

	/**
	 * 初始化当前的界面布局文件
	 */
	private void initView() {
		total_money = (TextView) findViewById(R.id.total_money);
		product_total_money = (TextView) findViewById(R.id.product_total_money);
		return_money = (TextView) findViewById(R.id.return_money);
		checkAll = (CheckBox) findViewById(R.id.checkAll);
		checkAll.setOnClickListener(new CheckBox.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isChecked = !isChecked;
				Log.v(TAG,
						"Boolean.toString(isChecked)"
								+ Boolean.toString(isChecked));
				for (Map<String, String> map : info) {
					map.put("isChecked", Boolean.toString(isChecked));
				}
				// addView();
				priceHanlder.sendEmptyMessage(11);
			}
		});
		productcontent = (LinearLayout) findViewById(R.id.productcontent);

		ImageButton go_buy = (ImageButton) findViewById(R.id.go_buy);
		go_buy.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Variable.isLogin) {
					Intent intent = new Intent();
					intent.setAction(CarProductDetailActivity.class.getName());
					Bundle bundle = new Bundle();
					bundle.putString("totalMoney", String.valueOf(totalMoney));
					bundle.putSerializable("info", (Serializable) info);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setAction(LoginActivity.class.getName());
					startActivity(intent);
				}

			}
		});
	}
}
