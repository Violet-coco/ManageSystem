package com.manage_system;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.component.ApplicationComponent;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.base.BaseActivity;
import com.manage_system.ui.common.helper.UiHelper;
import com.manage_system.utils.OkManager;
import com.manage_system.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class SplashActivity extends BaseActivity {

    private RelativeLayout ll;
    private TextView[] ts;
    private String TAG = "SplashActivity";


    private void startTextInAnim(TextView t) {
        Random r = new Random();
        DisplayMetrics metrics = Utils.getMetrics(this);
        int x = r.nextInt(metrics.widthPixels * 4 / 3);   //1440不在内，只产生0-1439   乐事:1080*1920
        int y = r.nextInt(metrics.heightPixels * 4 / 3);  //2560
        float s = r.nextFloat() + 4.0f;  //r.nextFloat会产生一个0.0-1.0之间的  float
        ValueAnimator tranY = ObjectAnimator.ofFloat(t, "translationY", y - t.getY() , 0);//
        ValueAnimator tranX = ObjectAnimator.ofFloat(t, "translationX", x - t.getX(), 0);//
        ValueAnimator scaleX = ObjectAnimator.ofFloat(t, "scaleX", s, 1.0f);
        ValueAnimator scaleY = ObjectAnimator.ofFloat(t, "scaleY", s, 1.0f);
        ValueAnimator alpha = ObjectAnimator.ofFloat(t, "alpha", 0.0f, 1.0f);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(1800);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.play(tranX).with(tranY).with(scaleX).with(scaleY).with(alpha);
        if (t == findViewById(R.id.tv7)) {
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startImgAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        set.start();
    }

    private void startImgAnim() {

        ImageView image = (ImageView) findViewById(R.id.splash_logo);
        image.setVisibility(View.VISIBLE);
        ValueAnimator alpha = ObjectAnimator.ofFloat(image, "alpha", 0.0f, 1.0f);
        ValueAnimator tranY = ObjectAnimator.ofFloat(image, "translationY", -image.getHeight() / 2, 0);
        AnimatorSet set = new AnimatorSet();
        set.play(tranY).with(alpha);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
                        //sp.getString() userName, "";
                        if(sp.getString("token","").isEmpty()){
                            UiHelper.skipToOtherActivity(SplashActivity.this, LoginActivity.class);
                        }else{
                            initNewsData();
                            UiHelper.skipToOtherActivity(SplashActivity.this, MainActivity.class);
                        }
                    }
                }, 2000);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ll = (RelativeLayout)findViewById(R.id.ll);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{
                        getResources().getColor(R.color.xblue),
                        getResources().getColor(R.color.xxblue)
                });
//        ll.setBackground(gradientDrawable);
        ts = new TextView[]{
                (TextView) findViewById(R.id.tv1),
                (TextView) findViewById(R.id.tv2),
                (TextView) findViewById(R.id.tv3),
                (TextView) findViewById(R.id.tv4),
                (TextView) findViewById(R.id.tv5),
                (TextView) findViewById(R.id.tv6),
                (TextView) findViewById(R.id.tv7)
        };
        ts[0].post(new Runnable() {
            @Override
            public void run() {
                for (TextView t : ts) {
                    t.setVisibility(View.VISIBLE);
                    startTextInAnim(t);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    protected boolean isNeedToBeSubscriber() {
        return false;
    }

    @Override
    public void onRetry() {

    }

    public void initNewsData() {
        OkManager manager = OkManager.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("limit","20");
        manager.post(ApiConstants.commonApi + "/showAllNews", map,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                Log.e(TAG,responseBody);
                final JSONObject obj = JSON.parseObject(responseBody);
                if(obj.get("statusCode").equals(100)){
                    SharedPreferences sp=getSharedPreferences("processData", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("news_list", obj.toString());
                    //提交修改
                    editor.commit();
                }
            }
        });
    }
}
