/**
 * 
 */
package com.xwtec.yycoin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xwtec.util.tool.Variable;
import com.xwtec.yycoin.R;

/**
 * @author Administrator
 * 
 */
public class PersonInfoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info_activity);
		showTop("个人信息");

		initview();
	}

	private void initview() {
		TextView user_name = (TextView) findViewById(R.id.user_name);
		user_name.setText(Variable.username);
		TextView email = (TextView) findViewById(R.id.email);
		email.setText(Variable.email);
		TextView name = (TextView) findViewById(R.id.name);
		name.setText(Variable.trueName);
		TextView cell_phone = (TextView) findViewById(R.id.cell_phone);
		cell_phone.setText(Variable.phoneNumber);
		TextView address = (TextView) findViewById(R.id.address);
		address.setText(Variable.address);
		TextView detail_address = (TextView) findViewById(R.id.detail_address);
		detail_address.setText(Variable.detailAddress);

		ImageButton change_password_btn = (ImageButton) findViewById(R.id.change_password_btn);
		change_password_btn
				.setOnClickListener(new ImageButton.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setAction(ChangePasswordActivity.class.getName());
						startActivity(intent);
					}
				});
	}
}
