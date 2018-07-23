package page;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.lenovo.yizhku.BusActivity;
import com.example.lenovo.yizhku.DormitoryActivity;
import com.example.lenovo.yizhku.JobActivity;
import com.example.lenovo.yizhku.NewDetailActivity;
import com.example.lenovo.yizhku.R;
import com.example.lenovo.yizhku.WaterActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.BasePager;
import bean.LunBoTu;
import bean.New;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import global.Constants;
import utils.SharePreferenceUtils;

public class HomePager extends BasePager {
    private TextView tv_user_name;
    private ImageView iv_user_head;

    public HomePager(Context mCtx) {
        super(mCtx);
    }

    SliderLayout sliderLayout;
    RelativeLayout gongaolan;
    TextView tv_water;
    TextView tv_dormitory;
    TextView tv_bus;
    TextView tv_job;
    ScrollView scrollView;

    ListView lv;
    ProgressBar pb;
    MyAdapter adapter;
    List<LunBoTu> list;
    List<New> list_lv;

    public void initData() {
        BmobQuery<LunBoTu> query = new BmobQuery<LunBoTu>();
        query.setLimit(100);
        query.order("-updatedAt");
        query.findObjects(new FindListener<LunBoTu>() {
            public void done(List<LunBoTu> object, BmobException e) {
                if (e == null) {
                    list.addAll(object);
                    initSlider();
                } else {
                    // Toast.makeText(mCtx, "获取商品分类失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<New> query_New = new BmobQuery<New>();
        query_New.setLimit(10);
        query_New.order("-updatedAt");
        query_New.findObjects(new FindListener<New>() {
            public void done(List<New> object, BmobException e) {
                if (e == null) {
                    list_lv.addAll(object);
                    if (adapter == null) {
                        Log.d("适配器为空", "适配器为空");
                        return;
                    }
                    updateListView();
                } else {
                    // Toast.makeText(mCtx, "获取商品分类失败！", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    public View addToView() {
        View view = View.inflate(mCtx, R.layout.homepager, null);
        sliderLayout = (SliderLayout) view.findViewById(R.id.slider);
        tv_user_name=(TextView)view.findViewById(R.id.tv_user_name);
        iv_user_head=(ImageView)view.findViewById(R.id.iv_user_head);
        scrollView=(ScrollView)view.findViewById(R.id.scrollView);
        String user_name= (String) SharePreferenceUtils.get(mCtx, Constants.USER_NAME,"");
        if (user_name!=null||!user_name.equals("")){
            tv_user_name.setText(user_name);
        }
        int sex= (int) SharePreferenceUtils.get(mCtx, Constants.USER_SEX,-1);
        if(sex==1){
            iv_user_head.setImageResource(R.mipmap.boy);
        }else if(sex==0){
            iv_user_head.setImageResource(R.mipmap.girl);
        }else{
            iv_user_head.setImageResource(R.mipmap.head_1);
        }

        pb = (ProgressBar) view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        gongaolan = (RelativeLayout) view.findViewById(R.id.gongaolan);
        gongaolan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, NewDetailActivity.class);
                intent.putExtra("new_url", "http://www.zhku.edu.cn/");
                mCtx.startActivity(intent);
            }
        });
        tv_water = (TextView) view.findViewById(R.id.water);
        tv_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, WaterActivity.class);
                mCtx.startActivity(intent);
            }
        });
        tv_dormitory = (TextView) view.findViewById(R.id.dormitory);
        tv_dormitory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, DormitoryActivity.class);
                mCtx.startActivity(intent);
            }
        });
        tv_bus = (TextView) view.findViewById(R.id.bus);
        tv_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, BusActivity.class);
                mCtx.startActivity(intent);
            }
        });
        tv_job = (TextView) view.findViewById(R.id.job);
        tv_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(mCtx, JobActivity.class);
                mCtx.startActivity(intent);
            }
        });
        list = new ArrayList<LunBoTu>();
        list_lv = new ArrayList<New>();
        lv = (ListView) view.findViewById(R.id.lv);
        adapter = new MyAdapter();
        lv.setAdapter(adapter);
        return view;
    }

    private void initSlider() {
        for (int i = 0; i < list.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(mCtx);
            textSliderView
                    // .description(promotionList.get(i).getInfo())//给轮播图的每一个图片,添加描述文字 将promotionList.get(i).getInfo()进行替换为自己的描述信息
                    .image(list.get(i).getBitmap().getUrl())//指定图片 将promotionList.get(i).getPic()进行替换
                    .setScaleType(BaseSliderView.ScaleType.Fit);//ScaleType设置图片展示方式(fitxy  centercrop)
            //向SliderLayout控件的内部添加条目
            sliderLayout.addSlider(textSliderView);
        }
        //viewpager--->ImageView放置图片
        // viewpager 等价于 SliderLayout
        // TextSliderView 等价于 ImageView,只不过添加了一个显示文本功能
        //默认指定的动画类型
        //slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        //指定圆点指示器的位置
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //定义一个描述动画
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        //动画时长
        sliderLayout.setDuration(4000);

    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list_lv.size();
        }

        public New getItem(int position) {
            return list_lv.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mCtx, R.layout.list_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_title.setText(getItem(position).getTitle());
            viewHolder.tv_time.setText(getItem(position).getTime());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, NewDetailActivity.class);
                    intent.putExtra("new_url", list_lv.get(position).getUrl());
                    mCtx.startActivity(intent);
                   // Toast.makeText(mCtx, list_lv.get(position).getUrl(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    public void updateListView() {
        Collections.reverse(list_lv);
        adapter.notifyDataSetChanged();
        pb.setVisibility(View.GONE);
        measureListView(lv);
    }

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

        //修复 由于scroll控件太多而出现的 当界面加载结束的时候scrollView被滑到了底部
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_time;
    }

}

