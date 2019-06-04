package com.manage_system.ui.personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manage_system.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends Activity {
	@BindView(R.id.news_main)
	LinearLayout news_main;
	@BindView(R.id.iv_back)
	ImageButton iv_back;
	@BindView(R.id.news_title)
	TextView news_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		ButterKnife.bind(this);
		news_main.setVisibility(View.GONE);
		news_title.setText("使用帮助");
		WebView webview=(WebView)findViewById(R.id.webView1);//获取布局管理器中添加的WebView组件
		//创建一个字符串构建器，将要显示的HTML内容放置在该构建器中
		StringBuilder sb=new StringBuilder();
		sb.append("<div>《毕业管理系统》使用帮助：</div>");
		sb.append("<ul>");
		sb.append("<li>学生</li>");
		sb.append("<li>教师</li>");
		sb.append("<li>系主任</li>");
		sb.append("</ul>");
		webview.loadDataWithBaseURL(null, sb.toString(),"text/html","utf-8",null);//加载数据
	}

	@OnClick(R.id.iv_back)
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_back:
				finish();
				break;
			default:
				break;
		}
	}
}
