package page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.lenovo.yizhku.MainActivity;
import com.example.lenovo.yizhku.R;

import java.util.ArrayList;
import java.util.List;

import base.BasePager;
import bean.Order;
import bean.RepairBean;
import database.manager.OrderManager;
import database.manager.RepairManager;
import fragment.BaseFragment;
import fragment.FragmentFactory;
import fragment.OrderFragmentFactory;
import view.MyOrderPagerTab;
import view.MyPagerTab;

/**
 * Created by lenovo on 2018/7/12.
 */

public class OrderPager extends BasePager{
    RepairManager repairManager;
    OrderManager orderManager;
    List<RepairBean> repairBeanList = new ArrayList<RepairBean>();

    public OrderPager(Context mCtx) {
        super(mCtx);
        repairManager = RepairManager.getInstance(mCtx);
        orderManager = OrderManager.getInstance(mCtx);
    }

    @Override
    public void initData() {

    }

    @Override
    public View addToView() {
        View view = View.inflate(mCtx, R.layout.pager_order, null);
//        Button btn_water = view.findViewById(R.id.btn_water);
//        Button btn_repair = view.findViewById(R.id.btn_repair);
//
//        btn_water.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Order> orderList = orderManager.queryAllOrder();
//                Log.d("#############桶装水的订单数量：", orderList.size() + "");
//            }
//        });
//        btn_repair.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<RepairBean> repairBeens = repairManager.queryRepairList(repairBeanList.size(), 15);
//                Log.d("返回维修订单repairBeens的行数：", repairBeens.size() + "");
//            }
//        });
        ViewPager viewPager=(ViewPager)view.findViewById(R.id.order_viewpager);
        viewPager.setAdapter(new MyAdapter(((MainActivity)mCtx).getSupportFragmentManager()));
        MyOrderPagerTab myPagerTab=(MyOrderPagerTab)view.findViewById(R.id.pagerTab);
        myPagerTab.setViewPager(viewPager);
        return view;
    }



    class MyAdapter extends FragmentPagerAdapter {

        private String[] mTabNames;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabNames = new String[]{"桶装水订单", "设备报修预约"};
        }

        // 返回页签标题
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }

        // 返回当前页面位置的fragment对象
        @Override
        public Fragment getItem(int position) {
          return OrderFragmentFactory.createFragment(position);
        }

        // fragment数量
        @Override
        public int getCount() {
            return mTabNames.length;
        }
    }


}
