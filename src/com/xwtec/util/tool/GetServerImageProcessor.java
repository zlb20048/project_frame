package com.xwtec.util.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.xwtec.util.download.ImageDownload;
import com.xwtec.yycoin.R;

/**
 * 
 * @author shifeng
 * 
 */
public class GetServerImageProcessor {

	private static GetServerImageProcessor instance = null;

	private GetServerImageProcessor() {
	}

	public static synchronized GetServerImageProcessor getInstance() {
		if (instance == null) {
			instance = new GetServerImageProcessor();
		}
		return instance;
	}

	/**
	 * 获取底部的菜单图片
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public Bitmap getShowImage(Context context, String imageUrl, String id,
			Handler handler) {
		Bitmap bitmap = BitmapFactory.decodeFile(Constant.FILE_PATH + "show_"
				+ id + ".png");
		if (null == bitmap) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.image_default);
			// 从数据库中查询到相应的数据
			new ImageDownload(context).getImg(imageUrl, "show_" + id + ".png",
					handler);
		}
		return bitmap;
	}

	/**
	 * 广告图标
	 * 
	 * @param context
	 * @param bid
	 * @return
	 */
	public Bitmap getGridItemImage(Context context, String address,
			String pk_id, Handler handler) {
		Log.v("pk_id", "pk_id = " + pk_id);
		Bitmap bitmap = BitmapFactory.decodeFile(Constant.FILE_PATH + "grid_"
				+ pk_id + ".jpg");

		if (null == bitmap) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.image_default);
			if (address != null) {
				String imgUrl = address;
				new ImageDownload(context).getImg(imgUrl, "grid_" + pk_id
						+ ".jpg", handler);
			}
		}
		return bitmap;
	}

	/**
	 * 广告图标
	 * 
	 * @param context
	 * @param bid
	 * @return
	 */
	public Bitmap getBannerImage(Context context, String address, String pk_id,
			Handler handler) {
		Bitmap bitmap = BitmapFactory.decodeFile(Constant.FILE_PATH + "banner_"
				+ pk_id + ".jpg");

		if (null == bitmap) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.banner_default);
			if (address != null) {
				String imgUrl = Constant.IMAGE_URL + address;
				new ImageDownload(context).getImg(imgUrl, "banner_" + pk_id
						+ ".jpg", handler);
			}
		}
		return bitmap;
	}

	/**
	 * 广告图标
	 * 
	 * @param context
	 * @param bid
	 * @return
	 */
	public Bitmap getGridBannerImage(Context context, String address,
			String pk_id, Handler handler) {
		Bitmap bitmap = BitmapFactory.decodeFile(Constant.FILE_PATH
				+ "grid_banner_" + pk_id + ".jpg");

		if (null == bitmap) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.banner_default);
			if (address != null) {
				String imgUrl = Constant.IMAGE_URL + address;
				new ImageDownload(context).getImg(imgUrl, "grid_banner_" + pk_id
						+ ".jpg", handler);
			}
		}
		return bitmap;
	}

	/**
	 * 广告图标
	 * 
	 * @param context
	 * @param bid
	 * @return
	 */
	public Bitmap getListItemImage(Context context, String address,
			String pk_id, Handler handler) {
		Bitmap bitmap = BitmapFactory.decodeFile(Constant.FILE_PATH
				+ "list_item_" + pk_id + ".jpg");

		if (null == bitmap) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.image_default);
			if (address != null) {
				String imgUrl = address;
				new ImageDownload(context).getImg(imgUrl, "list_item_" + pk_id
						+ ".jpg", handler);
			}
		}
		return bitmap;
	}
}
