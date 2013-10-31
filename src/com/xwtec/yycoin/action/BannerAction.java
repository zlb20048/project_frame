/**
 * 
 */
package com.xwtec.yycoin.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Administrator
 * 
 */
public class BannerAction extends BaseAction implements HttpListener {

	private final static String TAG = BannerAction.class.getSimpleName();

	public BannerAction(Handler handler) {
		this.handler = handler;
	}

	public void getMessage(String canshu) {
		Log.v(TAG, "canshu = " + canshu);
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
		Log.v(TAG, "onFinish...." + new String(data));

		String str = new String(data).trim();
		Log.v(TAG, "str = " + str);
		try {
			JSONArray json = new JSONArray(str);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			int length = json.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject json1 = json.getJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", json1.getString("id"));
					map.put("name", json1.get("name"));
					map.put("image", json1.get("image"));
					map.put("desc", json1.get("desc"));
					map.put("times", json1.get("times"));

					JSONArray array = json1.getJSONArray("productes");
					List<Map<String, String>> productes = new ArrayList<Map<String, String>>();
					map.put("productes", productes);
					for (int j = 0; j < array.length(); j++) {
						Map<String, String> item = new HashMap<String, String>();
						productes.add(item);
						JSONObject o = array.getJSONObject(j);
						item.put("aid", o.getString("aid"));
						item.put("title", o.getString("title"));
						item.put("image", o.getString("image"));
						item.put("banner", o.getString("banner"));
						item.put("curl", o.getString("curl"));
						item.put("purl", o.getString("purl"));
						item.put("jg", o.getString("jg"));
						item.put("tc", o.getString("tc"));
						item.put("cz", o.getString("cz"));
						item.put("zl", o.getString("zl"));
						item.put("gg", o.getString("gg"));
						item.put("productno", o.getString("productno"));
					}
					list.add(map);
				}
				Message msg = new Message();
				msg.what = Constant.CONNECT_SUCCRESS;
				msg.obj = list;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = Constant.NO_DATA;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {

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
