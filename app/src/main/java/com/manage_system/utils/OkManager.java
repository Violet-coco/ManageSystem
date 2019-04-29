package com.manage_system.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.manage_system.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 封装工具类
 * 这一个类主要将OkHttp3工具类进行封装，用于对数据的传输，包括Spring,Json,img，表单等数据的提交与获取
 */
public class OkManager {
    private OkHttpClient client;
    private volatile static OkManager manager;   //防止多个线程访问时
    private final String TAG = OkManager.class.getSimpleName();  //获得类名
    private Handler handler;

    //提交json数据
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串数据
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    private OkManager() {
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    //采用单例模式获取对象
    public static OkManager getInstance() {
        OkManager instance = null;
        if (manager == null) {
            synchronized (OkManager.class) {                //同步代码块
                if (instance == null) {
                    instance = new OkManager();
                    manager = instance;
                }
            }
        }
        return instance;
    }

    /**
     * 请求返回的是JSON字符串
     *
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonStringMethod(final String jsonValue, final Fun1 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(jsonValue);
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    /**
     * 请求返回相应结果的是Json对象
     *
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonObjectMethod(final String jsonValue, final Fun4 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(new JSONObject(jsonValue));
                    } catch (JSONException e) {

                    }
                }
            }
        });
    }

    /**
     * 返回响应的对象是一个字节数组
     *
     * @param data
     * @param callBack
     */
    private void onSuccessByteMethod(final byte[] data, final Fun2 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(data);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void onSuccessImgMethod(final Bitmap bitmap, final Fun3 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(bitmap);
                    } catch (Exception e) {

                    }

                }
            }
        });
    }

    /**
     * 同步请求,在Android开发中不常用，因为会阻塞UI线程
     *
     * @param url
     * @return
     */

    public String synaGetByUrl(String url) {
        //构建一个Request请求
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();  //execute用于同步请求数据
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 异步请求,请求返回Json字符串
     *
     * @param url
     * @param callback
     */
    public void asyncJsonStringByURL(String url, final Fun1 callback) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            //enqueue是调用了一个入队的方法
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonStringMethod(response.body().string(), callback);
                }
            }
        });

    }

    /**
     * 异步请求，请求返回Json对象
     *
     * @param url
     * @param callback
     */

    public void asyncJsonObjectByUrl(String url, final Fun4 callback) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObjectMethod(response.body().string(), callback);
                }
            }
        });

    }

    /**
     * 异步请求，请求返回的byte字节数组
     *
     * @param url
     * @param callback
     */
    public void asyncGetByteByUrl(String url, final Fun2 callback) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessByteMethod(response.body().bytes(), callback);
                }
            }
        });
    }

    /**
     * 异步请求，请求返回图片
     *
     * @param url
     * @param callback
     */
    public void asyncDownLoadImgtByUrl(String url, final Fun3 callback) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    byte[] data = response.body().bytes();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    onSuccessImgMethod(bitmap, callback);

                    System.out.println(data.length);
                }
            }
        });

    }

    /**
     * 模拟表单的提交
     *
     * @param url
     * @param param
     * @param callback
     */
    public void sendComplexForm(String url, Map<String, String> param, final Fun4 callback) {
        FormBody.Builder form_builder = new FormBody.Builder();  //表单对象，包含以input开始的对象，模拟一个表单操作，以HTML表单为主
        //如果键值对不为空，且值不为空
        if (param != null && !param.isEmpty()) {
            //循环这个表单，zengqiang for循环
            for (Map.Entry<String, String> entry : param.entrySet()) {
                form_builder.add(entry.getKey(), entry.getValue());
            }
        }
        //声明一个请求对象体
        RequestBody request_body = form_builder.build();
        //采用post的方式进行提交
        SharedPreferences sp=MyApp.getAppContext().getSharedPreferences("loginInfo", MODE_PRIVATE);

        Request request = new Request.Builder().header("token",sp.getString("token" , "")).url(url).post(request_body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObjectMethod(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * 向服务器提交String请求
     *
     * @param url
     * @param content
     * @param callback
     */
    public void sendStringByPost(String url, String content, final Fun4 callback) {
        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, content)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObjectMethod(response.body().string(), callback);
                }
            }
        });

    }

    //回调
    public interface Fun1 {
        void onResponse(String result);
    }

    interface Fun2 {
        void onResponse(byte[] result);
    }

    public interface Fun3 {
        void onResponse(Bitmap bitmap);
    }

    public interface Fun4 {
        void onResponse(JSONObject jsonObject);
    }


}
