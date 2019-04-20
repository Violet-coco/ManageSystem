package com.manage_system.ui.index.presenter;

import com.manage_system.bean.NewsArticleBean;
import com.manage_system.net.NewsApi;
import com.manage_system.net.RxSchedulers;
import com.manage_system.ui.base.BasePresenter;
import com.manage_system.ui.index.contract.ImageBrowseContract;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/24 .
 */
public class ImageBrowsePresenter extends BasePresenter<ImageBrowseContract.View> implements ImageBrowseContract.Presenter {

    NewsApi mNewsApi;

    @Inject
    public ImageBrowsePresenter(NewsApi newsApi) {
        this.mNewsApi = newsApi;
    }

    @Override
    public void getData(String aid,boolean isCmpp) {
        mNewsApi.getNewsArticle(aid)
                .compose(RxSchedulers.<NewsArticleBean>applySchedulers())
                .compose(mView.<NewsArticleBean>bindToLife())
                .subscribe(new Observer<NewsArticleBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull NewsArticleBean articleBean) {
                        mView.loadData(articleBean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.showFaild();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }
}
