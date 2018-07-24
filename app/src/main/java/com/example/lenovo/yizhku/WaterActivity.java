package com.example.lenovo.yizhku;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.xiangcheng.marquee3dview.Marquee3DView;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.BaseActivity;
import bean.Good;
import bean.LunBoTu;
import bean.LunBoTuWater;
import bean.Order;
import bean.OrderGood;
import bean.OrderPerson;
import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import global.Constants;
import utils.SMSUtil;
import utils.TimeUtils;

public class WaterActivity extends BaseActivity {
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @BindView(R.id.lv)
    ListView mLv;

    @BindView(R.id.btn_clear)
    Button mBtnClear;

    @BindView(R.id.btn_account)
    Button mBtnAccount;

    @BindView(R.id.rl_shoping_car_show)
    RelativeLayout rl_shoping_car_show;

    @BindView(R.id.tv_total_money)
    TextView tv_total_money;

    @BindView(R.id.tv_total_count)
    TextView tv_total_count;

    private Context mCtx = this;
    //获取所有的商品类型
    List<Good> list;
    //获取轮播图的图片
    List<LunBoTuWater> list_lunbotu_water;
    SliderLayout sliderLayout;
    Marquee3DView marquee3DView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottomSheetLayout)
    BottomSheetLayout bottomSheetLayout;


    int totalNumber = 0;  //  下购物车 商品 总数
    MyAdapter adapter;
    ShoppingCarAdapter shoppingCarAdapter;

    boolean ball_isMoving = false;
    int shoppingCar_width;

    //用于临时记录用户选择了哪些商品
    List<OrderGood> orderGoodList;

    List<OrderGood> orderGoodList_ShoppingCar;

    Order order;
    ImageOptions imageOptions;
    private ImageOptions largeImageOptions;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water2_activity2);
        ButterKnife.bind(this);
        toolbar.setTitle("订水服务");
        toolbar.setTitleTextColor(Color.WHITE);
        order = new Order();
        list = new ArrayList<Good>();
        orderGoodList = new ArrayList<OrderGood>();
        orderGoodList_ShoppingCar = new ArrayList<OrderGood>();

        list_lunbotu_water = new ArrayList<LunBoTuWater>();
        initImageOptions();
        initSliderData();
        getAllGood();
        adapter = new MyAdapter();
        shoppingCarAdapter = new ShoppingCarAdapter();
        mLv.setAdapter(adapter);

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

    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return list.size() + 2;
            //   return 20;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 2; //默认是1种条目
        }

        public int getItemViewType(int position) {
            if (position >= 2) {
                return 2;
            } else {
                return position;
            }
        }

        public Object getItem(int position) {
            if (position > 1) {
                return list.get(position - 2);
            }
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == 0) {
                View view_first = View.inflate(mCtx, R.layout.water_first_item, null);
                sliderLayout = (SliderLayout) view_first.findViewById(R.id.slider);
                initSlider();
                return view_first;
            } else if (itemViewType == 1) {
                View view_second = View.inflate(mCtx, R.layout.water_second_item, null);
                initMarquee3DView(view_second);
                return view_second;
            } else {
                final ViewHolder viewHolder;
                final int[] count = {0};
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(mCtx, R.layout.water_good_item, null);
                    viewHolder.iv_water_picture = (ImageView) convertView.findViewById(R.id.iv_water_picture);
                    viewHolder.tv_water_name = (TextView) convertView.findViewById(R.id.tv_water_name);
                    viewHolder.tv_water_capacity = (TextView) convertView.findViewById(R.id.tv_water_capacity);
                    viewHolder.tv_water_price = (TextView) convertView.findViewById(R.id.tv_water_price);
                    viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                    viewHolder.iv_minus_2 = (ImageView) convertView.findViewById(R.id.iv_minus_2);
                    viewHolder.iv_add_2 = (ImageView) convertView.findViewById(R.id.iv_add_2);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //必须是LOLLIPOP版本的手机才有效果
                    //Build.VERSION_CODES.LOLLIPOP可以换成一个数字  版本数
                    viewHolder.iv_water_picture.setOnClickListener(new View.OnClickListener() {
                        //     @TargetApi(Build.VERSION_CODES.LOLLIPOP)   //必须是LOLLIPOP版本的手机才有的效果
                        public void onClick(View view) {
                            // Intent intent = new Intent(view.getContext(), LargeImageViewActivity.class);
                            //  intent.putExtra(Constants.IMAGEVIEW_URL,list.get(position).getBitmap().getUrl());
                            // 注意这里的sharedImageView
                            // Content，View（动画作用view），String（sharedView，和XML保持一样的命名）
//                            Constants.IMAGEVIEW_URL_VALUE=list.get(position).getBitmap().getUrl();
//                            view.getContext().startActivity(intent,
//                               ActivityOptions.makeSceneTransitionAnimation(
//                                       (Activity) view.getContext(), view, "sharedImageView").toBundle());

                            //smallImgClick(viewHolder.iv_water_picture);
                            smallImgClick(list.get(position - 2).getBitmap().getUrl());
                        }
                    });
                }
                //初始状态  设置  为  不可见
                viewHolder.iv_minus_2.setVisibility(View.GONE);
                viewHolder.tv_count.setVisibility(View.INVISIBLE);
                if (orderGoodList.get(position - 2).getCount() > 0) {
                    viewHolder.tv_count.setText(orderGoodList.get(position - 2).getCount() + "");
                    viewHolder.tv_count.setVisibility(View.VISIBLE);
                    viewHolder.iv_minus_2.setVisibility(View.VISIBLE);
                }
                viewHolder.tv_water_name.setText(list.get(position - 2).getName());
                viewHolder.tv_water_capacity.setText(list.get(position - 2).getCapacity() + "");
                viewHolder.tv_water_price.setText("￥ " + list.get(position - 2).getPrice() + "");
                x.image().bind(viewHolder.iv_water_picture, list.get(position - 2).getBitmap().getUrl(), imageOptions);
                viewHolder.iv_minus_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        count[0]--;
                        if (count[0] < 0) {
                            count[0] = 0;
                        }
                        orderGoodList.get(position - 2).setCount(count[0]);
                        deleteGoods(viewHolder.iv_minus_2, viewHolder.tv_count, count[0]);
                        setTotalMoney();


                    }
                });

                viewHolder.iv_add_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ball_isMoving == true) {   //如果上一个小球正在 运行动画，则当前点击的 小球不运行
                            return;
                        }
                        ball_isMoving = true;

                        int[] clickLocation = new int[2];
                        viewHolder.iv_add_2.getLocationInWindow(clickLocation);

                        Log.d("点击位置  x轴 " + clickLocation[0], "点击位置  y轴 " + clickLocation[1]);

                        //#################动画跳到 购物车
                        addBallToShoppingCar(clickLocation);

                        count[0]++;
                        orderGoodList.get(position - 2).setCount(count[0]);
                        addGoods(viewHolder.iv_minus_2, viewHolder.tv_count, count[0]);
                        setTotalMoney();

                    }
                });

                return convertView;

            }
        }

    }

    static class ViewHolder {
        ImageView iv_water_picture;
        TextView tv_water_name;
        TextView tv_water_capacity;
        TextView tv_water_price;
        ImageView iv_minus_2;
        TextView tv_count;
        ImageView iv_add_2;
    }

    private void initSlider() {
        for (int i = 0; i < list_lunbotu_water.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(mCtx);
            textSliderView
                    // .description(promotionList.get(i).getInfo())//给轮播图的每一个图片,添加描述文字 将promotionList.get(i).getInfo()进行替换为自己的描述信息
                    .image(list_lunbotu_water.get(i).getBitmap().getUrl())//指定图片 将promotionList.get(i).getPic()进行替换
                    .setScaleType(BaseSliderView.ScaleType.Fit);//ScaleType设置图片展示方式(fitxy  centercrop)
            //向SliderLayout控件的内部添加条目
            sliderLayout.addSlider(textSliderView);
        }
        //viewpager--->ImageView放置图片
        // viewpager 等价于 SliderLayout
        // TextSliderView 等价于 ImageView,只不过添加了一个显示文本功能
        //默认指定的动画类型
        //slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage); //Transformer.Stack
        //指定圆点指示器的位置
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //定义一个描述动画
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        //动画时长
        sliderLayout.setDuration(4000);

    }

    public void initSliderData() {
        BmobQuery<LunBoTuWater> query = new BmobQuery<LunBoTuWater>();
        query.setLimit(100);
        query.findObjects(new FindListener<LunBoTuWater>() {
            public void done(List<LunBoTuWater> object, BmobException e) {
                if (e == null) {
                    list_lunbotu_water.addAll(object);
                    initSlider();
                } else {
                    // Toast.makeText(mCtx, "获取商品分类失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initMarquee3DView(View view) {
        marquee3DView = (Marquee3DView) view.findViewById(R.id.marquee3DView);
        final ArrayList<String> labels = new ArrayList<String>();
        final ArrayList<Bitmap> labelBitmapList = new ArrayList<Bitmap>();
//        final Bitmap[] bitmaps = {
//                BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.head_1),
//                BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.default_avatar_img),
//                BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.single),
//                BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.timg),
//                BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.default_avatar_img)
//        };
        //#################################需要修改  获取订单的前5个订单的 名字   说不出来的忧伤已成功下单
        //                                           再跟着sex 去指定 该位置是显示boy 还是 girl


//        String end ="2016-01-05 20:20:17";
//        SimpleDateFormat sdf1 =newSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date1  =null;
//        try{
//            date1 = sdf1.parse(end);
//        }catch(ParseException e){
//            e.printStackTrace();
//        }

        BmobQuery<Order> query = new BmobQuery<Order>();
        query.setLimit(5);
//        try {
//            query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(TimeUtils.getCurrentDateTime()));
//        }catch (ParseException e){
//            Log.d("按时间查询失败","按时间查询失败");
//            e.printStackTrace();
//        }

        //    String start = "2018-07-19 00:00:00";


        query.order("-updatedAt");  //  根据updatedAt 时间  从上到下  开始  返回  5条数据
//        String start = TimeUtils.geDateTime(System.currentTimeMillis());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = null;
//        try {
//            date = sdf.parse(start);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));


        query.findObjects(new FindListener<Order>() {
            public void done(List<Order> object, BmobException e) {
                if (e == null) {
                    Log.d("前五条数据的 数量是" + object.size(), "前五条数据的 数量是" + object.size());
                    for (Order order : object) {
                        labels.add(order.getName() + " 已成功下单！");
                        if (order.getSex() == Constants.SEX_MAN) {
                            labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.boy));
                        } else {
                            labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.girl));
                        }
                    }
                    marquee3DView.setMarqueeLabels(labels);
                    marquee3DView.setLabelBitmap(labelBitmapList);

                } else {
                    labels.add("牵丶你右手 已成功下单！！");
                    labels.add("无上限宠你 已成功下单！");
                    labels.add("我想静静 已成功下单！");
                    labels.add("说不出来的忧伤 已成功下单！");
                    labels.add("灬丶君莫笑 已成功下单！");

                    Log.d("获取5个订单用户失败", "获取5个订单用户失败");

                    labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.girl));
                    labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.boy));
                    labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.girl));
                    labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.boy));
                    labelBitmapList.add(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.girl));
                    marquee3DView.setMarqueeLabels(labels);
                    marquee3DView.setLabelBitmap(labelBitmapList);
                }
            }
        });
