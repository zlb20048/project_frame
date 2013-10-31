/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xwtec.util.tool.Constant;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.ProductTypeAction;

/**
 * @author liuzixiang
 * 
 */
public class SearchActivity extends BaseActivity {

	private final static String TAG = SearchActivity.class.getSimpleName();

	private RelativeLayout rl_1 = null;
	private RelativeLayout rl_2 = null;
	private RelativeLayout rl_3 = null;
	private RelativeLayout rl_4 = null;
	private RelativeLayout rl_5 = null;
	private RelativeLayout rl_6 = null;
	private RelativeLayout rl_7 = null;

	private TextView text_1 = null;
	private TextView text_2_1 = null;
	private TextView text_2_2 = null;
	private TextView text_3 = null;
	private TextView text_4 = null;
	private TextView text_5 = null;
	private TextView text_6 = null;
	private TextView text_7 = null;

	private String text_1_value = "";
	private String text_2_1_value = "";
	private String text_2_2_value = "";
	private String text_3_value = "";
	private String text_4_value = "";
	private String text_5_value = "";
	private String text_6_value = "";
	private String text_7_value = "";

	private Button btn = null;

	private Bundle bundle = null;

	/**
	 * 获取到各个类型的数据
	 */
	private ProductTypeAction action = null;

	private String[] caizhiStrs;

	private String[] ticaiStrs;

	/**
	 * 材质
	 */
	private Handler caizhiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				int length = tempList.size();
				caizhiStrs = new String[length + 1];
				caizhiStrs[0] = "全部";
				for (int i = 1; i < length + 1; i++) {
					caizhiStrs[i] = (String) tempList.get(i - 1).get("name");
				}
				break;
			case Constant.NO_DATA:
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 题材
	 */
	private Handler ticaiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				int length = tempList.size();
				ticaiStrs = new String[length + 1];
				ticaiStrs[0] = "全部";
				for (int i = 1; i < length + 1; i++) {
					ticaiStrs[i] = (String) tempList.get(i - 1).get("name");
				}
				break;
			case Constant.NO_DATA:
				break;
			default:
				break;
			}
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
		setContentView(R.layout.search_activity);
		bundle = getIntent().getExtras();
		showDailer();
		action = new ProductTypeAction();

		rl_1 = (RelativeLayout) findViewById(R.id.layout_1);
		rl_2 = (RelativeLayout) findViewById(R.id.layout_2);
		rl_3 = (RelativeLayout) findViewById(R.id.layout_3);
		rl_4 = (RelativeLayout) findViewById(R.id.layout_4);
		rl_5 = (RelativeLayout) findViewById(R.id.layout_5);
		rl_6 = (RelativeLayout) findViewById(R.id.layout_6);
		rl_7 = (RelativeLayout) findViewById(R.id.layout_7);

		text_1 = (TextView) findViewById(R.id.text_1);
		text_2_1 = (TextView) findViewById(R.id.text_2_1);
		text_2_2 = (TextView) findViewById(R.id.text_2_2);
		text_3 = (TextView) findViewById(R.id.text_3);
		text_4 = (TextView) findViewById(R.id.text_4);
		text_5 = (TextView) findViewById(R.id.text_5);
		text_6 = (TextView) findViewById(R.id.text_6);
		text_7 = (TextView) findViewById(R.id.text_7);

		btn = (Button) findViewById(R.id.button);

		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				text_1_value = text_1.getText().toString();
				text_2_1_value = text_2_1.getText().toString();
				text_2_2_value = text_2_2.getText().toString();
				// text_7_value = text_7.getText().toString();

				Intent intent = new Intent();
				if (null == bundle) {
					bundle = new Bundle();
				}
				bundle.putBoolean("isSearch", true);
				bundle.putString("text_1_value", text_1_value);
				bundle.putString("text_2_1_value", text_2_1_value);
				bundle.putString("text_2_2_value", text_2_2_value);
				bundle.putString("text_3_value", text_3_value);
				bundle.putString("text_4_value", text_4_value);
				bundle.putString("text_5_value", text_5_value);
				bundle.putString("text_6_value", text_6_value);
				// bundle.putString("text_7_value", text_7_value);
				intent.putExtras(bundle);
				intent.setClass(SearchActivity.this, GridActivity.class);
				startActivity(intent);
			}
		});

		// rl_2.setOnClickListener(new RelativeLayout.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// new AlertDialog.Builder(SearchActivity.this)
		// .setTitle("价格（单位为元）")
		// .setItems(R.array.search_type_2,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// final String[] searchTypeText = getResources()
		// .getStringArray(
		// R.array.search_type_2);
		// text_2_value = searchTypeText[which];
		// text_2_1.setText(searchTypeText[which]);
		// }
		// }).show();
		// }
		// });

		rl_3.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						SearchActivity.this);
				alertDialog.setTitle("选择题材");

				if (null != ticaiStrs && ticaiStrs.length != 0) {
					alertDialog.setItems(ticaiStrs,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								    if (which == 0) {
                                        text_3_value = "";
                                    } else {
                                        text_3_value = ticaiStrs[which];
                                    }
									text_3.setText(ticaiStrs[which]);
								}
							});
				} else {
					alertDialog.setItems(R.array.search_type_1,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final String[] searchTypeText = getResources()
											.getStringArray(
													R.array.search_type_1);
									if (which == 0) {
									    text_3_value = "";
									} else {
									    text_3_value = searchTypeText[which];
									}
									text_3.setText(searchTypeText[which]);
								}
							});
				}
				alertDialog.show();
			}
		});

		rl_4.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						SearchActivity.this);
				alertDialog.setTitle("选择");

				if (null != caizhiStrs && caizhiStrs.length != 0) {
					alertDialog.setItems(caizhiStrs,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								    if (which == 0) {
								        text_4_value = "";
								    } else {
								        text_4_value = caizhiStrs[which];
								    }
									text_4.setText(caizhiStrs[which]);
								}
							});
				} else {
					alertDialog.setItems(R.array.search_type_1,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final String[] searchTypeText = getResources()
											.getStringArray(
													R.array.search_type_1);
									 if (which == 0) {
	                                     text_4_value = "";
	                                 } else {
	                                     text_4_value = searchTypeText[which];
	                                 }
									text_4.setText(searchTypeText[which]);
								}
							});
				}
				alertDialog.show();
			}
		});

		rl_5.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SearchActivity.this)
						.setTitle("选择")
						.setItems(R.array.search_type_5,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										final String[] searchTypeText = getResources()
												.getStringArray(
														R.array.search_type_5);
										if (which == 0) {
										    text_5_value = "";
										} else {
										    text_5_value = searchTypeText[which];
										}
										text_5.setText(searchTypeText[which]);
									}
								}).show();
			}
		});

		rl_6.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SearchActivity.this)
						.setTitle("选择")
						.setItems(R.array.search_type_6,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										final String[] searchTypeText = getResources()
												.getStringArray(
														R.array.search_type_6);
										if (which == 0) {
										    text_6_value = "";
                                        } else {
                                            text_6_value = searchTypeText[which];
                                        }
										text_6.setText(searchTypeText[which]);
									}
								}).show();
			}
		});

		getType();
	}

	private void getType() {
		ProductTypeAction action1 = new ProductTypeAction();
		action1.setHandler(caizhiHandler);
		action1.getMessage("basedataList.jsp?type=1");

		ProductTypeAction action2 = new ProductTypeAction();
		action2.setHandler(ticaiHandler);
		action2.getMessage("basedataList.jsp?type=2");
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
