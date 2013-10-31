package com.xwtec.util.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.util.Log;

/******************************************************************
 * 文件名称: CookieManager.java 作 者: fushangbin 创建时间: 2011-2-26 上午10:52:20 文件描述:
 * cookie 管理类
 ******************************************************************/
public final class CookieManager {
	private static CookieManager sRef;
	private static final int SECURE_LENGTH = "secure".length();
	private static final int HTTP_ONLY_LENGTH = "httponly".length();
	private Map<String, ArrayList<Cookie>> mCookieMap;
	private boolean mAcceptCookie;
	private static final String BAD_COUNTRY_2LDS[] = { "ac", "co", "com", "ed",
			"edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" };
	private final String TAG = "==CookieManager==";
	private static final CookieComparator COMPARATOR = new CookieComparator(
			null);

	static {
		Arrays.sort(BAD_COUNTRY_2LDS);
	}

	private static final class CookieComparator implements Comparator<Object> {

		public int compare(Cookie cookie1, Cookie cookie2) {
			int diff = cookie2.path.length() - cookie1.path.length();
			if (diff != 0)
				return diff;
			diff = cookie2.domain.length() - cookie1.domain.length();
			if (diff != 0)
				return diff;
			if (cookie2.value == null) {
				if (cookie1.value != null)
					return -1;
			} else if (cookie1.value == null)
				return 1;
			return cookie1.name.compareTo(cookie2.name);
		}

		public int compare(Object obj, Object obj1) {
			return compare((Cookie) obj, (Cookie) obj1);
		}

		private CookieComparator() {
		}

		CookieComparator(CookieComparator cookiecomparator) {
			this();
		}
	}

	private CookieManager() {
		mCookieMap = new LinkedHashMap<String, ArrayList<Cookie>>(200, 0.75F,
				true);
		mAcceptCookie = true;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("doesn't implement Cloneable");
	}

	public static synchronized CookieManager getInstance() {
		if (sRef == null)
			sRef = new CookieManager();
		return sRef;
	}

	public synchronized void setAcceptCookie(boolean accept) {
		mAcceptCookie = accept;
	}

	public synchronized boolean acceptCookie() {
		return mAcceptCookie;
	}

	/****************************************************************************************
	 * 函数名称：setCookie 函数描述：设置网络请求返回的cookie 输入参数：@param url 输入参数：@param value
	 * 输出参数： 返回 值：void 备 注：
	 ****************************************************************************************/
	public synchronized void setCookie(String url, String value) {
		Log.i(TAG, "setCookie=" + value);
		if (value != null && value.length() > 4096)
			return;
		if (!mAcceptCookie || url == null)
			return;
		String hostAndPath[] = getHostAndPath(url);
		if (hostAndPath == null)
			return;
		if (hostAndPath[1].length() > 1) {
			int index = hostAndPath[1].lastIndexOf('/');
			hostAndPath[1] = hostAndPath[1].substring(0, index <= 0 ? index + 1
					: index);
		}
		ArrayList<Cookie> cookies = null;
		try {
			cookies = parseCookie(hostAndPath[0], hostAndPath[1], value);
		} catch (RuntimeException ex) {
			Log.e(TAG,
					(new StringBuilder("parse cookie failed for: ")).append(
							value).toString());
		}
		if (cookies == null || cookies.size() == 0)
			return;
		String baseDomain = getBaseDomain(hostAndPath[0]);
		ArrayList<Cookie> cookieList = (ArrayList<Cookie>) mCookieMap
				.get(baseDomain);
		if (cookieList == null) {
			cookieList = new ArrayList<Cookie>();
			mCookieMap.put(baseDomain, cookieList);
		}
		long now = System.currentTimeMillis();
		int size = cookies.size();
		for (int i = 0; i < size; i++) {
			Cookie cookie = (Cookie) cookies.get(i);
			boolean done = false;
			for (Iterator<Cookie> iter = cookieList.iterator(); iter.hasNext();) {
				Cookie cookieEntry = (Cookie) iter.next();
				if (cookie.exactMatch(cookieEntry)) {
					if (cookie.expires < 0L || cookie.expires > now) {
						if (!cookieEntry.secure
								|| "https".equals(getScheme(url))) {
							cookieEntry.value = cookie.value;
							cookieEntry.expires = cookie.expires;
							cookieEntry.secure = cookie.secure;
							cookieEntry.lastAcessTime = now;
							cookieEntry.lastUpdateTime = now;
							cookieEntry.mode = 3;
						}
					} else {
						cookieEntry.lastUpdateTime = now;
						cookieEntry.mode = 2;
					}
					done = true;
					break;
				}
			}
			if (!done && (cookie.expires < 0L || cookie.expires > now)) {
				cookie.lastAcessTime = now;
				cookie.lastUpdateTime = now;
				cookie.mode = 0;
				if (cookieList.size() > 50) {
					Cookie toDelete = new Cookie();
					toDelete.lastAcessTime = now;
					for (Iterator<Cookie> iter2 = cookieList.iterator(); iter2
							.hasNext();) {
						Cookie cookieEntry2 = (Cookie) iter2.next();
						if (cookieEntry2.lastAcessTime < toDelete.lastAcessTime
								&& cookieEntry2.mode != 2)
							toDelete = cookieEntry2;
					}
					toDelete.mode = 2;
				}
				cookieList.add(cookie);
			}
		}
	}

