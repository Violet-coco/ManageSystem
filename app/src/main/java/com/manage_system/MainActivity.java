package com.manage_system;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.BaseActivity;
import com.manage_system.ui.base.SupportFragment;
import com.manage_system.ui.browse.fragment.BrowseFragment;
import com.manage_system.ui.index.fragment.IndexFragment;
import com.manage_system.ui.manage.fragment.LeaderFragment;
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

    private SupportFragment[] mtFragments = new SupportFragment[5];
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
        initNewsData();
        initData();
        SharedPreferences spt = getSharedPreferences("loginInfo", MODE_PRIVATE);
        if(spt.getString("authority" , "").equals("3")){
            Log.e(TAG,"身份："+spt.getString("authority" , "").equals("3"));
            StatusBarUtil.setTranslucentForImageViewInFragment(MainActivity.this, 0, null);
            if (savedInstanceState == null) {
                mtFragments[0] = IndexFragment.newInstance();
                mtFragments[1] = BrowseFragment.newInstance();
                mtFragments[2] = ManageFragment.newInstance();
                mtFragments[3] = LeaderFragment.newInstance();
                mtFragments[4] = PersonalFragment.newInstance();

                getSupportDelegate().loadMultipleRootFragment(R.id.contentContainer, 0,
                        mtFragments[0],
                        mtFragments[1],
                        mtFragments[2],
                        mtFragments[3],
                        mtFragments[4]);
            } else {
                mtFragments[0] = findFragment(IndexFragment.class);
                mtFragments[1] = findFragment(BrowseFragment.class);
                mtFragments[2] = findFragment(ManageFragment.class);
                mtFragments[3] = findFragment(LeaderFragment.class);
                mtFragments[3] = findFragment(PersonalFragment.class);
            }

            mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_index, "首页"))
                    .addItem(new BottomBarTab(this, R.drawable.ic_browse, "浏览"))
                    .addItem(new BottomBarTab(this, R.drawable.ic_manage, "管理"))
                    .addItem(new BottomBarTab(this, R.drawable.ic_leader, "系主任"))
                    .addItem(new BottomBarTab(this, R.drawable.ic_my, "我的"));
            mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
                @Override
                public void onTabSelected(int position, int prePosition) {
                    getSupportDelegate().showHideFragment(mtFragments[position], mtFragments[prePosition]);
                    Log.w(TAG,position+"位置");
                }

                @Override
                public void onTabUnselected(int position) {

                }

                @Override
                public void onTabReselected(int position) {

                }
            });
        }else{
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

    }

    @Override
    public void initData() {
        // 连接接口
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.commonApi + "/showRoleInfo", map,new okhttp3.Callback() {
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
                            SharedPreferences sp1=getSharedPreferences("loginInfo", MODE_PRIVATE);
                            //获取编辑器
                            SharedPreferences.Editor editor1=sp1.edit();
                            editor1.putString("name", obj.getJSONObject("data").getString("name"));
                            editor1.putString("id", obj.getJSONObject("data").getString("identifier"));
                            //提交修改
                            editor1.commit();

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
                            editor.putString("department", obj.getJSONObject("data").getString("department"));
                            editor.putString("email", obj.getJSONObject("data").getString("email"));
                            Log.e(TAG,sp.getString("authority",""));
                            if(sp1.getString("authority","").equals("1")){
                                editor.putString("grade", obj.getJSONObject("data").getString("grade"));
                                editor.putString("classNo", obj.getJSONObject("data").getString("classNo"));
                                editor.putString("pName", obj.getJSONObject("data").getString("pName"));
                                editor.putString("mtName", obj.getJSONObject("data").getString("tName"));
                                editor.putString("tId", obj.getJSONObject("data").getString("tId"));
                                editor.putString("mtId", obj.getJSONObject("data").getString("mtId"));
                            }
                            //提交修改
                            editor.commit();
                        }else if(obj.get("statusCode").equals(102)){
                            Toast.makeText(MainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }

                    }
                });

            }
        });
    }


    public void initNewsData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","20");
        manager.post(ApiConstants.commonApi + "/showAllNews", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                if(obj.get("statusCode").equals(100)){
                    SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("news_list", obj.toString());
                    //提交修改
                    editor.commit();
                }
            }
        });
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
