package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lenovo on 2018/7/12.
 */

public class LunBoTuWater extends BmobObject {
    BmobFile bitmap;

    public BmobFile getBitmap() {
        return bitmap;
    }

    public void setBitmap(BmobFile bitmap) {
        this.bitmap = bitmap;
    }
}
