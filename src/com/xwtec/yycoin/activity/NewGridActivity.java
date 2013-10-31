/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.array;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xwtec.util.download.ImageObj;
import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.BannerAction;
import com.xwtec.yycoin.action.GridAction;
import com.xwtec.yycoin.action.PriceAction;
import com.xwtec.yycoin.action.UpdateAction;
import com.xwtec.yycoin.info.ImageAndText;
import com.xwtec.yycoin.info.ProductCarInfo;
import com.xwtec.yycoin.widget.BannerLayout;

/**
 * @author liuzixiang
 * 
 */
public class NewGridActivity extends BaseActivity {

	private final static String TAG = NewGridActivity.class.getSimpleName();

	/**
	 * 记录第一次点击返回的时刻
	 */
	private long fristTime = 0;

	/**
	 * 记录第二次点击返回的时刻
	 */
	private long secondTime = 0;

	/**
	 * 退出客户端的间隔时间
	 */
	private final static long TIME_USED = 2000;

	/**
	 * GridView
	 */
	private GridView gridview = null;

	/**
	 * 更多按钮
	 */
	private ImageButton more_btn = null;

	private SimpleAdapter saImageItems;

	private ImageAndTextListAdapter adapter = null;

	List<ImageAndText> imageAndTexts = null;

	private GridAction action = null;

	int currentPage = 1;

	private String aurl = null;

	private UpdateAction updateAction = null;

	private BannerLayout banner;

	private LinearLayout bottomPonitLayout;

