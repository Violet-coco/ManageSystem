package com.manage_system;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.manage_system.component.ApplicationComponent;
import com.manage_system.ui.base.BaseActivity;
import com.manage_system.ui.base.SupportFragment;
import com.manage_system.ui.browse.BrowseFragment;
import com.manage_system.ui.index.IndexFragment;
import com.manage_system.ui.manage.fragment.ManageFragment;
import com.manage_system.ui.personal.PersonalFragment;
import com.manage_system.utils.StatusBarUtil;
import com.manage_system.widget.BottomBar;
import com.manage_system.widget.BottomBarTab;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

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
