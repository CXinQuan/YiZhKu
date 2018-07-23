package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/6/5.
 */

public class Suggestion extends BmobObject {

    private String suggest;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

}
