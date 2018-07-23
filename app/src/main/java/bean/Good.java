package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lenovo on 2018/7/15.
 */

public class Good extends BmobObject{
    BmobFile bitmap;
    String name;
    String capacity;
    float price;

    public BmobFile getBitmap() {
        return bitmap;
    }

    public void setBitmap(BmobFile bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
