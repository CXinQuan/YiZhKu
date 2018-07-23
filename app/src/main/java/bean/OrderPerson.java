package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/15.
 */

public class OrderPerson extends BmobObject{

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
