package database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bean.Order;
import bean.OrderGood;
import bean.UserAddressInfo;
import cn.bmob.v3.datatype.BmobFile;
import database.helper.MyDataBaseHelper;
import global.Constants;

/**
 * Created by lenovo on 2018/7/17.
 */

public class OrderManager {

    public static final String TABLENAME_ORDER = "OrderInfo";
    public static final String TABLENAME_ORDERGOOD = "OrderGoodInfo";
    public static final String ID = "objectid";
    public static final String NAME = "name";
    public static final String SEX = "sex";
    public static final String PHONE = "phone";
    public static final String DORMITORY = "dormitory";
    public static final String FLOOR = "floor";
    public static final String DORM_NUMBER = "dorm_number";
    public static final String ISFINISH = "isfinish";
    public static final String ORDER_TIME = "order_time";
    public static final String COUNT = "count";

    public static final String PRICE = "price";
    public static final String BITMAP_URL = "bitmap_url";
    public static final String CAPACITY = "capacity";


    private static OrderManager manager;
    private static SQLiteDatabase db;
    static MyDataBaseHelper helper;

    private OrderManager(Context context) {
        helper = new MyDataBaseHelper(context);
    }

    //单例模式   懒汉式
    public static OrderManager getInstance(Context context) {
        if (manager == null) {
            synchronized (OrderManager.class) {
                if (manager == null) {
                    manager = new OrderManager(context);
                }
            }
        }
        return manager;
    }

    // 开始增删改查

    /**
     * 保存一条   商品 信息
     *
     * @param list
     */
    public void saveOrderGoodList(List<OrderGood> list) {
        if (list != null) {
            db = helper.getWritableDatabase();
            for (OrderGood orderGood : list) {
                ContentValues values = new ContentValues();
                values.put(ID, orderGood.getFather_id());
                values.put(NAME, orderGood.getName());
                values.put(COUNT, orderGood.getCount());
                values.put(PRICE, orderGood.getPrice());
                values.put(BITMAP_URL, orderGood.getBitmap().getUrl());
                values.put(CAPACITY, orderGood.getCapacity());
                db.insert(TABLENAME_ORDERGOOD, null, values);
                //    db.close();
            }
        }
    }

    /**
     * 保存一条  订单信息
     *
     * @param order
     */
    public void saveOrder(Order order) {
        if (order != null) {
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID, order.getObjectId());
            values.put(NAME, order.getName());
            values.put(PHONE, order.getPhone());
            values.put(SEX, order.getSex());
            values.put(DORMITORY, order.getDormitory());
            values.put(FLOOR, order.getFloor());
            values.put(DORM_NUMBER, order.getDorm_number());
            values.put(ORDER_TIME, order.getTime());
            values.put(ISFINISH, order.isfinish() ? Constants.FINISH : Constants.UNFINISH);
            db.insert(TABLENAME_ORDER, null, values);
            saveOrderGoodList(order.getList_order_good());

            //  db.close();
        }
    }


    /**
     * 根据订单号查询  订单  商品 信息
     *
     * @param objectid
     * @return
     */
    public List<OrderGood> queryOrderGood(String objectid) {
        List<OrderGood> list = new ArrayList<OrderGood>();
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_ORDERGOOD, null, ID + "=?", new String[]{objectid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                OrderGood orderGood = new OrderGood();
                orderGood.setFather_id(cursor.getString(cursor.getColumnIndex(ID)));
                orderGood.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                orderGood.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
                orderGood.setPrice(cursor.getFloat(cursor.getColumnIndex(PRICE)));
                orderGood.setBitmapurl(cursor.getString(cursor.getColumnIndex(BITMAP_URL)));
                orderGood.setCapacity(cursor.getString(cursor.getColumnIndex(CAPACITY)));
                list.add(orderGood);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }
        return list;
    }


    /**
     * 查询所有  订单
     * 根据订单号  拿到  该 订单的  所有 商品信息
     */
    public List<Order> queryAllOrder() {
        List<Order> list = new ArrayList<Order>();
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_ORDER, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setObjectId(cursor.getString(cursor.getColumnIndex(ID)));
                order.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                order.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                order.setSex(cursor.getInt(cursor.getColumnIndex(SEX)));
                order.setDormitory(cursor.getString(cursor.getColumnIndex(DORMITORY)));
                order.setFloor(cursor.getInt(cursor.getColumnIndex(FLOOR)));
                order.setDorm_number(cursor.getString(cursor.getColumnIndex(DORM_NUMBER)));
                order.setTime(cursor.getString(cursor.getColumnIndex(ORDER_TIME)));
                order.setIsfinish(cursor.getInt(cursor.getColumnIndex(ISFINISH)) == Constants.FINISH ? true : false);
                order.setList_order_good(queryOrderGood(cursor.getString(cursor.getColumnIndex(ID))));
                int i= order.getList_order_good().size();
                list.add(order);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
            //db.close();
        }
        return list;
    }

    public static void close() {
        db.close();
    }


}