//        BmobQuery<OrderPerson> query = new BmobQuery<OrderPerson>();
//        //返回50条数据，如果不加上这条语句，默认返回10条数据
//        query.setLimit(100);
//        //执行查询方法
//        query.findObjects(new FindListener<OrderPerson>() {
//            public void done(List<OrderPerson> object, BmobException e) {
//                if (e == null) {
//                    Log.d("广告条  查询成功：共" + object.size(), "条数据。");
//                    if (object.size() <= 0) {
//                        return;
//                    }
//                    for (int i = 0; i < object.size(); i++) {
//                        if (object.get(i).getName() != null) {
//                            Log.d("广告条图片文字", object.get(i).getName() + "");
//                            labels.add(object.get(i).getName());
//                        }
//                    }
//                } else {
//                    Log.i("bmob  广告条", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                    Toast.makeText(mCtx, "服务器获取数据失败！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        marquee3DView.setOnWhereItemClick(new Marquee3DView.OnWhereItemClick() {
//            @Override
//            public void onItemClick(int position) {
//
//            }
//        });


    }

    private void addGoods(ImageView ibMinus, TextView tvCount, int number) {
        totalNumber++;
        setTotalCount();
        mBtnAccount.setClickable(true);
        if (totalNumber == 1) {
            mBtnAccount.setBackgroundColor(Color.parseColor("#553090E6"));
        }
        //以下动画,只有在商品数量是0件的时候触发
        if (number == 1) {
            //构建3组动画
            //1.旋转
            RotateAnimation rotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            //2.透明
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            //3.平移(y轴不动,x轴由离自己右侧2个宽度的距离,回到初始位置)
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            //4.动画的集合
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.setDuration(500);
            ibMinus.startAnimation(animationSet);
            ibMinus.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.VISIBLE);
        }
        tvCount.setText(number + "");
    }

    private void deleteGoods(final ImageView ibMinus, final TextView tvCount, int number) {
        totalNumber--;

        if (totalNumber <= 0) {
            totalNumber = 0;
            mBtnAccount.setBackgroundColor(Color.parseColor("#003090E6"));
            mBtnAccount.setClickable(false);
        } else {
            mBtnAccount.setClickable(true);
        }
        setTotalCount();
        //点中的商品的数量是否为1件,是1件执行(滚动+透明+平移)
        if (number == 0) {
            //构建3组动画
            //1.旋转
            RotateAnimation rotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            //2.透明
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            //3.平移(y轴不动,x轴由离自己右侧2个宽度的距离,回到初始位置)
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 2,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            //4.动画的集合
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.setDuration(500);
            ibMinus.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ibMinus.setVisibility(View.GONE);
                    tvCount.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        tvCount.setText(number + "");
    }

    //获取所有商品类型
    public void getAllGood() {
        BmobQuery<Good> query = new BmobQuery<Good>();
        query.setLimit(1000);
        query.order("-updatedAt");
        query.findObjects(new FindListener<Good>() {
            public void done(List<Good> object, BmobException e) {
                if (e == null) {
                    list.addAll(object);
                    adapter.notifyDataSetChanged();
                    //初始化 订单商品 记录表   临时记录  下单情况
                    // orderGoodList = new ArrayList<OrderGood>();
                    for (Good good : list) {
                        OrderGood orderGood = new OrderGood();
                        orderGood.setName(good.getName());
                        orderGood.setCount(0);
                        orderGood.setPrice(good.getPrice());
                        orderGood.setBitmap(good.getBitmap());
                        orderGood.setCapacity(good.getCapacity());
                        orderGoodList.add(orderGood);
                    }

                } else {
                    Toast.makeText(mCtx, "获取商品失败,请检查网络！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.btn_account, R.id.btn_clear, R.id.rl_shoping_car_show})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_account:
                if (orderGoodList == null) {
                    return;
                }
                //#################################需要修改  从sharePreference，还需要在下订单成功之后将数据重置
                order.setName("啊耸");
                order.setPhone("123456789456");
                order.setSex(1);
                List<OrderGood> orderGoodList_submit = new ArrayList<OrderGood>();
                for (OrderGood orderGood : orderGoodList) {
                    if (orderGood.getCount() > 0) {
                        orderGoodList_submit.add(orderGood);
                    }
                }
                if (orderGoodList_submit.size() <= 0) {
                    return;
                }
                order.setList_order_good(orderGoodList_submit);


                //  submitOrder(order);

                Log.d("提交订单详情", "提交订单详情");
                for (OrderGood orderGood : orderGoodList_submit) {
                    Log.d(orderGood.getName() + "", orderGood.getCount() + "");
                }

                Intent intent = new Intent(mCtx, OrderDetailActivity.class);
                intent.putExtra(Constants.ORDER_INFO, order);
                //##############  startActivityForResult 为了 知道 订单详情页面是否下单成功

                startActivityForResult(intent, 20);

                break;

            case R.id.btn_clear:   //这个id 是购物车
//                clearOrderGoodListData();
//                break;

       //     case R.id.rl_shoping_car_show:

                showShoppingCar();
                break;
        }
    }

    //清空临时 订单商品 列表
    private void clearOrderGoodListData() {
        if (orderGoodList == null) {
            return;
        }
        for (OrderGood orderGood : orderGoodList) {
            orderGood.setCount(0);
        }
        //重新更新视图  将其改变为 初始状态
        adapter.notifyDataSetChanged();
        totalNumber = 0;
        mBtnAccount.setBackgroundColor(Color.parseColor("#003090E6"));
        setTotalCount();
        setTotalMoney();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void smallImgClick(ImageView v) {
        //有背景图
//        final AlertDialog dialog = new AlertDialog.Builder(this).create();
//        ImageView imgView = (ImageView)v;
//        dialog.setView(imgView);
//        dialog.show();

        // 全屏显示的方法
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        ImageView newImageView = new ImageView(mCtx);
        newImageView.setBackground(v.getDrawable());
        dialog.setContentView(newImageView);
        dialog.show();

        // 点击图片消失
        newImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void smallImgClick(String url) {
        //有背景图
//        final AlertDialog dialog = new AlertDialog.Builder(this).create();
//        ImageView imgView = (ImageView)v;
//        dialog.setView(imgView);
//        dialog.show();

        // 全屏显示的方法
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView imageView = new ImageView(mCtx);
        x.image().bind(imageView, url, imageOptions);
        dialog.setContentView(imageView);
        dialog.show();

        // 点击图片消失
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initLargeImageOptions() {
//      Animation scaleAnimation=  new ScaleAnimation(1.0f,2.0f,1.0f,2.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        scaleAnimation.setDuration(3000);

        largeImageOptions = new ImageOptions.Builder()
//                .setSize(DensityUtil.dip2px(70), DensityUtil.dip2px(70))//图片大小
//                .setRadius(DensityUtil.dip2px(35))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.water)//加载中默认显示图片
                .setFailureDrawableId(R.mipmap.water)//加载失败后默认显示图片
                .build();
    }

    //展示购物车详情
    public void showShoppingCar() {
        orderGoodList_ShoppingCar.clear();
        for (OrderGood orderGood : orderGoodList) {
            if (orderGood.getCount() > 0) {
                orderGoodList_ShoppingCar.add(orderGood);
            }
        }
        //弹出View  bottomSheet即是要弹出的view
        bottomSheetLayout.showWithSheetView(getShoppingCarView());

    }

    public View getShoppingCarView() {
        View view = View.inflate(mCtx, R.layout.shopping_car_show, null);
        TextView tv_clear = view.findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetLayout.dismissSheet();
                clearOrderGoodListData();

            }
        });
        ListView lv_shoppingCar_detail = view.findViewById(R.id.lv_order_detail);
        lv_shoppingCar_detail.setAdapter(shoppingCarAdapter);
        return view;
    }

    class ShoppingCarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orderGoodList_ShoppingCar.size();
        }

        @Override
        public OrderGood getItem(int position) {
            return orderGoodList_ShoppingCar.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ShoppingCarViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ShoppingCarViewHolder();
                convertView = View.inflate(mCtx, R.layout.shopping_car_item, null);
                viewHolder.tvGoodName = convertView.findViewById(R.id.tv_good_name);
                viewHolder.tv_good_price = convertView.findViewById(R.id.tv_good_price);
                viewHolder.tv_good_count = convertView.findViewById(R.id.tv_good_count);
                viewHolder.iv_good_add = convertView.findViewById(R.id.iv_good_add);
                viewHolder.iv_good_minus = convertView.findViewById(R.id.iv_good_minus);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ShoppingCarViewHolder) convertView.getTag();
            }
            viewHolder.tvGoodName.setText(getItem(position).getName());
            if (getItem(position).getCount() > 0) {
                viewHolder.tv_good_count.setText(getItem(position).getCount() + "");
            }
            viewHolder.tv_good_price.setText("￥ " + getItem(position).getPrice());

            //由于每次显示 购物车  详情的 时候，orderGoodList_ShoppingCar 都会去遍历  orderGoodList，所以  加减 时，只要操作 orderGoodList即可
            viewHolder.iv_good_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = orderGoodList_ShoppingCar.get(position).getCount();
                    count++;
                    totalNumber++;
                    setTotalCount();
                    viewHolder.tv_good_count.setText(count + "");
                    if (orderGoodList.contains(getItem(position))) {
                        int i = orderGoodList.indexOf(getItem(position));
                        orderGoodList.get(i).setCount(count);
                        adapter.notifyDataSetChanged();
                    }
                    setTotalMoney();
                }
            });

            viewHolder.iv_good_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = orderGoodList_ShoppingCar.get(position).getCount();
                    count--;
                    if (count <= 0) {
                        count = 0;
                    }
                    totalNumber--;
                    if (totalNumber <= 0) {
                        totalNumber = 0;
                    }
                    setTotalCount();

                    if (orderGoodList.contains(getItem(position))) {
                        int i = orderGoodList.indexOf(getItem(position));
                        orderGoodList.get(i).setCount(count);
                        adapter.notifyDataSetChanged();
                    }

                    if (count == 0) {
                        orderGoodList_ShoppingCar.remove(position);
                        if (totalNumber > 0) {
                            shoppingCarAdapter.notifyDataSetChanged();
                        } else {
                            bottomSheetLayout.dismissSheet();
                        }

                    } else {
                        viewHolder.tv_good_count.setText(count + "");
                    }

                    setTotalMoney();
                }
            });

            return convertView;
        }
    }

    //  添加 加号 小球到 购物车
    public void addBallToShoppingCar(int[] clickLocation) {

        ImageView imageview = new ImageView(mCtx);
        imageview.setImageResource(R.mipmap.button_add);
        imageview.setMaxWidth(20);
        imageview.setMaxHeight(20);
        int[] desLocation = new int[2];
        mBtnClear.getLocationInWindow(desLocation);
        Log.d("目的地 x 轴 " + desLocation[0], "目的地  y 轴" + desLocation[1]);
        addViewToWindowView((ViewGroup) getWindow().getDecorView(), imageview, clickLocation, desLocation);

        // 添加的 ImageView 必须是  添加在最底层的 DecorView ，这样ImageView的移动范围才是整个屏幕，
        //    (ViewGroup) getWindow().getDecorView()
        //  而不是 convertView ，如果是 converView 的话，那么 其 Y 轴  移动的 范围就只是convertView 的高度，
        // 就会产生 imgaeView被 listView的item吃掉的错觉

    }

    /**
     * 添加一个ImageView 到 父View 上
     *
     * @param clickLocation
     * @param desLocation
     */
    private void addViewToWindowView(ViewGroup fatherView, View imageview, int[] clickLocation, int[] desLocation) {

        int x = clickLocation[0];
        int y = clickLocation[1];
        int[] fatherLocation = new int[2];
        fatherView.getLocationInWindow(fatherLocation);  //测出父view的位置

        imageview.setX(x);
        imageview.setY(y - fatherLocation[1]);

        fatherView.addView(imageview, 50, 50);  // 因为是添加在父View 上的，而且 使用的是setX  和 setY，所以要用  相对位置来表示imageView的位置
        //                指定其宽高为50
        startAnimation(fatherView, imageview, clickLocation, desLocation);

    }

    public void startAnimation(final ViewGroup vg, final View view, int[] start_location, int[] desLocation) {

        //动画主要是根据 物理的平抛运动，水平方向匀速，竖直方向加速

        AnimationSet set = new AnimationSet(false);
        Animation translationX = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, desLocation[0] - start_location[0]+shoppingCar_width,// 本来是移动到购物车View的左上角
                                                                                            // 由于现在我的数量是显示在右上角的
                                                                                            //所以让其在x轴不要移太远，移到右上角即可
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0
        );
        //线性插值器 默认就是线性
        translationX.setInterpolator(new LinearInterpolator());

        Animation translationY = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, desLocation[1] - start_location[1]);

        //设置加速插值器
        translationY.setInterpolator(new AccelerateInterpolator());

        //   Animation scale = new ScaleAnimation(0.5f, 0.5f, 0.5f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        Animation alpha = new AlphaAnimation(1, 0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        //   set.addAnimation(scale);

        set.setDuration(500);
        view.startAnimation(set);

        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(final Animation animation) {
                //直接remove可能会因为界面仍在绘制中成而报错
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                        vg.removeView(view);  //结束后，父View必须将 添加进来的ImageView移除，因为它本来就不属于父View
                        ball_isMoving = false;

                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    class ShoppingCarViewHolder {
        TextView tvGoodName;
        TextView tv_good_price;
        ImageView iv_good_add;
        TextView tv_good_count;
        ImageView iv_good_minus;
    }

    public void setTotalMoney() {
        float totalMoney = 0;
        for (OrderGood orderGood : orderGoodList) {
            totalMoney += orderGood.getCount() * orderGood.getPrice();
        }
        tv_total_money.setText("￥ " + totalMoney);
    }

    public void setTotalCount() {
        if (totalNumber >= 0) {
            tv_total_count.setText(totalNumber + "");
        }
        if (totalNumber <= 0) {
            mBtnAccount.setBackgroundColor(Color.parseColor("#003090E6"));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 20:
                  //  mBtnAccount.setEnabled(true);
                    boolean isSuccess = intent.getBooleanExtra("isSuccess", false);
                    if (isSuccess) {
                        clearOrderGoodListData();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        shoppingCar_width = mBtnClear.getWidth();

        Log.d("000###########", "购物车的宽度: width = " + shoppingCar_width );
    }


}
