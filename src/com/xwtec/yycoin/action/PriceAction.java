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
import com.xwtec.util.tool.Variable;

/**
 * @author Administrator
 * 
 */
public class PriceAction extends BaseAction implements HttpListener {

	private final static String TAG = PriceAction.class.getSimpleName();

	public PriceAction(Handler handler) {
		this.handler = handler;
	}

	public void getMessage(String canshu) {
		Log.v(TAG, "canshu = " + canshu);
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
			JSONArray obj = jsonObject.getJSONArray("obj");
			for (int i = 0; i < obj.length(); i++) {
				JSONObject o = obj.getJSONObject(i);
				Variable.priceMap.put(o.getString("productCode"),
						o.getString("price"));
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