	/****************************************************************************************
	 * 函数名称：getCookie 函数描述：根据url获取服务器返回的cookie值 输入参数：@param uri 输入参数：@return
	 * 输出参数： 返回 值：String 备 注：
	 ****************************************************************************************/
	public synchronized String getCookie(String uri) {
		if (!mAcceptCookie || uri == null)
			return null;
		String hostAndPath[] = getHostAndPath(uri);
		if (hostAndPath == null)
			return null;
		String baseDomain = getBaseDomain(hostAndPath[0]);
		ArrayList<Cookie> cookieList = (ArrayList<Cookie>) mCookieMap
				.get(baseDomain);
		if (cookieList == null) {
			cookieList = new ArrayList<Cookie>();
			mCookieMap.put(baseDomain, cookieList);
		}
		long now = System.currentTimeMillis();
		boolean secure = "https".equals(getScheme(uri));
		Iterator<Cookie> iter = cookieList.iterator();
		SortedSet<Cookie> cookieSet = new TreeSet<Cookie>(COMPARATOR);
		while (iter.hasNext()) {
			Cookie cookie = (Cookie) iter.next();
			if (cookie.domainMatch(hostAndPath[0])
					&& cookie.pathMatch(hostAndPath[1])
					&& (cookie.expires < 0L || cookie.expires > now)
					&& (!cookie.secure || secure) && cookie.mode != 2) {
				cookie.lastAcessTime = now;
				cookieSet.add(cookie);
			}
		}
		StringBuilder ret = new StringBuilder(256);
		for (Iterator<Cookie> setIter = cookieSet.iterator(); setIter.hasNext();) {
			Cookie cookie = (Cookie) setIter.next();
			if (ret.length() > 0) {
				ret.append(';');
				// ret.append(' ');
			}
			ret.append(cookie.name);
			if (cookie.value != null) {
				ret.append('=');
				ret.append(cookie.value);
			}
		}
		if (ret.length() > 0)
			return ret.toString();
		else
			return null;
	}

	/****************************************************************************************
	 * 函数名称：removeSessionCookie 函数描述：清空所有session cookie 输入参数： 输出参数： 返回 值：void 备
	 * 注：
	 ****************************************************************************************/
	public void removeSessionCookie() {
		Runnable clearCache = new Runnable() {
			public void run() {
				synchronized (CookieManager.this) {
					Collection<ArrayList<Cookie>> cookieList = mCookieMap
							.values();
					for (Iterator<ArrayList<Cookie>> listIter = cookieList
							.iterator(); listIter.hasNext();) {
						ArrayList<Cookie> list = (ArrayList<Cookie>) listIter
								.next();
						for (Iterator<Cookie> iter = list.iterator(); iter
								.hasNext();) {
							Cookie cookie = (Cookie) iter.next();
							if (cookie.expires == -1L)
								iter.remove();
						}
					}
				}
			}
		};
		(new Thread(clearCache)).start();
	}

	/****************************************************************************************
	 * 函数名称：removeAllCookie 函数描述：清空所有cookie值 输入参数： 输出参数： 返回 值：void 备 注：
	 ****************************************************************************************/
	public void removeAllCookie() {
		Runnable clearCache = new Runnable() {
			public void run() {
				synchronized (CookieManager.this) {
					mCookieMap = new LinkedHashMap<String, ArrayList<Cookie>>(
							200, 0.75F, true);
				}
			}
		};
		(new Thread(clearCache)).start();
	}