	List<Map<String, Object>> bannerlist;
	private Handler bannerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				bannerlist = (List<Map<String, Object>>) msg.obj;
				showBanner(bannerlist);
				break;
			}
		}

	};

	private Handler ponitHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			refreshView();
		}
	};

	private void refreshView() {
		bottomPonitLayout.removeAllViews();
		int childCount = banner.getChildCount();
		int currentScreenIndex = banner.getCurrentScreenIndex();
		for (int i = 0; i < childCount; i++) {
			ImageView image = new ImageView(NewGridActivity.this);
			if (i == currentScreenIndex) {
				image.setBackgroundResource(R.drawable.current_position_select);
			} else {
				image.setBackgroundResource(R.drawable.current_position_unselect);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
			params.leftMargin = 5;
			bottomPonitLayout.addView(image, params);
		}
	}

	private Handler bannerImageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImageObj image = (ImageObj) msg.obj;
			if (msg.what == Constant.DOWNLOAD_IMAGE_FINISH) {
				Log.v(TAG, "DOWNLOAD_IMAGE_FINISH..." + image.getName());
				ImageView imageview = (ImageView) banner.findViewWithTag(image
						.getName());
				Bitmap bm = BitmapFactory.decodeFile(Constant.FILE_PATH
						+ image.getName());
				if (null != bm && null != imageview) {
					imageview.setImageBitmap(bm);
				}
			}
		}

	};

	private void showBanner(List<Map<String, Object>> list) {
		banner.removeAllViews();
		for (Map<String, Object> map : list) {
			ImageView imageView = new ImageView(this);
			Bitmap bm = GetServerImageProcessor.getInstance()
					.getGridBannerImage(this, (String) map.get("image"),
							(String) map.get("id"), bannerImageHandler);
			imageView.setTag("grid_banner_" + (String) map.get("id") + ".jpg");
			imageView.setImageBitmap(bm);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			banner.addView(imageView);
		}
	}

	/**
	 * 下载图片,然后刷新界面
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ImageObj image = (ImageObj) msg.obj;
			if (msg.what == Constant.DOWNLOAD_IMAGE_FINISH) {
				Log.v(TAG, "DOWNLOAD_IMAGE_FINISH..." + image.getName());
				ImageView imageview = (ImageView) gridview
						.findViewWithTag(image.getName());
				Bitmap bm = BitmapFactory.decodeFile(Constant.FILE_PATH
						+ image.getName());
				if (null != bm && null != imageview) {
					imageview.setImageBitmap(bm);
				}
				adapter.notifyDataSetChanged();
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 更新请求
	 */
	private Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int version = Integer.parseInt((String) map.get("version"));
				int currentVersion = getCurrentVersionCode();
				if (version > currentVersion) {
					showUpdateDialog((String) map.get("android"));
				}
				break;
			case Constant.NO_DATA:

				break;
			}
		}

	};

	private void showUpdateDialog(final String url) {
		new AlertDialog.Builder(this).setTitle("升级信息")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				}).setMessage("有新版本哦！点解确定更新！")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	private Handler dataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				List<Map<String, Object>> tempList = (List<Map<String, Object>>) msg.obj;
				ImageAndText imageAndText = null;
				for (Map<String, Object> map : tempList) {
					String id = (String) map.get("aid");
					String tilte = (String) map.get("title");
					String imageUrl = (String) map.get("image");
					Bitmap bitmap = GetServerImageProcessor.getInstance()
							.getGridItemImage(NewGridActivity.this,
									Constant.IMAGE_URL + imageUrl,
									(String) map.get("productno"), handler);
					String purl = (String) map.get("purl");
					String curl = (String) map.get("curl");
					String jg = (String) map.get("jg");
					String gg = (String) map.get("gg");
					String zl = (String) map.get("zl");
					String tc = (String) map.get("tc");
					String cz = (String) map.get("cz");
					imageAndText = new ImageAndText(bitmap, tilte, id, purl,
							curl, jg, gg, zl, tc, cz,
							(String) map.get("banner"),
							(String) map.get("productno"), imageUrl);
					imageAndTexts.add(imageAndText);
				}
				getPrice();
				adapter.notifyDataSetChanged();
				break;
			case Constant.NO_DATA:
				Toast.makeText(NewGridActivity.this, "暂无数据...",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate...");

		setContentView(R.layout.grid_activity);
		banner = (BannerLayout) findViewById(R.id.banner);
		banner.setVisibility(View.VISIBLE);
		banner.setOnItemClickListener(new BannerLayout.OnItemClickListener() {

			public void onClick(int index, View childview) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				Map<String, Object> tempMap = bannerlist.get(index);
				bundle.putSerializable("map", (Serializable) tempMap);
				intent.putExtras(bundle);
				intent.setAction(BannerActivity.class.getName());
				startActivity(intent);
			}
		});
		banner.setPonitHandler(ponitHandler);
		BannerAction action = new BannerAction(bannerHandler);
		action.getMessage("activelistInterface.jsp");
		bottomPonitLayout = (LinearLayout) findViewById(R.id.bottomPonitLayout);
		bottomPonitLayout.setVisibility(View.VISIBLE);
		// 发起一个版本更新的请求
		updateAction = new UpdateAction(updateHandler);
		updateAction.getMessage();
	}

	protected Handler priceHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				for (ImageAndText imageAndText : imageAndTexts) {
					if (Variable.priceMap.containsKey(imageAndText
							.getProductno())) {
						imageAndText.setJg(Variable.priceMap.get(imageAndText
								.getProductno()));
					}
				}
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void getPrice() {
		try {
			if (Variable.isLogin) {
				PriceAction action = new PriceAction(priceHanlder);
				JSONArray array = new JSONArray();
				for (ImageAndText imageAndText : imageAndTexts) {
					JSONObject jsonObj = new JSONObject();
					String productNo = imageAndText.getProductno();
					if (!TextUtils.isEmpty(productNo)) {
						jsonObj.put("productCode", productNo);
						jsonObj.put("productName", URLEncoder.encode(
								imageAndText.getText(), "utf-8"));
						array.put(jsonObj);
					}
				}
				JSONObject o = new JSONObject();
				o.put("products", array);
				o.put("userId", Variable.id);
				action.getMessage("?method=queryPrice&params=" + o.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		imageAndTexts = new ArrayList<ImageAndText>();
		// 根据请求返回数据，展示页面
		gridview = (GridView) findViewById(R.id.grid);
		more_btn = (ImageButton) findViewById(R.id.more_btn);
		// 从网络中请求数据
		currentPage = 1;
		action = new GridAction(dataHandler);
		aurl = "boardArticleNew.jsp?pages=" + currentPage;
		action.getMessage(aurl);
		showTop("首页");
		backBtn.setVisibility(View.GONE);

		more_btn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 刷新界面，进行请求
				currentPage++;
				action.getMessage(aurl + currentPage);
			}
		});

		adapter = new ImageAndTextListAdapter(this, imageAndTexts, gridview);

		// 添加并且显示
		gridview.setAdapter(adapter);
		// 添加消息处理
		gridview.setOnItemClickListener(new ItemClickListener());
	}

	/**
	 * 定义一个View类用于显示一记录中的详细信息
	 * 
	 * @author shiyu
	 * 
	 */
	protected final class ViewHolder {
		public TextView price; // price
		public ImageView tv1;// name
		public TextView tv2;// number
		public ImageButton addcar;
	}

	private class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

		private GridView gridview;

		private List<ImageAndText> imageAndTexts = null;

		public ImageAndTextListAdapter(Activity activity,
				List<ImageAndText> imageAndTexts, GridView gridview) {
			super(activity, 0, imageAndTexts);
			this.imageAndTexts = imageAndTexts;
			this.gridview = gridview;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Activity activity = (Activity) getContext();
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = activity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.grid_item, null);
				holder.tv1 = (ImageView) convertView.findViewById(R.id.image);// 停车场名
				holder.tv2 = (TextView) convertView.findViewById(R.id.text);// 停车场车位
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.addcar = (ImageButton) convertView
						.findViewById(R.id.addcar);
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
				holder.addcar.setTag(msg);
				holder.addcar
						.setOnClickListener(new ImageButton.OnClickListener() {

							@Override
							public void onClick(View v) {
								ImageAndText msg = (ImageAndText) v.getTag();
								if (Variable.isLogin) {
									ProductCarInfo productCarInfo = new ProductCarInfo();
									int productCount = productCarInfo
											.queryByProductID(msg.getId());
									Toast.makeText(NewGridActivity.this,
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

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
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
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				fristTime = System.currentTimeMillis();
				if (fristTime - secondTime < TIME_USED) {
					finish();
				} else {
					Toast.makeText(this, "再次点击退出客户端", Toast.LENGTH_SHORT)
							.show();
					secondTime = System.currentTimeMillis();
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
