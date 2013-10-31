/**
 * 
 */
package com.xwtec.yycoin.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.util.Log;

import com.xwtec.util.http.HttpThread;
import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Util;

/**
 * @author liuzixiang
 * 
 */
public class BaseAction {
	/**
	 * TAG
	 */
	private static final String TAG = "BaseAction";

	/**
	 * 请求线程
	 */
	protected HttpThread thread;

	/**
	 * http数据返回监听
	 */
	protected Handler handler;

	/**
	 * 中断请求
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void cancelConnect() {
		if (thread != null) {
			thread.cancel();
		}
	}

	/**
	 * 增加解析json
	 * 
	 * @param data
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 */
	protected Object parseJSON(byte[] data) throws UnsupportedEncodingException, JSONException {
		JSONObject json = null;
		JSONArray array = null;
		// 把缓存以UTF_8编码格式转换成字符串
		String str = null;
		// 是否有[]
		boolean hasArrayTag = true;
		String charset = "utf-8";
		boolean isGBK = false;
		if (data.length > 3) {
			if (data[0] == (byte) 0xFF && data[1] == (byte) 0xFE) {// UTF-16LE
				isGBK = false;
				charset = "UTF-16LE";
			} else if (data[0] == (byte) 0xFE && data[1] == (byte) 0xFF) {// UTF-16BE
				isGBK = false;
				charset = "UTF-16BE";
			}
			// else if(data[0] == (byte)0xEF && data[1] == (byte)0xBB && data[2]
			// == (byte)0xBF)
			else if (data[0] == -17 && data[1] == -69 && data[2] == -65) {// 判断是否为UTF－8
																			// 编码
				isGBK = false;
				charset = "UTF-8";
			}
		}
		Log.i(TAG, "charset=" + charset);
		if (isGBK) {
			str = new String(data, "GB2312");
			str = new String(Util.gbk2utf8(str), HTTP.UTF_8);
		} else {
			str = new String(data, charset);
		}
		str = URLDecoder.decode(str, "utf-8");
		if (str.startsWith("ok")) {
			str = str.substring(2);
		}
		Log.i("json", "return:" + str);
		// 把字符串转换成JSONObject格式
		// 避免有些服务器返回JSON格式数据不正确
		// Android平台对格式要求一定要带'[]'，否则可能解析异常
		if (!str.startsWith("[")) {
			hasArrayTag = false;
			str = "[" + str + "]";
		}
		if (hasArrayTag) {
			// 有数组就传递数组
			array = new JSONArray(str);
			return array;
		} else {
			// 没有就构造 并且取数组首位JSONObject
			json = new JSONArray(str).optJSONObject(0);
			return json;
		}
	}
}
