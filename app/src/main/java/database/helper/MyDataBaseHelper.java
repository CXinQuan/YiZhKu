package database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import bean.OrderGood;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lenovo on 2018/7/17.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String NAME = "UserAddressInfo.db";
    public static final String CREATE_USERADDRESSINFO = "create table UserAddressInfo ("
            + "id integer primary key autoincrement,"  // id唯一标识
            + "isselect integer,"
            + "name text,"  //昵称
            + "sex integer,"//性别
            + "phone text,"//手机号码
            + "dormitory text,"//宿舍楼
            + "floor integer,"//楼层
            + "dorm_number text)";//宿舍号

    public static final String CREATE_ORDERINFO = "create table OrderInfo ("
            + "objectid text primary key,"//订单号
            + "dormitory text,"//宿舍楼
            + "floor integer,"//楼层
            + "dorm_number text,"//宿舍号
            + "phone text,"//手机号码
            + "name text,"
            + "sex integer,"
            + "isfinish integer,"//使用整型来表示是否送达  0表示未送到，1表示已送达
            + "order_time text )";//下单时间

    public static final String CREATE_ORDERGOODINFO = "create table OrderGoodInfo (" +
            "objectid text primary key," +   //订单号  唯一标识
            "name text," +    //水名
            "count integer," +  //数量
            "price float," +  //  价格
            "bitmap_url text," +  //图片的url
            "capacity text)";//容量

    public MyDataBaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USERADDRESSINFO);
        db.execSQL(CREATE_ORDERINFO);
        db.execSQL(CREATE_ORDERGOODINFO);
        Log.d("用户地址数据库创建成功", "用户地址数据库创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
