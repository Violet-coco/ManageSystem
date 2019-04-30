package com.manage_system.ui.personal;

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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.ui.base.TextClearSuit;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageView ivEmailClear,ivBindPhoneClear,ivPhoneClear;
	private EditText person_email,person_bind_phone,person_phone;
	private EditText person_name,person_id,person_college,person_department,person_major,person_class,person_sex;
	private ImageButton iv_back;
	private Button person_info_save;
	private String bindTel,contactTel,email;

	private OkManager manager;
	//登录验证请求
	private String save_info_path="http://www.yuanbw.cn:20086/gpms/rol/modifyRoleInfo";
	private String path="http://www.yuanbw.cn:20086/gpms/rol/showRoleInfo";

	private String TAG = "保存个人信息修改页面：";

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
				break;
			case R.id.person_info_save:
				savePersonInfo();
				break;
			default:
				break;
		}
	}

	public void onViewClicked() {
		finish();
	}

	public void savePersonInfo() {

		bindTel=person_bind_phone.getText().toString().trim();
		contactTel=person_phone.getText().toString().trim();
		email=person_email.getText().toString().trim();
		// 连接接口
		manager = OkManager.getInstance();
		Map<String, String> map = new HashMap<String, String>();
		map.put("bindTel", bindTel);
		map.put("contactTel", contactTel);
		map.put("email", email);

		Log.w(TAG,bindTel);
		Log.w(TAG,contactTel);
		Log.w(TAG,email);
		SharedPreferences sp1=getSharedPreferences("loginInfo", MODE_PRIVATE);
		Log.w(TAG,sp1.getString("token",""));
		manager.post(save_info_path, map,new okhttp3.Callback() {
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
							// 连接接口
							Map<String, String> map1 = new HashMap<String, String>();
							manager.post(path, map1,new okhttp3.Callback() {
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
											SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
											//获取编辑器
											SharedPreferences.Editor editor=sp.edit();
											editor.putString("bindTel",obj.getJSONObject("data").getString("bindTel"));
											editor.putString("contactTel",obj.getJSONObject("data").getString("contactTel"));
											editor.putString("email",obj.getJSONObject("data").getString("email"));
											editor.commit();
										}
									});

								}
							});
//							manager.sendComplexForm(path, map1, new OkManager.Fun4() {
//								@Override
//								public void onResponse(org.json.JSONObject jsonObject) {
//									JSONObject obj = JSON.parseObject(jsonObject.toString());
//									Log.w(TAG,obj.toString());
//									SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
//									//获取编辑器
//									SharedPreferences.Editor editor=sp.edit();
//									editor.putString("bindTel",obj.getJSONObject("data").getString("bindTel"));
//									editor.putString("contactTel",obj.getJSONObject("data").getString("contactTel"));
//									editor.putString("email",obj.getJSONObject("data").getString("email"));
//									editor.commit();
//								}
//							});
							finish();
						}else{
							Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
						}
					}
				});

			}
		});
//		manager.sendComplexForm(save_info_path, map, new OkManager.Fun4() {
//			@Override
//			public void onResponse(org.json.JSONObject jsonObject) {
//				JSONObject obj = JSON.parseObject(jsonObject.toString());
//				Log.w(TAG,obj.toString());
//				if(obj.get("statusCode").equals(100)){
//					Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
//					// 连接接口
//					Map<String, String> map1 = new HashMap<String, String>();
//					manager.sendComplexForm(path, map1, new OkManager.Fun4() {
//						@Override
//						public void onResponse(org.json.JSONObject jsonObject) {
//							JSONObject obj = JSON.parseObject(jsonObject.toString());
//							Log.w(TAG,obj.toString());
//							SharedPreferences sp=getSharedPreferences("personInfo", MODE_PRIVATE);
//							//获取编辑器
//							SharedPreferences.Editor editor=sp.edit();
//							editor.putString("bindTel",obj.getJSONObject("data").getString("bindTel"));
//							editor.putString("contactTel",obj.getJSONObject("data").getString("contactTel"));
//							editor.putString("email",obj.getJSONObject("data").getString("email"));
//							editor.commit();
//						}
//					});
//					finish();
//				}else{
//					Toast.makeText(PersonInfoActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
	}

}