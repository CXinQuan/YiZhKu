package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/14.
 */

public class LostFoundInfo extends BmobObject {

    /**
     * 男：1  女：0
     */
    int sex;
    String name;
    String phone;
    /**
     * 捡到：0  遗失：1
     */
    int type;
    String content;
    String thing;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }
}
