package com.example.lenovo.yizhku;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import global.Constants;
import utils.SMSUtil;

public class DormNumberActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.et_ssh)
    EditText etSsh;

    int floor=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_number);
        ButterKnife.bind(this);

        floor=getIntent().getIntExtra(Constants.USER_FLOOR,-1);
        if(floor==-1){
            Toast.makeText(this, "请重新填写对应的楼层号!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etSsh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //当文本有变化后,如果文本内容不是空的,可以显示x号让,用户可以点击删除
                String ssh = etSsh.getText().toString();
                if (!TextUtils.isEmpty(ssh)) {
                    //显示x,提示用户可以删除
                    tvSave.setVisibility(View.VISIBLE);
                } else {
                    tvSave.setVisibility(View.GONE);
                }
            }
        });

        MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();
        etSsh.setOnFocusChangeListener(myOnFocusChangeListener);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SMSUtil.isRightDormNmber(floor,etSsh.getText().toString().trim())){
                    Toast.makeText(DormNumberActivity.this, "宿舍号与楼层不对应", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (!SMSUtil.isDormNmber(etSsh.getText().toString().trim())) {
//                    Toast.makeText(DormNumberActivity.this, "请正确填写宿舍号", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.USER_DORM_NUMBER, etSsh.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.et_ssh) {
                //v就是注册此焦点变化监听的控件
                //hasFocus 在回调此方法的时候,是否获取了焦点  true 有焦点  false没有焦点
                String ssh = etSsh.getText().toString();
                if (!TextUtils.isEmpty(ssh) && hasFocus) {
                    tvSave.setVisibility(View.VISIBLE);
                } else {
                    tvSave.setVisibility(View.GONE);
                }
            }
        }
    }

}
