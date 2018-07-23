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
import com.example.lenovo.yizhku.R;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import bean.DormitoryNotice;
import bean.Order;
import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import database.manager.OrderManager;
import utils.ToastUtil;

/**
 * Created by lenovo on 2018/7/13.
 */

public class Order_Water_Fragment extends BaseFragment {
    List<Order> water_order_list = new ArrayList<Order>();
    OrderManager manager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.order_pager_water, null);
        RecyclerView rv_order_water = view.findViewById(R.id.rv_order_water);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_order_water.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = OrderManager.getInstance(getActivity());
        water_order_list.addAll(manager.queryAllOrder());

    }

    public void loadData(final int skip) {

    }

    public void loadData() {

    }

    class MyRecycleAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item_view = View.inflate(getActivity(), R.layout.order_pager_water_item, null);
            MyViewHolder viewHolder = new MyViewHolder(item_view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
      //  @BindView(R.id.)

        public MyViewHolder(View itemView) {
            super(itemView);
        }


    }


}
