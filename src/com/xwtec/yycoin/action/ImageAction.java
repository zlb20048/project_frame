/**
 * 
 */
package com.xwtec.yycoin.action;

import java.io.UnsupportedEncodingException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xwtec.util.http.HttpListener;
import com.xwtec.util.http.HttpThread;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.tool.Constant;

/**
 * @author liuzixiang
 * 
 */
public class ImageAction extends BaseAction implements HttpListener {

	private final static String TAG = ImageAction.class.getSimpleName();

	/**
	 * <默认构造函数>
	 * 
	 * @param dataListener
	 *            dataListener
	 */
	public ImageAction(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 获取参数信息
	 * 
	 * @param canshu
	 *            需要传递的参数
	 */
	public void getMessage(String canshu) {
		Log.v(TAG, "....");
		thread = new HttpThread(Constant.SERVER_URL + canshu, this, null);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.util.http.HttpListener#onSetSize(int)
	 */
	@Override
	public void onSetSize(int size) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.util.http.HttpListener#onFinish(byte[])
	 */
	@Override
	public void onFinish(byte[] data) throws UnsupportedEncodingException,
			JSONException {
		// TODO Auto-generated method stub
		Log.v(TAG, "onFinish...." + new String(data));

		String str = new String(data).trim();
		Log.v(TAG, "str = " + str);
		Message msg = new Message();
		msg.what = Constant.CONNECT_SUCCRESS;
		msg.obj = str;
		handler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.util.http.HttpListener#onProgress(int)
	 */
	@Override
	public void onProgress(int percent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.util.http.HttpListener#onError(int, java.lang.String)
	 */
	@Override
	public void onError(int code, String message) {
		// TODO Auto-generated method stub

	}

}
