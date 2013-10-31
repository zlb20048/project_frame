package com.xwtec.yycoin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xwtec.util.db.DBException;
import com.xwtec.util.db.DBTool;
import com.xwtec.util.tool.Constant;
import com.xwtec.yycoin.R;

public class WelcomeActivity extends BaseActivity {

	/**
	 * TAG
	 */
	private final static String TAG = "WelcomeActivity";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Intent intent = new Intent();
			intent.setClass(WelcomeActivity.this, BottomTabAcitivity.class);
			startActivity(intent);
			finish();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initDBConfig();

		handler.sendEmptyMessageDelayed(0, 3 * 1000);
	}

	/**
	 * 初始化数据库配置
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	private void initDBConfig() {
		// 初始化数据库连接
		DBTool.init(this);
		// 播放列表
		// 初始化数据库连接
		// 第一参数是Application对象
		// 第二个参数是数据库名称

		// 创建下载任务表
		// String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME
		// + "(collectionID TEXT not null,content TEXT not null,"
		// + "title TEXT not null, saveData TEXT not null, "
		// + "purl TEXT not null, curl TEXT not null, jg TEXT not null,"
		// + "gg TEXT not null," + "zl TEXT not null,tc TEXT not null )";
		//
		// String sql1 = "CREATE TABLE IF NOT EXISTS " + Constant.PRODUCT_CAR
		// + "(productCarId TEXT not null,title TEXT not null, "
		// + "jg TEXT not null, cz TEXT not null," + "gg TEXT not null,"
		// + "zl TEXT not null,tc TEXT not null, " + "count INTEGER )";

		// boolean isNew = false;

		// try {
		// 如果已存在，则创建失败不影响
		// DBTool.getInstance().creatTable(Constant.PRODUCT_CAR, false, sql1);
		// DBTool.getInstance().creatTable(Constant.TABLE_NAME, false, sql);
		// } catch (DBException e) {
		// Log.e(TAG, "excute sql:" + sql + "\n" + e.toString());
		// }

		// sql = null;
		Log.i(TAG, "Init DB config successed.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		DBTool.getInstance().onClose();
		super.onDestroy();
	}

}