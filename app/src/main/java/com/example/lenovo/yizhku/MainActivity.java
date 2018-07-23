package com.example.lenovo.yizhku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import base.BaseActivity;
import base.BasePager;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import global.Constants;
import interface_package.MyObserver;
import interface_package.MyObserverManager;
import page.HomePager;
import page.HomePager2;
import page.MyPager;
import page.OrderPager;
import utils.SharePreferenceUtils;
import view.NoScrollViewPager;

public class MainActivity extends BaseActivity implements MyObserver {
    private Context mCtx = this;
    MyObserverManager myObserverManager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottom_navigation;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    private ArrayList<BasePager> mPagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        bottom_navigation.setItemIconTintList(null);
        ColorStateList textColor = (ColorStateList) getResources().getColorStateList(R.color.btn_s);
        bottom_navigation.setItemTextColor(textColor);
        mPagers = new ArrayList<BasePager>();
        mPagers.add(new HomePager2(this));
        mPagers.add(new OrderPager(this));
        mPagers.add(new MyPager(this));
        viewPager.setAdapter(new MyAdapter());

        myObserverManager=MyObserverManager.getInstance();
        myObserverManager.regiest(this);

        bottom_navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                viewPager.setCurrentItem(0, false);
                                break;
                            case R.id.order:
                                viewPager.setCurrentItem(1, false);
                                break;
                            case R.id.my:
                                viewPager.setCurrentItem(2, false);
                                break;
                        }
                        return false;
                    }
                });
    }

    @Override
    public void update(float money) {
        ((MyPager) mPagers.get(2)).updateMoney(money);
    }


    class MyAdapter extends PagerAdapter {
        public int getCount() {
            return mPagers.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            BasePager pager = mPagers.get(position);
            View view = pager.root_view;// 获取当前页面对象的布局
            // pager.initData();
            // 初始化数据, viewpager会默认加载下一个页面,
            // 为了节省流量和性能,不要在此处调用初始化数据的方法
            container.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    TextView textView = (TextView) mPagers.get(2).root_view.findViewById(R.id.tv_user_dorm_number);
                    String dorm_number = data.getStringExtra(Constants.USER_DORM_NUMBER);
                    textView.setText(dorm_number);
                    if (!dorm_number.equals(SharePreferenceUtils.get(MainActivity.this, Constants.USER_DORM_NUMBER, dorm_number))) {
                        TextView tv_save = mPagers.get(2).root_view.findViewById(R.id.tv_save);
                        tv_save.setVisibility(View.VISIBLE);
                    }
                    SharePreferenceUtils.put(MainActivity.this, Constants.USER_DORM_NUMBER, dorm_number);
                    break;

                case 3:   //下单成功返回
//                    //更新订单历史
//                    OrderPager orderPager = (OrderPager) mPagers.get(1);
//                    orderPager.notify_DataSetChanged();//更新订单orderPage
//
//                    //更新余额
//                    float money=data.getFloatExtra("money",-1.0f);
//                    MyPager myPager = (MyPager) mPagers.get(2);
//                    myPager.updateMoney(money);

                    break;
                default:
                    break;


            }
        }
    }

    @Override
    protected void onDestroy() {
        myObserverManager.unregiest(this);
        super.onDestroy();
    }
}
