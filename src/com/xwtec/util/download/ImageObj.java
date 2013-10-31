package com.xwtec.util.download;

import android.os.Handler;

/**
 * 
 * @author shifeng
 *
 */
public class ImageObj {
	
	private byte[] img = null;
	
	private String name = null;
	
	private String url = null;
	
	private Handler handler = null;
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
