package com.example.lenovo.yizhku;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import bean.User;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import global.Constants;
import manager.BmobManager;
import utils.SMSUtil;
import utils.SharePreferenceUtils;

public class RegisterActivity extends AppCompatActivity {
    public Context mCtx = this;
    private static final int KEEP_TIME_MIN = 100;
    private static final int RESET_TIME = 101;
    //发送验证码成功
    private static final int SEND_CODE_SUCCESS = 102;
    //发送验证码失败
    private static final int SEND_CODE_FAIL = 103;
    //检测验证码和手机能够匹配
    private static final int CHECK_CODE_SUCCESS = 104;
    //检测验证码和手机不能匹配
    private static final int CHECK_CODE_FAIL = 105;
    private ImageView ivUserBack;//返回
    private TextView ivUserPasswordLogin;//密码登录
    private EditText etUserPhone;//手机号码
    private TextView tvUserCode;//获取验证码
    private EditText etUserPsd;//密码
    private EditText etUserCode;//验证码
    private TextView login;//登录
    private int time = 60;
    private TextView tv_dormitory;
    private TextView tv_floor;
    private TextView tv_dorm_number;
    private EditText et_user_name;
    private RelativeLayout rl_ssl;
    private RelativeLayout rl_ssh;
    private RelativeLayout rl_lc;
    private RadioGroup rg_sex;


