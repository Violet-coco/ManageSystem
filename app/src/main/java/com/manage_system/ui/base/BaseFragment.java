package com.manage_system.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage_system.ui.inter.FragmentPresenter;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.manage_system.MyApp;
import com.manage_system.R;
import com.manage_system.ui.inter.IBase;
import com.manage_system.utils.DialogHelper;
import com.manage_system.utils.T;
import com.manage_system.widget.MultiStateView;
import com.manage_system.widget.SimpleMultiStateView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T1 extends BaseContract.BasePresenter> extends SupportFragment implements IBase, BaseContract.BaseView,FragmentPresenter {

    protected Context mContext;
    protected View mRootView;
    protected Dialog mLoadingDialog = null;
    Unbinder unbinder;
    private static final String TAG = "BaseFragment";

    @Nullable
    @Inject
    protected T1 mPresenter;

    @Nullable
    @BindView(R.id.SimpleMultiStateView)
    SimpleMultiStateView mSimpleMultiStateView;

    /**
     * 添加该Fragment的Activity
     * @warn 不能在子类中创建
     */
    protected BaseActivity context = null;

    /**
     * 布局解释器
     * @warn 不能在子类中创建
     */
    protected LayoutInflater inflater = null;

    /**
     * 该Fragment全局视图
     * @must 非abstract子类的onCreateView中return view;
     * @warn 不能在子类中创建
     */
    protected View view = null;

    /**
     * 添加这个Fragment视图的布局
     * @warn 不能在子类中创建
     */
    @Nullable
    protected ViewGroup container = null;

    private boolean isAlive = false;

    /**
     * @must 在非abstract子类的onCreateView中super.onCreateView且return view;
     */
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
//        context = (BaseActivity) getActivity();
//        isAlive = true;
//
//        this.inflater = inflater;
//        this.container = container;
//
//        return view;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isAlive = true;
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = createView(inflater, container, savedInstanceState);
        }

        mContext = mRootView.getContext();
        mLoadingDialog = DialogHelper.getLoadingDialog(getActivity());
        return mRootView;
    }

    /**设置界面布局
     * @warn 最多调用一次
     * @param layoutResID
     * @use 在onCreateView后调用
     */
    public void setContentView(int layoutResID) {
        setContentView(inflater.inflate(layoutResID, container, false));
    }
    /**设置界面布局
     * @warn 最多调用一次
     * @param v
     * @use 在onCreateView后调用
     */
    public void setContentView(View v) {
        setContentView(v, null);
    }
    /**设置界面布局
     * @warn 最多调用一次
     * @param v
     * @param params
     * @use 在onCreateView后调用
     */
    public void setContentView(View v, ViewGroup.LayoutParams params) {
        view = v;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initInjector(MyApp.getInstance().getApplicationComponent());
        attachView();
        bindView(view, savedInstanceState);
        initStateView();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Nullable
    @Override
    public View getView() {
        return mRootView;
    }

    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onRetry() {

    }


    protected void showLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }

    protected void showLoadingDialog(String str) {
        if (mLoadingDialog != null) {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
            tv.setText(str);
            mLoadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }


    private void initStateView() {
        if (mSimpleMultiStateView == null) return;
        mSimpleMultiStateView.setEmptyResource(R.layout.view_empty)
                .setRetryResource(R.layout.view_retry)
                .setLoadingResource(R.layout.view_loading)
                .setNoNetResource(R.layout.view_nonet)
                .build()
                .setonReLoadlistener(new MultiStateView.onReLoadlistener() {
                    @Override
                    public void onReload() {
                        onRetry();
                    }
                });
    }

    @Override
    public void showLoading() {
        if (mSimpleMultiStateView != null) {
            mSimpleMultiStateView.showLoadingView();
        }
    }

    @Override
    public void showSuccess() {
        hideLoadingDialog();
        if (mSimpleMultiStateView != null) {
            mSimpleMultiStateView.showContent();
        }
    }

    @Override
    public void showFaild() {
        if (mSimpleMultiStateView != null) {
            mSimpleMultiStateView.showErrorView();
        }
    }

    @Override
    public void showNoNet() {
        if (mSimpleMultiStateView != null) {
            mSimpleMultiStateView.showNoNetView();
        }
    }

    protected void T(String string) {
        T.showShort(MyApp.getContext(), string);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        super.onDestroy();
        unbinder.unbind();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    public final boolean isAlive() {
        return isAlive && context != null;// & ! isRemoving();导致finish，onDestroy内runUiThread不可用
    }

    //进度弹窗<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /**展示加载进度条,无标题
     * @param stringResId
     */
    public void showProgressDialog(int stringResId){
        if (isAlive() == false) {
            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.showProgressDialog(context.getResources().getString(stringResId));
    }
    /**展示加载进度条,无标题
     * @param dialogMessage
     */
    public void showProgressDialog(String dialogMessage){
        if (isAlive() == false) {
            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.showProgressDialog(dialogMessage);
    }
    /**展示加载进度条
     * @param dialogTitle 标题
     * @param dialogMessage 信息
     */
    public void showProgressDialog(String dialogTitle, String dialogMessage){
        if (isAlive() == false) {
            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.showProgressDialog(dialogTitle, dialogMessage);
    }

    /** 隐藏加载进度
     */
    public void dismissProgressDialog(){
        if (isAlive() == false) {
            Log.w(TAG, "dismissProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.dismissProgressDialog();
    }
    //进度弹窗>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //show short toast<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /**快捷显示short toast方法，需要long toast就用 Toast.makeText(string, Toast.LENGTH_LONG).show(); ---不常用所以这个类里不写
     * @param stringResId
     */
    public void showShortToast(int stringResId) {
        if (isAlive() == false) {
            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.showShortToast(stringResId);
    }
    /**快捷显示short toast方法，需要long toast就用 Toast.makeText(string, Toast.LENGTH_LONG).show(); ---不常用所以这个类里不写
     * @param string
     */
    public void showShortToast(String string) {
        Log.w(TAG,"isAlive()====="+isAlive());
//        if (isAlive() == false) {
//            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
//            return;
//        }
        context.showShortToast(string);
    }
    /**快捷显示short toast方法，需要long toast就用 Toast.makeText(string, Toast.LENGTH_LONG).show(); ---不常用所以这个类里不写
     * @param string
     * @param isForceDismissProgressDialog
     */
    public void showShortToast(String string, boolean isForceDismissProgressDialog) {
        if (isAlive() == false) {
            Log.w(TAG, "showProgressDialog  isAlive() == false >> return;");
            return;
        }
        context.showShortToast(string, isForceDismissProgressDialog);
    }
    //show short toast>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //启动新Activity方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**打开新的Activity，向左滑入效果
     * @param intent
     */
    public void toActivity(Intent intent) {
        toActivity(intent, true);
    }
    /**打开新的Activity
     * @param intent
     * @param showAnimation
     */
    public void toActivity(Intent intent, boolean showAnimation) {
        toActivity(intent, -1, showAnimation);
    }
    /**打开新的Activity，向左滑入效果
     * @param intent
     * @param requestCode
     */
    public void toActivity(Intent intent, int requestCode) {
        toActivity(intent, requestCode, true);
    }
    /**打开新的Activity
     * @param intent
     * @param requestCode
     * @param showAnimation
     */
    public void toActivity(final Intent intent, final int requestCode, final boolean showAnimation) {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (intent == null) {
                    Log.w(TAG, "toActivity  intent == null >> return;");
                    return;
                }
                //fragment中使用context.startActivity会导致在fragment中不能正常接收onActivityResult
                if (requestCode < 0) {
                    startActivity(intent);
                } else {
                    startActivityForResult(intent, requestCode);
                }
                if (showAnimation) {
                    overridePendingTransition(R.anim.right_push_in, R.anim.hold);
                } else {
                    overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
                }
            }
        });
    }
    //启动新Activity方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**在UI线程中运行，建议用这个方法代替runOnUiThread
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (isAlive() == false) {
            Log.w(TAG, "runUiThread  isAlive() == false >> return;");
            return;
        }
        runOnUiThread(action);
    }

    /**运行线程
     * @param name
     * @param runnable
     * @return
     */
    public final Handler runThread(String name, Runnable runnable) {
        if (isAlive() == false) {
            Log.w(TAG, "runThread  isAlive() == false >> return null;");
            return null;
        }
        return context.runThread(name + hashCode(), runnable);//name, runnable);同一Activity出现多个同名Fragment可能会出错
    }

    //运行线程>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**通过id查找并获取控件，使用时不需要强转
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V extends View> V findView(int id) {
        return (V) view.findViewById(id);
    }
    /**通过id查找并获取控件，并setOnClickListener
     * @param id
     * @param l
     * @return
     */
    public <V extends View> V findView(int id, View.OnClickListener l) {
        V v = findView(id);
        v.setOnClickListener(l);
        return v;
    }
    /**通过id查找并获取控件，使用时不需要强转
     * @warn 调用前必须调用setContentView
     * @param id
     * @return
     */
    public <V extends View> V findViewById(int id) {
        return findView(id);
    }
    /**通过id查找并获取控件，并setOnClickListener
     * @param id
     * @param l
     * @return
     */
    public <V extends View> V findViewById(int id, View.OnClickListener l) {
        return findView(id, l);
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        /* compiled code */
    }

    public final void runOnUiThread(java.lang.Runnable action) { /* compiled code */ }

}
