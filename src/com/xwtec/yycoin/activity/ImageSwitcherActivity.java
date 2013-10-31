package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.xwtec.util.download.ImageObj;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GalleryAdapter;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.yycoin.R;

public class ImageSwitcherActivity extends Activity implements
		OnItemSelectedListener, ViewFactory, OnItemClickListener {
	// private ImageSwitcher is;
	private Gallery gallery;

	private final static String TAG = ImageSwitcherActivity.class
			.getSimpleName();

	private Integer[] mImageIds = { R.drawable.b, R.drawable.c, R.drawable.d,
			R.drawable.f, R.drawable.g, };

	public static List<Bitmap> bitmaps = null;

	List<String> tag = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageswitcherpage);

		Bundle bundle = getIntent().getExtras();
		List<Map<String, Object>> list = (List<Map<String, Object>>) bundle
				.getSerializable("list");

		// is = (ImageSwitcher) findViewById(R.id.switcher);
		// is.setFactory(this);

		// is.setInAnimation(AnimationUtils.loadAnimation(this,
		// android.R.anim.fade_in));
		// is.setOutAnimation(AnimationUtils.loadAnimation(this,
		// android.R.anim.fade_out));

		gallery = (Gallery) findViewById(R.id.gallery);

		bitmaps = new ArrayList<Bitmap>();
		tag = new ArrayList<String>();

		// 注：此处为获取图片信息的地方，
		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> map : list) {

				Bitmap bm = GetServerImageProcessor.getInstance().getShowImage(
						this, Constant.IMAGE_URL + map.get("imageUrl"),
						map.get("id").toString(), handler);
				bitmaps.add(bm);
				tag.add("show_" + map.get("id") + ".png");
			}
		}

		gallery.setAdapter(new GalleryAdapter(this, bitmaps, tag));
		gallery.setOnItemSelectedListener(this);
		gallery.setOnItemClickListener(this);
	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.DOWNLOAD_IMAGE_FINISH:
				ImageObj image = (ImageObj) msg.obj;
				Log.v(TAG, "DOWNLOAD_IMAGE_FINISH...");
				ImageView imageview = (ImageView) gallery.findViewWithTag(image
						.getName());
				Bitmap bm = BitmapFactory.decodeFile(Constant.FILE_PATH
						+ image.getName());
				Log.v(TAG, "image.getName() = " + image.getName());
				if (null != imageview) {
					imageview.setImageBitmap(bm);
				}
				break;
			case Constant.CONNECT_SUCCRESS:

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Log.v(TAG, "tag.get(position) = " + tag.get(position));
		// Drawable drawable = Drawable.createFromPath(Constant.FILE_PATH
		// + tag.get(position));
		// Log.v(TAG, "drawable = " + drawable);
		// is.setImageDrawable(drawable);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// bundle.putSerializable("list", (Serializable) bitmaps);
		bundle.putInt("position", position);
		intent.putExtras(bundle);
		intent.setClass(this, ShowViewActivity.class);
		startActivity(intent);
	}
}
