package com.manage_system.ui.inter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage_system.component.ApplicationComponent;


public interface IBase {

    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    View getView();

    int getContentLayout();

    void initInjector(ApplicationComponent appComponent);

    void bindView(View view,Bundle savedInstanceState);

    void initData();

    /**
     * 是否存活(已启动且未被销毁)
     */
    boolean isAlive();

}
