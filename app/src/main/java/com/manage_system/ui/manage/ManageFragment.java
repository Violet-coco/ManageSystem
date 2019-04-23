package com.manage_system.ui.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.manage_system.EditPasswordActivity;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ManageApi;
import com.manage_system.ui.adapter.ProcessDocumentAdapter;
import com.manage_system.ui.adapter.StudentTitleAdapter;
import com.manage_system.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ManageFragment extends BaseFragment {

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    private ManagePagerAdapter mManagePagerAdapter;

    public static ManageFragment newInstance() {
        Bundle args = new Bundle();
        ManageFragment fragment = new ManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_manage;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        List<String> strings = new ArrayList<>();
        strings.add("学生选题");
        strings.add("过程文档");
        strings.add("答辩管理");
        mManagePagerAdapter = new ManagePagerAdapter(getChildFragmentManager(), strings);
        mViewpager.setAdapter(mManagePagerAdapter);
        mViewpager.setOffscreenPageLimit(1);
        mViewpager.setCurrentItem(0, false);
        mTabLayout.setupWithViewPager(mViewpager, true);
    }

    @Override
    public void initEvent() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void onRetry() {

    }

    public class ManagePagerAdapter extends FragmentStatePagerAdapter {
        private List<String> titles;

        public ManagePagerAdapter(FragmentManager fm, List<String> titles) {
            super(fm);
            this.titles = titles;
        }

        private String TAG = "测试中***************";

        @Override
        public BaseFragment getItem(int position) {
            Intent intent = new Intent();
            switch (position) {
                case 0:
                    Log.w(TAG,"点击1"+JdDetailFragment.newInstance(ManageApi.TYPE_FRESH,new StudentTitleAdapter(getActivity(),null)));
//                    intent.setClass(getActivity(), EditPasswordActivity.class);
//                    startActivity(intent);
//                    break;
                    return JdDetailFragment.newInstance(ManageApi.TYPE_FRESH,new StudentTitleAdapter(getActivity(),null));
                case 1:
                    Log.w(TAG,"点击2");
//                    intent.setClass(getActivity(), EditPasswordActivity.class);
//                    startActivity(intent);
                    return JdDetailFragment.newInstance(ManageApi.TYPE_BORED,new ProcessDocumentAdapter(getActivity(),null));
                case 2:
                    Log.w(TAG,"点击3");
//                    intent.setClass(getActivity(), EditPasswordActivity.class);
//                    startActivity(intent);
                    return JdDetailFragment.newInstance(ManageApi.TYPE_GIRLS,new ProcessDocumentAdapter(getActivity(),null));
//                case 3:
//                    return JdDetailFragment.newInstance(ManageApi.TYPE_Duan,new JokesAdapter(null));
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

}
