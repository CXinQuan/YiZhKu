package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/20.
 */

public class ErrorInfo extends BmobObject{

    String errorinfo;
    String user_phone_info;

    public String getPhone_info() {
        return user_phone_info;
    }

    public void setPhone_info(String user_phone_info) {
        this.user_phone_info = user_phone_info;
    }

    public String getErrorinfo() {
        return errorinfo;
    }

    public void setErrorinfo(String errorinfo) {
        this.errorinfo = errorinfo;
    }
}
