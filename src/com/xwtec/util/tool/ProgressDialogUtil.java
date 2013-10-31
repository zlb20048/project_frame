package com.xwtec.util.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

public class ProgressDialogUtil {
	/**
	 * 进度显示框
	 */
	private ProgressDialog pd;

	/**
	 * 构造方法，初始化进度显示框的数据
	 * 
	 * @param context
	 *            进度显示框所依赖的上下文
	 * @param title
	 *            进度条上的标题文字ID
	 * @param res
	 *            进度条上的内容文字ID
	 * @param permitClose
	 *            是否允许关闭
	 */
	public ProgressDialogUtil(final Context context, int title, int res,
			final boolean permitClose, final boolean forceCloseWiddow) {
		pd = new ProgressDialog(context) {
			@Override
			public boolean dispatchKeyEvent(KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
						if (!permitClose) {
							return true;
						}
						if (forceCloseWiddow) {
							// LogX.closeFile();
							// final String name = context.getPackageName();
							// final ActivityManager manager = (ActivityManager)
							// context
							// .getSystemService(Context.ACTIVITY_SERVICE);
							// manager.restartPackage(name);
						}
					} else if (event.getKeyCode() != KeyEvent.KEYCODE_HOME) {
						return true;
					}
				}
				return super.dispatchKeyEvent(event);
			}
		};
		pd.setTitle(title);
		pd.setMessage(context.getResources().getText(res));
	}

	/**
	 * 设置提示信息
	 * 
	 * @param message
	 *            提示信息
	 */
	public void setMessage(String message) {
		if (pd != null) {
			pd.setMessage(message);
		}
	}

	/**
	 * 设置提示标题
	 * 
	 * @param title
	 *            标题
	 */
	public void setTitle(String title) {
		if (pd != null) {
			pd.setTitle(title);
		}
	}

	/**
	 * 显示进度框
	 */
	public void showProgress() {
		if (pd != null && !pd.isShowing()) {
			try {
				pd.show();
			} catch (RuntimeException e) {
				// LogX.trace("pd.show()/ err/or!", ":" + e.getMessage());
			}
		}
	}

	/**
	 * 关闭进度框
	 */
	public void closeProgress() {
		if (pd != null && pd.isShowing()) {
			try {
				pd.dismiss();
				pd = null;
			} catch (RuntimeException e) {
				// LogX.trace("pd.dismiss() error!", ":" + e.getMessage());
			}
		}
	}
}
