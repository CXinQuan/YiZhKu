package page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.yizhku.DormNumberActivity;
import com.example.lenovo.yizhku.LoginActivity;
import com.example.lenovo.yizhku.MainActivity;
import com.example.lenovo.yizhku.R;
import com.example.lenovo.yizhku.YJFKActivity;

import java.util.List;

import base.BasePager;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import global.Constants;
import utils.SMSUtil;
import utils.SharePreferenceUtils;

/**
 * Created by lenovo on 2018/7/12.
 */

public class MyPager extends BasePager {
    View view;
    private RelativeLayout mRlSsh;
    private RelativeLayout mRlSsl;
    private RelativeLayout mRlYjfk;
    private RelativeLayout mRlLc;
    private ImageView mIvUserHead;
    private TextView mTvUserName;
    private TextView mTvSave;
    private TextView mTvUserPhoneNumber;
    private TextView mTvUserMoney;
    private TextView mTvUserDormitory;
    private TextView mTvUserFloor;
    private TextView mTvUserDormNumber;
    private Button mBtnLogout;
    private TextView tv_save;

    private float money = -1;
    User user = null;
    private String userid;
    private boolean isSaving = false;
    private String password;
    private int sex;

    private int mTempWhich_Dormitory;// 记录临时选择的dormitory
    private int mCurrenWhich_Dormitory = 0;// 设置默认dormitory
    private int mTempWhich_Floor;// 记录临时选择的floor
    private int mCurrenWhich_Floor = 0;// 设置默认floor
    String[] dormitorys = null;
    String[] floor = null;
    private String phone;
    private String dormitory;
    private int mfloor;
    private String dorm_number;
    private String name;


    public MyPager(Context mCtx) {
        super(mCtx);
    }

