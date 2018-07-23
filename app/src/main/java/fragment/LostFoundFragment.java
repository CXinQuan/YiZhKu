package fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.yizhku.R;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.LostFoundInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import global.Constants;
import utils.SharePreferenceUtils;
import utils.TimeUtils;
import utils.ToastUtil;
import utils.VerificationCodeUtil;

/**
 * Created by lenovo on 2018/7/13.
 */

public class LostFoundFragment extends BaseFragment {
    int totalCount=0;
    RelativeLayout mGongaolan;
    ImageView mIv;
    RefreshLayout refreshLayout;
    ListView lv_lost_found;
    List<LostFoundInfo> list_lostfound_info;
    MyAdapter adapter;
    TextView iv_fatie;
    String yanzhengma;
    int type; //捡到还是遗失

    String currentTime;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            super.handleMessage(msg);
        }
    };
    public void initViews(View view) {
        mGongaolan = (RelativeLayout) view.findViewById(R.id.gongaolan);
        mIv = (ImageView) view.findViewById(R.id.iv);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        lv_lost_found = (ListView) view.findViewById(R.id.lv_lost_found);
        iv_fatie = (TextView) view.findViewById(R.id.iv_fatie);
        iv_fatie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        lv_lost_found.setAdapter(adapter);
    }

    public void loadData(final int skip) {

        //每次下拉刷新都需要清空
        if (skip == 0) {
            list_lostfound_info.clear();
            currentTime= TimeUtils.geDateTime(System.currentTimeMillis());
       //     Log.d("当前时间是  "+System.currentTimeMillis(),"当前时间是  "+System.currentTimeMillis());
        //    Log.d(currentTime+"currentTime",currentTime+"");
          //  loadData();
        }
        // 当返回的数据的 size 为0 ，那么就说明没有更多数据，不用通过获取全部的size再判断
//        else if (list_lostfound_info.size() >= totalCount) {
//            handler.sendEmptyMessageDelayed(0, 2000);
//            Toast.makeText(getActivity(), "已经没有更多数据", Toast.LENGTH_SHORT).show();
//            return;
//        }
        BmobQuery<LostFoundInfo> query = new BmobQuery<LostFoundInfo>();
        query.order("-updatedAt");  // - 号  表示 根据updatedAt 时间  从上到下  开始  返回  5条数据， 不带  -  号 ，表示正序 排列

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date  = null;
//        try {
//            date = sdf.parse(currentTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        query.addWhereLessThanOrEqualTo("updatedAt",new BmobDate(date));
        query.setLimit(Constants.PAGING_LEGTH);
        query.setSkip(skip); // 忽略前10条数据（即第一页数据结果）
//        boolean isCache = query.hasCachedResult(LostFoundInfo.class);
//        if (isCache && skip != 0) {
//            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
//        }
        query.findObjects(new FindListener<LostFoundInfo>() {
            public void done(List<LostFoundInfo> object, BmobException e) {
                if (e == null) {
                    //返回数据为0，那么说明没有更多数据了
                    if(object.size()==0){
                        ToastUtil.showToast(getActivity(),"没有更多数据！");
                    }else{
                        list_lostfound_info.addAll(object);
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
//        BmobQuery<LostFoundInfo> query = new BmobQuery<LostFoundInfo>();
//        query.setLimit(1000000);
//        query.findObjects(new FindListener<LostFoundInfo>() {
//            public void done(List<LostFoundInfo> object, BmobException e) {
//                if (e == null) {
//                    totalCount = object.size();
//                } else {
//                    Toast.makeText(getActivity(), "加载数据出错！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    //展示发帖对话框
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.lostfound);
        builder.setTitle("失物招领");
        View view = View.inflate(getActivity(), R.layout.dialog_lost_found, null);
        builder.setView(view);//添加自定义View
        final AlertDialog alertDialog = builder.create();
        final RadioGroup mRg = (RadioGroup) view.findViewById(R.id.rg);
        RadioButton mRbtnFound = (RadioButton) view.findViewById(R.id.rbtn_found);
        RadioButton mRbtnLost = (RadioButton) view.findViewById(R.id.rbtn_lost);

        final EditText mEtThing = (EditText) view.findViewById(R.id.et_thing);
        final EditText mEtContent = (EditText) view.findViewById(R.id.et_content);

        final EditText mEtYanzhengma = (EditText) view.findViewById(R.id.et_yanzhengma);
        final TextView mTvYanzhengma = (TextView) view.findViewById(R.id.tv_yanzhengma);

        yanzhengma = VerificationCodeUtil.yanzhengma();
        mTvYanzhengma.setText(yanzhengma);
        mTvYanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yanzhengma =VerificationCodeUtil.yanzhengma();
                mTvYanzhengma.setText(yanzhengma);
            }
        });
        Button mBtnCancle = (Button) view.findViewById(R.id.btn_cancle);
        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtThing.getText().toString().trim() == null || mEtThing.getText().toString().trim().equals("") ||
                        mEtContent.getText().toString().trim() == null || mEtContent.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "请将必要内容填写完整！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEtYanzhengma.getText().toString().trim() == null ||
                        mEtYanzhengma.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "请输入验证码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mEtYanzhengma.getText().toString().trim().equals(yanzhengma)) {
                    Toast.makeText(getActivity(), "验证码错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final LostFoundInfo info = new LostFoundInfo();
                info.setThing(mEtThing.getText().toString().trim());
                mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        switch (checkedId) {
                            case R.id.rbtn_found:
                                type = Constants.TYPE_FOUND;
                                break;
                            case R.id.rbtn_lost:
                                type = Constants.TYPE_LOST;
                                break;
                        }
                    }
                });

                info.setType(type);
                // 名字和性别需要 从shareprefernece中获取
                int sex = (int) SharePreferenceUtils.get(getActivity(), Constants.USER_SEX, 1);
                String name= (String) SharePreferenceUtils.get(getActivity(),Constants.USER_NAME,"匿名用户");
                info.setName(name);
                info.setSex(sex);
                info.setContent(mEtContent.getText().toString().trim());
                info.save(new SaveListener<String>() {
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "发帖成功！", Toast.LENGTH_SHORT).show();
                            list_lostfound_info.add(0,info);
                            adapter.notifyDataSetChanged();
                            lv_lost_found.setSelection(0);
//                            yanzhengma =VerificationCodeUtil.yanzhengma();
//                            mTvYanzhengma.setText(yanzhengma);
                        } else {
                            Toast.makeText(getActivity(), "发帖失败，请检查网络！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_lostfound, null);
        initViews(view);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new DeliveryHeader(getActivity()));

        //设置 Footer 为 经典 样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override //加载更多
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(list_lostfound_info.size());
            }

            @Override //下拉刷新
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(0);
            }
        });
        return view; //super.onCreateView(inflater, container, savedInstanceState)
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_lostfound_info = new ArrayList<LostFoundInfo>();
        adapter = new MyAdapter();
        loadData();
        loadData(0);
    }

    //    public void loadData() {
