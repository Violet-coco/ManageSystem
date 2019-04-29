package com.manage_system.ui.browse;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.manage_system.R;
import com.manage_system.bean.VideoChannelBean;
import com.manage_system.bean.VideoDetailBean;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.component.DaggerHttpComponent;
import com.manage_system.ui.adapter.VideoPagerAdapter;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.browse.contract.VideoContract;
import com.manage_system.ui.browse.presenter.VideoPresenter;

import java.util.List;

import butterknife.BindView;

public class BrowseFragment1 extends BaseFragment<VideoPresenter> implements VideoContract.View {
    private static final String TAG = "BrowseFragment1";
    @BindView(R.id.tablayout)
    TabLayout mTablayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    private VideoPagerAdapter mVideoPagerAdapter;


    public static BrowseFragment1 newInstance() {
        Bundle args = new Bundle();
        BrowseFragment1 fragment = new BrowseFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_video;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mPresenter.getVideoChannel();
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

    @Override
    public void loadVideoChannel(List<VideoChannelBean> channelBean) {
        Log.i(TAG, "loadVideoChannel: " + channelBean.toString());
        mVideoPagerAdapter = new VideoPagerAdapter(getChildFragmentManager(), channelBean.get(0));
        mViewpager.setAdapter(mVideoPagerAdapter);
        mViewpager.setOffscreenPageLimit(1);
        mViewpager.setCurrentItem(0, false);
        mTablayout.setupWithViewPager(mViewpager, true);
    }

    @Override
    public void loadMoreVideoDetails(List<VideoDetailBean> detailBean) {

    }

    @Override
    public void loadVideoDetails(List<VideoDetailBean> detailBean) {

    }

}
