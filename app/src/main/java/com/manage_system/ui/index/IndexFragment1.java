package com.manage_system.ui.index;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.manage_system.R;
import com.manage_system.bean.Channel;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.component.DaggerHttpComponent;
import com.manage_system.database.ChannelDao;
import com.manage_system.event.NewChannelEvent;
import com.manage_system.event.SelectChannelEvent;
import com.manage_system.ui.adapter.ChannelPagerAdapter;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.index.contract.NewsContract;
import com.manage_system.ui.index.presenter.NewsPresenter;
import com.manage_system.widget.ChannelDialogFragment;
import com.manage_system.widget.CustomViewPager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class IndexFragment1 extends BaseFragment<NewsPresenter> implements NewsContract.View {

    private static final String TAG = "IndexFragment1";

    @BindView(R.id.viewpager)
    CustomViewPager mViewpager;
    @BindView(R.id.iv_edit)
    ImageView mIvEdit;
    @BindView(R.id.SlidingTabLayout)
    com.flyco.tablayout.SlidingTabLayout SlidingTabLayout;

    private ChannelPagerAdapter mChannelPagerAdapter;

    private List<Channel> mSelectedDatas;
    private List<Channel> mUnSelectedDatas;

    private int selectedIndex;
    private String selectedChannel;

    public static IndexFragment1 newInstance() {
        Bundle args = new Bundle();
        IndexFragment1 fragment = new IndexFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_news_new;
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
        EventBus.getDefault().register(this);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedIndex = position;
                selectedChannel = mSelectedDatas.get(position).getChannelName();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mSelectedDatas = new ArrayList<>();
        mUnSelectedDatas = new ArrayList<>();
        mPresenter.getChannel();
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
    public void loadData(List<Channel> channels, List<Channel> unSelectedDatas) {
        if (channels != null) {
            mSelectedDatas.clear();
            mSelectedDatas.addAll(channels);
            mUnSelectedDatas.clear();
            mUnSelectedDatas.addAll(unSelectedDatas);
            mChannelPagerAdapter = new ChannelPagerAdapter(getChildFragmentManager(), channels);
            mViewpager.setAdapter(mChannelPagerAdapter);
            mViewpager.setOffscreenPageLimit(2);
            mViewpager.setCurrentItem(0, false);
            SlidingTabLayout.setViewPager(mViewpager);
        } else {
            T("数据异常");
        }
    }

    @Subscriber
    private void updateChannel(NewChannelEvent event) {
        if (event == null) return;
        if (event.selectedDatas != null && event.unSelectedDatas != null) {
            mSelectedDatas = event.selectedDatas;
            mUnSelectedDatas = event.unSelectedDatas;
            mChannelPagerAdapter.updateChannel(mSelectedDatas);
            SlidingTabLayout.notifyDataSetChanged();
            ChannelDao.saveChannels(event.allChannels);

            List<String> integers = new ArrayList<>();
            for (Channel channel : mSelectedDatas) {
                integers.add(channel.getChannelName());
            }
            if (TextUtils.isEmpty(event.firstChannelName)) {
                if (!integers.contains(selectedChannel)) {
                    selectedChannel = mSelectedDatas.get(selectedIndex).getChannelName();
                    mViewpager.setCurrentItem(selectedIndex, false);
                } else {
                    setViewpagerPosition(integers, selectedChannel);
                }
            } else {
                setViewpagerPosition(integers, event.firstChannelName);
            }
        }
    }

    @Subscriber
    private void selectChannelEvent(SelectChannelEvent selectChannelEvent) {
        if (selectChannelEvent == null) return;
        List<String> integers = new ArrayList<>();
        for (Channel channel : mSelectedDatas) {
            integers.add(channel.getChannelName());
        }
        setViewpagerPosition(integers, selectChannelEvent.channelName);
    }

    /**
     * 设置 当前选中页
     *
     * @param integers
     * @param channelName
     */
    private void setViewpagerPosition(List<String> integers, String channelName) {
        if (TextUtils.isEmpty(channelName) || integers == null) return;
        for (int j = 0; j < integers.size(); j++) {
            if (integers.get(j).equals(channelName)) {
                selectedChannel = integers.get(j);
                selectedIndex = j;
                break;
            }
        }
        mViewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewpager.setCurrentItem(selectedIndex, false);
            }
        }, 100);
    }

    @OnClick(R.id.iv_edit)
    public void onViewClicked() {
        ChannelDialogFragment dialogFragment = ChannelDialogFragment.newInstance(mSelectedDatas, mUnSelectedDatas);
        dialogFragment.show(getChildFragmentManager(), "CHANNEL");
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
