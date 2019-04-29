package com.manage_system.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.manage_system.R;
import com.manage_system.ui.base.AlertDialog;
import com.manage_system.ui.base.TextClearSuit;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageView ivEmailClear,ivBindPhoneClear,ivPhoneClear;
	private EditText person_email,person_bind_phone,person_phone;
	private EditText person_name,person_id,person_college,person_department,person_major,person_class,person_sex;
	private ImageButton iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_person_info);
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

		ivBindPhoneClear = (ImageView)findViewById(R.id.ivBindPhoneClear);

		ivPhoneClear = (ImageView)findViewById(R.id.ivPhoneClear);

		iv_back = (ImageButton)findViewById(R.id.iv_back);

		iv_back.setOnClickListener(this);

		ivEmailClear.setOnClickListener(this);
		ivBindPhoneClear.setOnClickListener(this);
		ivPhoneClear.setOnClickListener(this);

		person_name = (EditText)findViewById(R.id.person_name);
		person_id = (EditText)findViewById(R.id.person_id);
		person_college = (EditText)findViewById(R.id.person_college);
		person_department = (EditText)findViewById(R.id.person_department);
		person_major = (EditText)findViewById(R.id.person_major);
		person_class = (EditText)findViewById(R.id.person_class);
		person_sex = (EditText)findViewById(R.id.person_sex);
		person_email = (EditText)findViewById(R.id.person_email);
		person_bind_phone = (EditText)findViewById(R.id.person_bind_phone);
		person_phone = (EditText)findViewById(R.id.person_phone);

		SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
		person_name.setText(sp.getString("name" , ""));
		person_id.setText(sp.getString("identifier" , ""));
		person_college.setText(sp.getString("college" , ""));
		person_department.setText(sp.getString("department" , ""));
		person_major.setText(sp.getString("major" , ""));
		person_class.setText(sp.getString("grade" , "")+"级-"+sp.getString("classNo" , "")+"班");
		person_sex.setText(sp.getString("sex" , ""));
		person_phone.setText(sp.getString("contactTel" , ""));
		person_bind_phone.setText(sp.getString("bindTel" , ""));
		person_email.setText(sp.getString("email" , ""));
	}
	private Context context;
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