    public void initData() {
        phone = (String) SharePreferenceUtils.get(mCtx, Constants.USER_PHONE, "00000000000");
        dormitory = (String) SharePreferenceUtils.get(mCtx, Constants.USER_DORMITORY, "");
        mfloor = (int) SharePreferenceUtils.get(mCtx, Constants.USER_FLOOR, 0);
        dorm_number = (String) SharePreferenceUtils.get(mCtx, Constants.USER_DORM_NUMBER, "000");
        name = (String) SharePreferenceUtils.get(mCtx, Constants.USER_NAME, "账户异常");
        sex= (int) SharePreferenceUtils.get(mCtx,Constants.USER_SEX,-1);
        //网络获取不到才显示这些数据
        mTvUserPhoneNumber.setText(SMSUtil.encryptionPhone(phone));
        mTvUserMoney.setText(money + "元");
        mTvUserDormitory.setText(dormitory);
        mTvUserFloor.setText(mfloor + "楼");
        mTvUserDormNumber.setText(dorm_number);
        mTvUserName.setText(name);
        if(sex==1){
            mIvUserHead.setImageResource(R.mipmap.boy);
        }else if(sex==0){
            mIvUserHead.setImageResource(R.mipmap.girl);
        }else{
            mIvUserHead.setImageResource(R.mipmap.head_1);
        }
        setUserNameColor(mTvUserName);

        BmobQuery<User> query = new BmobQuery<User>();
        query.setLimit(1);
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<User>() {
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    Log.d("MyPager查询成功：共" + object.size(), "条数据。");
                    user = object.get(0);
                    userid = object.get(0).getObjectId();
                    password = object.get(0).getPassword();
                    if (user != null) {
                        phone = user.getPhone();
                        dormitory = user.getDormitory();
                        mfloor = user.getFloor();
                        dorm_number = user.getDorm_number();
                        money = user.getMoney();
                        name = user.getName();
                        SharePreferenceUtils.saveUser(mCtx, user);
                    }
                    //由于子线程的问题，这里必须添加setText，否则，下面主线程的setText先执行，到导致数据不是显示网络加载的数据
                    mTvUserPhoneNumber.setText(SMSUtil.encryptionPhone(phone));
                    mTvUserMoney.setText(money + "元");
                    mTvUserDormitory.setText(dormitory);
                    mTvUserFloor.setText(mfloor + "楼");
                    mTvUserDormNumber.setText(dorm_number);
                    mTvUserName.setText(name);
                    setUserNameColor(mTvUserName);
                } else {
                    Log.i("bmob", "MyPager失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(mCtx, "服务器获取数据失败！", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    /**
     * 后期   下单时会用到
     * @param money
     */
    public void updateMoney(float money) {
        if (money != -1.0f) {
            mTvUserMoney.setText(money + "元");
        }
    }

    private void showChooseDormitoryDialog() {
        dormitorys = new String[]{"A栋", "B栋", "C栋", "D栋", "E栋", "F栋", "6栋", "7栋", "8栋"};
        dormitory = (String) SharePreferenceUtils.get(mCtx, Constants.USER_DORMITORY, "");
        //找出手机 保存的是  哪一栋楼，将其设置到  对话框的默认项
        if (!dormitory.equals("")) {
            for (int i = 0; i < dormitorys.length; i++) {
                if (dormitory.equals(dormitorys[i].trim())) {
                    mCurrenWhich_Dormitory = i;
                    break;
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setTitle("选择宿舍楼");
        builder.setIcon(R.mipmap.dormitory3);
        builder.setSingleChoiceItems(dormitorys, mCurrenWhich_Dormitory, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich_Dormitory = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTvUserDormitory.setText(dormitorys[mTempWhich_Dormitory]);
                mCurrenWhich_Dormitory = mTempWhich_Dormitory;
                if (!dormitorys[mTempWhich_Dormitory].equals(dormitory)) {
                    tv_save.setVisibility(View.VISIBLE);
                }
                SharePreferenceUtils.put(mCtx, Constants.USER_DORMITORY, dormitorys[mTempWhich_Dormitory]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showChooseFloorDialog() {
        mCurrenWhich_Floor = (int) SharePreferenceUtils.get(mCtx, Constants.USER_FLOOR, 0) - 1;
        floor = new String[]{"1楼", "2楼", "3楼", "4楼", "5楼", "6楼", "7楼", "8楼"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setTitle("选择楼层");
        builder.setIcon(R.mipmap.floor);
        builder.setSingleChoiceItems(floor, mCurrenWhich_Floor, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich_Floor = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTvUserFloor.setText(floor[mTempWhich_Floor]);
                mCurrenWhich_Floor = mTempWhich_Floor;
                if (mfloor != (mTempWhich_Floor + 1)) {  // mfloor 是保存在手机的持久化数据，如果不一样的话，则显示保存 按钮
                    tv_save.setVisibility(View.VISIBLE);
                }
                SharePreferenceUtils.put(mCtx, Constants.USER_FLOOR, mTempWhich_Floor + 1);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public View addToView() {
        View view = View.inflate(mCtx, R.layout.mypager, null);
        initViews(view);

        return view;
    }

    private void setUserNameColor(TextView tv_user_name) {
        ForegroundColorSpan user_name_Span = new ForegroundColorSpan(mCtx.getResources().getColor(R.color.user_name_color));
        //这里注意一定要先给textview赋值
        String name = tv_user_name.getText().toString().trim();
        SpannableStringBuilder builder = new SpannableStringBuilder(name);
        //四个参数分别为，颜色值，起始位置，结束位置，最后的为类型。
        //将颜色值设置为builder
        builder.setSpan(user_name_Span, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //最后为textview赋值
        tv_user_name.setText(builder);
    }

    private void initViews(View view) {
        mIvUserHead = (ImageView) view.findViewById(R.id.iv_user_head);
        mTvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        mTvSave = (TextView) view.findViewById(R.id.tv_save);
        mTvUserPhoneNumber = (TextView) view.findViewById(R.id.tv_user_phone_number);
        mTvUserMoney = (TextView) view.findViewById(R.id.tv_user_money);
        mRlSsl = (RelativeLayout) view.findViewById(R.id.rl_ssl);
        mTvUserDormitory = (TextView) view.findViewById(R.id.tv_user_dormitory);
        mRlLc = (RelativeLayout) view.findViewById(R.id.rl_lc);
        mTvUserFloor = (TextView) view.findViewById(R.id.tv_user_floor);
        mRlSsh = (RelativeLayout) view.findViewById(R.id.rl_ssh);
        mTvUserDormNumber = (TextView) view.findViewById(R.id.tv_user_dorm_number);
        mRlYjfk = (RelativeLayout) view.findViewById(R.id.rl_yjfk);
        mBtnLogout = (Button) view.findViewById(R.id.btn_logout);
        tv_save = (TextView) view.findViewById(R.id.tv_save);

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceUtils.put(mCtx, Constants.USER_PHONE, "00000000000");
                SharePreferenceUtils.put(mCtx, Constants.USER_DORMITORY, "0");
                SharePreferenceUtils.put(mCtx, Constants.USER_FLOOR, 0);
                SharePreferenceUtils.put(mCtx, Constants.USER_DORM_NUMBER, "000");
                SharePreferenceUtils.put(mCtx, Constants.USER_SEX,-1);
                SharePreferenceUtils.put(mCtx, Constants.USER, "");
                Intent intent = new Intent(mCtx, LoginActivity.class);
                mCtx.startActivity(intent);
                ((MainActivity) mCtx).finish();
            }
        });


        mRlSsl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDormitoryDialog();
            }
        });

        mRlLc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFloorDialog();
            }
        });

        mRlSsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, DormNumberActivity.class);
                int floor = (int) SharePreferenceUtils.get(mCtx, Constants.USER_FLOOR, -1);
                if (floor == -1) {
                    Toast.makeText(mCtx, "请先填写对应的楼层！", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(Constants.USER_FLOOR, floor);
                ((MainActivity) mCtx).startActivityForResult(intent, 2);
            }
        });
        mRlYjfk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, YJFKActivity.class);
                mCtx.startActivity(intent);
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSaving) {
                    //再判断一次是不是表示同一层，因为有可能是先填宿舍号再填楼层，这里不能在点击修改宿舍号的时候判断是否有修改楼层，因为有可能楼层是不用修改的
                    if (!SMSUtil.isRightDormNmber((int) SharePreferenceUtils.get(mCtx, Constants.USER_FLOOR, 0), (String) SharePreferenceUtils.get(mCtx, Constants.USER_DORM_NUMBER, "000"))) {
                        Toast.makeText(mCtx, "宿舍号和楼层号不匹配", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isSaving = true;
                    if (money == -1) {  //有可能加载Money时网络异常，如果不判断的话，那么就有可能 -1 会替代掉Bmob后台存储的Money，就会导致无法想象的后果，用户的钱无端端就没了
                        Toast.makeText(mCtx, "您当前的余额为异常，请重新登录后再试！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = new User();
                    user.setDormitory((String) SharePreferenceUtils.get(mCtx, Constants.USER_DORMITORY, ""));
                    user.setFloor((int) SharePreferenceUtils.get(mCtx, Constants.USER_FLOOR, 0));
                    user.setDorm_number((String) SharePreferenceUtils.get(mCtx, Constants.USER_DORM_NUMBER, "000"));
                    user.setSex((int) SharePreferenceUtils.get(mCtx,Constants.USER_SEX,-1));
                    user.setPassword(password);
                    user.setPhone(phone);
                    user.setMoney(money);
                    user.update(userid, new UpdateListener() {
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d("bmob", "更新成功");
                                Toast.makeText(mCtx, "信息更新成功！", Toast.LENGTH_SHORT).show();

                                tv_save.setVisibility(View.INVISIBLE);
                            } else {
                                Log.d("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                Toast.makeText(mCtx, "网络异常，无法更新信息！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                isSaving = false;
            }
        });
    }


}
