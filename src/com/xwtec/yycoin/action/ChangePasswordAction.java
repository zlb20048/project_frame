/**
 * 
 */
package com.xwtec.yycoin.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xwtec.util.http.HttpListener;
import com.xwtec.util.http.HttpThread;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;

/**
 * @author Administrator
 * 
 */
public class ChangePasswordAction extends BaseAction implements HttpListener {

	private final static String TAG = ChangePasswordAction.class
			.getSimpleName();

	public ChangePasswordAction(Handler handler) {
		this.handler = handler;
	}

	public void getMessage(String canshu) {
		Log.v(TAG, "canshu = " + canshu);
		thread = new HttpThread(Constant.NEW_SERVER_URL + canshu, this, null);
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
		Log.v(TAG, "onFinish...." + new String(data));

		String str = new String(data).trim();
		Log.v(TAG, "str = " + str);
		if (null != str && !"".equals(str)) {
			// 此处解析数据,并且将数据封装，然后返回给界面
			JSONObject jsonObject = new JSONObject(str);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("retCode", jsonObject.getInt("retCode"));
			map.put("retMsg", jsonObject.getString("retMsg"));
			Message msg = new Message();
			msg.what = Constant.NO_DATA;
			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			Message msg = new Message();
			msg.what = Constant.NO_DATA;
			handler.sendMessage(msg);
		}
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
