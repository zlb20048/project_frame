/**
 * 
 */
package com.xwtec.yycoin.info;

/**
 * @author liuzixiang
 *
 */
public class ShowImageInfo {
	
	private String imageUrl;
	private int id;
	
	
	public ShowImageInfo(String imageUrl, int id) {
		this.imageUrl = imageUrl;
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public int getId() {
		return id;
	}
}
