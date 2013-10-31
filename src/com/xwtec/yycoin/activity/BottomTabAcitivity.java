/**
 * 
 */
package com.xwtec.yycoin.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.xwtec.yycoin.R;

/**
 * @author liuzixiang
 * 
 */
public class BottomTabAcitivity extends TabActivity {

	/**
	 * 标识
	 */
	private final static String TAG = BottomTabAcitivity.class.getSimpleName();

	private static final String TAB_1 = "tab_1";

	private static final String TAB_2 = "tab_2";

	private static final String TAB_3 = "tab_3";

	private static final String TAB_4 = "tab_4";

	private static final String TAB_5 = "tab_5";

	private static final String TAB_6 = "tab_6";

	private TabHost tabHost;

	/**
	 * 第一个列表的action
	 */
	private final static String TAB_1_ACTION = "com.xwtec.yycoin.activity.NewGridActivity";

	/**
	 * 第二个列表的action
	 */
	private final static String TAB_2_ACTION = "com.xwtec.yycoin.activity.ProductListActivity";

	/**
	 * 第三个列表的action
	 */
	private final static String TAB_3_ACTION = "com.xwtec.yycoin.activity.GridActivity";

	/**
	 * 第四个列表的action
	 */
	private final static String TAB_4_ACTION = "com.xwtec.yycoin.activity.SearchActivity";

	/**
	 * 第五个列表的action
	 */
	private final static String TAB_5_ACTION = "com.xwtec.yycoin.activity.CarProductActivity";

	/**
	 * 第六个列表的action
	 */
	private final static String TAB_6_ACTION = "com.xwtec.yycoin.activity.InfoCenterActivity";

	public static RadioGroup radioGroup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate...");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bottom_tab_activity);
		tabHost = this.getTabHost();

		Intent intent = null;

		intent = new Intent();
		intent.setAction(TAB_1_ACTION);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isSearch", true);
		intent.putExtras(bundle);
		TabSpec tabSpec1 = tabHost.newTabSpec(TAB_1).setIndicator(TAB_1)
				.setContent(intent);
		intent = new Intent();
		intent.setAction(TAB_2_ACTION);
		TabSpec tabSpec2 = tabHost.newTabSpec(TAB_2).setIndicator(TAB_2)
				.setContent(intent);

		intent = new Intent();
		intent.setAction(TAB_3_ACTION);
		intent.putExtra("isColletion", true);
		TabSpec tabSpec3 = tabHost.newTabSpec(TAB_3).setIndicator(TAB_3)
				.setContent(intent);

		intent = new Intent();
		intent.setAction(TAB_4_ACTION);
		TabSpec tabSpec4 = tabHost.newTabSpec(TAB_4).setIndicator(TAB_4)
				.setContent(intent);

		intent = new Intent();
		intent.setAction(TAB_5_ACTION);
		TabSpec tabSpec5 = tabHost.newTabSpec(TAB_5).setIndicator(TAB_5)
				.setContent(intent);

		intent = new Intent();
		intent.setAction(TAB_6_ACTION);
		TabSpec tabSpec6 = tabHost.newTabSpec(TAB_6).setIndicator(TAB_6)
				.setContent(intent);

		tabHost.addTab(tabSpec1);
		tabHost.addTab(tabSpec2);
		tabHost.addTab(tabSpec3);
		tabHost.addTab(tabSpec4);
		tabHost.addTab(tabSpec5);
		tabHost.addTab(tabSpec6);

		// 按钮组事件
		radioGroup = (RadioGroup) this.findViewById(R.id.rg_main_btns);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {// 选择事件

					public void onCheckedChanged(RadioGroup group, int checkedId) {

						switch (checkedId) {
						case R.id.tab_message:// 详细tab
							tabHost.setCurrentTabByTag(TAB_1);
							break;
						case R.id.tab_map:// 地图页面
							tabHost.setCurrentTabByTag(TAB_2);
							break;
						case R.id.tab_say:// 上报页面
							tabHost.setCurrentTabByTag(TAB_3);
							break;
						case R.id.tab_discuss:// 评论页面
							tabHost.setCurrentTabByTag(TAB_4);
							break;
						case R.id.tab_car:
							tabHost.setCurrentTabByTag(TAB_5);
							break;
						case R.id.tab_center_info:
							tabHost.setCurrentTabByTag(TAB_6);
							break;
						default:
							break;
						}

					}
				});

	}
}
