package com.example.lenovo.yizhku;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import base.BaseActivity;
import bean.Suggestion;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import utils.SharePreferenceUtils;
import utils.VerificationCodeUtil;

/**
 * Created by lenovo on 2018/6/5.
 */

public class YJFKActivity extends BaseActivity {

    @BindView(R.id.iv_goback)
    ImageView iv_goback;
    @BindView(R.id.textarea_yjfk)
    EditText textarea_yjfk;
    @BindView(R.id.et_yanzhengma)
    EditText et_yanzhengma;
    @BindView(R.id.tv_yanzhengma)
    TextView tv_yanzhengma;
    @BindView(R.id.btn_submit)
    Button btn_submit;

    String yzm;
    Suggestion s = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.jyfk_layout);
        ButterKnife.bind(this);
        s = new Suggestion();
        yzm = VerificationCodeUtil.yanzhengma();
        tv_yanzhengma.setText(yzm);
    }

    @OnClick({R.id.btn_submit, R.id.iv_goback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            case R.id.btn_submit:
                if (textarea_yjfk.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "无法提交空白建议！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!et_yanzhengma.getText().toString().trim().equals(yzm)) {
                    Toast.makeText(this, "请正确填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String phone = (String) SharePreferenceUtils.get(this, "phone", "");
                if (phone.trim().equals("")) {
                    Toast.makeText(this, "账号异常，请重新登录后再进行反馈！", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_submit.setText("反馈中...");
                s.setPhone(phone);
                s.setSuggest(textarea_yjfk.getText().toString());
                s.save(new SaveListener<String>() {
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(YJFKActivity.this, "反馈成功！", Toast.LENGTH_SHORT).show();
                            et_yanzhengma.setText("");
                            textarea_yjfk.setText("");
                            yzm = VerificationCodeUtil.yanzhengma();
                            tv_yanzhengma.setText(yzm);
                        } else {
                            Toast.makeText(YJFKActivity.this, "反馈失败！", Toast.LENGTH_SHORT).show();
                        }
                        btn_submit.setText("提交");
                    }
                });
                break;
            case R.id.tv_yanzhengma:
                yzm = VerificationCodeUtil.yanzhengma();
                tv_yanzhengma.setText(yzm);
                break;
            default:
                break;
        }
    }

//    public String yanzhengma() {
//        Random random = new Random();
//        Set<Integer> set = new HashSet<Integer>();
//        while (set.size() < 4) {
//            int randomInt = random.nextInt(10);
//            set.add(randomInt);
//        }
//        StringBuffer sb = new StringBuffer();
//        for (Integer i : set) {
//            sb.append("" + i);
//        }
//        return sb.toString();
//    }

}
