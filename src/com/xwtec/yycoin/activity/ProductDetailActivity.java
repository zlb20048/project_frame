/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xwtec.util.download.ImageDownload;
import com.xwtec.util.download.ImageObj;
import com.xwtec.util.json.JSONArray;
import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.GetServerImageProcessor;
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.ImageAction;
import com.xwtec.yycoin.action.ProductDetailAction;
import com.xwtec.yycoin.info.CollectionInfo;
import com.xwtec.yycoin.info.ProductCarInfo;

/**
 * @author liuzixiang
 * 
 */
public class ProductDetailActivity extends BaseActivity {

	private final static String TAG = ProductDetailActivity.class
			.getSimpleName();

	private RadioGroup rg = null;

	private WebView webview = null;

	private WebSettings settings;

	private ImageAction imageaction = null;

	private ProductDetailAction detailAction = null;

	private String detail;

	private String saveData = null;

	private ImageView image = null;

	private TextView price = null;

	// private TextView ggtitle = null;

	private TextView gltitle = null;

	private TextView tctitle = null;

	private int collectCount;

	private CollectionInfo collectionInfo;

	private int productCount;

	private ProductCarInfo productCarInfo;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private Handler imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				saveData = (String) msg.obj;
				try {
					JSONArray jsonarray = new JSONArray(saveData);
					for (int i = 0; i < jsonarray.length(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						JSONObject json = jsonarray.getJSONObject(i);
						String id = json.getString("id");
						map.put("id", id);
						String imageUrl = json.getString("images");
						map.put("imageUrl", imageUrl);
						new ImageDownload(ProductDetailActivity.this).getImg(
								Constant.IMAGE_URL + imageUrl, "show_" + id
										+ ".png", null);
						list.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
		}
	};

	private Handler imageHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.DOWNLOAD_IMAGE_FINISH:
				ImageObj image = (ImageObj) msg.obj;
				Log.v(TAG, "DOWNLOAD_IMAGE_FINISH...");
				Bitmap bm = BitmapFactory.decodeFile(Constant.FILE_PATH
						+ image.getName());
				ProductDetailActivity.this.image.setImageBitmap(bm);
				break;
			case Constant.CONNECT_SUCCRESS:

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler detailHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				detail = (String) msg.obj;
				Log.v(TAG, "detail = " + detail);
				// webview.loadData(detail, "", "gbk");
				webview.loadDataWithBaseURL("http://221.226.19.219:8080/",
						detail, "", "utf-8", "");
				break;
			case 11:
				detailAction = new ProductDetailAction(detailHandler);
				detailAction.getMessage(curl);
				break;
			default:
				break;
			}
		}
	};

	private boolean isCollection;

	private String curl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xwtec.yycoin.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate...");
		setContentView(R.layout.product_detail_activity);

		showTop("详情");

		image = (ImageView) findViewById(R.id.image);
		price = (TextView) findViewById(R.id.price);
		// ggtitle = (TextView) findViewById(R.id.ggtitle);
		gltitle = (TextView) findViewById(R.id.gltitle);
		tctitle = (TextView) findViewById(R.id.tctitle);

		webview = (WebView) findViewById(R.id.webview);
		rg = (RadioGroup) findViewById(R.id.rg_main_btns);
		// webview.loadUrl("http://www.baidu.com");
		settings = webview.getSettings();
		settings.setDefaultFontSize(18);

		Bundle bundle = getIntent().getExtras();
		isCollection = bundle.getBoolean("isCollection");
		final String purl = bundle.getString("purl");
		final String banner = bundle.getString("banner");
		curl = bundle.getString("curl");
		final String id = bundle.getString("id");
		final String title = bundle.getString("title");
		final String jg = bundle.getString("jg");
		final String gg = bundle.getString("gg");
		final String zl = bundle.getString("zl");
		final String cz = bundle.getString("cz");
		final String tc = bundle.getString("tc");
		final String productno = bundle.getString("productno");
		final String imageurl = bundle.getString("image");

		price.setText(jg + " 元");
		// ggtitle.setText(gg);
		gltitle.setText(cz);
		tctitle.setText(tc);

		Bitmap bm = GetServerImageProcessor.getInstance().getBannerImage(this,
				banner, id, imageHandler1);
		image.setImageBitmap(bm);

		// 不发请求
		if (isCollection) {
			CollectionInfo info = new CollectionInfo();
			Map<String, String> map = info.queryByCollectionByID(id);
			Message message = new Message();
			message.what = Constant.CONNECT_SUCCRESS;
			message.obj = map.get("content");
			detailHandler.sendMessage(message);
			Message message1 = new Message();
			message1.what = Constant.CONNECT_SUCCRESS;
			message1.obj = map.get("saveData");
			imageHandler.sendMessage(message1);
		} else {
			imageaction = new ImageAction(imageHandler);
			imageaction.getMessage(purl);
			detailHandler.sendEmptyMessage(11);
		}

		collectionInfo = new CollectionInfo();
		collectCount = collectionInfo.queryByCollectionID(id);
		if (collectCount > 0) {
			RadioButton radio = (RadioButton) rg.getChildAt(3);
			radio.setChecked(true);
		}

		// 请求回来的时候就要进行图片下载，当收藏的时候就将下载的图片也放到数据库中，方便下次使用，这边进行数据请求
		// new ImageDownload(this).getImg("url", "show_" + "id" + ".png", null);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radio;
				switch (checkedId) {
				case R.id.tab_message:
					// 跳转到图片展示界面
					Intent intent = new Intent();
					intent.setAction("com.xwtec.yycoin.activity.ImageSwitcherActivity");
					Bundle bundle = new Bundle();
					bundle.putSerializable("list", (Serializable) list);
					intent.putExtras(bundle);
					startActivity(intent);
					radio = (RadioButton) rg.getChildAt(0);
					radio.setChecked(false);
					break;
				// case R.id.tab_map:
				// Log.v(TAG,
				// "settings.getDefaultFontSize() = "
				// + settings.getDefaultFontSize());
				// if (settings.getDefaultFontSize() == 20) {
				// settings.setDefaultFixedFontSize(25);
				// } else if (settings.getDefaultFontSize() == 25) {
				// settings.setDefaultFixedFontSize(30);
				// } else {
				// settings.setDefaultFixedFontSize(20);
				// }
				// webview.loadUrl("javascript:document.getElementsByTagName('body')[0].style.webkitTextSizeAdjust=%60");
				// // webview.reload();
				// radio = (RadioButton) rg.getChildAt(1);
				// radio.setChecked(false);
				// break;
				case R.id.tab_say:
					intent = new Intent();
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
					intent.putExtra(Intent.EXTRA_TEXT, "永银，有你喜欢的收藏，欢迎前来购买");
					startActivity(Intent.createChooser(intent, getTitle()));
					radio = (RadioButton) rg.getChildAt(1);
					radio.setChecked(false);
					break;
				case R.id.tab_car:
					if (Variable.isLogin) {
						productCarInfo = new ProductCarInfo();
						productCount = productCarInfo.queryByProductID(id);
						Toast.makeText(ProductDetailActivity.this, "已添加到购入车！",
								Toast.LENGTH_LONG).show();
						if (productCount == 0) {
							// 暂时不做处理，可能需要给出相应的提示信息
							productCarInfo.insert(id, title, jg, cz, gg, zl,
									tc, 1, productno, imageurl);
						} else {
							productCount = productCount + 1;
							productCarInfo.update(productno, productCount);
						}
					} else {
						Intent intent1 = new Intent();
						intent1.setAction(LoginActivity.class.getName());
						startActivity(intent1);
					}
					radio = (RadioButton) rg.getChildAt(2);
					radio.setChecked(false);
					break;
				case R.id.tab_discuss:
					// 收藏，将数据收藏到数据库中，通过解析数据库得到值，先根据得到的模块ID来进行查询，看看是否已经收藏

					if (collectCount > 0) {
						// 暂时不做处理，可能需要给出相应的提示信息
						collectionInfo.delete(id);
						collectCount = collectionInfo.queryByCollectionID(id);
						Toast.makeText(ProductDetailActivity.this, "删除收藏成功！",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ProductDetailActivity.this, "您已收藏成功！",
								Toast.LENGTH_LONG).show();
						collectionInfo
								.insert(detail, id, title, saveData, purl,
										curl, jg, gg, cz, tc, productno,
										imageurl);
					}

					radio = (RadioButton) rg.getChildAt(3);
					radio.setChecked(false);
					break;
				default:
					break;
				}
			}
		});
	}
}