	/****************************************************************************************
	 * 函数名称：hasCookies 函数描述：返回是否支持cookie，默认返回为true 输入参数：@return 输出参数： 返回
	 * 值：boolean 备 注：
	 ****************************************************************************************/
	public synchronized boolean hasCookies() {
		return true;
	}

	/****************************************************************************************
	 * 函数名称：removeExpiredCookie 函数描述：清除失效的cookie 值 输入参数： 输出参数： 返回 值：void 备 注：
	 ****************************************************************************************/
	public void removeExpiredCookie() {
		Runnable clearCache = new Runnable() {
			public void run() {
				synchronized (CookieManager.this) {
					long now = System.currentTimeMillis();
					Collection<ArrayList<Cookie>> cookieList = mCookieMap
							.values();
					for (Iterator<ArrayList<Cookie>> listIter = cookieList
							.iterator(); listIter.hasNext();) {
						ArrayList<Cookie> list = (ArrayList<Cookie>) listIter
								.next();
						for (Iterator<Cookie> iter = list.iterator(); iter
								.hasNext();) {
							Cookie cookie = (Cookie) iter.next();
							if (cookie.expires > 0L && cookie.expires < now)
								iter.remove();
						}
					}
				}
			}
		};
		(new Thread(clearCache)).start();
	}

