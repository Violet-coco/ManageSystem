package com.manage_system;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.BaseActivity;
import com.manage_system.ui.base.SupportFragment;
import com.manage_system.ui.browse.fragment.BrowseFragment;
import com.manage_system.ui.index.fragment.IndexFragment;
import com.manage_system.ui.manage.fragment.ManageFragment;
import com.manage_system.ui.personal.PersonalFragment;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.StatusBarUtil;
import com.manage_system.widget.BottomBar;
import com.manage_system.widget.BottomBarTab;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.contentContainer)
    FrameLayout mContentContainer;
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;

    private SupportFragment[] mFragments = new SupportFragment[4];


    @Override
    public int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }


    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        // 连接接口
        OkManager manager = OkManager.getInstance();
        Map<String, String> map1 = new HashMap<String, String>();
        manager.post(ApiConstants.commonApi + "/showRoleInfo", map1,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
                //获取编辑器
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("name", obj.getJSONObject("data").getString("name"));
                editor.putString("id", obj.getJSONObject("data").getString("identifier"));
                //提交修改
                editor.commit();
                Log.e(TAG,obj.toString());
                Log.e(TAG,obj.getJSONObject("data").getString("name"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp = getSharedPreferences("personInfo", MODE_PRIVATE);
                        //获取编辑器
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("identifier", obj.getJSONObject("data").getString("identifier"));
                        editor.putString("name", obj.getJSONObject("data").getString("name"));
                        editor.putString("college", obj.getJSONObject("data").getString("college"));
                        editor.putString("sex", obj.getJSONObject("data").getString("sex"));
                        editor.putString("contactTel", obj.getJSONObject("data").getString("contactTel"));
                        editor.putString("bindTel", obj.getJSONObject("data").getString("bindTel"));
                        editor.putString("major", obj.getJSONObject("data").getString("major"));
                        editor.putString("grade", obj.getJSONObject("data").getString("grade"));
                        editor.putString("classNo", obj.getJSONObject("data").getString("classNo"));
                        editor.putString("department", obj.getJSONObject("data").getString("department"));
                        editor.putString("email", obj.getJSONObject("data").getString("email"));
                        //提交修改
                        editor.commit();
                    }
                });

            }
        });
        StatusBarUtil.setTranslucentForImageViewInFragment(MainActivity.this, 0, null);
        if (savedInstanceState == null) {
            mFragments[0] = IndexFragment.newInstance();
            mFragments[1] = BrowseFragment.newInstance();
            mFragments[2] = ManageFragment.newInstance();
            mFragments[3] = PersonalFragment.newInstance();

            getSupportDelegate().loadMultipleRootFragment(R.id.contentContainer, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2],
                    mFragments[3]);
        } else {
            mFragments[0] = findFragment(IndexFragment.class);
            mFragments[1] = findFragment(BrowseFragment.class);
            mFragments[2] = findFragment(ManageFragment.class);
            mFragments[3] = findFragment(PersonalFragment.class);
        }

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_index, "首页"))
                .addItem(new BottomBarTab(this, R.drawable.ic_browse, "浏览"))
                .addItem(new BottomBarTab(this, R.drawable.ic_manage, "管理"))
                .addItem(new BottomBarTab(this, R.drawable.ic_my, "我的"));
        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                getSupportDelegate().showHideFragment(mFragments[position], mFragments[prePosition]);
                Log.w(TAG,position+"位置");
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public void onBackPressedSupport() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressedSupport();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
