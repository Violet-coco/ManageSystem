package com.manage_system;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.manage_system.ui.base.TextClearSuit;

import butterknife.OnClick;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageView ivEmailClear,ivBindPhoneClear,ivPhoneClear;
	private EditText person_email,person_bind_phone,person_phone;
	private ImageButton iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info);
        getId();
	}

	/**启动这个Activity的Intent
	 * @param context
	 * @return
	 */
	public static Intent createIntent(Context context) {
		return new Intent(context, PersonInfoActivity.class);
	}

	/**
	 * 获取id
	 */
	private void getId()
	{
		ivEmailClear = (ImageView)findViewById(R.id.ivEmailClear);
		person_email = (EditText)findViewById(R.id.person_email);

		ivBindPhoneClear = (ImageView)findViewById(R.id.ivBindPhoneClear);
		person_bind_phone = (EditText)findViewById(R.id.person_bind_phone);

		ivPhoneClear = (ImageView)findViewById(R.id.ivPhoneClear);
		person_phone = (EditText)findViewById(R.id.person_phone);

		iv_back = (ImageButton)findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		ivEmailClear.setOnClickListener(this);
		ivBindPhoneClear.setOnClickListener(this);
		ivPhoneClear.setOnClickListener(this);
	}

	public void onClick(View v) {//直接调用不会显示v被点击效果
		switch (v.getId()) {
			case R.id.ivEmailClear:
				//清空邮箱按钮点击监听
				new TextClearSuit().addClearListener(person_email, ivEmailClear);
				break;
			case R.id.ivBindPhoneClear:
				//清空绑定手机按钮点击监听
				new TextClearSuit().addClearListener(person_bind_phone, ivBindPhoneClear);
				break;
			case R.id.ivPhoneClear:
				//清空手机按钮点击监听
				new TextClearSuit().addClearListener(person_phone, ivPhoneClear);
				break;
			case R.id.iv_back:
				onViewClicked();
			default:
				break;
		}
	}

	public void onViewClicked() {
		finish();
	}
}