	synchronized ArrayList<Cookie> getUpdatedCookiesSince(long last) {
		ArrayList<Cookie> cookies = new ArrayList<Cookie>();
		Collection<ArrayList<Cookie>> cookieList = mCookieMap.values();
		for (Iterator<ArrayList<Cookie>> listIter = cookieList.iterator(); listIter
				.hasNext();) {
			ArrayList<?> list = (ArrayList<?>) listIter.next();
			for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
				Cookie cookie = (Cookie) iter.next();
				if (cookie.lastUpdateTime > last)
					cookies.add(cookie);
			}
		}
		return cookies;
	}

	synchronized void deleteACookie(Cookie cookie) {
		if (cookie.mode == 2) {
			String baseDomain = getBaseDomain(cookie.domain);
			ArrayList<Cookie> cookieList = (ArrayList<Cookie>) mCookieMap
					.get(baseDomain);
			if (cookieList != null) {
				cookieList.remove(cookie);
				if (cookieList.isEmpty())
					mCookieMap.remove(baseDomain);
			}
		}
	}

	synchronized void syncedACookie(Cookie cookie) {
		cookie.mode = 1;
	}

	synchronized ArrayList<Cookie> deleteLRUDomain() {
		int count = 0;
		int mapSize = mCookieMap.size();
		if (mapSize < 15) {
			Collection<ArrayList<Cookie>> cookieLists = mCookieMap.values();
			ArrayList<Cookie> list;
			for (Iterator<ArrayList<Cookie>> listIter = cookieLists.iterator(); listIter
					.hasNext() && count < 1000; count += list.size())
				list = (ArrayList<Cookie>) listIter.next();
		}
		ArrayList<Cookie> retlist = new ArrayList<Cookie>();
		if (mapSize >= 15 || count >= 1000) {
			Object domains[] = mCookieMap.keySet().toArray();
			for (int toGo = mapSize / 10 + 1; toGo-- > 0;) {
				String domain = domains[toGo].toString();
				retlist.addAll((Collection<Cookie>) mCookieMap.get(domain));
				mCookieMap.remove(domain);
			}
		}
		return retlist;
	}

	/****************************************************************************************
	 * 函数名称：parseCookie 函数描述：解析cookie值 输入参数：@param host 输入参数：@param path
	 * 输入参数：@param cookieString 输入参数：@return 输出参数： 返回 值：ArrayList<Cookie> 备 注：
	 ****************************************************************************************/
	private ArrayList<Cookie> parseCookie(String host, String path,
			String cookieString) {
		ArrayList<Cookie> ret = new ArrayList<Cookie>();
		int index = 0;
		int length = cookieString.length();
		do {
			Cookie cookie = null;
			if (index < 0 || index >= length)
				break;
			if (cookieString.charAt(index) == ' ') {
				index++;
				continue;
			}
			int semicolonIndex = cookieString.indexOf(';', index);
			int equalIndex = cookieString.indexOf('=', index);
			cookie = new Cookie(host, path);
			if (semicolonIndex != -1 && semicolonIndex < equalIndex
					|| equalIndex == -1) {
				if (semicolonIndex == -1)
					semicolonIndex = length;
				cookie.name = cookieString.substring(index, semicolonIndex);
				cookie.value = null;
			} else {
				cookie.name = cookieString.substring(index, equalIndex);
				if (equalIndex < length - 1
						&& cookieString.charAt(equalIndex + 1) == '"') {
					index = cookieString.indexOf('"', equalIndex + 2);
					if (index == -1)
						break;
				}
				semicolonIndex = cookieString.indexOf(';', index);
				if (semicolonIndex == -1)
					semicolonIndex = length;
				if (semicolonIndex - equalIndex > 4096)
					cookie.value = cookieString.substring(equalIndex + 1,
							equalIndex + 1 + 4096);
				else if (equalIndex + 1 == semicolonIndex
						|| semicolonIndex < equalIndex)
					cookie.value = "";
				else
					cookie.value = cookieString.substring(equalIndex + 1,
							semicolonIndex);
			}
			index = semicolonIndex;
			do {
				if (index < 0 || index >= length)
					break;
				if (cookieString.charAt(index) == ' '
						|| cookieString.charAt(index) == ';') {
					index++;
					continue;
				}
				if (cookieString.charAt(index) == ',') {
					index++;
					break;
				}
				if (length - index >= SECURE_LENGTH
						&& cookieString.substring(index, index + SECURE_LENGTH)
								.equalsIgnoreCase("secure")) {
					index += SECURE_LENGTH;
					cookie.secure = true;
					if (index == length)
						break;
					if (cookieString.charAt(index) == '=')
						index++;
					continue;
				}
				if (length - index >= HTTP_ONLY_LENGTH
						&& cookieString.substring(index,
								index + HTTP_ONLY_LENGTH).equalsIgnoreCase(
								"httponly")) {
					index += HTTP_ONLY_LENGTH;
					if (index == length)
						break;
					if (cookieString.charAt(index) == '=')
						index++;
					continue;
				}
				equalIndex = cookieString.indexOf('=', index);
				if (equalIndex > 0) {
					String name = cookieString.substring(index, equalIndex)
							.toLowerCase();
					if (name.equals("expires")) {
						int comaIndex = cookieString.indexOf(',', equalIndex);
						if (comaIndex != -1 && comaIndex - equalIndex <= 10)
							index = comaIndex + 1;
					}
					semicolonIndex = cookieString.indexOf(';', index);
					int commaIndex = cookieString.indexOf(',', index);
					if (semicolonIndex == -1 && commaIndex == -1)
						index = length;
					else if (semicolonIndex == -1)
						index = commaIndex;
					else if (commaIndex == -1)
						index = semicolonIndex;
					else
						index = Math.min(semicolonIndex, commaIndex);
					String value = cookieString
							.substring(equalIndex + 1, index);
					if (value.length() > 2 && value.charAt(0) == '"') {
						int endQuote = value.indexOf('"', 1);
						if (endQuote > 0)
							value = value.substring(1, endQuote);
					}
					if (name.equals("expires")) {
						try {
							cookie.expires = parseDate(value);
						} catch (IllegalArgumentException ex) {
							Log.e(TAG,
									(new StringBuilder(
											"illegal format for expires: "))
											.append(value).toString());
							try {
								Date date = new Date(value);
								Calendar cl = Calendar.getInstance();
								cl.setTime(date);
								cookie.expires = parseDate(""
										+ (cl.getTimeInMillis()));
							} catch (Exception e) {
								Log.e(TAG, "expires==" + e.toString());
							}
						}
						continue;
					}
					if (name.equals("max-age")) {
						try {
							cookie.expires = System.currentTimeMillis() + 1000L
									* Long.parseLong(value);
						} catch (NumberFormatException ex) {
							Log.e(TAG,
									(new StringBuilder(
											"illegal format for max-age: "))
											.append(value).toString());
						}
						continue;
					}
					if (name.equals("path")) {
						if (value.length() > 0)
							cookie.path = value;
						continue;
					}
					if (!name.equals("domain"))
						continue;
					int lastPeriod = value.lastIndexOf('.');
					if (lastPeriod == 0) {
						cookie.domain = null;
						continue;
					}
					try {
						Integer.parseInt(value.substring(lastPeriod + 1));
						if (!value.equals(host))
							cookie.domain = null;
						continue;
					} catch (NumberFormatException numberformatexception) {
						value = value.toLowerCase();
						if (value.charAt(0) != '.') {
							value = (new StringBuilder(String.valueOf('.')))
									.append(value).toString();
							lastPeriod++;
						}
					}
					if (host.endsWith(value.substring(1))) {
						int len = value.length();
						int hostLen = host.length();
						if (hostLen > len - 1
								&& host.charAt(hostLen - len) != '.') {
							cookie.domain = null;
							continue;
						}
						if (len == lastPeriod + 3 && len >= 6 && len <= 8) {
							String s = value.substring(1, lastPeriod);
							if (Arrays.binarySearch(BAD_COUNTRY_2LDS, s) >= 0) {
								cookie.domain = null;
								continue;
							}
						}
						cookie.domain = value;
					} else {
						cookie.domain = null;
					}
				} else {
					index = length;
				}
			} while (true);
			if (cookie != null && cookie.domain != null)
				ret.add(cookie);
		} while (true);
		return ret;
	}

	/*******************************************************************
	 * 函数名称: parseDate 函数描述: 输入参数: @param value 输入参数: @return 输出参数: void 返回值 :
	 * long 备注: N/A
	 ******************************************************************/
	private long parseDate(String value) {
		return Long.parseLong(value);
	}

	/*******************************************************************
	 * 函数名称: getScheme 函数描述: 获取url采用的协议格式 输入参数: @param url 输入参数: @return 输出参数:
	 * void 返回值 : String 备注: N/A
	 ******************************************************************/
	private String getScheme(String url) {
		return url.substring(0, url.indexOf(':'));
	}

	/*******************************************************************
	 * 函数名称: getHost 函数描述: 获取url的主机地址 输入参数: @param url 输入参数: @return 输出参数: void
	 * 返回值 : String 备注: N/A
	 ******************************************************************/
	private String getHost(String url) {
		if (url.indexOf('/', 7) == -1) {
			return url.substring(0);
		}
		return url.substring(0, url.indexOf('/', 7));
	}

	/*******************************************************************
	 * 函数名称: getPath 函数描述: 获取url的目录地址 输入参数: @param url 输入参数: @return 输出参数: void
	 * 返回值 : String 备注: N/A
	 ******************************************************************/
	private String getPath(String url) {
		if (url.indexOf('/', 7) == -1) {
			return "/";
		}
		return url.substring(url.indexOf('/', 7));
	}

	/*******************************************************************
	 * 函数名称: getHostAndPath 函数描述: 获取url的主机地址和目录地址 输入参数: @param uri 输入参数: @return
	 * 输出参数: void 返回值 : String[] 备注: N/A
	 ******************************************************************/
	private String[] getHostAndPath(String uri) {
		if (uri != null) {
			String ret[] = new String[2];
			ret[0] = getHost(uri);
			ret[1] = getPath(uri);
			int index = ret[0].indexOf('.');
			if (index == -1) {
				if (uri.substring(0, 4).equalsIgnoreCase("file")) {
					ret[0] = "localhost";
				}
			} else if (index == ret[0].lastIndexOf('.')) {
				ret[0] = (new StringBuffer(String.valueOf('.'))).append(ret[0])
						.toString();
			}
			if (ret[1].charAt(0) != '/') {
				return null;
			}
			index = ret[1].indexOf('?');
			if (index != -1) {
				ret[1] = ret[1].substring(0, index);
			}
			return ret;
		} else {
			return null;
		}
	}

	/*******************************************************************
	 * 函数名称: getBaseDomain 函数描述: 获取主域名 输入参数: @param host 主机地址 输入参数: @return
	 * 输出参数: void 返回值 : String 备注: N/A
	 ******************************************************************/
	private String getBaseDomain(String host) {
		int startIndex = 0;
		int nextIndex = host.indexOf('.');
		for (int lastIndex = host.lastIndexOf('.'); nextIndex < lastIndex; nextIndex = host
				.indexOf('.', startIndex)) {
			startIndex = nextIndex + 1;
		}
		if (startIndex > 0) {
			return host.substring(startIndex);
		} else {
			return host;
		}
	}

	/*******************************************************************
	 * 函数名称: clearCookie 函数描述: 清空cookie 返回值 : void 备注: N/A
	 ******************************************************************/
	public void clearCookie() {
		if (mCookieMap != null) {
			mCookieMap.clear();
		}
	}

}
