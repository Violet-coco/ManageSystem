package com.manage_system.component;

import android.content.Context;

import com.manage_system.MyApp;
import com.manage_system.module.ApplicationModule;
import com.manage_system.module.HttpModule;
import com.manage_system.net.ManageApi;
import com.manage_system.net.NewsApi;

import dagger.Component;

@Component(modules = {ApplicationModule.class,HttpModule.class})
public interface ApplicationComponent {

    MyApp getApplication();

    NewsApi getNetEaseApi();

    ManageApi getJanDanApi();

    Context getContext();

}
