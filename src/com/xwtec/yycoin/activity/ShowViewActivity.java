/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xwtec.yycoin.R;
import com.xwtec.yycoin.widget.ViewOnThouchListener;

/**
 * @author liuzixiang
 * 
 */
public class ShowViewActivity extends BaseActivity {

	private final static String TAG = ShowViewActivity.class.getSimpleName();

	private ImageView imageview = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);

		Bundle bundle = getIntent().getExtras();
		List<Bitmap> list = ImageSwitcherActivity.bitmaps;
		int position = bundle.getInt("position");

		Log.v(TAG, "position..." + position);

		Bitmap bitmap = list.get(position);

		Log.v(TAG, "bitmap..." + bitmap);

		imageview = (ImageView) findViewById(R.id.imageview);
		imageview.setImageBitmap(bitmap);
		ViewOnThouchListener listener = new ViewOnThouchListener(imageview);
		listener.setListener(new ViewOnThouchListener.BackListener() {

			@Override
			public void back() {
				finish();
			}
		});
		imageview.setOnTouchListener(listener);
		imageview.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
