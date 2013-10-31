/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xwtec.util.tool.ProgressDialogUtil;
import com.xwtec.yycoin.R;

/**
 * @author liuzixiang
 * 
 */
public class BaseActivity extends Activity {

	private final static String TAG = BaseActivity.class.getSimpleName();

	private Handler handler = new Handler();

	protected ProgressDialogUtil pdu = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏虚拟键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	/*
	 * 写入文件到默认路径
	 */
	public void saveToDefault(byte[] buf, String imgName) {
		FileOutputStream fos;
		try {
			fos = this.openFileOutput(imgName, Context.MODE_PRIVATE);
			fos.write(buf);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void showSureDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.sure_btn_str, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void showProgessDialog() {
		if (null == pdu) {
			pdu = new ProgressDialogUtil(this, R.string.tip, R.string.loading,
					true, false);
		}
		pdu.showProgress();
	}

	protected void closeProgressDialog() {
		if (null != pdu) {
			pdu.closeProgress();
			pdu = null;
		}
	}

	/**
	 * 获取当前的版本信息
	 * 
	 * @return
	 */
	protected int getCurrentVersionCode() {
		int versionCode = -1;
		try {
			PackageManager pm = this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}

	ImageButton backBtn;

	/**
	 * 显示头部
	 * 
	 * @param title
	 *            显示title
	 */
	protected void showTop(String title) {
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		TextView topText = (TextView) findViewById(R.id.top);
		topText.setText(title);

		backBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		showDailer();
	}

	protected void showDailer() {
		ImageButton dialerBtn = (ImageButton) findViewById(R.id.dialerBtn);
		dialerBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BaseActivity.this);
				builder.setMessage("4006518859");
				builder.setTitle("客服热线电话");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								call();
							}
						});
				builder.setNegativeButton("取消", null);
				AlertDialog dialog = builder.create();
				dialog.show();

			}
		});
	}

	private void call() {
		Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ "4006518859"));
		startActivity(phoneIntent);
	}

}
