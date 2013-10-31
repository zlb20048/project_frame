/*
 * 文 件 名: Constant.java 版 权: Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,
 * All rights reserved 描 述: <描述> 修 改 人: y00109551 修改时间: 2009-6-29 跟踪单号: <跟踪单号>
 * 修改单号: <修改单号> 修改内容: <修改内容>
 */
package com.xwtec.util.tool;

/**
 * 常量类
 * 
 * @author y00109551
 * @version [版本号, 2009-6-29]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface Constant {
	// ------------应用配置相关参数
	String FILE_PATH = "/data/data/com.xwtec.yycoin/files/";

//	String SERVER_URL = "http://115.28.15.119:8080/cps_nwu/interface/";
	 String SERVER_URL = "http://221.226.19.219:8080/cps_nwu/interface/";
	// String SERVER_URL = "http://192.168.8.35:8080/cps_nwu/interface/";

	String NEW_SERVER_URL = "http://221.226.19.219:8899/uportal/openService/appsService.do";

	String IMAGE_URL = "http://221.226.19.219:8080/cps_nwu/";
	// String IMAGE_URL = "http://192.168.8.35:8080/cps_nwu/";

	String TABLE_NAME = "t_collection";

	String PRODUCT_CAR = "t_product";

	int DOWNLOAD_IMAGE_FINISH = 300;

	int CONNECT_SUCCRESS = 200;

	int NO_DATA = 501;

	/**
	 * 超时时间
	 */
	int CONNECT_OUT_TIME = 30 * 1000;

	/**
	 * wrap地址
	 */
	String WAP_ADDRESS = "127.0.0.1";

	/**
	 * 端口号
	 */
	String WAP_PORT = "80";

	/**
	 * contentType类型 json
	 */
	public final static String HTTP_CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";

	/**
	 * contentType类型 text
	 */
	public final static String HTTP_CONTENT_TYPE_TEXT_PLANE = "text/plain; charset=utf-8";

	/**
	 * contentType类型 application
	 */
	public final static String HTTP_CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	/**
	 * 下载图片
	 */
	public final static String DOWN_LOAD_IMAGE = "download_image";
}
