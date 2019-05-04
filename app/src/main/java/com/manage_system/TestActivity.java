package com.manage_system;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.manage_system.ui.manage.fragment.ManageFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_teacher_reply_review_detail);
		//传递复杂些的参数
		Map<String, Object> map = new HashMap<>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map);
		Intent intent = new Intent(TestActivity.this,MainActivity.class);
		intent.putExtra("list",list.toString());
		Bundle bundle = new Bundle();
		//须定义一个list用于在budnle中传递需要传递的ArrayList<Object>,这个是必须要的
		ArrayList bundlelist = new ArrayList();
		bundlelist.add(list);
		bundle.putParcelableArrayList("list",bundlelist);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
