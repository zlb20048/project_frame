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
import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;

/**
 * @author liuzixiang
 * 
 */
public class UpdateAction extends BaseAction implements HttpListener {

	private final static String TAG = UpdateAction.class.getSimpleName();

	/**
	 * <默认构造函数>
	 * 
	 * @param dataListener
	 *            dataListener
	 */
	public UpdateAction(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 获取参数信息
	 * 
	 * @param canshu
	 *            需要传递的参数
	 */
	public void getMessage() {
		Log.v(TAG, "....");
		thread = new HttpThread(Constant.SERVER_URL + "version.jsp", this, null);
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
		if (!str.startsWith("[")) {
			str = "[" + str + "]";
		}
		try {
			JSONArray json = new JSONArray(str);
			int length = json.length();
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject json1 = json.getJSONObject(i);

					map.put("version", json1.getString("version"));
					map.put("android", json1.get("android"));
				}
				Message msg = new Message();
				msg.what = Constant.CONNECT_SUCCRESS;
				msg.obj = map;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = Constant.NO_DATA;
				handler.sendMessage(msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
