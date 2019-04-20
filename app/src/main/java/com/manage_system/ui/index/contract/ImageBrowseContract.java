package com.manage_system.ui.index.contract;

import com.manage_system.bean.NewsArticleBean;
import com.manage_system.ui.base.BaseContract;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/7 .
 */
public interface ImageBrowseContract {

    interface View extends BaseContract.BaseView{

        void loadData(NewsArticleBean newsArticleBean);

    }

    interface Presenter extends BaseContract.BasePresenter<View>{

        void getData(String aid,boolean isCmpp);

    }

}
