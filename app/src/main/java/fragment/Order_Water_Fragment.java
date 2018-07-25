package fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.yizhku.NoticeDetailActivity;
import com.example.lenovo.yizhku.OrderHistoryDetailActivity;
import com.example.lenovo.yizhku.R;
import com.scwang.smartrefresh.header.DropBoxHeader;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.DormitoryNotice;
import bean.Order;
import bean.OrderGood;
import bean.RepairBean;
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
import view.SpaceItemDecoration;

/**
 * Created by lenovo on 2018/7/13.
 */

public class Order_Water_Fragment extends BaseFragment implements MyWaterOrderObserver {
    List<Order> water_order_list;
    OrderManager dbManager;
    MyRecycleAdapter adapter = new MyRecycleAdapter();
    MyOrderObserverManager myOrderObserverManager;
    RefreshLayout refreshLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            super.handleMessage(msg);
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.order_pager_water, null);
        RecyclerView rv_order_water = view.findViewById(R.id.rv_order_water);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        rv_order_water.setLayoutManager(layoutManager);

        //设置 RecyclerView 的间距
        rv_order_water.addItemDecoration(new SpaceItemDecoration(30));

        rv_order_water.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = OrderManager.getInstance(getActivity());
        water_order_list = new ArrayList<Order>();
        myOrderObserverManager = MyOrderObserverManager.getInstance();
        myOrderObserverManager.regiestWaterObserver(this);
        List<Order> orders = dbManager.queryAllOrder();
        water_order_list.clear();
        Collections.reverse(orders);  //倒序
        water_order_list.addAll(orders);
        adapter.notifyDataSetChanged();
    }

    public void initRefreshLayout(RefreshLayout refreshLayout) {

        refreshLayout.setRefreshHeader(new DropBoxHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                List<RepairBean> repairBeens = dbManager.queryRepairList(repairBeanList.size(), 15);
//                repairBeanList.addAll(repairBeens);
                //由于这里是直接 拿出数据库中的所有数据，不是 分页加载，所以这里如果被执行，那么说明  没有更多数据
                ToastUtil.showToast(getActivity(), "没有更多数据！");
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                water_order_list.clear();
                List<Order> orders = dbManager.queryAllOrder();
                Collections.reverse(orders);  //倒序
                water_order_list.addAll(orders);
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    int popupWindow_dx = 0;

    public void setTounch(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        popupWindow_dx = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        popupWindow_dx = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        //       showPopupWindow();
                        break;
                }
                return false;  //这里必须返回false，点击事件和触摸事件才能同时生效
                //如果没有点击事件，那么就必须返回true
            }
        });
    }

    protected void showPopupWindow(View view, final int position) {
        //1,窗体上展示的内容
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.mipmap.popupwindow_delete);
        //2,挂载在popupWindow上
        final PopupWindow popupWindow = new PopupWindow(imageView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true);  //宽高各100，true表示要获取焦点
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbManager.deleteOrderInfo(water_order_list.get(position).getObjectId());
               //必须先删除 数据库的 数据，再删除集合的，否则会出现  IndexOutOfBoundsException:
                water_order_list.remove(position);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                popupWindow_dx = 0;
            }
        });
        //3,指定popupWindow的背景(设置上背景,点击返回按钮才会有响应)
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //一定要设置背景，这里设置为透明背景
        //4,指定popupWindow所在位置(挂载在哪个控件上,在父控件上的位置,偏离距离)
        popupWindow.showAsDropDown(view, popupWindow_dx, -view.getHeight() - 15); //在根布局上显示，正中间，距离正中间的x，y往右往下各偏离100
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

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_order = itemView.findViewById(R.id.tv_order);
            tv_order_money_count = itemView.findViewById(R.id.tv_order_money_count);

            setTounch(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    showPopupWindow(itemView, position);
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OrderHistoryDetailActivity.class);
                    intent.putExtra(Constants.ORDER_INFO, water_order_list.get(position));
                    //  Log.d( water_order_list.get(position).getObjectId()+"","id 是 ");
                    startActivity(intent);
                }
            });
        }
    }

//    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
//        int mSpace;
//        public SpaceItemDecoration(int space) {
//            this.mSpace = space;
//        }
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
////            outRect.left = mSpace;      //左边距
////            outRect.right = mSpace;     //右边距
////            outRect.bottom = mSpace;    // 下边距
//            if (parent.getChildAdapterPosition(view) == 0) {  //如果是RecyclerView的第一个子项，则设置该子项上方的间距 为0
//                outRect.top = 0;
//            }else{
//                outRect.top = mSpace;
//            }
//
//        }
//
//
//    }

    public void onDestroy() {
        dbManager.close();
        myOrderObserverManager.unregiestWaterObserver(this);
        super.onDestroy();
    }

}
