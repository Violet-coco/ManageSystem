package com.manage_system.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.manage_system.bean.VideoChannelBean;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.browse.DetailFragment;

/**
 * desc:
 * author: Will .
 * date: 2017/9/10 .
 */
public class VideoPagerAdapter extends FragmentStatePagerAdapter {

    private VideoChannelBean videoChannelBean;

    public VideoPagerAdapter(FragmentManager fm, VideoChannelBean videoChannelBean) {
        super(fm);
        this.videoChannelBean = videoChannelBean;
    }

    @Override
    public BaseFragment getItem(int position) {
        return DetailFragment.newInstance("clientvideo_" + videoChannelBean.getTypes().get(position).getId());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return videoChannelBean.getTypes().get(position).getName();
    }

    @Override
    public int getCount() {
        return videoChannelBean != null ? videoChannelBean.getTypes().size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
