package com.example.lenovo.yizhku;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import utils.SMSUtil;
import utils.SharePreferenceUtils;

public class LoginActivity extends AppCompatActivity {
    private Context mCtx = this;
    @BindView(R.id.et_user_phone)
    EditText mEtUserPhone;
    @BindView(R.id.et_user_password)
    EditText mEtUserPassword;
    @BindView(R.id.tv_login)
    TextView mBtnLogin;
    @BindView(R.id.tv_new_user)
    TextView mTvNewUser;
    @BindView(R.id.tv_forget_password)
    TextView mTvForgetPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_forget_password, R.id.tv_new_user, R.id.tv_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_new_user:
            case R.id.tv_forget_password:
                Intent intent = new Intent(mCtx, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login:
                if (!SMSUtil.judgePhoneNums(mCtx, mEtUserPhone.getText().toString().trim())) {
                    Toast.makeText(mCtx, "请正确输入手机号码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEtUserPassword.getText().toString().trim() == null ||
                        TextUtils.isEmpty(mEtUserPassword.getText().toString().trim())) {
                    Toast.makeText(mCtx, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                mBtnLogin.setText("正在登录，请稍后...");
                BmobQuery<User> query = new BmobQuery<User>();
                //查询条件
                query.addWhereEqualTo("phone", mEtUserPhone.getText().toString().trim());
                query.addWhereEqualTo("password", mEtUserPassword.getText().toString().trim());
                //返回1条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(1);
                //执行查询方法
                query.findObjects(new FindListener<User>() {
                    public void done(List<User> object, BmobException e) {
                        if (e == null) {
                            Log.d("该用户存在", "该用户存在");
                            User user=object.get(0);
                            if(user!=null){
                                SharePreferenceUtils.saveUser(mCtx,user);
                            }
                            Toast.makeText(mCtx, "登录成功！", Toast.LENGTH_SHORT).show();
                            mBtnLogin.setText("登录");
                            Intent intent=new Intent(mCtx,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("该用户不存在", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            mBtnLogin.setText("登录");
                            Toast.makeText(mCtx, "账号或密码输入有误！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            break;
        }
    }


}
