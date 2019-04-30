package com.manage_system.component;

import android.content.Context;

import com.manage_system.MyApp;
import com.manage_system.module.ApplicationModule;

import dagger.Component;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    MyApp getApplication();

    Context getContext();

}
