package com.example.lenovo.yizhku;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import base.BaseActivity;
import global.Constants;

public class LargeImageViewActivity extends BaseActivity {

    private ImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_view);

        String url= getIntent().getStringExtra(Constants.IMAGEVIEW_URL);
        ImageView imageView= (ImageView) findViewById(R.id.iv_large);
      //  imageView.setImageResource(R.mipmap.boy);
      //  x.image().bind(imageView,Constants.IMAGEVIEW_URL_VALUE,imageOptions);

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 注意这里不使用finish
                ActivityCompat.finishAfterTransition(LargeImageViewActivity.this);
            }
        });
    }



    private void initImageOptions() {
        imageOptions = new ImageOptions.Builder()
//                .setSize(DensityUtil.dip2px(70), DensityUtil.dip2px(70))//图片大小
//                .setRadius(DensityUtil.dip2px(35))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.water)//加载中默认显示图片
                .setFailureDrawableId(R.mipmap.water)//加载失败后默认显示图片
                .build();
    }
}
