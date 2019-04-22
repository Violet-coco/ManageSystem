package com.manage_system.ui.manage;

import android.util.Log;

import com.manage_system.bean.FreshNewsBean;
import com.manage_system.bean.JdDetailBean;
import com.manage_system.net.BaseObserver;
import com.manage_system.net.ManageApi;
import com.manage_system.net.RxSchedulers;
import com.manage_system.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/27 .
 */
public class JanDanPresenter extends BasePresenter<JanDanContract.View> implements JanDanContract.Presenter {
    private static final String TAG = "JanDanPresenter";
    ManageApi mManageApi;

    @Inject
    public JanDanPresenter(ManageApi manageApi) {
        this.mManageApi = manageApi;
    }

    @Override
    public void getData(String type, int page) {
        if (type.equals(ManageApi.TYPE_FRESH)) {
            getFreshNews(page);
        } else {
            getDetailData(type, page);
        }
    }

    @Override
    public void getFreshNews(final int page) {
        mManageApi.getFreshNews(page)
                .compose(RxSchedulers.<FreshNewsBean>applySchedulers())
                .compose(mView.<FreshNewsBean>bindToLife())
                .subscribe(new BaseObserver<FreshNewsBean>() {
                    @Override
                    public void onSuccess(FreshNewsBean freshNewsBean) {
                        if (page > 1) {
                            mView.loadMoreFreshNews(freshNewsBean);
                        } else {
                            mView.loadFreshNews(freshNewsBean);
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });

    }

    @Override
    public void getDetailData(final String type, final int page) {
        mManageApi.getJdDetails(type, page)
                .compose(RxSchedulers.<JdDetailBean>applySchedulers())
                .compose(mView.<JdDetailBean>bindToLife())
                .map(new Function<JdDetailBean, JdDetailBean>() {
                    @Override
                    public JdDetailBean apply(@NonNull JdDetailBean jdDetailBean) throws Exception {
                        for (JdDetailBean.CommentsBean bean : jdDetailBean.getComments()) {
                            if (bean.getPics() != null) {
                                if (bean.getPics().size() > 1) {
                                    bean.itemType = JdDetailBean.CommentsBean.TYPE_MULTIPLE;
                                } else {
                                    bean.itemType = JdDetailBean.CommentsBean.TYPE_SINGLE;
                                }
                            }
                        }
                        return jdDetailBean;
                    }
                })
                .subscribe(new BaseObserver<JdDetailBean>() {
                    @Override
                    public void onSuccess(JdDetailBean jdDetailBean) {
                        if (page > 1) {
                            mView.loadMoreDetailData(type, jdDetailBean);
                        } else {
                            mView.loadDetailData(type, jdDetailBean);
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        if (page > 1) {
                            mView.loadMoreDetailData(type, null);
                        } else {
                            mView.loadDetailData(type, null);
                        }
                        Log.i(TAG, "onFail: " + e.getMessage());
                    }
                });
    }
}
