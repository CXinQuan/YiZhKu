package database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bean.OrderGood;
import bean.RepairBean;
import cn.bmob.v3.datatype.BmobFile;
import database.helper.MyDataBaseHelper;

/**
 * Created by lenovo on 2018/7/23.
 */

public class RepairManager {
    /**
     * 维修  订单 数据库   字符串常量
     */
    public static final String ID = "id";
    public static final String PHONE = "phone";
    public static final String NAME = "name";
    public static final String REPAIRTYPE = "repairType";
    public static final String ADDRESS = "address";
    public static final String DESCRIBE = "describe";
    public static final String SERVICE_TIME = "service_time";
    public static final String STATE = "state";
    public static final String PHOTO = "photo";
    public static final String REPAIRORDERINFO = "RepairOrderInfo";



    private static RepairManager manager;
    private static SQLiteDatabase db;
    static MyDataBaseHelper helper;

    private RepairManager(Context context) {
        helper = new MyDataBaseHelper(context);
    }

    //单例模式   懒汉式
    public static RepairManager getInstance(Context context) {
        if (manager == null) {
            synchronized (RepairManager.class) {
                if (manager == null) {
                    manager = new RepairManager(context);
                }
            }
        }
        return manager;
    }

    // 开始增删改查

    /**
     * 保存一条   维修订单 信息
     *
     * @param
     */
    public void saveRepairInfo(RepairBean repairBean) {
        if (repairBean != null) {
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID, repairBean.getObjectId());
            values.put(NAME, repairBean.getName());
            values.put(PHONE, repairBean.getPhone());
            values.put(REPAIRTYPE, repairBean.getRepairType());
            values.put(ADDRESS, repairBean.getAddress());
            values.put(DESCRIBE, repairBean.getDescribe());
            values.put(SERVICE_TIME, repairBean.getService_time());
            values.put(STATE, repairBean.getState());
            values.put(PHOTO, repairBean.getPhoto().getUrl());
            db.insert(REPAIRORDERINFO, null, values);

        }
    }


    /**
     *   查询  所有 维修 订单
     */
    public List<RepairBean> queryRepairList(int start,int count) {
        List<RepairBean> list = new ArrayList<RepairBean>();
        db = helper.getReadableDatabase();
      //  Cursor cursor = db.query(REPAIRORDERINFO, null, null, null, null, null, "20");

        Cursor cursor = db.rawQuery("select * from RepairOrderInfo limit ?,?",new String[]{start+"",count+"" }) ;//限制返回 从start开始，返回count条数据


        Log.d("游标长度："+  cursor.getColumnCount(),"游标长度"+  cursor.getColumnCount());
        if (cursor.moveToFirst()) {
            do {
                RepairBean repairBean = new RepairBean();
                repairBean.setObjectId(cursor.getString(cursor.getColumnIndex(ID)));
                repairBean.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                repairBean.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                repairBean.setRepairType(cursor.getString(cursor.getColumnIndex(REPAIRTYPE)));
                repairBean.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
                repairBean.setDescribe(cursor.getString(cursor.getColumnIndex(DESCRIBE)));
                repairBean.setService_time(cursor.getString(cursor.getColumnIndex(SERVICE_TIME)));
                repairBean.setState(cursor.getInt(cursor.getColumnIndex(STATE)));
                repairBean.setPhotoUrl(cursor.getString(cursor.getColumnIndex(PHOTO)));
                list.add(repairBean);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }
        return list;
    }

    public static void closeDB(){
        db.close();
    }


}
