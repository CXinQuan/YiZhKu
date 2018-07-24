package com.example.lenovo.yizhku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;
import java.util.Observer;

import base.BaseActivity;
import bean.Order;
import bean.OrderGood;
import bean.User;
import bean.UserAddressInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import database.manager.OrderManager;
import database.manager.UserAddressManager;
import global.Constants;
import interface_package.MyObserverManager;
import interface_package.MyOrderObserverManager;
import utils.SharePreferenceUtils;
import utils.TimeUtils;
import utils.UIUtils;

public class OrderDetailActivity extends BaseActivity  {

    @BindView(R.id.iv_goback)
    ImageView ivGoback;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.ll_user_address)
    LinearLayout llUserAddress;
    @BindView(R.id.tv_order_user_name)
    TextView tvOrderUserName;
    @BindView(R.id.tv_order_user_phone)
    TextView tvOrderUserPhone;
    @BindView(R.id.tv_order_address)
    TextView tvOrderAddress;
    @BindView(R.id.iv_changed_address)
    ImageView ivChangedAddress;
    @BindView(R.id.tv_add_user_address)
    TextView tvAddUserAddress;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.lv_order_detail)
    ListView lvOrderDetail;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.btn_sure_order)
    Button btnSureOrder;

    boolean hasAddress = false;
    float totalMoney;
    List<OrderGood> list_order_good;
    Order order;
    ImageOptions imageOptions;
    UserAddressManager manager;
    OrderManager orderManager;

    MyObserverManager myObserverManager;
    MyOrderObserverManager myOrderObserverManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        orderManager=OrderManager.getInstance(mCtx);

        myObserverManager=MyObserverManager.getInstance();

        myOrderObserverManager=MyOrderObserverManager.getInstance();

        order = (Order) getIntent().getSerializableExtra(Constants.ORDER_INFO);
        Log.d("订单详情对象" + order, "订单详情对象" + order);

        if (order != null && order.getList_order_good().size() > 0) {
            list_order_good = order.getList_order_good();
            for (OrderGood orderGood : list_order_good) {
                totalMoney += orderGood.getCount() * orderGood.getPrice();
            }
            if (totalMoney > 0) {
                tvTotalMoney.setText(" ￥ " + totalMoney);
            }
        }
        CharSequence str=UIUtils.setTextWithColor("下单时间："+TimeUtils.geDateTime(System.currentTimeMillis())+"\n"+"\n"+"商品详情：", Color.parseColor("#3f67f4"));
        tvOrderTime.setText(str);
        lvOrderDetail.setAdapter(new MyAdapter());
        measureListView(lvOrderDetail);
        //  需要使用一个变量来保存是否有地址 ，没有 则不能提交订单
        //  先遍历数据库的地址
        manager = UserAddressManager.getInstance(mCtx);
        //显示用户  收货地址
        showUserAddress(manager);
    }

    private void showUserAddress(UserAddressManager manager) {
        List<UserAddressInfo> userAddressInfos = manager.queryAllUserAddressInfo();
        UserAddressInfo user_select = null;
        if (userAddressInfos.size() == 0) {
            // 没有 地址,则提示用户要添加 选择地址
            hasAddress = false;
            llUserAddress.setVisibility(View.GONE);
            tvAddUserAddress.setVisibility(View.VISIBLE);
        } else {
            // 有的话，则查找默认地址
            hasAddress = true;
            llUserAddress.setVisibility(View.VISIBLE);
            tvAddUserAddress.setVisibility(View.GONE);
            for (UserAddressInfo user : userAddressInfos) {
                if (user.isSelect()) {
                    user_select = user;
                    break;
                }
            }
            if (user_select == null) {
                Toast.makeText(this, "请设置默认地址", Toast.LENGTH_SHORT).show();
                return;
            }
            order.setPhone(user_select.getPhone());
            order.setName(user_select.getName());
            order.setSex(user_select.getSex());
            order.setDorm_number(user_select.getDorm_number());
            order.setDormitory(user_select.getDormitory());
            order.setFloor(user_select.getFloor());

            if (user_select != null) {
                tvOrderUserName.setText("联系人："+order.getName() + (order.getSex() == Constants.SEX_MAN ? "（先生）" : "（女士）"));
                tvOrderUserPhone.setText("联系电话："+order.getPhone());
                tvOrderAddress.setText("收货地址：仲恺农业工程学院 " + order.getDormitory() + " " + order.getDorm_number());
            }
        }
    }

    @OnClick({R.id.iv_goback, R.id.btn_sure_order, R.id.iv_changed_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            case R.id.iv_changed_address:
                Intent intent = new Intent(mCtx, AddressListActivity.class);
                startActivityForResult(intent, Constants.CHANGE_ADDRESS_INFO_REQUEST_CODE);
                break;
            case R.id.btn_sure_order:
                if (!hasAddress) {
                    Toast.makeText(mCtx, "请选择收货地址！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //提交订单
                submitOrder(order);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.CHANGE_ADDRESS_INFO_REQUEST_CODE:
                    UserAddressInfo userAddressInfo = (UserAddressInfo) intent.getSerializableExtra(Constants.SELECT_ORDER_USER_INFO);
                    if (userAddressInfo != null) {
                        llUserAddress.setVisibility(View.VISIBLE);
                        tvAddUserAddress.setVisibility(View.GONE);
                        order.setPhone(userAddressInfo.getPhone());
                        order.setName(userAddressInfo.getName());
                        order.setSex(userAddressInfo.getSex());
                        order.setDorm_number(userAddressInfo.getDorm_number());
                        order.setDormitory(userAddressInfo.getDormitory());
                        order.setFloor(userAddressInfo.getFloor());
                        tvOrderUserName.setText("联系人："+order.getName() + (order.getSex() == Constants.SEX_MAN ? "（先生）" : "（女士）"));
                        tvOrderUserPhone.setText("联系电话："+order.getPhone());
                        tvOrderAddress.setText("收货地址：仲恺农业工程学院 " + order.getDormitory() + " " + order.getDorm_number());
                        //  showUserAddress(manager);
                        hasAddress = true;
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    //#####################需要对余额进行更新
    public void submitOrder(final Order order) {

        if (!hasAddress) {
            Toast.makeText(this, "请添加收货地址喔！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalMoney <= 0) {
            Toast.makeText(this, "订单出错，请重新下单！", Toast.LENGTH_SHORT).show();
            return;
        }

        final String phone = (String) SharePreferenceUtils.get(mCtx, Constants.USER_PHONE, "");
        final User user = new User();
        boolean isSuccess = false;
        BmobQuery<User> query = new BmobQuery<User>();
        //查询条件
        query.addWhereEqualTo("phone", phone);
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            public void done(final List<User> object, BmobException e) {
                if (e == null) {
                    final float money = object.get(0).getMoney() - totalMoney;
                    if (money < 0) {
                        Toast.makeText(mCtx, "下单失败：您的余额不足以抵扣！", Toast.LENGTH_LONG).show();
                        //    finish();
                        return;
                    }
                    user.setSex(object.get(0).getSex());
                    user.setName(object.get(0).getName());
                    user.setPhone(object.get(0).getPhone());
                    user.setDorm_number(object.get(0).getDorm_number());
                    user.setFloor(object.get(0).getFloor());
                    user.setPassword(object.get(0).getPassword());
                    user.setMoney(money);
                    user.setDormitory(object.get(0).getDormitory());
                    user.setObjectId(object.get(0).getObjectId());

                    Log.d("下单前查询用户，该用户存在" + object.get(0).getObjectId(), "该用户存在");
                    user.update(object.get(0).getObjectId(), new UpdateListener() {//user.getObjectId()
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d("bmob", "更新余额成功");
                                // Toast.makeText(mCtx, "更新余额成功！！！", Toast.LENGTH_LONG).show();
//                                final Order order_submit = new Order();
//
//                                order_submit.setTime(TimeUtils.geDateTime(System.currentTimeMillis()));
//                                order_submit.setPhone(order.getPhone());
//                                order_submit.setIsfinish(false);
//                                order_submit.setDorm_number(order.getDorm_number());
//                                order_submit.setFloor(order.getFloor());
//                                order_submit.setDormitory(order.getDormitory());
//                                order_submit.setList_order_good(order_submit.getList_order_good());
                                order.setTime(TimeUtils.geDateTime(System.currentTimeMillis()));
                                order.setIsfinish(false);

                           //     final Intent intent = new Intent();
                                order.save(new SaveListener<String>() {
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            //   return_objectId = objectId;
                                            order.setObjectId(objectId);
                                            for(OrderGood orderGood:order.getList_order_good()){
                                                orderGood.setFather_id(objectId);
                                            }
                                            // 保存到数据库
                                            orderManager.saveOrder(order);


                                            Toast.makeText(mCtx, "下单成功，订单号为：" + objectId, Toast.LENGTH_LONG).show();

                  //    ########################进行  订单历史的更新 利用观察者模式  更新界面

                                            myOrderObserverManager.notifyAllWaterObserver(order);


                                          // #########################更新界面上的余额
                                            //利用观察者模式  更新界面
                                                 myObserverManager.notifyAllObserver(money);

                                            // 告诉上一个  WaterActivity  已经下单成功，让其清空数据
                                            Intent intent = new Intent();
                                            intent.putExtra("isSuccess", true);
                                            setResult(RESULT_OK, intent);
                                            finish();

                                        } else {
                                            Toast.makeText(mCtx, "订单失败：" + e.getMessage() + "请联系工作人员", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent();
                                            intent.putExtra("isSuccess", false);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                Log.d("bmob", "更新余额失败：" + e.getMessage() + "," + e.getErrorCode());
                                Toast.makeText(mCtx, "下单失败，更新余额失败！！！", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });

                } else {
                    Log.d("下单前查询用户，该用户不存在", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(mCtx, "下单失败：该用户不存在！！！", Toast.LENGTH_LONG).show();
                    return;
                }
//                List<Order> orders=orderManager.queryAllOrder();
//                Log.d("当前数据库的 个数："+orders.size(),"当前数据库的 个数："+orders.size());

            }
        });

        //##################### 先对用户的余额进行判断，是否足够扣除
//        order.save(new SaveListener<String>() {
//            public void done(String objectId, BmobException e) {
//                if (e == null) {
//                    Toast.makeText(mCtx, "下单成功！订单号:" + objectId, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(mCtx, "下单失败:" + e.getErrorCode() + "," + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d(e.getErrorCode() + "," + e.getMessage(), e.getErrorCode() + "," + e.getMessage());
//                }
//            }
//        });
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list_order_good.size();
        }

        public OrderGood getItem(int position) {
            return list_order_good.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mCtx, R.layout.order_item, null);
                viewHolder.ivWaterPicture = convertView.findViewById(R.id.iv_water_picture);
                viewHolder.tvWaterName = convertView.findViewById(R.id.tv_water_name);
                viewHolder.tvWaterCapacity = convertView.findViewById(R.id.tv_water_capacity);
                viewHolder.tvWaterPrice = convertView.findViewById(R.id.tv_water_price);
                viewHolder.tvCount = convertView.findViewById(R.id.tv_count);
                viewHolder.tvMoney = convertView.findViewById(R.id.tv_money);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OrderGood orderGood = getItem(position);
            x.image().bind(viewHolder.ivWaterPicture, getItem(position).getBitmap().getUrl(), imageOptions);
            viewHolder.tvWaterName.setText(orderGood.getName());
            viewHolder.tvWaterCapacity.setText(orderGood.getCapacity());
            viewHolder.tvWaterPrice.setText("￥ " + orderGood.getPrice());
            viewHolder.tvCount.setText("× " + orderGood.getCount());
            viewHolder.tvMoney.setText("￥ " + orderGood.getPrice() * orderGood.getCount());
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivWaterPicture;
        TextView tvWaterName;
        TextView tvWaterCapacity;
        TextView tvWaterPrice;
        TextView tvCount;
        TextView tvMoney;
    }

    private void initImageOptions() {
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(70), DensityUtil.dip2px(70))//图片大小
                .setRadius(DensityUtil.dip2px(35))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.water)//加载中默认显示图片
                .setFailureDrawableId(R.mipmap.water)//加载失败后默认显示图片
                .build();
    }

    public void measureListView(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int count = listAdapter.getCount();
        if (count == 0) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View itemView = listAdapter.getView(i, null, listView);
            itemView.measure(0, 0);
            totalHeight += itemView.getMeasuredHeight();  //必须是getMeasuredHeight，不能是getHeight
        }
        totalHeight += listView.getDividerHeight() * (count - 1) + 5;
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.height = totalHeight;
        listView.setLayoutParams(lp);

        //修复 由于scroll控件太多而出现的 当界面加载结束的时候scrollView被滑到了底部
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

}
