/**
 * 
 */
package com.xwtec.yycoin.activity;

import android.app.LocalActivityManager;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * @author liuzixiang
 * 
 */
public class ProductListTabAcitivity extends BaseActivity {

	/**
	 * 标识
	 */
	private final static String TAG = ProductListTabAcitivity.class
			.getSimpleName();

	/**
	 * 当前的TabHost
	 */
	private TabHost tabs;

	/**
	 * 本地类的管理类
	 */
	LocalActivityManager mLocalActivityManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
