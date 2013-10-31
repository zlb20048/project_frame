/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.xwtec.util.json.JSONException;
import com.xwtec.util.json.JSONObject;
import com.xwtec.util.tool.Constant;
import com.xwtec.util.tool.MD5;
import com.xwtec.util.tool.Util;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.RegisterAction;

/**
 * @author Administrator
 * 
 */
public class RegisterActivity extends BaseActivity {

	private EditText name_input;

	private EditText email_input;

	private EditText password_input;

	private EditText sure_password_input;

	private EditText true_name_input;

	private EditText phone_input;

	private EditText address_input;

	private ImageButton regestor_btn;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int retCode = (Integer) map.get("retCode");
				if (retCode == 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setMessage("注册成功");
					builder.setPositiveButton(R.string.sure_btn_str,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					String retMsg = (String) map.get("retMsg");
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setMessage(retMsg);
					builder.setPositiveButton(R.string.sure_btn_str, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regeistor_activity);
		showTop("注册");
		initView();
	}

	private void initView() {
		name_input = (EditText) findViewById(R.id.name_input);
		email_input = (EditText) findViewById(R.id.email_input);
		password_input = (EditText) findViewById(R.id.password_input);
		sure_password_input = (EditText) findViewById(R.id.sure_password_input);
		true_name_input = (EditText) findViewById(R.id.true_name_input);
		phone_input = (EditText) findViewById(R.id.phone_input);
		address_input = (EditText) findViewById(R.id.address_input);
		regestor_btn = (ImageButton) findViewById(R.id.regestor_btn);

		regestor_btn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {

				sendRegistorRq();
				if (TextUtils.isEmpty(name_input.getText().toString())) {
					showSureDialog(getString(R.string.user_name_not_be_null));
					return;
				}
				if (TextUtils.isEmpty(email_input.getText().toString())) {
					showSureDialog(getString(R.string.email_not_be_null));
					return;
				}

				if (!Util.checkEmail(email_input.getText().toString())) {
					showSureDialog("请输入正确的邮件地址");
					return;
				}

				if (TextUtils.isEmpty(password_input.getText().toString())
						|| password_input.getText().toString().length() < 8) {
					showSureDialog(getString(R.string.pwd_leng_not_right));
					return;
				}
				if (password_input.getText().toString()
						.equals(sure_password_input.getText().toString())) {
					showSureDialog(getString(R.string.sure_pwd_leng_not_right));
					return;
				}
				if (TextUtils.isEmpty(true_name_input.getText().toString())) {
					showSureDialog(getString(R.string.user_name_not_be_null));
					return;
				}
				if (TextUtils.isEmpty(phone_input.getText().toString())) {
					showSureDialog(getString(R.string.user_name_not_be_null));
					return;
				}

				if (!Util.isMobileNO(phone_input.getText().toString())) {
					showSureDialog("请输入正确的手机号码");
					return;
				}

				if (TextUtils.isEmpty(address_input.getText().toString())) {
					showSureDialog(getString(R.string.user_name_not_be_null));
					return;
				}

			}
		});
	}

	private void sendRegistorRq() {
		showProgessDialog();
		// 发送请求
		String canshu = "?method=createOrModifyUser&params";
		try {
			JSONObject object = new JSONObject();
			object.put("addressId", "");
			object.put("city", URLEncoder.encode("南京", "utf-8"));
			object.put("email", "");
			object.put("fullAddress", URLEncoder.encode("迈皋桥街", "utf-8"));
			object.put("loginName", "lzx1981");
			object.put("mobile", "15261894435");
			object.put("oprType", "1");
			object.put("password", MD5.getMD5("12345678".getBytes()));
			object.put("province", URLEncoder.encode("江苏", "utf-8"));
			object.put("userId", "");
			object.put("verifyCode", "");
			RegisterAction action = new RegisterAction(handler);
			action.getMessage(canshu + "=" + object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
