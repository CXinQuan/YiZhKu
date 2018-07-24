package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lenovo on 2018/7/15.
 */

public class OrderGood extends BmobObject {
    String name;
    int count;
    float price;
    BmobFile bitmap;
    String capacity;
    String father_id;

    public String getFather_id() {
        return father_id;
    }

    public void setFather_id(String father_id) {
        this.father_id = father_id;
    }

    public String getBitmapurl() {
        return bitmapurl;
    }

    public void setBitmapurl(String bitmapurl) {
        this.bitmapurl = bitmapurl;
    }

    String bitmapurl;

    public BmobFile getBitmap() {
        return bitmap;
    }

    public void setBitmap(BmobFile bitmap) {
        this.bitmap = bitmap;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
