package com.example.lenovo.yizhku;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import base.BaseActivity;
import global.Constants;
import utils.SharePreferenceUtils;

public class SplashActivity extends BaseActivity {
private Context mCtx=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ScaleAnimation animation_scale = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation animation_alpha = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation=new AnimationSet(true);
        animation.addAnimation(animation_alpha);
        animation.addAnimation(animation_scale);
        animation.setDuration(2000);
        animation.setInterpolator(new BounceInterpolator());
        animation.setFillAfter(true);  //停留在最后的位置，这样就不会在结束后还返回原始状态
        ImageView iv_logo = (ImageView)findViewById(R.id.iv_logo);
        iv_logo.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                Intent intent = null;
                // user应该包含  name + phone
                // sex + dormitory + floor + dorm_number  另外存储
                String user= (String) SharePreferenceUtils.get(mCtx, Constants.USER, "");
                if (user.equals("")) {
                    intent = new Intent(mCtx, LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(mCtx, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationStart(Animation animation) {
            }
        });
    }
}
