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
public class OrderDetailAction extends BaseAction implements HttpListener {

	private final static String TAG = OrderDetailAction.class.getSimpleName();

	public OrderDetailAction(Handler handler) {
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
			JSONObject obj = jsonObject.getJSONObject("obj");

			map.put("orderNo", obj.get("orderNo"));
			map.put("statusName", obj.get("statusName"));
			map.put("payStatusName", obj.get("payStatusName"));
			map.put("total", obj.get("total"));
			map.put("loginName", obj.get("loginName"));

			JSONObject distribution = obj.getJSONObject("distribution");
			map.put("province", distribution.get("province"));
			map.put("city", distribution.get("city"));
			map.put("fullAddress", distribution.get("fullAddress"));
			map.put("receiver", distribution.get("receiver"));

			List<Map<String, String>> items = new ArrayList<Map<String, String>>();
			JSONArray arrys = obj.getJSONArray("items");
			for (int i = 0; i < arrys.length(); i++) {
				JSONObject item = arrys.getJSONObject(i);
				Map<String, String> itemMap = new HashMap<String, String>();
				itemMap.put("id", item.getString("id"));
				itemMap.put("orderNo", item.getString("orderNo"));
				itemMap.put("productId", item.getString("productId"));
				itemMap.put("productCode", item.getString("productCode"));
				itemMap.put("productName", item.getString("productName"));
				itemMap.put("picPath", item.getString("picPath"));
				itemMap.put("amount", item.getString("amount"));
				itemMap.put("price", item.getString("price"));
				itemMap.put("money", item.getString("money"));
				itemMap.put("description", item.getString("description"));
				items.add(itemMap);
			}

			map.put("items", items);
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
