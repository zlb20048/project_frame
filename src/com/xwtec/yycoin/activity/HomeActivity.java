/**
 * 
 */
package com.xwtec.yycoin.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.xwtec.yycoin.R;

/**
 * @author liuzixiang
 * 
 */
public class HomeActivity extends BaseActivity {
	/**
	 * 记录第一次点击返回的时刻
	 */
	private long fristTime = 0;

	/**
	 * 记录第二次点击返回的时刻
	 */
	private long secondTime = 0;

	/**
	 * 退出客户端的间隔时间
	 */
	private final static long TIME_USED = 2000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				fristTime = System.currentTimeMillis();
				if (fristTime - secondTime < TIME_USED) {
					finish();
				} else {
					Toast.makeText(this, "再次点击退出客户端", Toast.LENGTH_SHORT)
							.show();
					secondTime = System.currentTimeMillis();
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

}
