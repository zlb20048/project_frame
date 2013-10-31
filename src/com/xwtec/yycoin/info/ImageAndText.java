package com.xwtec.yycoin.info;

import android.graphics.Bitmap;

public class ImageAndText {
	private String imageUrl;
	private String text;
	private String id;
	private String curl;
	private String purl;
	private Bitmap bitmap;
	private String jg;
	private String gg;
	private String zl;
	private String tc;
	private String cz;
	private String banner;
	private String productno;
	private String image;

	public ImageAndText(Bitmap bitmap, String text, String id, String purl,
			String curl, String jg, String gg, String zl, String tc, String cz,
			String banner, String productno, String image) {
		this.bitmap = bitmap;
		this.text = text;
		this.id = id;
		this.curl = curl;
		this.purl = purl;
		this.jg = jg;
		this.gg = gg;
		this.zl = zl;
		this.tc = tc;
		this.cz = cz;
		this.banner = banner;
		this.productno = productno;
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public String getBanner() {
		return banner;
	}

	public String getProductno() {
		return productno;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getCz() {
		return cz;
	}

	public String getJg() {
		return jg;
	}

	public void setJg(String jg) {
		this.jg = jg;
	}
	
	public String getGg() {
		return gg;
	}

	public String getZl() {
		return zl;
	}

	public String getTc() {
		return tc;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getText() {
		return text;
	}

	public String getId() {
		return id;
	}

	public String getCurl() {
		return curl;
	}

	public String getPurl() {
		return purl;
	}

}