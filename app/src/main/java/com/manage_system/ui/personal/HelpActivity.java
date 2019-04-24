package com.manage_system.ui.personal;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.manage_system.R;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		WebView webview=(WebView)findViewById(R.id.webView1);//获取布局管理器中添加的WebView组件
		//创建一个字符串构建器，将要显示的HTML内容放置在该构建器中
		StringBuilder sb=new StringBuilder();
		sb.append("<div>《ilife》使用帮助：</div>");
		sb.append("<ul>");
		sb.append("<li>日历：查看日历，设置相关纪念日的提醒。</li>");
		sb.append("<li>日记：写日记，同时进行相关查询、修改、删除工作。</li>");
		sb.append("<li>闹钟：设置相关时间，定时提醒。</li>");
		sb.append("<li计划表：写入相关计划，进行相关增删改查工作。</li>");
		sb.append("<li>生活涂鸦：选择图片进行涂鸦，放飞心情。</li>");
		sb.append("<li>记账本：记录相关账目，便于理财。</li>");
		sb.append("</ul>");
		webview.loadDataWithBaseURL(null, sb.toString(),"text/html","utf-8",null);//加载数据
	}
}
