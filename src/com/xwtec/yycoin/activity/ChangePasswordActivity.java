/**
 * 
 */
package com.xwtec.yycoin.activity;

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
import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;
import com.xwtec.yycoin.action.ChangePasswordAction;

/**
 * @author Administrator
 * 
 */
public class ChangePasswordActivity extends BaseActivity {

	private EditText old_password_input;

	private EditText password_input;

	private EditText sure_password_input;

	private ImageButton changePwdBtn;

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
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ChangePasswordActivity.this);
					builder.setMessage("修改密码成功");
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
							ChangePasswordActivity.this);
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
		setContentView(R.layout.change_password_activity);
		showTop("修改密码");
		old_password_input = (EditText) findViewById(R.id.old_password_input);
		password_input = (EditText) findViewById(R.id.password_input);
		sure_password_input = (EditText) findViewById(R.id.sure_password_input);
		changePwdBtn = (ImageButton) findViewById(R.id.change_password_btn);
		changePwdBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				String oldpassword = old_password_input.getText().toString();
				String password = password_input.getText().toString();
				String surePassword = sure_password_input.getText().toString();
				if (!checkPasswod(oldpassword)) {
					showSureDialog(getString(R.string.old_pwd_leng_not_right));
					return;
				}
				if (!checkPasswod(password)) {
					// 密码判断
					showSureDialog(getString(R.string.new_pwd_leng_not_right));
					return;
				}
				if (!checkIsSameWithPasswordAndSurePassword(password,
						surePassword)) {
					showSureDialog(getString(R.string.sure_pwd_leng_not_right));
					return;
				}
				// 发起请求
				changePassword();
			}
		});
	}

	private void changePassword() {
		ChangePasswordAction action = new ChangePasswordAction(handler);
		JSONObject object = new JSONObject();
		try {
			object.put("newPassword", MD5.getMD5("lzx1981".getBytes()));
			object.put("orignalPassword", MD5.getMD5("12345678".getBytes()));
			object.put("id", Variable.id);
			action.getMessage("?method=modifyPassword&params="
					+ object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkIsSameWithPasswordAndSurePassword(String password,
			String surePassword) {
		if (password.equals(surePassword)) {
			return true;
		}
		return false;
	}

	/**
	 * 检测是否正确
	 * 
	 * @param text
	 *            输入的内容
	 * @return 是否合理
	 */
	private boolean checkPasswod(String text) {
		if (TextUtils.isEmpty(text)) {
			return false;
		}
		if (text.length() < 8) {
			return false;
		}
		return true;
	}
}
