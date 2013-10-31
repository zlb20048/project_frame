/*
 * 文 件 名: HttpThread.java
 * All rights reserved 描 述: <描述> 修 改 人: y00109551 修改时间: 2009-6-4 跟踪单号: <跟踪单号>
 * 修改单号: <修改单号> 修改内容: <修改内容>
 */
package com.xwtec.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Locale;

import android.util.Log;

import com.xwtec.util.cookie.CookieManager;
import com.xwtec.util.manager.ProxyManager;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.Util;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author y00109551
 * @version [版本号, 2009-6-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class HttpThread extends Thread {
	/**
	 * 日志标签
	 */
	private static final String TAG = "HttpThread";

	/**
	 * 当取不到返回返回报文的大小时，设置缓冲区大小
	 */
	private static final int MAX_LENGTH = 40 * 1024;

	/**
	 * 用户控制是否中断请求的标志
	 */
	private boolean cancel = false;

	/**
	 * 请求url
	 */
	private String url;

	/**
	 * POST请求数据，如果为null则为GET方式，不为null时
	 * 
	 */
	private String data;

	/**
	 * HTTP请求监听的实现类，当请求有结果时会设置该类的相应方法
	 */
	private HttpListener listener;

	/**
	 * 重发请求机制，最多只会发送2次
	 */
	private int count = 2;

	private int realLen = -1;

	/**
	 * 清楚数据类型
	 */
	private String contentType = null;

	/**
	 * 构造方法
	 * 
	 * @param url
	 *            请求url
	 * @param listener
	 *            实现监听请求状态的类
	 * @param data
	 *            POST方式请求时的数据，如果为get方式发送，该字段为null
	 * 
	 */
	public HttpThread(String url, HttpListener listener, String data) {
		this.url = url;

		Log.i(TAG, "the http url is :" + url);

		// 如果data不为null，则为POST方式发送请求
		this.contentType = Constant.HTTP_CONTENT_TYPE_OCTET_STREAM;
		this.data = data;
		this.listener = listener;
	}

	/**
	 * 发送HTTP请求，并将结果设置回HttpListener的实现类中
	 * 
	 */
	public void run() {
		byte[] result = null;

		while (count != 0 && !cancel) {
			count--;

			result = sendRequest();

			if (result != null) {
				// 需要对报文检查下是否为WAP报文
				boolean checkResult = false;
				try {
					String checkString = new String(result, 0,
							realLen == -1 ? result.length : realLen, "utf-8");
					realLen = -1;
					checkResult = checkResponse(checkString);
					result = checkString.getBytes("utf-8");
				} catch (Exception e) {
					Log.e(TAG, "run e:" + e.toString());
				}
				if (checkResult) {
					break;
				} else {
					result = null;
				}
			}
		}

		// 如果用户还没有中断请求
		if (!cancel) {
			try {
				if (result != null) {
					listener.onFinish(result);
				} else {
					// 这个地方先这么做，调用点统一打印错误信息
					listener.onError(-1, "");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送请求获取响应
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private byte[] sendRequest() {

		URL myURL = null;
		HttpURLConnection conn = null;
		OutputStreamWriter out = null;
		InputStream is = null;

		byte[] buffer = null;
		try {
			myURL = new URL(url);
			// 如果没有初始化需要先初始化proxy
			if (!ProxyManager.getInstance().isProxyInitialized()) {
				ProxyManager.getInstance().initProxy();
			}
			Proxy proxy = ProxyManager.getInstance().getProxy();
			if (proxy != null) {
				Log.i(TAG, "conncet using proxy.");
				conn = (HttpURLConnection) myURL.openConnection(proxy);
			} else {
				Log.i(TAG, "conncet directly.");
				conn = (HttpURLConnection) myURL.openConnection();
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Pragma", "No-cache");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("accept-charset", "utf-8");
			conn.setRequestProperty("Content-Type", contentType);
			conn.setRequestProperty("x-client-plat", "5");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			// 设置超时时间
			conn.setConnectTimeout(Constant.CONNECT_OUT_TIME);
			conn.setReadTimeout(Constant.CONNECT_OUT_TIME);
			String cookie = CookieManager.getInstance().getCookie(url);
			if (null != cookie) {
				conn.addRequestProperty("cookie", cookie);
			}

			if (data != null) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				Log.i(TAG, "http post data is " + data);
				byte[] byteData = data.getBytes("UTF-8");
				conn.setRequestProperty("Content-Length",
						String.valueOf(byteData.length));
				out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				out.write(data);
				out.flush();
				Log.d(TAG, "request complete");
			} else {
				conn.setRequestMethod("GET");
			}

			// 执行到该句就是开始建立连接并取得连接的响应结果
			int code = conn.getResponseCode();
			Log.i(TAG, "http response code is " + code);

			// 写Cookie
			for (int j = 1; conn.getHeaderFieldKey(j) != null; j++) {
				if (conn.getHeaderFieldKey(j).toLowerCase()
						.equals("set-cookie")) {
					Log.i(TAG, "setcookie=" + conn.getHeaderField(j));
					CookieManager.getInstance().setCookie(url,
							conn.getHeaderField(j));
				}
			}

			if (code != HttpURLConnection.HTTP_OK) {
				Log.w(TAG, "http response is failed!");
			} else {
				// 获取输入流，为了直接取得字节数组，需要从输入流中读取
				is = conn.getInputStream();

				int size = conn.getContentLength();
				listener.onSetSize(size);

				// 每次读取字节数
				int reads;
				boolean bomFlag = false;
				boolean lengthKnownFlag = false;
				boolean lengthOverThree = false;
				if (size != -1) {
					lengthKnownFlag = true;
					byte[] bomHead = new byte[3];
					if (size >= 3) {
						lengthOverThree = true;
						int readHeadSize = is.read(bomHead, 0, 3);
						if (readHeadSize < 3) {
							Log.d(TAG, "readHeadSize < 3");
						}

						// 判断BOM头
						if (bomHead[0] == -17 && bomHead[1] == -69
								&& bomHead[2] == -65) {
							bomFlag = true;
							// 响应大小已知，确定缓冲区大小
							buffer = new byte[size - 3];
						} else {
							// 响应大小已知，确定缓冲区大小
							buffer = new byte[size];
							System.arraycopy(bomHead, 0, buffer, 0, 3);
						}
					} else {
						// 响应大小已知，确定缓冲区大小
						buffer = new byte[size];
						System.arraycopy(bomHead, 0, buffer, 0, size);
					}

				} else {
					byte[] bomHead = new byte[3];
					int readNum = is.read(bomHead, 0, 3);
					if (readNum >= 3) {
						lengthOverThree = true;

						int tempSize = MAX_LENGTH;

						// 判断BOM头
						if (bomHead[0] == -17 && bomHead[1] == -69
								&& bomHead[2] == -65) {
							bomFlag = true;
							// 响应大小已知，确定缓冲区大小
							buffer = new byte[tempSize - 3];
						} else {
							// 响应大小已知，确定缓冲区大小
							buffer = new byte[tempSize];
							System.arraycopy(bomHead, 0, buffer, 0, 3);
						}
					} else {
						// 响应大小已知，确定缓冲区大小
						buffer = new byte[readNum];
						System.arraycopy(bomHead, 0, buffer, 0, readNum);
					}
				}

				// 百分比
				int percent = 0;
				int tmpPercent = 0;

				// 缓存区索引
				int index = 0;

				while (!cancel && lengthOverThree) {
					int leftLen = bomFlag ? (buffer.length - index)
							: ((buffer.length - 3 - index));

					// 128个字节读取
					leftLen = leftLen >= 128 ? 128 : leftLen;
					reads = is.read(buffer, bomFlag ? index : (index + 3),
							leftLen);

					// 数据读取完毕
					if (reads <= 0) {
						if (size == -1) {
							Log.d(TAG, "bomFlag:" + bomFlag);
							realLen = index + 3;
						}
						break;
					}

					// 更新索引
					index += reads;

					// 必须知道报文大小才能设置进度
					if (lengthKnownFlag) {
						// 更新进度
						tmpPercent = (size >= 3 ? (index + 3) : index) * 100
								/ size;
						if (tmpPercent != percent) {
							percent = tmpPercent;
							listener.onProgress(percent);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			buffer = null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.e(TAG, "close outputStream " + e.toString());
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "close inputstream " + e.toString());
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		if ("gzip".equals(conn.getHeaderField("Content-Encoding"))) {
			Log.i(TAG,
					"Content-Encoding="
							+ conn.getHeaderField("Content-Encoding"));
			try {
				buffer = Util.gzipdecompress(buffer);
			} catch (Exception e) {
				Log.e(TAG, "gzip = " + e.toString());
			}
		}

		return buffer;
	}
	
	/**
	 * 该方法逻辑为了过滤请求时返回的WAP报文
	 * 
	 * @param temp
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private boolean checkResponse(String response) {
		Log.i(TAG, "the http response is :" + response);
		// Modify by wangtao_hw at 2010-3-29 下午04:40:36
		// Reason:toLowerCase修改，传入 Locale参数，使用英local
		if (response.toLowerCase(Locale.ENGLISH).indexOf("cache-control") != -1) {
			Log.i(TAG, "the http response is wap report!");
			return false;
		}
		return true;
	}

	/**
	 * 中断请求操作
	 */
	public void cancel() {
		cancel = true;
		this.interrupt();
	}
}
