package com.manage_system.ui.manage.activity.teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.manage_system.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeacherReplyTypeActivity extends Activity {

	@BindView(R.id.iv_back)
	ImageButton iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ms_teacher_reply_plan);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.iv_back,R.id.teacher_info,R.id.student_info})
	public void onClick(View v) {//直接调用不会显示v被点击效果
		Intent intent;
		switch (v.getId()) {
			case R.id.teacher_info:
				intent = new Intent(TeacherReplyTypeActivity.this,TeacherReplyMainActivity.class);
				intent.putExtra("reply_type","teacher_info");
				startActivity(intent);
				break;
			case R.id.student_info:
				intent = new Intent(TeacherReplyTypeActivity.this,TeacherReplyMainActivity.class);
				intent.putExtra("reply_type","student_info");
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
