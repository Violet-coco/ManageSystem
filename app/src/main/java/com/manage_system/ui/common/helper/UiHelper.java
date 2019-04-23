package com.manage_system.ui.common.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * <p>
 * Activity跳转工具类
 * </p>
 */

public class UiHelper {


    public UiHelper() {
        throw new UnsupportedOperationException("UiHelper不许被实例化！");
    }

    /**
     * 普通界面跳转
     *
     * @param context
     * @param clazz
     */
    public static void skipToOtherActivity(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * 普通界面跳转携带数据
     *
     * @param context
     * @param clazz
     * @param key
     * @param value
     */
    public static void skipToOtherActivityWithExtra(Context context, Class<? extends Activity> clazz, String key, String value) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }


    /**
     * 普通界面跳转不需要finsh
     *
     * @param context
     * @param clazz
     */
    public static void skipActivityNofinish(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

}
