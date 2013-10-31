/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.info.ImageAndText;
import com.xwtec.yycoin.info.ProductCarInfo;

/**
 * @author Administrator
 * 
 */
public class BannerActivity extends BaseActivity {

	private TextView titleView = null;

	private ListView listView = null;

	private ImageAndTextListAdapter mAdapter = null;

	List<ImageAndText> imageAndTexts = new ArrayList<ImageAndText>();

	private Map<String, Object> dataMap = new HashMap<String, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.banner_activity);
		titleView = (TextView) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.listview);
		Bundle bundle = getIntent().getExtras();
		dataMap = (Map<String, Object>) bundle.get("map");
		showTop(dataMap.get("name").toString());
		titleView.setText(dataMap.get("desc").toString());
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.setAction(ProductDetailActivity.class.getName());
				ImageAndText imageAndText = imageAndTexts.get(arg2);
				// 显示所选Item的ItemText
				Bundle bundle = new Bundle();
				bundle.putString("id", imageAndText.getId());
				bundle.putString("purl", imageAndText.getPurl());
				bundle.putString("curl", imageAndText.getCurl());
				bundle.putString("title", imageAndText.getText());
				bundle.putString("jg", imageAndText.getJg());
				bundle.putString("gg", imageAndText.getGg());
				bundle.putString("zl", imageAndText.getZl());
				bundle.putString("tc", imageAndText.getTc());
				bundle.putString("cz", imageAndText.getCz());
				bundle.putString("banner", imageAndText.getBanner());
				bundle.putString("productno", imageAndText.getProductno());
				bundle.putString("image", imageAndText.getImage());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		ImageAndText imageAndText = null;
		List<Map<String, Object>> tempList = (List<Map<String, Object>>) dataMap
				.get("productes");

		for (Map<String, Object> map : tempList) {
			String id = (String) map.get("aid");
			String tilte = (String) map.get("title");
			String imageUrl = (String) map.get("image");
			Bitmap bitmap = GetServerImageProcessor.getInstance()
					.getGridItemImage(BannerActivity.this,
							Constant.IMAGE_URL + imageUrl,
							(String) map.get("productno"), null);
			String purl = (String) map.get("purl");
			String curl = (String) map.get("curl");
			String jg = (String) map.get("jg");
			String gg = (String) map.get("gg");
			String zl = (String) map.get("zl");
			String tc = (String) map.get("tc");
			String cz = (String) map.get("cz");
			imageAndText = new ImageAndText(bitmap, tilte, id, purl, curl, jg,
					gg, zl, tc, cz, (String) map.get("banner"),
					(String) map.get("productno"), imageUrl);
			imageAndTexts.add(imageAndText);
		}

		mAdapter = new ImageAndTextListAdapter(this, imageAndTexts);
		listView.setAdapter(mAdapter);
	}

	protected final class ViewHolder {
		public TextView price; // price
		public ImageView tv1;// name
		public TextView tv2;// number
		public ImageButton image;
	}

	private class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

		private List<ImageAndText> imageAndTexts = null;

		// private AsyncImageLoader asyncImageLoader;

		public ImageAndTextListAdapter(Activity activity,
				List<ImageAndText> imageAndTexts) {
			super(activity, 0, imageAndTexts);
			this.imageAndTexts = imageAndTexts;
			// asyncImageLoader = new AsyncImageLoader();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Activity activity = (Activity) getContext();
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = activity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.banner_item, null);
				holder.tv1 = (ImageView) convertView.findViewById(R.id.image);// 停车场名
				holder.tv2 = (TextView) convertView.findViewById(R.id.text);// 停车场车位
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.image = (ImageButton) convertView
						.findViewById(R.id.addCar);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (null != imageAndTexts && !imageAndTexts.isEmpty()) {
				ImageAndText msg = imageAndTexts.get(position);

				holder.tv1.setTag("grid_" + msg.getProductno() + ".jpg");
				holder.tv1.setImageBitmap(msg.getBitmap());
				holder.tv2.setText(msg.getText());
				holder.price.setText(msg.getJg() + "￥");
				holder.image.setTag(msg);
				holder.image
						.setOnClickListener(new ImageButton.OnClickListener() {

							@Override
							public void onClick(View v) {
								ImageAndText msg = (ImageAndText) v.getTag();
								if (Variable.isLogin) {
									ProductCarInfo productCarInfo = new ProductCarInfo();
									int productCount = productCarInfo
											.queryByProductID(msg.getId());
									Toast.makeText(BannerActivity.this,
											"已添加到购入车！", Toast.LENGTH_LONG)
											.show();
									if (productCount == 0) {
										// 暂时不做处理，可能需要给出相应的提示信息
										productCarInfo.insert(msg.getId(),
												msg.getText(), msg.getJg(),
												msg.getCz(), msg.getGg(),
												msg.getZl(), msg.getTc(), 1,
												msg.getProductno(),
												msg.getImageUrl());
									} else {
										productCount = productCount + 1;
										productCarInfo.update(
												msg.getProductno(),
												productCount);
									}
								} else {
									Intent intent = new Intent();
									intent.setAction(LoginActivity.class
											.getName());
									startActivity(intent);
								}
							}
						});
			}
			return convertView;
		}
	}
}
