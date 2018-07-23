package bean;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
    String name;//昵称
    int sex;//性别
    String phone;//手机号码
    String password;//密码
    String dormitory;//宿舍楼
    int floor;//楼层
    String dorm_number;//宿舍号
    float money;//余额

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

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
