package bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/15.
 */

public class Order extends BmobObject {
    String phone;
    String name;
    int sex;
    List<OrderGood> list_order_good; //=new ArrayList<OrderGood>();
    //  地址     6 栋    6楼   601
    String dormitory;//宿舍楼
    int floor;//楼层
    String dorm_number;//宿舍号
    //String ordernumber;  //订单号
    String time;
    boolean isfinish;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isfinish() {
        return isfinish;
    }

    public void setIsfinish(boolean isfinish) {
        this.isfinish = isfinish;
    }



    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getDorm_number() {
        return dorm_number;
    }

    public void setDorm_number(String dorm_number) {
        this.dorm_number = dorm_number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public List<OrderGood> getList_order_good() {
        return list_order_good;
    }

    public void setList_order_good(List<OrderGood> list_order_good) {
        this.list_order_good = list_order_good;
    }
}
