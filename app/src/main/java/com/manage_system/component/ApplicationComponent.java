package com.manage_system.component;

import android.content.Context;

import com.manage_system.MyApp;
import com.manage_system.module.ApplicationModule;
import com.manage_system.module.HttpModule;
import com.manage_system.net.JanDanApi;
import com.manage_system.net.NewsApi;

import dagger.Component;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/2 .
 */
@Component(modules = {ApplicationModule.class,HttpModule.class})
public interface ApplicationComponent {

    MyApp getApplication();

    NewsApi getNetEaseApi();

    JanDanApi getJanDanApi();

    Context getContext();

}
