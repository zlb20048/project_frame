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
public class OrderMessageAction extends BaseAction implements HttpListener {

	private final static String TAG = OrderMessageAction.class.getSimpleName();

	public OrderMessageAction(Handler handler) {
		this.handler = handler;
	}

	public void getMessage(String canshu) {
		Log.v(TAG, "....");
		thread = new HttpThread(Constant.NEW_SERVER_URL + canshu, this, null);
		thread.start();
	}

	@Override
	public void onSetSize(int size) {
		// TODO Auto-generated method stub

	}

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

			JSONArray array = jsonObject.getJSONArray("obj");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject o = array.getJSONObject(i);
				Map<String, String> mm = new HashMap<String, String>();
				mm.put("orderNo", o.getString("orderNo"));
				mm.put("status", o.getString("status"));
				mm.put("sale", o.getString("sale"));
				mm.put("pay", o.getString("pay"));
				mm.put("payAccount", o.getString("payAccount"));
				mm.put("payStatus", o.getString("payStatus"));
				mm.put("outTime", o.getString("outTime"));
				mm.put("productCode", o.getString("productCode"));
				mm.put("productName", o.getString("productName"));
				mm.put("picPath", o.getString("picPath"));
				list.add(mm);
			}
			map.put("obj", list);
			Message msg = new Message();
			msg.what = Constant.CONNECT_SUCCRESS;
			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			Message msg = new Message();
			msg.what = Constant.NO_DATA;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onProgress(int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int code, String message) {
		// TODO Auto-generated method stub

	}

}
