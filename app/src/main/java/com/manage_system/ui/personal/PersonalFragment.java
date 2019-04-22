package com.manage_system.ui.personal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.manage_system.BuildConfig;
import com.manage_system.EditPasswordActivity;
import com.manage_system.PersonInfoActivity;
import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.test.ClipImageActivity;
import com.manage_system.test.util.FileUtil;
import com.manage_system.test.view.CircleImageView;
import com.manage_system.ui.base.AlertDialog;
import com.manage_system.ui.base.AlertDialog.OnDialogButtonClickListener;
import com.manage_system.ui.base.BaseFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static com.manage_system.test.util.FileUtil.getRealFilePathFromUri;

public class PersonalFragment extends BaseFragment implements View.OnClickListener, OnDialogButtonClickListener {
    @BindView(R.id.person_icon)
    ImageView personIcon;
    @BindView(R.id.person_info)
    LinearLayout personInfo;
    @BindView(R.id.person_edit_password)
    LinearLayout personEditPassword;
    @BindView(R.id.person_exit)
    LinearLayout personExit;

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //头像1
    private CircleImageView person_icon;
    //头像2
//    private ImageView headImage2;
    //调用照相机返回图片文件
    private File tempFile;
    // 1: qq, 2: weixin
    private int type;

    public static PersonalFragment newInstance() {
        Bundle args = new Bundle();
        PersonalFragment fragment = new PersonalFragment();
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
//              ImageUtils.showImagePickDialog(this.getActivity());
//                intent.setClass(this.getActivity(), MainActivity1.class);
//                startActivity(intent);
                type = 1;
                uploadHeadImage();
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

    /**
     * 上传头像
     */
    private void uploadHeadImage() {
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_personal, null);
        person_icon = (CircleImageView) parent.findViewById(R.id.person_icon);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到相册
                    gotoPhoto();
                }
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    /**
     * 外部存储权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoPhoto();
            }
        }
    }


    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Log.d("evan", "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this.getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
//            Uri contentUri = FileProvider.getUriForFile(this.getActivity(),  "com.manage_system.fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == Activity.RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == Activity.RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    if (type == 1) {
                        Log.w(TAG,"确认选择头像！！");
                        Log.w(TAG,"11"+person_icon);
                        person_icon.setImageBitmap(bitMap);
                    } else {
//                        headImage2.setImageBitmap(bitMap);
                    }
                    //此处后面可以将bitMap转为二进制上传后台网络
                    //......

                }
                break;
        }
    }


    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }


    @Override
    public void onRetry() {

    }
}
