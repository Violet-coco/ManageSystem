package com.manage_system.ui.manage.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.LoginActivity;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.manage.activity.teacher.TeacherReplyMainActivity;
import com.manage_system.ui.manage.adapter.MyAdapter;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ManageFragment extends BaseFragment {

    private TabLayout tabLayout = null;

    private ViewPager viewPager;

    private Fragment[] mFragmentArrays = new Fragment[3];

    private String[] mTabTitles = new String[3];
    private String TAG = "ManageFragment";

    public static ManageFragment newInstance() {
        Bundle args = new Bundle();
        ManageFragment fragment = new ManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void getData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("proName","");
        map.put("proType","");
        map.put("proSource","");
        map.put("tecId","");
        map.put("tecName","");
        map.put("offset","");
        map.put("limit","100000");
        manager.post(ApiConstants.studentApi + "/showProjects", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                final JSONObject obj = JSON.parseObject(responseBody);
                Log.e(TAG,obj.toString());
                SharedPreferences sp=getActivity().getSharedPreferences("processData", MODE_PRIVATE);
                //获取编辑器
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("obj", obj.toString());
                //提交修改
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)) {
                            Log.w(TAG,"查询成功");
                        }else{
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void initLeaderData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        manager.post(ApiConstants.teacherApi + "/showDefPlan", map,new okhttp3.Callback() {
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
                if(obj.get("statusCode").equals(100)){
                    JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("teaGroup"));
                    Log.d(TAG,array.toString());
                    for (int i = 0; i < array.size(); i++) {
                        Log.e(TAG,"ttt");
                        JSONObject object = array.getJSONObject(i);
                        Log.w(TAG,object.toString());
                        if(object.getBooleanValue("leader")){
                            Log.w(TAG,"是不是");
                            SharedPreferences sp=getActivity().getSharedPreferences("loginInfo", MODE_PRIVATE);
                            //获取编辑器
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("leader", "1");
                            //提交修改
                            editor.commit();
                        }
                    }

                }else{
                    Looper.prepare();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
    }

    public void initView() {
        List<String> strings = new ArrayList<>();
        SharedPreferences sp=getActivity().getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        String authority = sp.getString("authority" , "");
        if(authority.equals("1")){
            getData();
            mTabTitles[0] = "学生选题";
            mTabTitles[1] = "过程文档";
            mTabTitles[2] = "答辩管理";
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            mFragmentArrays[0] = ChooseTitleFragment.newInstance();
            mFragmentArrays[1] = ProcessDocumentFragment.newInstance();
            mFragmentArrays[2] = ReplyFragment.newInstance();
        }else{
            initLeaderData();
            mTabTitles[0] = "题目管理";
            mTabTitles[1] = "答辩管理";
            mTabTitles[2] = "学生管理";
            tabLayout.setTabMode(TabLayout.MODE_FIXED);

            mFragmentArrays[0] = ChooseTitleFragment.newInstance();
            mFragmentArrays[1] = ProcessDocumentFragment.newInstance();
            mFragmentArrays[2] = ReplyFragment.newInstance();
        }

        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(),strings);
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_manage;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        initView();
        return view;
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    final class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm, List<String> strings) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }


        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];

        }
    }
}
