package com.example.lenovo.yizhku;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewDetailActivity extends BaseActivity {
    @BindView(R.id.pb_new)
    ProgressBar pbNew;
    @BindView(R.id.wv_new)
    WebView wvNew;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    private String mUrl;
    WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);
        ButterKnife.bind(this);
        mUrl = getIntent().getStringExtra("new_url");
        settings = wvNew.getSettings();
        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
        settings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
        settings.setJavaScriptEnabled(true);// 支持js功能
        settings.setBlockNetworkImage(false); //设置网页在加载的时候暂时不加载图片
        wvNew.loadUrl(mUrl);
        wvNew.setWebViewClient(new WebViewClient() {
            // 开始加载网页
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbNew.setVisibility(View.VISIBLE);
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbNew.setVisibility(View.GONE);
            }

            // 所有链接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("跳转链接:" + url);
                view.loadUrl(url);// 在跳转链接时强制在当前webview中加载
                return true;
            }
        });

        wvNew.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                System.out.println("进度:" + newProgress);
                if (newProgress >= 50) {
                    pbNew.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 网页标题
                System.out.println("网页标题:" + title);
            }
        });

    }

    @OnClick({R.id.iv_back})
    public void omClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


}
