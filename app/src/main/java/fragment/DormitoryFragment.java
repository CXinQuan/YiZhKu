package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import utils.ToastUtil;

/**
 * Created by lenovo on 2018/7/13.
 */

public class DormitoryFragment extends BaseFragment {
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            super.handleMessage(msg);
        }
    };

    ListView lv_dormitory_notice;
    List<DormitoryNotice> list_notice;
    MyAdapter adapter;
    RefreshLayout refreshLayout;
    int totalCount = 0;
    boolean isFirst = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_dormitory, null);
        lv_dormitory_notice = (ListView) view.findViewById(R.id.lv_dormitory_notice);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(getActivity()));

        //设置 Footer 为 球脉冲 样式
        refreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override //加载更多
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(list_notice.size());
            }

            @Override //下拉刷新
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(0);
            }
        });
        lv_dormitory_notice.setAdapter(adapter);
        lv_dormitory_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_notice.get(position) == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                intent.putExtra("notice_detail_who", list_notice.get(position).getWho());
                intent.putExtra("notice_detail_title", list_notice.get(position).getTitle());
                intent.putExtra("notice_detail_time", list_notice.get(position).getCreatedAt());
                intent.putExtra("notice_detail_content", list_notice.get(position).getContent());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_notice = new ArrayList<DormitoryNotice>();
        adapter = new MyAdapter();
        loadData(0);

    }

    public void loadData(final int skip) {
//        for (int i = 0; i < 20; i++) {
//            DormitoryNotice dormitoryNotice = new DormitoryNotice();
//           // dormitoryNotice.setTime("2018-7-13 12:43");
//            dormitoryNotice.setWho("宿舍管理处");
//            dormitoryNotice.setTitle("关于宿舍停水的通知");
//            dormitoryNotice.setContent("由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！" +
//                    "由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！" +
//                    "由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！" +
//                    "由于学校水管破裂，现通知，将在2018-7-14 16:00展开修理，请同学做好储水工作！");
//            list_notice.add(dormitoryNotice);
//        }

        //每次下拉刷新都需要重新计算总数
        if (skip == 0) {
            list_notice.clear();
         //   loadData();
        }
//        else if (list_notice.size() >= totalCount) {   //
//            handler.sendEmptyMessageDelayed(0, 2000);
//          //  Toast.makeText(getActivity(), "已经没有更多数据", Toast.LENGTH_SHORT).show();
//            return;
//        }
        BmobQuery<DormitoryNotice> query = new BmobQuery<DormitoryNotice>();
        query.setLimit(20);
        query.order("-updatedAt");
        query.setSkip(skip); // 忽略前10条数据（即第一页数据结果）
        boolean isCache = query.hasCachedResult(DormitoryNotice.class);
        if (isCache && skip != 0) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }
        query.findObjects(new FindListener<DormitoryNotice>() {
            public void done(List<DormitoryNotice> object, BmobException e) {
                if (e == null) {
                    if(object.size()==0){
                        ToastUtil.showToast(getActivity(),"没有更多数据！");
                    }else{
                        list_notice.addAll(object);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "加载数据出错！", Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        });
    }

    public void loadData() {
//        BmobQuery<DormitoryNotice> query = new BmobQuery<DormitoryNotice>();
//        query.setLimit(1000000);
//        query.findObjects(new FindListener<DormitoryNotice>() {
//            public void done(List<DormitoryNotice> object, BmobException e) {
//                if (e == null) {
//                    totalCount = object.size();
//                } else {
//                    Toast.makeText(getActivity(), "加载数据出错！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list_notice.size();
        }

        public DormitoryNotice getItem(int position) {
            return list_notice.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.dormitory_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_who = (TextView) convertView.findViewById(R.id.tv_who);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_content.setText(getItem(position).getContent());
            viewHolder.tv_who.setText(getItem(position).getWho());
            viewHolder.tv_title.setText(getItem(position).getTitle());
            viewHolder.tv_time.setText(getItem(position).getCreatedAt());
            //convertView如果设置了点击事件，那么setOnItemClickListener则无法响应
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Intent intent = new Intent(getActivity(), Activity.class);
////                    intent.putExtra("new_url", list_notice.get(position).getContent());
////                    getActivity().startActivity(intent);
//
//                }
//            });
            return convertView;
        }
    }

//    public void updateListView() {
//        Collections.reverse(list_lv);
//        adapter.notifyDataSetChanged();
//        pb.setVisibility(View.GONE);
//        measureListView(lv);
//    }

    /**
     * 由于listView和ScrollView嵌套，所以需要重新测量listView的高度
     */
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
    }

    class ViewHolder {
        TextView tv_who;
        TextView tv_content;
        TextView tv_title;
        TextView tv_time;
    }


}
