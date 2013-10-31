/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.xwtec.yycoin.activity.NewGridActivity.ViewHolder;
import com.xwtec.yycoin.info.CollectionInfo;
import com.xwtec.yycoin.info.ImageAndText;
import com.xwtec.yycoin.info.ProductCarInfo;
import com.xwtec.yycoin.widget.BannerLayout;

/**
 * @author liuzixiang
 * 
 */
public class GridActivity extends BaseActivity {

	/**
	 * 标识
	 */
	private final static String TAG = GridActivity.class.getSimpleName();

	/**
	 * GridView
	 */
	private GridView gridview = null;

	/**
	 * 更多按钮
	 */
	private ImageButton more_btn = null;

	private ImageButton backBtn = null;

	private SimpleAdapter saImageItems;

	private ImageAndTextListAdapter adapter = null;

	List<ImageAndText> imageAndTexts = null;

	private GridAction action = null;

	String tilte;

	String purl;

	String curl;

	boolean isCollection;

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
							.getGridItemImage(GridActivity.this,
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
				Toast.makeText(GridActivity.this, "暂无数据...", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		}

	};

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
			}
			super.handleMessage(msg);
		}

	};

	private String saveData;

	private String content;
	private String jg;
	private String gg;
	private String zl;
	private String tc;
	private String cz;

	/**
	 * 搜索字符转换
	 */
	private RelativeLayout searchTypeLayout = null;

	/**
	 * 搜索的类型
	 */
	private TextView searchType = null;

	ImageButton button;

	TextView editText = null;

	Button search_btn;

	int currentPage = 1;
	boolean isSearch;

	String aurl;

	String searchUrl;

	private BannerLayout banner;

	private LinearLayout bottomPonitLayout;

	private Handler bannerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Log.v(TAG, "CONNECT_SUCCRESS...");
				break;
			}
		}

	};

	private void showBanner() {
		banner.removeAllViews();
	}

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = getIntent();
		imageAndTexts = new ArrayList<ImageAndText>();
		final Bundle bundle = intent.getExtras();
		aurl = bundle.getString("aurl");
		action = new GridAction(dataHandler);
		// 根据请求返回数据，展示页面
		gridview = (GridView) findViewById(R.id.grid);
		more_btn = (ImageButton) findViewById(R.id.more_btn);
		search_btn = (Button) findViewById(R.id.search_btn);
		ImageAndText imageAndText = null;
		isCollection = intent.getBooleanExtra("isColletion", false);
		if (isCollection) {
			// 不需要请求数据
			CollectionInfo info = new CollectionInfo();
			List<Map<String, String>> list = info.queryAllCollectionInfo();
			for (Map<String, String> map : list) {
				String id = (String) map.get("collectionID");
				Log.v(TAG, ".........." + id);
				tilte = (String) map.get("title");
				purl = map.get("purl");
				curl = map.get("curl");
				saveData = map.get("saveData");
				content = map.get("content");
				jg = map.get("jg");
				gg = map.get("gg");
				cz = map.get("zl");
				tc = map.get("tc");
				// cz = map.get("cz");

				Bitmap bitmap = GetServerImageProcessor.getInstance()
						.getGridItemImage(GridActivity.this, null,
								map.get("productno"), handler);
				imageAndText = new ImageAndText(bitmap, tilte, id, purl, curl,
						jg, gg, zl, tc, cz, map.get("banner"),
						map.get("productno"), map.get("image"));
				imageAndTexts.add(imageAndText);
			}
		} else {
			// 从网络中请求数据
			currentPage = 1;
			isSearch = bundle.getBoolean("isSearch");
			if (isSearch) {
				String t_1 = bundle.getString("text_1_value");
				String t_2_1 = bundle.getString("text_2_1_value");
				String t_2_2 = bundle.getString("text_2_2_value");
				String t_3 = bundle.getString("text_3_value");
				String t_4 = bundle.getString("text_4_value");
				String t_5 = bundle.getString("text_5_value");
				String t_6 = bundle.getString("text_6_value");
				String t_7 = bundle.getString("text_7_value");
				searchUrl = "boardArticleSearch.jsp?1=1";
				try {
					if (null != t_1 && !"".equals(t_1)) {
						searchUrl = searchUrl + "&title="
								+ URLEncoder.encode(t_1, "GBK");
					}
					if (null != t_2_1 && !"".equals(t_2_1)) {
						searchUrl = searchUrl + "&jg_min="
								+ t_2_1;
					}
					if (null != t_2_2 && !"".equals(t_2_2)) {
						searchUrl = searchUrl + "&jg_max="
								+ t_2_2;
					}
					if (null != t_3 && !"".equals(t_3) && !"全部".equals(t_3)) {
						searchUrl = searchUrl + "&tc="
								+ URLEncoder.encode(t_3, "GBK");
					}
					if (null != t_4 && !"".equals(t_4) && !"全部".equals(t_4)) {
						searchUrl = searchUrl + "&cz="
								+ URLEncoder.encode(t_4, "GBK");
					}
					if (null != t_5 && !"".equals(t_5) && !"全部".equals(t_5)) {
						searchUrl = searchUrl + "&kh="
								+ URLEncoder.encode(t_5, "GBK");
					}
					if (null != t_6 && !"".equals(t_6) && !"全部".equals(t_6)) {
						searchUrl = searchUrl + "&xy="
								+ URLEncoder.encode(t_6, "GBK");
					}
					if (null != t_7 && !"".equals(t_7)) {
						searchUrl = searchUrl + "&yt="
								+ URLEncoder.encode(t_7, "GBK");
					}

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showProgessDialog();
				action.getMessage(searchUrl + "&pages=" + currentPage);
			} else {
				showProgessDialog();
				action.getMessage(aurl + "&pages=" + currentPage);
			}
		}

		showTop("产品展示");

		more_btn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 刷新界面，进行请求
				showProgessDialog();
				currentPage++;
				if (isSearch) {
					action.getMessage(searchUrl + "&pages=" + currentPage);
				} else {
					action.getMessage(aurl + "&pages=" + currentPage);
				}
			}
		});

		adapter = new ImageAndTextListAdapter(this, imageAndTexts, gridview);

		// 添加并且显示
		gridview.setAdapter(adapter);
		// 添加消息处理
		gridview.setOnItemClickListener(new ItemClickListener());

		searchTypeLayout = (RelativeLayout) findViewById(R.id.r1);
		searchType = (TextView) findViewById(R.id.showType);
		button = (ImageButton) findViewById(R.id.searchBtn);
		editText = (TextView) findViewById(R.id.searchEdt);

		search_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GridActivity.this, SearchActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(GridActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

				String searchText = editText.getText().toString();
				// 进行搜索请求，后期需要完善，暂时不知道请求的地址以及参数是什么
			}
		});

		searchTypeLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(TAG, "searchTypeLayout: setOnClickListener");

				new AlertDialog.Builder(GridActivity.this)
						.setTitle("选择搜索类型")
						.setItems(R.array.search_type,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										final String[] searchTypeText = getResources()
												.getStringArray(
														R.array.search_type);
										searchType
												.setText(searchTypeText[which]);
									}
								}).show();
			}
		});
	}

	/**
	 * 定义一个View类用于显示一记录中的详细信息
	 * 
	 * @author shiyu
	 * 
	 */
	protected final class ViewHolder {
		public ImageView tv1;// name
		public TextView tv2;// number
		public TextView price;
		public ImageButton addcar;
	}

	private class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

		private GridView gridview;

		private List<ImageAndText> imageAndTexts = null;

		// private AsyncImageLoader asyncImageLoader;

		public ImageAndTextListAdapter(Activity activity,
				List<ImageAndText> imageAndTexts, GridView gridview) {
			super(activity, 0, imageAndTexts);
			this.imageAndTexts = imageAndTexts;
			this.gridview = gridview;
			// asyncImageLoader = new AsyncImageLoader();
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
									Toast.makeText(GridActivity.this,
											"已添加到购入车！", Toast.LENGTH_LONG)
											.show();
									ProductCarInfo productCarInfo = new ProductCarInfo();
									productCarInfo.insert(msg.getId(),
											msg.getText(), msg.getJg(),
											msg.getCz(), msg.getGg(),
											msg.getZl(), msg.getTc(), 1,
											msg.getProductno(),
											msg.getImageUrl());
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
			bundle.putBoolean("isCollection", isCollection);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				// 跳转到主界面
				if (isCollection) {
					RadioButton rb = (RadioButton) BottomTabAcitivity.radioGroup
							.getChildAt(0);
					rb.setChecked(true);
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
