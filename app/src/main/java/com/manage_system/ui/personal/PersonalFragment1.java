package com.manage_system.ui.personal;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.manage_system.PersonInfoActivity;
import com.manage_system.EditPasswordActivity;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.test.MainActivity1;
import com.manage_system.test.view.CircleImageView;
import com.manage_system.ui.base.AlertDialog;
import com.manage_system.ui.base.AlertDialog.OnDialogButtonClickListener;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.utils.ImageUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import com.manage_system.ui.inter.FragmentPresenter;

import java.io.File;

public class PersonalFragment1 extends BaseFragment implements OnDialogButtonClickListener {
    @BindView(R.id.person_icon)
    ImageView personIcon;
    @BindView(R.id.person_info)
    LinearLayout personInfo;
    @BindView(R.id.person_edit_password)
    LinearLayout personEditPassword;
    @BindView(R.id.person_exit)
    LinearLayout personExit;

    public static PersonalFragment1 newInstance() {
        Bundle args = new Bundle();
        PersonalFragment1 fragment = new PersonalFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_personal;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }



    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        Typeface mtypeface = Typeface.createFromAsset(getActivity().getAssets(), "font/consolab.ttf");
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "font/consola.ttf");
    }


    @Override
    public void onDialogButtonClick(int requestCode, boolean isPositive) {
        if (! isPositive) {
            return;
        }

        switch (requestCode) {
            case 0:
                logout();
                break;
            default:
                break;
        }
    }

    private void logout() {
        context.finish();
    }

    private static final String TAG = "个人页面";

    @OnClick({R.id.person_icon,R.id.person_info,R.id.person_edit_password,R.id.person_exit})
    public void onClick(View v) {//直接调用不会显示v被点击效果
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.person_icon:
//                ImageUtils.showImagePickDialog(this.getActivity());
                intent.setClass(this.getActivity(), MainActivity1.class);
                startActivity(intent);
                break;
            case R.id.person_info:
                intent.setClass(this.getActivity(), PersonInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.person_edit_password:
                intent.setClass(this.getActivity(), EditPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.person_exit:
                Log.w(TAG,"点击了");
                new AlertDialog(context, "退出登录", "确定退出登录？", true, 0, this).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

}
