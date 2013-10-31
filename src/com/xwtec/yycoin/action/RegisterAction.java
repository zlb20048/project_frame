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
public class RegisterAction extends BaseAction implements HttpListener {

	private final static String TAG = RegisterAction.class.getSimpleName();

	/**
	 * <默认构造函数>
	 * 
	 * @param dataListener
	 *            dataListener
	 */
	public RegisterAction(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 获取参数信息
	 * 
	 * @param canshu
	 *            需要传递的参数
	 */
	public void getMessage(String canshu) {
		Log.v(TAG, "canshu = " + canshu);
		thread = new HttpThread(Constant.NEW_SERVER_URL + canshu, this, null);
		thread.start();
	}

	@Override
	public void cancelConnect() {
		// TODO Auto-generated method stub
		super.cancelConnect();
	}

	@Override
	protected Object parseJSON(byte[] data)
			throws UnsupportedEncodingException, JSONException {
		// TODO Auto-generated method stub
		return super.parseJSON(data);
	}

	@Override
	public void onSetSize(int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish(byte[] data) throws UnsupportedEncodingException,
			JSONException {
		// TODO Auto-generated method stub
		Log.v(TAG, "onFinish...." + new String(data));

		String str = new String(data).trim();
		Log.v(TAG, "str = " + str);
		if (null != str && !"".equals(str)) {
			// 此处解析数据,并且将数据封装，然后返回给界面
			JSONObject jsonObject = new JSONObject(str);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("retCode", jsonObject.getInt("retCode"));
			map.put("retMsg", jsonObject.getString("retMsg"));Message msg = new Message();
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
