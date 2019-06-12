package com.manage_system.ui.personal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.TextClearSuit;
import com.manage_system.ui.manage.Manage;
import com.manage_system.ui.manage.activity.teacher.TeacherLeaderMainActivity;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageView ivEmailClear,ivBindPhoneClear,ivPhoneClear;
	private EditText person_email,person_bind_phone,person_phone;
	private EditText person_name,person_id,person_college,person_department,person_major,person_class,person_sex;
	private ImageButton iv_back;
	private Button person_info_save;
	private String bindTel,contactTel,email;
	@BindView(R.id.tm_id)
	TextView tm_id;
	@BindView(R.id.tm_class)
	LinearLayout tm_class;

	private OkManager manager;

	private String TAG = "保存个人信息修改页面：";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_person_info);
		ButterKnife.bind(this);
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

		person_info_save = (Button)findViewById(R.id.person_info_save);

		iv_back.setOnClickListener(this);

		ivEmailClear.setOnClickListener(this);
		ivBindPhoneClear.setOnClickListener(this);
		ivPhoneClear.setOnClickListener(this);
		person_info_save.setOnClickListener(this);

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
		SharedPreferences login=getSharedPreferences("loginInfo", MODE_PRIVATE);

		if(login.getString("authority","").equals("1")){
			person_class.setText(sp.getString("grade" , "")+"级-"+sp.getString("classNo" , "")+"班");
		}{
			tm_id.setText("工号");
			tm_class.setVisibility(View.GONE);
		}
		person_name.setText(sp.getString("name" , ""));
		person_id.setText(sp.getString("identifier" , ""));
		person_college.setText(sp.getString("college" , ""));
		person_department.setText(sp.getString("department" , ""));
		person_major.setText(sp.getString("major" , ""));
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
				finish();
				break;
			case R.id.person_info_save:
				showDialog();
				break;
			default:
				break;
		}
	}

	public void savePersonInfo() {

		bindTel=person_bind_phone.getText().toString().trim();
		contactTel=person_phone.getText().toString().trim();
		email=person_email.getText().toString().trim();
		// 连接接口
		manager = OkManager.getInstance();
		Map<String, String> map = new HashMap<String, String>();

		SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
		if(!bindTel.equals(sp.getString("bindTel" , ""))){
			map.put("bindTel", bindTel);
		}
		if(!contactTel.equals(sp.getString("contactTel" , ""))){
			map.put("contactTel", contactTel);
		}
		if(!email.equals(sp.getString("email" , ""))){
			map.put("email", email);
		}

		Log.w(TAG,bindTel);
		Log.w(TAG,contactTel);
		Log.w(TAG,email);
		SharedPreferences sp1=getSharedPreferences("loginInfo", MODE_PRIVATE);
		Log.w(TAG,sp1.getString("token",""));
		OkManager.post(ApiConstants.commonApi + "/modifyRoleInfo", map,new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.e(TAG, "onFailure: ",e);
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseBody = response.body().string();
				final JSONObject obj = JSON.parseObject(responseBody);
				Log.e(TAG,obj.toString());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(obj.get("statusCode").equals(100)){
							Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(PersonInfoActivity.this,PersonInfoActivity.class);
							startActivity(intent);
							finish();
							Manage.getPersonData();
						}else if(obj.get("statusCode").equals(102)){
							Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(PersonInfoActivity.this,LoginActivity.class);
							startActivity(intent);
							finish();
						}else{
							Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
						}
					}
				});

			}
		});
	}

	public void showDialog(){
		final Dialog dialog = new Dialog(PersonInfoActivity.this, R.style.MyDialog);
		//设置它的ContentView
		dialog.setContentView(R.layout.alert_dialog);
		((TextView)dialog.findViewById(R.id.tvAlertDialogTitle)).setText("提示");
		((TextView)dialog.findViewById(R.id.tvAlertDialogMessage)).setText("保存修改信息？");
		dialog.show();

		Button confirm = (Button)dialog.findViewById(R.id.btnAlertDialogPositive);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				savePersonInfo();
			}
		});
		Button cancel = (Button) dialog.findViewById(R.id.btnAlertDialogNegative);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

}