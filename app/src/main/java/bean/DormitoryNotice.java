package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/13.
 */

public class DormitoryNotice extends BmobObject{

    String who;
    String title;
    String content;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
