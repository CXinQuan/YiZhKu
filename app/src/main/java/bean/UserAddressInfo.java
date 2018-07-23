package bean;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/7/17.
 */

public class UserAddressInfo implements Serializable {
    int id;
    String name;//昵称
    int sex;//性别
    String phone;//手机号码
    String dormitory;//宿舍楼
    int floor;//楼层
    String dorm_number;//宿舍号
    boolean select;  //是否为默认   1 选中，0  未选择

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
