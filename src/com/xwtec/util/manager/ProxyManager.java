/**
 * 
 */
package com.xwtec.util.manager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;

import android.util.Log;

import com.xwtec.util.tool.Constant;

/**
 * @author liuzixiang
 * 
 */
public class ProxyManager {
	
	/**
	 * 标记位
	 */
	private static final String TAG = "ProxyManager";
	
	/**
	 * 是否已经初始化proxy
	 */
	private boolean proxyInitialized = false;

	/**
	 * 实例对象
	 */
	private static ProxyManager instance = null;
	
	 /**
     * 代理
     */
    private Proxy proxy;
	
	/**
	 * 私有构造方法
	 */
	private ProxyManager() {

	}

	/**
	 * 获取实例对象
	 * 
	 * @return 得到对象
	 */
	public static synchronized ProxyManager getInstance() {
		if (instance == null) {
			instance = new ProxyManager();
		}
		return instance;
	}
	
	/**
	 * 是否已经初始化过了
	 * 
	 * @return true false
	 */
	public boolean isProxyInitialized() {
		return proxyInitialized;
	}

	 /**
     * 初始化代理<BR>
     * [功能详细描述]
     */
    public void initProxy()
    {
        Log.i(TAG, "initProxy");
        InetAddress addr = null;

        // 默认port为80
        int port = 80;
        try
        {
            addr = InetAddress.getByName(Constant.WAP_ADDRESS);
            port = Integer.parseInt(Constant.WAP_PORT);
        }
        catch (Exception e)
        {
            Log.e(TAG, "initPorxy error:" + e.toString());
            proxy = null;
            proxyInitialized = true;
            return;
        }

        InetSocketAddress sa = new InetSocketAddress(addr, port);
        proxy = new Proxy(Proxy.Type.HTTP, sa);
        proxyInitialized = true;
    }
    
    /**
     * 获取Proxy
     * 
     * @return Proxy
     * @see [类、类#方法、类#成员]
     */
    public Proxy getProxy()
    {
//        return proxy;
    	return null;
    }
}
