package com.example.lenovo.yizhku;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fragment.BaseFragment;
import fragment.FragmentFactory;
import fragment.LostFoundFragment;
import fragment.RepairFragment;
import global.Constants;
import utils.SharePreferenceUtils;
import utils.UIUtils;
import view.MyPagerTab;

public class DormitoryActivity extends FragmentActivity implements View.OnClickListener {
    public Context mCtx = this;
    private MyPagerTab mPagerTab;
    private ViewPager mViewPager;
    private MyAdapter mAdapter;
    private ImageView iv_phone_top;
    private ImageView iv_qq_top;
    private ImageView iv_phone_bellow;
    private ImageView iv_qq_bellow;
    BaseFragment LostFoundfragment;
    BaseFragment repairfragment;


    int x;
    int y;
    boolean isSwitch = false;
    boolean isMove = false;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWM;
    private View viewToast;
    private int mScreenWidth;
    private int mScreenHeight;
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dormitory);
        //1,初始化窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        //获取屏幕宽度
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        mScreenHeight = mWM.getDefaultDisplay().getHeight();

        //2,初始化悬浮球布局
        showBall();

        mPagerTab = (MyPagerTab) findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mPagerTab.setViewPager(mViewPager);// 将指针和viewpager绑定在一起
        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = FragmentFactory.createFragment(position);
                // 开始加载数据
                //  fragment.loadData();
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 10:
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(this)) {
                            // mWM.addView(viewToast, params);
                            addBall();
                        }
                    }
                    break;

                case Constants.REQUEST_PHOTO:

                    Uri uri = data.getData();

                    if (uri != null && repairfragment != null) {
                        ((RepairFragment) repairfragment).upload_photo(uri);
                    }

                    break;

            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_phone_top:
            case R.id.iv_phone_bellow:
                Toast.makeText(mCtx, "点击了手机电话", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_qq_top:
            case R.id.iv_qq_bellow:
                Toast.makeText(mCtx, "点击了qq", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * FragmentPagerAdapter是PagerAdapter的子类, 如果viewpager的页面是fragment的话,就继承此类
     */
    class MyAdapter extends FragmentPagerAdapter {

        private String[] mTabNames;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabNames = new String[]{"宿舍公告", "设备报修", "失物招领"};
        }

        // 返回页签标题
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }

        // 返回当前页面位置的fragment对象
        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment = FragmentFactory.createFragment(position);
            if (position == 2) {
                LostFoundfragment = fragment;
            } else if (position == 1) {
                repairfragment = fragment;
            }
            return fragment;
        }

        // fragment数量
        @Override
        public int getCount() {
            return mTabNames.length;
        }
    }

    private void showBall() {
        //给吐司定义出来的参数(宽高,类型,触摸方式)
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = android.R.style.Animation_Translucent;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	因为吐司需要根据手势去移动,所以必须要能触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // params.type = WindowManager.LayoutParams.TYPE_PHONE;//不在和吐司类型相互绑定,通话的类型相互绑定
        //将吐司放置在左上角显示
        params.gravity = Gravity.TOP + Gravity.LEFT;  //以右上角作为原点参照点
        params.setTitle("联系我们");
        //定义吐司布局xml--->view挂载到屏幕上
        viewToast = View.inflate(this, R.layout.ball_layout, null);

        ImageView iv_ball = (ImageView) viewToast.findViewById(R.id.iv_ball);
        iv_phone_top = (ImageView) viewToast.findViewById(R.id.iv_phone_top);
        iv_qq_top = (ImageView) viewToast.findViewById(R.id.iv_qq_top);
        iv_phone_bellow = (ImageView) viewToast.findViewById(R.id.iv_phone_bellow);
        iv_qq_bellow = (ImageView) viewToast.findViewById(R.id.iv_qq_bellow);
        iv_phone_top.setOnClickListener(this);
        iv_phone_bellow.setOnClickListener(this);
        iv_qq_top.setOnClickListener(this);
        iv_qq_bellow.setOnClickListener(this);

//        int locationX = (int) SharePreferenceUtils.get(mCtx, "locationX", 0);
//        int locationY = (int) SharePreferenceUtils.get(mCtx, "locationY", 0);
//      //  指定宽高都为WRAP_CONTENT
//        layoutParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        //将左上角的坐标作用在iv_drag对应规则参数上
//        layoutParams.leftMargin = locationX;
//        layoutParams.topMargin = locationY;
//        params=layoutParams;

        iv_ball.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取按下的xy坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        //     isMove = true;
                        //获取移动xy坐标和按下的xy坐标做差,做差得到的值小火箭移动的距离
                        //移动过程中做容错处理
                        //第一次移动到的位置,作为第二次移动的初始位置
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        params.x = params.x + disX;
                        params.y = params.y + disY;
                        //在窗体中仅仅告知吐司的左上角的坐标
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth - viewToast.getWidth()) {
                            params.x = mScreenWidth - viewToast.getWidth();
                        }
                        if (params.y > mScreenHeight - 22 - viewToast.getHeight()) {
                            params.y = mScreenHeight - 22 - viewToast.getHeight();
                        }
                        //告知吐司在窗体上刷新
                        mWM.updateViewLayout(viewToast, params);
                        //在第一次移动完成后,将最终坐标作为第二次移动的起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        x = params.x;
                        y = params.y;
                        break;
