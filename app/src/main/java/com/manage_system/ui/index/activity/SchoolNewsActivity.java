package com.manage_system.ui.index.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
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

public class SchoolNewsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.webView1)
    WebView webView1;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.source)
    TextView source;


    private static String TAG = "SchoolNewsActivity";
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        JSONObject obj = JSON.parseObject(intent.getStringExtra("news_data"));
        title.setText(obj.getJSONObject("news").getString("title"));
        time.setText(DateUtil.getDateFormat(obj.getString("sendDate")));
        name.setText(obj.getString("sendName"));
        source.setText(obj.getString("sendCol"));
        initData(obj.getString("id"));
        URL = "<html><body>"+intent.getStringExtra("web_content")+"</body></html>";
        /**
         * 将文本HTML显示在webview中
         */
        webView1.loadDataWithBaseURL(null,URL,"text/html","utf-8",null);
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, SchoolNewsActivity.class);
    }

    public void initData(String nid) {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("nid",nid);
        OkManager.post(ApiConstants.commonApi + "/addPageView", map,new okhttp3.Callback() {
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

                        }else{
                            Toast.makeText(SchoolNewsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

}