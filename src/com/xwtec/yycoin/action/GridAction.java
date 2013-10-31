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
 * @author liuzixiang
 * 
 */
public class GridAction extends BaseAction implements HttpListener {

	private final static String TAG = GridAction.class.getSimpleName();

	/**
	 * <默认构造函数>
	 * 
	 * @param dataListener
	 *            dataListener
	 */
	public GridAction(Handler handler) {
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
		if (null != str && !"".equals(str)) {
			// 此处解析数据,并且将数据封装，然后返回给界面
			try {
				JSONArray json = new JSONArray(str);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				int length = json.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject json1 = json.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("title", json1.getString("title"));
						map.put("banner", json1.getString("banner"));
						map.put("aid", json1.get("aid"));
						map.put("curl", json1.get("curl"));
						map.put("purl", json1.get("purl"));
						map.put("image", json1.get("image"));
						map.put("jg", json1.get("jg"));
						map.put("gg", json1.get("gg"));
						map.put("zl", json1.get("zl"));
						map.put("tc", json1.get("tc"));
						map.put("cz", json1.get("cz"));
						map.put("productno", json1.get("productno"));
						Log.v(TAG, json1.getString("title"));
						Log.v(TAG, json1.getString("aid"));
						Log.v(TAG, json1.getString("purl"));
						Log.v(TAG, json1.getString("curl"));
						Log.v(TAG, json1.getString("image"));
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
