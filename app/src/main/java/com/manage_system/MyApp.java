package com.manage_system;


import android.content.Context;

import com.manage_system.component.ApplicationComponent;
import com.manage_system.component.DaggerApplicationComponent;
import com.manage_system.module.ApplicationModule;
import com.manage_system.utils.ContextUtils;
import com.mob.MobSDK;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;

public class MyApp extends LitePalApplication implements com.mob.tools.proguard.ProtectedMemberKeeper {

    private ApplicationComponent mApplicationComponent;

    private static MyApp sMyApp;

    public static int width = 0;

    public static int height = 0;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        sMyApp = this;
        BGASwipeBackManager.getInstance().init(this);
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        LitePal.initialize(this);
        width = ContextUtils.getSreenWidth(MyApp.getContext());
        height = ContextUtils.getSreenHeight(MyApp.getContext());

        MobSDK.init(this, null, null);
    }

    public static MyApp getInstance() {
        return sMyApp;
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public static Context getAppContext() {
        return MyApp.context;
    }

}
