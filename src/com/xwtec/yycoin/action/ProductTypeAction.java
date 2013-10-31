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
public class ProductTypeAction extends BaseAction implements HttpListener {

	private final static String TAG = ProductTypeAction.class.getSimpleName();

	/**
	 * <默认构造函数>
	 * 
	 * @param dataListener
	 *            dataListener
	 */
	public ProductTypeAction(Handler handler) {
		this.handler = handler;
	}

	public ProductTypeAction() {

	}

	/**
	 * 设置回调的类型
	 * 
	 * @param handler
	 */
	public void setHandler(Handler handler) {
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
		Log.v(TAG, "onFinish...." + new String(data));
		String str = new String(data).trim();

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
						map.put("bid", json1.getString("bid"));
						map.put("name", json1.get("name"));
						map.put("img", json1.get("img"));
						map.put("des", json1.get("des"));
						Log.v(TAG, json1.getString("bid"));
						Log.v(TAG, json1.getString("name"));
						Log.v(TAG, json1.getString("bid"));
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
