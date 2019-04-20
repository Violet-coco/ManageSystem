package com.manage_system.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.manage_system.PersonInfoActivity;
import com.manage_system.EditPasswordActivity;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.ui.base.AlertDialog;
import com.manage_system.ui.base.AlertDialog.OnDialogButtonClickListener;
import com.manage_system.ui.base.BaseActivity;
import com.manage_system.ui.base.BaseFragment;
import com.manage_system.ui.base.CutPictureActivity;
import com.manage_system.ui.base.SelectPictureActivity;
import com.manage_system.ui.inter.FragmentPresenter;
import com.manage_system.utils.DataKeeper;
import com.manage_system.utils.StringUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.manage_system.ui.inter.FragmentPresenter.RESULT_OK;

public class PersonalFragment extends BaseFragment implements View.OnClickListener, OnDialogButtonClickListener {
    @BindView(R.id.person_icon)
    ImageView personIcon;
    @BindView(R.id.person_info)
    LinearLayout personInfo;
    @BindView(R.id.person_edit_password)
    LinearLayout personEditPassword;
    @BindView(R.id.person_exit)
    LinearLayout personExit;

    public static PersonalFragment newInstance() {
        Bundle args = new Bundle();
        PersonalFragment fragment = new PersonalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ScrollView svDemoMain;
    ImageView person_icon;

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, PersonalFragment.class);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View square = inflater.inflate(R.layout.fragment_personal, container, false);

        person_icon = (ImageView)square.findViewById(R.id.person_icon);
        person_icon.setOnClickListener(this);
        return square;
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

    }

    private String picturePath;
    /**选择图片
     */
    private void selectPicture() {
//        intent.setClass(this.getActivity(), PersonInfoActivity.class);
//        startActivity(intent);
        toActivity(SelectPictureActivity.createIntent(context), REQUEST_TO_SELECT_PICTURE, false);
    }

    /**裁剪图片
     * @param path
     */
    private void cutPicture(String path) {
        if (StringUtil.isFilePath(path) == false) {
            Log.e(TAG, "cutPicture  StringUtil.isFilePath(path) == false >> showShortToast(找不到图片);return;");
            showShortToast("找不到图片");
            return;
        }
        this.picturePath = path;

        toActivity(CutPictureActivity.createIntent(context, path
                , DataKeeper.imagePath, "photo" + System.currentTimeMillis(), 200)
                , REQUEST_TO_CUT_PICTURE);
    }

    /**显示图片
     * @param path
     */
    private void setPicture(String path) {
        if (StringUtil.isFilePath(path) == false) {
            Log.e(TAG, "setPicture  StringUtil.isFilePath(path) == false >> showShortToast(找不到图片);return;");
            showShortToast("找不到图片");
            return;
        }
        this.picturePath = path;

        svDemoMain.smoothScrollTo(0, 0);
        Glide.with(context).load(path).into(person_icon);
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
                showShortToast("onClick  ivSettingHead");
                selectPicture();
                break;
            case R.id.person_info:
                showShortToast("onClick  1");
                intent.setClass(this.getActivity(), PersonInfoActivity.class);
                startActivity(intent);
//                toActivity(PersonInfoActivity.createIntent(context));
                break;
            case R.id.person_edit_password:
                showShortToast("onClick  2");
//                toActivity(EditPasswordActivity.createIntent(context));
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

    private static final int REQUEST_TO_SELECT_PICTURE = 20;
    private static final int REQUEST_TO_CUT_PICTURE = 21;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != FragmentPresenter.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TO_SELECT_PICTURE:
                if (data != null) {
                    cutPicture(data.getStringExtra(SelectPictureActivity.RESULT_PICTURE_PATH));
                }
                break;
            case REQUEST_TO_CUT_PICTURE:
                if (data != null) {
                    setPicture(data.getStringExtra(CutPictureActivity.RESULT_PICTURE_PATH));
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onRetry() {

    }
}
