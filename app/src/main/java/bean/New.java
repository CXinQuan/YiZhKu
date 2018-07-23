package bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2018/7/12.
 */

public class New extends BmobObject{
    String title;
    String url;
    String time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
