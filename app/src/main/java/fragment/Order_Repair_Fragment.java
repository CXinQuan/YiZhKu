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
import com.example.lenovo.yizhku.RepairHistoryDetailActivity;
import com.scwang.smartrefresh.header.DropBoxHeader;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.DormitoryNotice;
import bean.Order;
import bean.OrderGood;
import bean.RepairBean;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import database.manager.OrderManager;
import database.manager.RepairManager;
import global.Constants;
import interface_package.MyOrderObserverManager;
import interface_package.MyRepairOrderObserver;
import utils.ToastUtil;
import view.SpaceItemDecoration;

/**
 * Created by lenovo on 2018/7/13.
 */

public class Order_Repair_Fragment extends BaseFragment implements MyRepairOrderObserver {
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


    String[] type_text = new String[]{"风扇", "电灯", "线路", "水龙头", "水管", "热水设备", "床", "窗户", "其他"};
    int[] type_image = new int[]{R.mipmap.fan, R.mipmap.light, R.mipmap.line, R.mipmap.water_tap,
            R.mipmap.water_pipe, R.mipmap.hot_water, R.mipmap.bed, R.mipmap.window,
            R.mipmap.other};

    MyRecycleAdapter adapter = new MyRecycleAdapter();
    List<RepairBean> repairBeanList;
    RepairManager dbManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.order_pager_repair, null);
        RecyclerView rv_order_repair = view.findViewById(R.id.rv_order_repair);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_order_repair.setLayoutManager(layoutManager);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        initRefreshLayout(refreshLayout);
        //设置 RecyclerView 的间距
        rv_order_repair.addItemDecoration(new SpaceItemDecoration(30));

        rv_order_repair.setAdapter(adapter);
        return view;
    }

    //初始化 下拉刷新
    public void initRefreshLayout(RefreshLayout refreshLayout) {

        refreshLayout.setRefreshHeader(new DropBoxHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                List<RepairBean> repairBeens = dbManager.queryRepairList(repairBeanList.size(), 15);
                repairBeanList.addAll(repairBeens);
                if (repairBeens.size() <= 0) {
                    ToastUtil.showToast(getActivity(), "没有更多数据!");
                } else {
                    repairBeanList.addAll(repairBeens);
                }
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

                List<RepairBean> repairBeens = dbManager.queryRepairList(repairBeanList.size(), 15);
                if (repairBeens != null && repairBeens.size() > 0) {
                    repairBeanList.clear();
                    Collections.reverse(repairBeens);  //倒序
                    repairBeanList.addAll(repairBeens);
                }
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repairBeanList = new ArrayList<RepairBean>();
        dbManager = RepairManager.getInstance(getActivity());
        myOrderObserverManager = MyOrderObserverManager.getInstance();
        myOrderObserverManager.regiestRepair(this);
        List<RepairBean> repairBeens = dbManager.queryRepairList(repairBeanList.size(), 15);
        Collections.reverse(repairBeens);  //倒序
        repairBeanList.addAll(repairBeens);

    }

    public void loadData(final int skip) {

    }

    public void loadData() {

    }

    @Override
    public void update(RepairBean repairBean) {
        repairBeanList.add(0, repairBean);
        adapter.notifyDataSetChanged();
    }


    class MyRecycleAdapter extends RecyclerView.Adapter {
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item_view = View.inflate(getActivity(), R.layout.order_pager_repair_item, null);
            MyViewHolder viewHolder = new MyViewHolder(item_view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((MyViewHolder) holder).iv_photo.setImageResource(R.mipmap.other);
            ((MyViewHolder) holder).setPosition(position);
            for (int i=0;i< type_text.length;i++ ) {
                if (type_text[i].equals(repairBeanList.get(position).getRepairType())) {
                    ((MyViewHolder) holder).iv_photo.setImageResource(type_image[i]);
                    break;
                }
            }
            ((MyViewHolder) holder).tv_door_time.setText("预约上门时段：" + repairBeanList.get(position).getService_time());
            ((MyViewHolder) holder).tv_time.setText(repairBeanList.get(position).getSub_time());
            ((MyViewHolder) holder).tv_repair_type.setText("维修类型：" + repairBeanList.get(position).getRepairType());

        }

        @Override
        public int getItemCount() {
            return repairBeanList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo;
        TextView tv_time;
        TextView tv_repair_type;
        TextView tv_door_time;
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        public MyViewHolder(final View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_repair_type = itemView.findViewById(R.id.tv_repair_type);
            tv_door_time = itemView.findViewById(R.id.tv_door_time);
            setTounch(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {

                    showPopupWindow(itemView, position);
                    return false;

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RepairHistoryDetailActivity.class);
                    intent.putExtra(Constants.REPAIR_INFO, repairBeanList.get(position));
                    //  Log.d( water_order_list.get(position).getObjectId()+"","id 是 ");
                    startActivity(intent);
                }
            });
        }
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
                dbManager.deleteRepairBean(repairBeanList.get(position).getObjectId());
                repairBeanList.remove(position);
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


    @Override
    public void onDestroy() {
        myOrderObserverManager.unregiestRepairObserver(this);
        super.onDestroy();
    }
}
