/*
 * 文 件 名:  HttpListener.java
 * 描    述:  HTTP请求观察类

 * 修 改 人:  y00109551
 * 修改时间:  2009-2-5
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.xwtec.util.http;

import java.io.UnsupportedEncodingException;

import com.xwtec.util.json.JSONException;

/**
 * HTTP请求观察类，实现该接口的类能够观察到HTTP请求发送的情况
 * 
 * 
 * @author y00109551
 * @version [版本号, 2009-2-5]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface HttpListener
{
    /**
     * 设置请求报文长度，不一定准确
     * 
     * @param size size
     */
    void onSetSize(int size);

    /**
     * HTTP请求处理成功时调用该方法
     * 
     * @param data data
     * @throws JSONException 
     * @throws UnsupportedEncodingException 
     */
    void onFinish(byte[] data) throws UnsupportedEncodingException, JSONException;

    /**
     * 处理进度的百分比，100为上限
     * 
     * @param percent percent
     */
    void onProgress(int percent);

    /**
     * HTTP请求异常时会调用该方法
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    void onError(int code, String message);
}
