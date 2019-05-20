package com.manage_system.ui.index.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.manage.fragment.ChooseTitleFragment;
import com.manage_system.ui.manage.fragment.ProcessDocumentFragment;
import com.manage_system.ui.manage.fragment.ReplyFragment;
import com.manage_system.utils.ImageLoaderUtil;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class IndexFragment extends BaseFragment {

    private TabLayout tabLayout = null;

    private ViewPager viewPager;

    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];
    private String TAG = "IndexFragment";

    public static IndexFragment newInstance() {
        Bundle args = new Bundle();
        IndexFragment fragment = new IndexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void initView() {
        initData();
        List<String> strings = new ArrayList<>();
        mTabTitles[0] = "校内新闻";
        mTabTitles[1] = "通知公告";
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        mFragmentArrays[0] = SchoolNewsFragment.newInstance();
        mFragmentArrays[1] = AnnouncementFragment.newInstance();
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
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<String, String>();
        map.put("limit","10000");
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(obj.get("statusCode").equals(100)){
                            SharedPreferences sp=getActivity().getSharedPreferences("processData", MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("news_list", obj.toString());
                            //提交修改
                            editor.commit();
                        }
                    }
                });

            }
        });
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
