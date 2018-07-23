package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lenovo on 2018/7/22.
 */

public class RepairBean extends BmobObject {

    String repairType;  //维修类型
    String address;     //宿舍地址
    String describe;    //维修描述
    String phone;       //联系人电话
    String name;       //联系人名称
    String service_time;//预约上门维修时间
    int state;       //状态    已提交     已受理     已维修
    BmobFile photo;      //图片描述
    String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
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

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BmobFile getPhoto() {

        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
}
