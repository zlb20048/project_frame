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
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.info.UserInfo;

/**
 * @author Administrator
 * 
 */
public class LoginAction extends BaseAction implements HttpListener {

	private final static String TAG = LoginAction.class.getSimpleName();

	public LoginAction(Handler handler) {
		super();
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

			JSONObject json1 = jsonObject.getJSONObject("obj");
			UserInfo info = new UserInfo();
			Variable.id = info.id = json1.getString("id");
			Variable.address = info.address = json1.getString("province")
					+ json1.getString("city");
			Variable.detailAddress = info.detailAddress = json1
					.getString("fullAddress");
			Variable.email = info.email = json1.getString("email");
			Variable.phoneNumber = info.phoneNumber = json1.getString("mobile");
			Variable.trueName = info.trueName = json1.getString("customerName");
			Variable.username = info.username = json1.getString("loginName");
			Variable.isLogin = true;
			map.put("obj", info);
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
