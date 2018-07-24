package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.yizhku.NoticeDetailActivity;
import com.example.lenovo.yizhku.OrderHistoryDetailActivity;
import com.example.lenovo.yizhku.R;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.DormitoryNotice;
import bean.Order;
import bean.OrderGood;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import database.manager.OrderManager;
import global.Constants;
import interface_package.MyOrderObserverManager;
import interface_package.MyWaterOrderObserver;
import utils.ToastUtil;

/**
 * Created by lenovo on 2018/7/13.
 */

public class Order_Water_Fragment extends BaseFragment implements MyWaterOrderObserver {
    List<Order> water_order_list;
    OrderManager manager;
    MyRecycleAdapter adapter = new MyRecycleAdapter();
    MyOrderObserverManager myOrderObserverManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.order_pager_water, null);
        RecyclerView rv_order_water = view.findViewById(R.id.rv_order_water);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_order_water.setLayoutManager(layoutManager);
        rv_order_water.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = OrderManager.getInstance(getActivity());
        water_order_list = new ArrayList<Order>();
        myOrderObserverManager = MyOrderObserverManager.getInstance();
        myOrderObserverManager.regiestWaterObserver(this);
        List<Order> orders = manager.queryAllOrder();
        water_order_list.clear();
        Collections.reverse(orders);  //倒序
        water_order_list.addAll(orders);
        adapter.notifyDataSetChanged();
    }

    public void loadData(final int skip) {

    }

    public void loadData() {

    }

    public void update(Order order) {
        if (order != null) {
            water_order_list.add(0, order);
            adapter.notifyDataSetChanged();
        }
    }

    class MyRecycleAdapter extends RecyclerView.Adapter {
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item_view = View.inflate(getActivity(), R.layout.order_pager_water_item, null);
            MyViewHolder viewHolder = new MyViewHolder(item_view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int count = 0;
            float money = 0;
            for (OrderGood orderGood : water_order_list.get(position).getList_order_good()) {
                count += orderGood.getCount();
                money += orderGood.getCount() * orderGood.getPrice();
            }
            if (count == 0 || money == 0) {
                return;
            }
            ((MyViewHolder) holder).setPosition(position);
            ((MyViewHolder) holder).tv_time.setText(water_order_list.get(position).getTime());
            ((MyViewHolder) holder).tv_order.setText(water_order_list.get(position).getList_order_good().get(0).getName());
            ((MyViewHolder) holder).tv_order_money_count.setText("等" + count + "件商品  " + "￥" + money);
        }

        @Override
        public int getItemCount() {
            return water_order_list.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_time;
        TextView tv_order;
        TextView tv_order_money_count;
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_order = itemView.findViewById(R.id.tv_order);
            tv_order_money_count = itemView.findViewById(R.id.tv_order_money_count);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OrderHistoryDetailActivity.class);
                    intent.putExtra(Constants.ORDER_INFO, water_order_list.get(position));
                    startActivity(intent);
                }
            });
        }
    }

    public void onDestroy() {
        manager.close();
        myOrderObserverManager.regiestWaterObserver(this);
        super.onDestroy();
    }

}
