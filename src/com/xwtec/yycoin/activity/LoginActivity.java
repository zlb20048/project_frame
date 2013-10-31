/**
 * 
 */
package com.xwtec.yycoin.activity;

import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.LoginAction;

/**
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity {

	private EditText name_input;

	private EditText password_input;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			closeProgressDialog();
			switch (msg.what) {
			case Constant.CONNECT_SUCCRESS:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int retCode = (Integer) map.get("retCode");
				String retMsg = (String) map.get("retMsg");
				if (retCode == 0) {
					saveInfo();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
					builder.setMessage("登陆成功");
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
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
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
		setContentView(R.layout.login_activity);
		name_input = (EditText) findViewById(R.id.name_input);
		password_input = (EditText) findViewById(R.id.password_input);

		name_input.setText(getInfo().getString("name", "ccg520ty"));
		password_input.setText(getInfo().getString("password_input", "12345678"));
		showDailer();
		ImageButton loginBtn = (ImageButton) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 登陆请求，登陆成功
				if (TextUtils.isEmpty(name_input.getText().toString())) {
					showSureDialog(getString(R.string.user_name_not_be_null));
					return;
				}
				if (TextUtils.isEmpty(password_input.getText().toString())
						|| password_input.getText().toString().length() < 8) {
					showSureDialog(getString(R.string.pwd_leng_not_right));
					return;
				}
				login();
			}
		});
		ImageButton regeisterBtn = (ImageButton) findViewById(R.id.regeister_btn);
		regeisterBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(RegisterActivity.class.getName());
				startActivity(intent);
			}
		});
		ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 登陆
	 */
	private void login() {
		showProgessDialog();
		LoginAction action = new LoginAction(handler);
		JSONObject object = new JSONObject();
		try {
			object.put("loginName", name_input.getText());
			object.put("password",
					MD5.getMD5(password_input.getText().toString().getBytes()));
			action.getMessage("?method=userLogin&params=" + object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveInfo() {
		SharedPreferences.Editor sp = getSharedPreferences("login",
				Context.MODE_PRIVATE).edit();
		sp.putString("name", name_input.getText().toString());
		sp.putString("password", password_input.getText().toString());
		sp.commit();
	}

	private SharedPreferences getInfo() {
		return getSharedPreferences("login", Context.MODE_PRIVATE);
	}

}