//        for (int i = 0; i < 10; i++) {
//            LostFoundInfo info = new LostFoundInfo();
//            info.setName("说不出来的忧伤");
//            info.setContent("本人在北区球场捡到避孕套1盒，20个，请失主快到6604找帅帅的耸拿，联系号码43545425453");
//            info.setSex(1);
//            info.setType(0);
//            info.setThing("避孕套");
//            list_lostfound_info.add(info);
//        }
//        for (int i = 0; i < 10; i++) {
//            LostFoundInfo info = new LostFoundInfo();
//            info.setName("我想静静");
//            info.setContent("本人在北区球场遗失避孕套1盒，20个，请捡到的好心人帮忙拿到6604，找帅帅的阿耸，联系号码43545425453");
//            info.setSex(0);
//            info.setType(1);
//            info.setThing("避孕套");
//            list_lostfound_info.add(info);
//        }
//        adapter.notifyDataSetChanged();
//
//    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list_lostfound_info.size();
        }

        public LostFoundInfo getItem(int position) {
            return list_lostfound_info.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lostfound_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getItem(position).getSex() == Constants.SEX_MAN) {
                viewHolder.iv_head.setImageResource(R.mipmap.boy);
            } else {
                viewHolder.iv_head.setImageResource(R.mipmap.girl);
            }
            if (getItem(position).getType() == Constants.TYPE_FOUND) {
                viewHolder.tv_type.setText("捡到  " + getItem(position).getThing());
            } else {
                viewHolder.tv_type.setText("遗失  " + getItem(position).getThing());
            }
            viewHolder.tv_content.setText(getItem(position).getContent());
            viewHolder.tv_name.setText(getItem(position).getName());
            viewHolder.tv_time.setText("2018-7-14 11:13");//getItem(position).getCreatedAt()
            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_head;
        TextView tv_content;
        TextView tv_type;
        TextView tv_time;
        TextView tv_name;
    }

}
