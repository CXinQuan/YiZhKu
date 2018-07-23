package manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import utils.SMSUtil;

/**
 * Created by lenovo on 2018/6/1.
 */

public class BmobManager {

    public static boolean IS_REGISTER_SUCCESS = false;
    public static boolean IS_LOGIN_SUCCESS = false;
    public static boolean IS_UPDATE_SUCCESS = false;
    public static boolean IS_INSERT_SUCCESS = false;

    public static boolean insertBmob(final Context context, String phone, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(context, "成功创建账户：" + objectId, Toast.LENGTH_LONG).show();
                    Log.d("添加数据成功，返回objectId为：", objectId);
                    IS_INSERT_SUCCESS = true;
                } else {
                    Toast.makeText(context, "创建失败：" + objectId, Toast.LENGTH_LONG).show();
                    Log.d("创建数据失败：", e.getMessage());
                    IS_INSERT_SUCCESS = true;
                }
            }
        });
        return IS_INSERT_SUCCESS;
    }

    public static boolean registerBmob(final Context context, final String phone, final String password) {
        BmobQuery<User> query = new BmobQuery<User>();
        //查询条件
        query.addWhereEqualTo("phone", phone);
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    Log.d("该用户存在", "该用户存在");
                    IS_REGISTER_SUCCESS=updateBmob(object.get(0).getObjectId(), password);
                } else {
                    Log.d("该用户不存在", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    IS_REGISTER_SUCCESS=insertBmob(context, phone, password);
                }
            }
        });
       return IS_REGISTER_SUCCESS;
    }

    public static boolean updateBmob(String id, String password) {

        User user = new User();
        user.setPassword(password);
        user.update(id, new UpdateListener() {
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("bmob", "更新成功");
                    IS_UPDATE_SUCCESS = true;
                } else {
                    Log.d("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    IS_UPDATE_SUCCESS = false;
                }
            }
        });
        return IS_UPDATE_SUCCESS;
    }

    public static boolean loginBmob(final Context context, final String phone, final String password) {
        if (!SMSUtil.judgePhoneNums(context, phone)) {
            Toast.makeText(context, "请正确输入手机号码！", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "密码不能为空！", Toast.LENGTH_LONG).show();
            return false;
        }
        BmobQuery<User> query = new BmobQuery<User>();
        //查询条件
        query.addWhereEqualTo("phone", phone);
        query.addWhereEqualTo("password", password);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    Log.d("该用户存在", "该用户存在");
                    IS_LOGIN_SUCCESS = true;
                } else {
                    Log.d("该用户不存在", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    IS_LOGIN_SUCCESS = false;
                }
            }
        });
        return IS_LOGIN_SUCCESS;
    }

}
