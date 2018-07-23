package com.example.lenovo.yizhku;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import base.BaseActivity;

public class NoticeDetailActivity extends BaseActivity {
    String who;
    String time;
    String title;
    String content;
    private TextView mTvWho;
    private TextView mTvTime;
    private TextView mTvTitle;
    private TextView mTvContent;
    private ImageView iv_back_notice_detail;

    private void initViews() {
        mTvWho = (TextView) findViewById(R.id.tv_who);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        iv_back_notice_detail = (ImageView) findViewById(R.id.iv_back_notice_detail);
        iv_back_notice_detail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("被点击 了","被点击 了");
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            who = intent.getStringExtra("notice_detail_who");
            title = intent.getStringExtra("notice_detail_title");
            time = intent.getStringExtra("notice_detail_time");
            content = intent.getStringExtra("notice_detail_content");
            if (who == null || title == null || time == null || content == null) {
                finish();
            }
            mTvWho.setText(who);
            mTvTime.setText(time);
            mTvTitle.setText(title);
            mTvContent.setText(content);
        }
    }

}
