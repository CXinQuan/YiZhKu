package com.example.lenovo.yizhku;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
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
import utils.SharePreferenceUtils;
import utils.TimeUtils;
import utils.ToastUtil;
import utils.UIUtils;

public class OrderHistoryDetailActivity extends BaseActivity {
    @BindView(R.id.iv_goback)
    ImageView ivGoback;
    @BindView(R.id.tv_order_user_name)
    TextView tvOrderUserName;
    @BindView(R.id.tv_order_user_phone)
    TextView tvOrderUserPhone;
    @BindView(R.id.tv_order_address)
    TextView tvOrderAddress;

    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.lv_order_detail)
    ListView lvOrderDetail;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.scrollView)
    ScrollView scrollView;


    float totalMoney;
    List<OrderGood> list_order_good;
    Order order;
    ImageOptions imageOptions;
    OrderManager orderManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);
        ButterKnife.bind(this);
        initImageOptions();
        orderManager = OrderManager.getInstance(mCtx);

        order = (Order) getIntent().getSerializableExtra(Constants.ORDER_INFO);
        Log.d("订单详情对象" + order, "订单详情对象" + order);

        if (order != null && order.getList_order_good().size() > 0) {
            list_order_good = order.getList_order_good();
            for (OrderGood orderGood : list_order_good) {
                totalMoney += orderGood.getCount() * orderGood.getPrice();
            }
            if (totalMoney > 0) {
                tvTotalMoney.setText("  ￥ " + totalMoney);
            }
        } else {
            ToastUtil.showToast(mCtx, "该订单异常!");
            return;
        }
        CharSequence str = UIUtils.setTextWithColor("下单时间：" + order.getTime() + "\n" + "\n" + "商品详情：", Color.parseColor("#3f67f4"));
        tvOrderTime.setText(str);
        lvOrderDetail.setAdapter(new MyAdapter());
        measureListView(lvOrderDetail);
        //显示用户  收货地址
        showUserAddress();
    }

    private void showUserAddress() {

        tvOrderUserName.setText("联系人：" + order.getName() + (order.getSex() == Constants.SEX_MAN ? "（先生）" : "（女士）"));
        tvOrderUserPhone.setText("联系电话：" + order.getPhone());
        tvOrderAddress.setText("收货地址：仲恺农业工程学院 " + order.getDormitory() + " " + order.getDorm_number());

    }

    @OnClick({R.id.iv_goback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_goback:
                finish();
                break;
        }
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
            x.image().bind(viewHolder.ivWaterPicture, orderGood.getBitmapurl(), imageOptions);
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
