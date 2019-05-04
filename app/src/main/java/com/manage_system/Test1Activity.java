package com.manage_system;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test1Activity extends Activity {
	private static String TAG = "测试页面";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_browse_item);
		//接收参数
		Bundle bundle = getIntent().getExtras();
		ArrayList list = bundle.getParcelableArrayList("list");
		Log.w(TAG,list+"");
		//从List中将参数转回 List<Map<String, Object>>
		List<Map<String, Object>> lists= (List<Map<String, Object>>)list.get(0);
		Log.w(TAG,lists+"");

		String data = "";
		for (Map<String, Object> m : lists) {
			for (String k : m.keySet()) {
				data += "\r\n" + k + " : " + m.get(k);
			}
		}
		Log.w(TAG,data);
	}
}
