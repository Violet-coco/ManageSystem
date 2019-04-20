package com.manage_system.ui.base;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.manage_system.R;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.utils.CommonUtil;
import com.manage_system.utils.DataKeeper;


import java.io.File;

/**通用选择单张照片Activity,已自带选择弹窗
 * @author Lemon
 * @use
 * <br> toActivity或startActivityForResult (SelectPictureActivity.createIntent(...), requestCode);
 * <br> 然后在onActivityResult方法内
 * <br> data.getStringExtra(SelectPictureActivity.RESULT_PICTURE_PATH); 可得到图片存储路径
 */
public class SelectPictureActivity extends BaseActivity implements OnClickListener {
//	private static final String TAG = "SelectPictureActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * @param context
	 * @return
	 */
	private static final String TAG = "头像";
	public static Intent createIntent(Context context) {
		Log.w(TAG,"点击进去了");
		return new Intent(context, SelectPictureActivity.class);
	}

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w(TAG,"进入主页");
		setContentView(R.layout.select_picture_activity);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public void initView() {//必须调用

	}

	private String picturePath = "";

	@Override
	public int getContentLayout() {
		return R.layout.select_picture_activity;
	}

	@Override
	public void initInjector(ApplicationComponent appComponent) {

	}

	@Override
	public void bindView(View view, Bundle savedInstanceState) {

	}

	@Override
	public void initData() {//必须调用

	}

	private File cameraFile;
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtil.isExitsSdcard()) {
			showShortToast("SD卡不存在，不能拍照");
			return;
		}

		intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定调用相机拍照后照片的储存路径
		cameraFile = new File(DataKeeper.imagePath, "photo" + System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();

		Uri uri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(context, BaseApplication.getInstance().getApplicationInfo().packageName + ".fileProvider", cameraFile);
		} else {
			uri = Uri.fromFile(cameraFile);
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		toActivity(intent, REQUEST_CODE_CAMERA);
	}


	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		toActivity(intent, REQUEST_CODE_LOCAL);
	}

	public static final String RESULT_PICTURE_PATH = "RESULT_PICTURE_PATH";
	/**根据图库图片uri发送图片
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			picturePath = file.getAbsolutePath();
		}
		setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public void initEvent() {//必须调用

		Log.w(TAG,"拍照调用");

		findViewById(R.id.llSelectPictureBg).setOnClickListener(this);

		toActivity(new Intent(context, BottomMenuWindow.class)
		.putExtra(BottomMenuWindow.INTENT_TITLE, "选择图片")
		.putExtra(BottomMenuWindow.INTENT_ITEMS, new String[]{"拍照", "图库"})
		, REQUEST_TO_BOTTOM_MENU, false);
	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.llSelectPictureBg) {
			finish();
		}
	}



	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public static final int REQUEST_TO_BOTTOM_MENU = 10;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_TO_BOTTOM_MENU:
				if (data != null) {
					switch (data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, -1)) {
					case 0:
						selectPicFromCamera();//照相
						return;
					case 1:
						selectPicFromLocal();//从图库筛选
						return;
					default:
						break;
					}
				}
				break;
			case REQUEST_CODE_CAMERA: //发送照片
				if (cameraFile != null && cameraFile.exists()) {
					picturePath = cameraFile.getAbsolutePath();
					setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
				}
			case REQUEST_CODE_LOCAL: //发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
				break;
			default:
				break;
			}
		}

		finish();
	}

	@Override
	public void finish() {
		exitAnim = enterAnim = R.anim.null_anim;
		super.finish();
	}

	@Override
	public void onRetry() {

	}

}