//                    case MotionEvent.ACTION_UP:
//                        //手指放开的时候,如果放手坐标,则指定区域内
//                    if (event.getRawX() <= mScreenWidth / 2) {
//                        params.x = 0;
//                        params.y = (int) event.getRawY() - 22;
//                    } else {
//                        params.x = mScreenWidth;
//                        params.y = (int) event.getRawY() - 22;
//                    }
//                    mWM.updateViewLayout(viewToast, params);
//                    break;

                    case MotionEvent.ACTION_UP:
                        isMove = false;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth / 2) {
                            params.x = mScreenWidth - viewToast.getWidth();
                        } else {
                            params.x = 0;
                        }
                        if (params.y > mScreenHeight - 22 - viewToast.getHeight()) {
                            params.y = mScreenHeight - 22 - viewToast.getHeight();
                        }
                        //告知吐司在窗体上刷新
                        mWM.updateViewLayout(viewToast, params);
                        x = params.x;
                        y = params.y;
//                        SharePreferenceUtils.put(mCtx, "locationX", params.x);
//                        SharePreferenceUtils.put(mCtx, "locationY", params.y);
                }
                return false;
            }
        });

        //双击弹出联系qq和电话
        iv_ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime != 0) {
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 500) {
                        Log.d("双击事件", "双击事件");
                        if (!isSwitch) {
                            if (y > 50) {
                                iv_phone_top.setVisibility(View.VISIBLE);
                                iv_qq_top.setVisibility(View.VISIBLE);
                                iv_phone_bellow.setVisibility(View.GONE);
                                iv_qq_bellow.setVisibility(View.GONE);
                            } else if (y < 50) {
                                iv_phone_top.setVisibility(View.GONE);
                                iv_qq_top.setVisibility(View.GONE);
                                iv_phone_bellow.setVisibility(View.VISIBLE);
                                iv_qq_bellow.setVisibility(View.VISIBLE);
                            }
//                    if (y > mScreenHeight - 100-iv_ball.getHeight()) {
//                     //   Log.d(mScreenHeight+"",mScreenHeight+"");
//                        iv_phone_top.setVisibility(View.VISIBLE);
//                        iv_qq_top.setVisibility(View.VISIBLE);
//                        iv_phone_bellow.setVisibility(View.GONE);
//                        iv_qq_bellow.setVisibility(View.GONE);
//                    }else {
//                        iv_phone_top.setVisibility(View.GONE);
//                        iv_qq_top.setVisibility(View.GONE);
//                        iv_phone_bellow.setVisibility(View.VISIBLE);
//                        iv_qq_bellow.setVisibility(View.VISIBLE);
//                    }
//                    Log.d(mScreenHeight+"",mScreenHeight+"");
                        } else {
                            iv_phone_top.setVisibility(View.GONE);
                            iv_qq_top.setVisibility(View.GONE);
                            iv_phone_bellow.setVisibility(View.GONE);
                            iv_qq_bellow.setVisibility(View.GONE);
                        }
                        isSwitch = !isSwitch;

                    }
                }
                startTime = System.currentTimeMillis();
            }
        });

        //开启悬浮球权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, 10);
            }
        }
        try {
//            mWM.addView(viewToast, params);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
    }

    public void addBall() {
        mWM.addView(viewToast, mParams);
    }

    public void onDestroy() {
        if (mWM != null && viewToast != null) {
            try {
               // mWM.removeView(viewToast);
            } catch (Exception e) {

            }
        }
        super.onDestroy();
    }


}
