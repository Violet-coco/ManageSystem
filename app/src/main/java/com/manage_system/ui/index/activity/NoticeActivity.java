package com.manage_system.ui.index.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.index.adapter.NoticeAdapter;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class NoticeActivity extends Activity {
	@BindView(R.id.student_choose_title_text)
	TextView student_choose_title_text;
	@BindView(R.id.student_choose_done_title_text)
	TextView student_choose_done_title_text;
	@BindView(R.id.student_choose_title)
	LinearLayout student_choose_title;
	@BindView(R.id.student_choose_done_title)
	LinearLayout student_choose_done_title;
	@BindView(R.id.point)
	ImageView point;

	private String TAG = "NoticeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_news_type);
		ButterKnife.bind(this);
		initData();
	}

	public void initData() {
		OkManager manager = OkManager.getInstance();
		Map<String, String> map = new HashMap<String, String>();
		manager.post(ApiConstants.commonApi + "/showUnReadCount", map,new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.e(TAG, "onFailure: ",e);
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String responseBody = response.body().string();
				Log.e(TAG,responseBody);
				final JSONObject obj = JSON.parseObject(responseBody);
				final String msg = obj.getString("msg");
				Log.e(TAG,responseBody);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(obj.get("statusCode").equals(100)){
							Intent intent = getIntent();
							if(intent.getStringExtra("news_type").equals("notice")){
								if(Integer.parseInt(obj.getJSONObject("data").getString("unread_notice"))>0){
									point.setVisibility(View.VISIBLE);
								}
							}else{
								if(Integer.parseInt(obj.getJSONObject("data").getString("unread_announcement"))>0){
									point.setVisibility(View.VISIBLE);
								}
							}

							Toast.makeText(NoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(NoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
						}
					}
				});

			}
		});
	}

	@OnClick({R.id.iv_back,R.id.student_choose_title,R.id.student_choose_done_title})
	public void onClick(View v) {//直接调用不会显示v被点击效果
		Intent intent;
		Intent getIntent = getIntent();
		switch (v.getId()) {
			case R.id.student_choose_title:
				intent = new Intent(NoticeActivity.this,NoticeMainActivity.class);
				if(getIntent.getStringExtra("news_type").equals("notice")){
					intent.putExtra("read_type","done");
				}else{
					intent.putExtra("read_type","ann_done");
				}
				startActivity(intent);
				break;
			case R.id.student_choose_done_title:
				intent = new Intent(NoticeActivity.this,NoticeMainActivity.class);
				if(getIntent.getStringExtra("news_type").equals("notice")){
					intent.putExtra("read_type","");
				}else{
					intent.putExtra("read_type","ann");
				}
				startActivity(intent);
				break;
			case R.id.iv_back:
				finish();
				break;
			default:
				break;
		}
	}
}
