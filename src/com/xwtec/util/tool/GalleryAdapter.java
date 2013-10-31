/**
 * 
 */
package com.xwtec.util.tool;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

/**
 * @author l
 * 
 */
public class GalleryAdapter extends BaseAdapter {

	private final static String TAG = GalleryAdapter.class.getSimpleName();

	private Context context;

	/**
	 * 图片数组
	 */
	private List<Bitmap> bitmap;

	/**
	 * 图片下载地址
	 */
	private List<String> tag;

	public GalleryAdapter(Context context, List<Bitmap> bitmap, List<String> tag) {
		this.context = context;
		this.bitmap = bitmap;
		Log.v(TAG, "bitmap = " + bitmap.size());
		this.tag = tag;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bitmap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bitmap.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * 获取到当前的下标
	 * 
	 * @return 当前的id
	 */
	public List<String> getUrl() {
		return tag;
	}

	/**
	 * 获取图片列表
	 * 
	 * @return 图片列表
	 */
	public List<Bitmap> getBitmap() {
		return bitmap;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(context);
		Log.v(TAG, "position = " + position);

		i.setImageBitmap(bitmap.get(position));
		i.setScaleType(ImageView.ScaleType.MATRIX);
		i.setTag(tag.get(position));
		i.setAdjustViewBounds(true);
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		// i.setBackgroundResource(R.drawable.e);
		return i;
	}

}