    private void initViews() {
        ivUserBack = (ImageView) findViewById(R.id.iv_user_back);
        ivUserPasswordLogin = (TextView) findViewById(R.id.iv_user_password_login);
        etUserPhone = (EditText) findViewById(R.id.et_user_phone);
        tvUserCode = (TextView) findViewById(R.id.tv_user_code);
        etUserPsd = (EditText) findViewById(R.id.et_user_psd);
        etUserCode = (EditText) findViewById(R.id.et_user_code);
        login = (TextView) findViewById(R.id.login);
        tv_dormitory = (TextView) findViewById(R.id.tv_dormitory);
        tv_floor = (TextView) findViewById(R.id.tv_floor);
        tv_dorm_number = (TextView) findViewById(R.id.tv_dorm_number);
        rl_ssl = (RelativeLayout) findViewById(R.id.rl_ssl);
        rl_ssh = (RelativeLayout) findViewById(R.id.rl_ssh);
        rl_lc = (RelativeLayout) findViewById(R.id.rl_lc);
        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        et_user_name = (EditText) findViewById(R.id.et_user_name);

        rl_ssl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDormitoryDialog();
            }
        });

        rl_lc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFloorDialog();
            }
        });

        rl_ssh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, DormNumberActivity.class);
                int floor = (int) SharePreferenceUtils.get(mCtx,Constants.USER_FLOOR, -1);
                if (floor == -1) {
                    Toast.makeText(mCtx, "请先填写对应的楼层！", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("floor", floor);
                startActivityForResult(intent, 1);
            }
        });

        //<editor-fold desc="点击获取验证码">
        tvUserCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sendCode(RegisterActivity.this);
                if (SMSUtil.judgePhoneNums(mCtx, etUserPhone.getText().toString().trim())) {
                    //请求发送短信
                    sendCode("86", etUserPhone.getText().toString());
                    //倒计时  timerTask  handler
                    new Thread() {
                        @Override
                        public void run() {
                            //每个1秒钟减少数组1
                            while (time > 0) {
                                //通过hanlder机制,告知主线程更新时间,更新时间周期,1秒钟更新一次
                                handler.sendEmptyMessage(KEEP_TIME_MIN);
                                try {
                                    Thread.sleep(999);//因为执行代码需要时间
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            //跳出循环，说明已经到达0秒了
                            //在60秒的计时过程中没有获取到验证码,重新获取验证码
                            handler.sendEmptyMessage(RESET_TIME);
                        }
                    }.start();
                } else {
                    Toast.makeText(RegisterActivity.this, "请正确输入手机号码！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //</editor-fold>

        //<editor-fold desc="点击登录按钮">
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_user_name.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "请先输入昵称！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etUserPhone.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "请先输入手机号码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etUserCode.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "请填写验证码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                login.setText("正在验证，请稍后...");
                submitCode("86", etUserPhone.getText().toString(), etUserCode.getText().toString());
            }
        });
        //</editor-fold>

        ivUserPasswordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ivUserBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String dorm_number = data.getStringExtra(Constants.USER_DORM_NUMBER);
            if (dorm_number != null) {
                tv_dorm_number.setText(dorm_number);
                SharePreferenceUtils.put(mCtx, Constants.USER_DORM_NUMBER, dorm_number);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEEP_TIME_MIN:
                    time--;
                    //时间更新在页面上
                    tvUserCode.setText("稍后再发(" + time + "s)");
                    break;
                case RESET_TIME:
                    //重新初始化time，等待下一次获取验证码
                    time = 60;
                    //时间更新在页面上
                    tvUserCode.setText("重新获取验证码");
                    break;
                case SEND_CODE_SUCCESS:
                    Toast.makeText(RegisterActivity.this, "验证码下发成功", Toast.LENGTH_SHORT).show();
                    break;
                case SEND_CODE_FAIL:
                    Toast.makeText(RegisterActivity.this, "验证码下发失败", Toast.LENGTH_SHORT).show();
                    break;
                case CHECK_CODE_SUCCESS:
//                    if (register(etUserPhone.getText().toString(), etUserPsd.getText().toString())) {
//                        //登录成功则将其进行保存
//                      SharePreferenceUtils.put(RegisterActivity.this, etUserPhone.getText().toString() + "," + etUserPsd.getText().toString(), "");
//                        Toast.makeText(RegisterActivity.this, "验证码验证通过", Toast.LENGTH_SHORT).show();
//                        //在此处可以做用户的注册,登录
//                        //向服务器发送了一个post请求,服务器指定的字段
//                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
                    /**
                     * 验证码   验证成功，先查  该用户是否存在
                     */
                    BmobQuery<User> query = new BmobQuery<User>();
                    //查询条件
                    query.addWhereEqualTo("phone", etUserPhone.getText().toString().trim());
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    //执行查询方法
                    query.findObjects(new FindListener<User>() {
                        public void done(List<User> object, BmobException e) {
                            if (e == null) {
                                Log.d("该用户存在", "该用户存在");
                                updateBmob(object.get(0).getObjectId(), object.get(0).getMoney(), etUserPsd.getText().toString().trim());
                            } else {
                                Log.d("该用户不存在", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                insertBmob(etUserPhone.getText().toString().trim(), etUserPsd.getText().toString().trim());
                            }
                        }
                    });
                    login.setText("注册");
                    break;
                case CHECK_CODE_FAIL:
                    Toast.makeText(RegisterActivity.this, "验证码验证失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 将手机号码和密码提交到Bmob，如果存在，则修改，不存在则插入
     */
    private boolean register(String phone, String password) {
        //1、先判断是否存在
        // 如果存在，则修改密码，不存在则插入用户数据
        //queryBmob(phone, password);
        return BmobManager.registerBmob(RegisterActivity.this, phone, password);
    }

    /**
     * 插入一个用户信息  ，  默认余额是  0元
     *
     * @param phone
     * @param password
     */
    public void insertBmob(final String phone, final String password) {
        final User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setDorm_number(tv_dorm_number.getText().toString().trim());
        user.setFloor(mCurrenWhich_Floor);
        user.setDormitory(tv_dormitory.getText().toString().trim());
        user.setMoney(0);//插入新用户，默认剩余  余额是0元
        user.setName(et_user_name.getText().toString().trim());
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.man:
                        user.setSex(Constants.SEX_MAN);
                        break;
                    case R.id.woman:
                        user.setSex(Constants.SEX_WOMAN);
                        break;
                }
            }
        });
        user.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(RegisterActivity.this, "成功创建账户：" + objectId, Toast.LENGTH_LONG).show();
                    Log.d("添加数据成功，返回objectId为：", objectId);


                    SharePreferenceUtils.saveUser(mCtx,user);
                    Intent intent = new Intent(mCtx, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "创建失败：" + objectId, Toast.LENGTH_LONG).show();
                    Log.d("创建数据失败：", e.getMessage());
                }
            }
        });
    }

    public void updateBmob(String id, float meney, final String password) {
        //必须更新一下获取到的 money，否则，会是默认值  0
        final User user = new User();
        user.setPassword(password);
        user.setPhone(etUserPhone.getText().toString().trim());
        user.setDorm_number(tv_dorm_number.getText().toString().trim());
        user.setFloor(mCurrenWhich_Floor);
        user.setDormitory(tv_dormitory.getText().toString().trim());
        user.setName(et_user_name.getText().toString().trim());
        user.setMoney(meney);
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.man:
                        user.setSex(Constants.SEX_MAN);
                        break;
                    case R.id.woman:
                        user.setSex(Constants.SEX_WOMAN);
                        break;
                }
            }
        });
        user.update(id, new UpdateListener() {
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("bmob", "更新成功");
                    Toast.makeText(mCtx, "更新成功", Toast.LENGTH_SHORT).show();
                    SharePreferenceUtils.saveUser(mCtx,user);
                    Intent intent = new Intent(mCtx, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //可视化
    public void sendCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                    Log.d("country:" + country, "phone:" + phone);

                } else {
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }

    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //给指定手机下发短信验证码的这个事件是成功的
                        handler.sendEmptyMessage(SEND_CODE_SUCCESS);
                    }
                } else {
                    // TODO 处理错误的结果
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //给指定手机下发短信验证码的这个事件是失败的
                        handler.sendEmptyMessage(SEND_CODE_FAIL);
                    }
                }
            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //验证码和手机号码是匹配的,做用户的注册和登录
                        handler.sendEmptyMessage(CHECK_CODE_SUCCESS);
                    }
                } else {
                    // TODO 失败 处理错误的结果
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //验证码和手机号码是不匹配的
                        handler.sendEmptyMessage(CHECK_CODE_FAIL);
                    }
                }
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    private int mTempWhich_Dormitory;// 记录临时选择的dormitory
    private int mCurrenWhich_Dormitory = 0;// 设置默认dormitory
    private int mTempWhich_Floor;// 记录临时选择的floor
    private int mCurrenWhich_Floor = 0;// 设置默认floor

    private void showChooseDormitoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择宿舍楼");
        final String[] dormitory = new String[]{"A栋", "B栋", "C栋", "D栋", "E栋", "F栋", "6栋", "7栋", "8栋"};
        builder.setSingleChoiceItems(dormitory, mCurrenWhich_Dormitory, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich_Dormitory = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_dormitory.setText(dormitory[mTempWhich_Dormitory]);
                mCurrenWhich_Dormitory = mTempWhich_Dormitory;
                SharePreferenceUtils.put(mCtx,Constants.USER_DORMITORY, dormitory[mTempWhich_Dormitory]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showChooseFloorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择楼层");
        final String[] floor = new String[]{"1楼", "2楼", "3楼", "4楼", "5楼", "6楼", "7楼", "8楼"};
        // final int[] floor = new int[]{1, 2, 3, 4, 5,6, 7, 8};
        builder.setSingleChoiceItems(floor, mCurrenWhich_Floor, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich_Floor = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_floor.setText(floor[mTempWhich_Floor]);
                mCurrenWhich_Floor = mTempWhich_Floor;
                SharePreferenceUtils.put(mCtx, Constants.USER_FLOOR, mTempWhich_Floor + 1);//floor[mTempWhich_Floor]
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }
}
