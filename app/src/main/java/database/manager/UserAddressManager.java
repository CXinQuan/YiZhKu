package database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bean.UserAddressInfo;
import database.helper.MyDataBaseHelper;

/**
 * Created by lenovo on 2018/7/17.
 */

public class UserAddressManager {

    public static final String TABLENAME = "UserAddressInfo";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SEX = "sex";
    public static final String PHONE = "phone";
    public static final String DORMITORY = "dormitory";
    public static final String FLOOR = "floor";
    public static final String DORM_NUMBER = "dorm_number";
    public static final String SELECT = "isselect";

    private static UserAddressManager manager;
    private static SQLiteDatabase db;
    static MyDataBaseHelper helper;


    private UserAddressManager(Context context) {
        helper = new MyDataBaseHelper(context);
    }

    //单例模式   懒汉式
    public static UserAddressManager getInstance(Context context) {
        if (manager == null) {
            synchronized (UserAddressManager.class) {
                if (manager == null) {
                    manager = new UserAddressManager(context);
                }
            }
        }
        return manager;
    }

    /**
     * 查询所有的用户地址信息
     */
    public static List<UserAddressInfo> queryAllUserAddressInfo() {
        List<UserAddressInfo> list = new ArrayList<UserAddressInfo>();
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UserAddressInfo userAddressInfo = new UserAddressInfo();
                userAddressInfo.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                userAddressInfo.setDormitory(cursor.getString(cursor.getColumnIndex(DORMITORY)));
                userAddressInfo.setFloor(cursor.getInt(cursor.getColumnIndex(FLOOR)));
                userAddressInfo.setDorm_number(cursor.getString(cursor.getColumnIndex(DORM_NUMBER)));
                userAddressInfo.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                userAddressInfo.setSex(cursor.getInt(cursor.getColumnIndex(SEX)));
                userAddressInfo.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                userAddressInfo.setSelect(cursor.getInt(cursor.getColumnIndex(SELECT)) == 1 ? true : false);

                list.add(userAddressInfo);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }
        return list;
    }

    public void closeDB() {
        db.close();
    }

    /**
     * 更新用户  是否 为默认地址
     *
     * @param objectId id 唯一标识
     */
    public void updateUserAddress_Select(int objectId) {
        db = helper.getWritableDatabase();
        //先将全部都不变成  0 ，非默认， 再将  objectId  项 设置为默认项
//        List<UserAddressInfo> userAddressInfos = queryAllUserAddressInfo();
//        for (UserAddressInfo userAddress : userAddressInfos) {
        ContentValues values_all = new ContentValues();
//            if (objectId == userAddress.getId()) {
//                values.put(SELECT, 1);
//            } else {
//                values.put(SELECT, 0);
//           }
        values_all.put(SELECT, 0);
        db.update(TABLENAME, values_all, null, null);

        //   }
        ContentValues values_objectId = new ContentValues();
        values_objectId.put(SELECT, 1);
        db.update(TABLENAME, values_objectId, ID + "=?", new String[]{objectId + ""});
        db.close();
    }

    /**
     * 修改 更新一条用户地址信息
     *
     * @param userAddressInfo
     */
    public void updateUserAddressInfo(UserAddressInfo userAddressInfo) {
        if (userAddressInfo != null) {
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PHONE, userAddressInfo.getPhone());
            values.put(NAME, userAddressInfo.getName());
            values.put(SEX, userAddressInfo.getSex());
            values.put(DORMITORY, userAddressInfo.getDormitory());
            values.put(FLOOR, userAddressInfo.getFloor());
            values.put(DORM_NUMBER, userAddressInfo.getDorm_number());
            values.put(SELECT, userAddressInfo.isSelect() ? 1 : 0);
            db.update(TABLENAME, values, ID + "=?", new String[]{userAddressInfo.getId() + ""});
            db.close();
        }
    }

    /**
     * 插入一条  用户地址信息
     *
     * @param userAddressInfo
     */
    public void saveUserAddressInfo(UserAddressInfo userAddressInfo) {
        if (userAddressInfo != null) {
            db = helper.getWritableDatabase();
            List<UserAddressInfo> list = queryAllUserAddressInfo();
            ContentValues values = new ContentValues();
            values.put(PHONE, userAddressInfo.getPhone());
            values.put(NAME, userAddressInfo.getName());
            values.put(SEX, userAddressInfo.getSex());
            values.put(DORMITORY, userAddressInfo.getDormitory());
            values.put(FLOOR, userAddressInfo.getFloor());
            values.put(DORM_NUMBER, userAddressInfo.getDorm_number());
            if (list.size() == 0) {    //如果插入的是第一条数据，那么设置为默认地址，如果不是  第一次插入，那么就设置为  非默认地址
                values.put(SELECT, 1);
            } else {
                values.put(SELECT, 0);
            }
            db.insert(TABLENAME, null, values);
            db.close();
        }

    }

    /**
     * 删除一条用户地址信息
     *
     * @param id 唯一性标识
     */
    public void deleteUserAddressInfo(int id) {
        db = helper.getWritableDatabase();
        db.delete(TABLENAME, ID + "=?", new String[]{id + ""});
        db.close();
    }


}
