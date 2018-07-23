package com.example.lenovo.yizhku;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import bean.UserAddressInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.manager.UserAddressManager;
import global.Constants;
import utils.SMSUtil;

public class AddressListActivity extends BaseActivity {
    @BindView(R.id.iv_goback)
    ImageView ivGoback;
    @BindView(R.id.tv_add_user_address)
    TextView tvAddUserAddress;
    @BindView(R.id.lv_add_address)
    ListView lvAddAddress;

    UserAddressManager manager;
    List<UserAddressInfo> list;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddress_list);
        ButterKnife.bind(this);
        manager = UserAddressManager.getInstance(mCtx);
        list = new ArrayList<UserAddressInfo>();

        list.addAll(manager.queryAllUserAddressInfo());
        adapter = new MyAdapter();
        lvAddAddress.setAdapter(adapter);
        lvAddAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //将  该 item  的 UserAddressInfo  返回给上一个activity
               /* Intent intent = new Intent();
                intent.putExtra(Constants.SELECT_ORDER_USER_INFO, list.get(position));
                setResult(RESULT_OK);
                finish();*/
            }
        });

    }

    @OnClick({R.id.tv_add_user_address, R.id.iv_goback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_user_address:

                //    显示 对话框 新增一个 用户收货信息

                showDialogUserAddress(null, 0);

                break;
            case R.id.iv_goback:
                finish();
                break;

        }
    }

    boolean isDefault = false;

    public void showDialogUserAddress(final UserAddressInfo userAddressInfo, final int position) {

        final UserAddressInfo info_Record = new UserAddressInfo();//用于 临时  记录

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_address_edit, null);
        dialog.setIcon(R.mipmap.car);
        dialog.setTitle("编辑收货地址");
        dialog.setView(view);
        Spinner spinner_ssl = view.findViewById(R.id.spinner_ssl);
        final Spinner spinner_ssh = view.findViewById(R.id.spinner_ssh);
        final Spinner spinner_floor = view.findViewById(R.id.spinner_lc);

        MySpinnerAdapter adapter_ssl = new MySpinnerAdapter(TYPE_SSL);
        final MySpinnerAdapter adapter_floor = new MySpinnerAdapter(TYPE_FLOOR);
        final MySpinnerAdapter adapter_ssh = new MySpinnerAdapter(TYPE_SSH);

        spinner_ssl.setAdapter(adapter_ssl);
        spinner_floor.setAdapter(adapter_floor);
        spinner_ssh.setAdapter(adapter_ssh);

        final RelativeLayout rl_ssh = view.findViewById(R.id.rl_ssh);
        final RelativeLayout rl_lc = view.findViewById(R.id.rl_lc);
        final EditText et_user_name = view.findViewById(R.id.et_user_name);
        final EditText et_user_phone = view.findViewById(R.id.et_user_phone);

        final RadioGroup radioGroup = view.findViewById(R.id.rg_sex);
        final RadioButton rbtn_man = view.findViewById(R.id.rbtn_man);
        RadioButton rbtn_woman = view.findViewById(R.id.rbtn_woman);
        Button btn_cancle = view.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 重置一下
                isDefault = false;
                ssl_select = 0;
                ssh_select = 0;
                floor_select = 0;
            }
        });
        Button btn_submit = view.findViewById(R.id.btn_submit);
        if (userAddressInfo == null) {  // 新建用户信息
            //让对话框显示一个自己定义的对话框界面效果
            rl_lc.setVisibility(View.GONE);
            rl_ssh.setVisibility(View.GONE);

        } else {  //更新用户信息
            et_user_phone.setText(userAddressInfo.getPhone());
            et_user_name.setText(userAddressInfo.getName());
            rl_lc.setVisibility(View.VISIBLE);
            rl_ssh.setVisibility(View.VISIBLE);
            info_Record.setId(userAddressInfo.getId());
            isDefault = userAddressInfo.isSelect();
            for (int i = 0; i < ssl.length; i++) {
                if (ssl[i].equals(userAddressInfo.getDormitory())) {
                    ssl_select = i;
                    break;
                }
            }
            spinner_ssl.setSelection(ssl_select);
            if (Integer.parseInt(userAddressInfo.getDorm_number()) % 10 == 0) {
                ssh_select = 9;
            } else {
                for (int i = 0; i < ssh.length - 1; i++) {
                    if (ssh[i].equals("0" + Integer.parseInt(userAddressInfo.getDorm_number()) % 10 + "")) {
                        ssh_select = i;
                        break;
                    }
                }
            }
            floor_select = userAddressInfo.getFloor();
            spinner_ssh.setSelection(ssh_select);
            spinner_floor.setSelection(floor_select - 1);// 必须减去1，因为楼层是 从1开始算的，而 数组是从 0开始算的

            if (userAddressInfo.getSex() == 1) {
                rbtn_man.setChecked(true);
                rbtn_woman.setChecked(false);
            } else {
                rbtn_man.setChecked(false);
                rbtn_woman.setChecked(true);
            }

        }
        spinner_ssl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ssl_select = position;
                rl_lc.setVisibility(View.VISIBLE);
                //      adapter_floor=new MySpinnerAdapter(TYPE_FLOOR);
                adapter_floor.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floor_select = position;
                rl_ssh.setVisibility(View.VISIBLE);
                adapter_ssh.notifyDataSetChanged();

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_ssh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ssh_select = position;
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_user_name.getText().toString().trim()==null||et_user_name.getText().toString().trim().equals("")) {
                    Toast.makeText(mCtx, "请输入联系人名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_user_phone.getText().toString().trim()==null
                        ||et_user_phone.getText().toString().trim().equals("")
                        ||! SMSUtil.judgePhoneNums(mCtx,et_user_phone.getText().toString().trim())) {
                    Toast.makeText(mCtx, "请检查输入的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                info_Record.setName(et_user_name.getText().toString().trim());
                info_Record.setSex(rbtn_man.isChecked() ? 1 : 0);
                info_Record.setDormitory(ssl[ssl_select]);
                info_Record.setDorm_number(floor[floor_select] + ssh[ssh_select]);
                info_Record.setFloor(floor[floor_select]);
                info_Record.setPhone(et_user_phone.getText().toString().trim());
                info_Record.setSelect(isDefault);
                if (userAddressInfo == null) {  //说明是新增用户
                    //需要插入信息
                    manager.saveUserAddressInfo(info_Record);
                    list.add(info_Record);
                    adapter.notifyDataSetChanged();
                } else {
                    //  编辑用户信息 需要更新
                    UserAddressInfo userAddressInfo1 = list.get(position);
                    userAddressInfo1 = info_Record;
                    list.get(position).setId(info_Record.getId());
                    list.get(position).setName(info_Record.getName());
                    list.get(position).setPhone(info_Record.getPhone());
                    list.get(position).setSex(info_Record.getSex());
                    list.get(position).setDormitory(info_Record.getDormitory());
                    list.get(position).setFloor(info_Record.getFloor());
                    list.get(position).setDorm_number(info_Record.getDorm_number());
                    manager.updateUserAddressInfo(userAddressInfo1);
//                    list.clear();
//                    list.addAll(manager.queryAllUserAddressInfo());
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
                // 重置一下
                isDefault = false;
                ssl_select = 0;
                ssh_select = 0;
                floor_select = 0;
            }
        });
        dialog.show();
    }

    int ssl_select;
    int ssh_select;
    int floor_select;
    String[] ssl = new String[]{"A栋", "B栋", "C栋", "D栋", "E栋", "F栋", "6栋", "7栋", "8栋"};
    int[] floor = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    String[] ssh = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "010"};//,"09","10","11","12","13","14","15","16"

    public static final int TYPE_SSL = 1;
    public static final int TYPE_FLOOR = 2;
    public static final int TYPE_SSH = 3;

    class MySpinnerAdapter extends BaseAdapter {
        int type;

        public MySpinnerAdapter(int type) {
            this.type = type;
        }

        public int getCount() {
            int count = 0;
            switch (type) {
                case TYPE_SSL:
                    count = ssl.length;
                    break;
                case TYPE_FLOOR:
                    switch (ssl_select) {
                        case 6:
                        case 7:
                        case 8:
                            count = 8;
                            break;
                        case 3:
                            count = 14;
                            break;
                        default:
                            count = 2;
                            break;
                    }
                case TYPE_SSH:
                    count = ssh.length;
                    break;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            Object o = null;
            switch (type) {
                case TYPE_SSL:
                    o = ssl[position];
                    break;
                case TYPE_FLOOR:
                    o = floor[position];
                    break;
                case TYPE_SSH:
                    o = ssh[position];
                    break;
            }
            return o;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mCtx);
            textView.setText(getItem(position).toString());
            textView.setTextSize(18);
            return textView;
        }
    }

    int popupWindow_dx;   //用于记录popupWindow的x轴偏离

    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return list.size();
        }

        public UserAddressInfo getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mCtx, R.layout.address_listview_item, null);
                viewHolder.tv_address = convertView.findViewById(R.id.tv_address);
                viewHolder.tv_user_name_phone = convertView.findViewById(R.id.tv_user_name_phone);
                viewHolder.iv_edit_address = convertView.findViewById(R.id.iv_edit_address);
                viewHolder.rb_set_default = convertView.findViewById(R.id.rb_set_default);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserAddressInfo userAddressInfo = getItem(position);
            viewHolder.tv_address.setText(userAddressInfo.getDormitory() + " "
                    + userAddressInfo.getFloor() + "层 "
                    + userAddressInfo.getDorm_number());
            viewHolder.tv_user_name_phone.setText(userAddressInfo.getName() + (userAddressInfo.getSex() == 1 ? "（先生）" : "（女士）")
                    + " " + userAddressInfo.getPhone());
            if (list.get(position).isSelect()) {
                viewHolder.rb_set_default.setChecked(true);
            } else {
                viewHolder.rb_set_default.setChecked(false);
            }
            viewHolder.iv_edit_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   编辑该项 收货信息
                    showDialogUserAddress(list.get(position), position);
                }
            });
            viewHolder.rb_set_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {  //如果是变为 选中，才执行以下代码
                        if (list.get(position).isSelect()) {  //如果选中项 本来就是 默认项的话，那么就不执行
                            return;
                        }
                        manager.updateUserAddress_Select(list.get(position).getId());
                        for (int i = 0; i < list.size(); i++) {
                            if (i == position) {
                                list.get(i).setSelect(true);
                            } else {
                                list.get(i).setSelect(false);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            // 长按弹出popupWindow 进行删除
            convertView.setLongClickable(true);
            final View finalConvertView = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SELECT_ORDER_USER_INFO, list.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            convertView.setOnTouchListener(new View.OnTouchListener() {
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
            finalConvertView.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View v) {
                    //    显示pp框
                    showPopupWindow(finalConvertView, position);
                    return false;
                }
            });
            return convertView;
        }
    }

    protected void showPopupWindow(View view, final int position) {
        //1,窗体上展示的内容
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.popupwindow_delete);
        //2,挂载在popupWindow上
        final PopupWindow popupWindow = new PopupWindow(imageView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true);  //宽高各100，true表示要获取焦点
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                adapter.notifyDataSetChanged();
                manager.deleteUserAddressInfo(list.get(position).getId());
                popupWindow.dismiss();
                popupWindow_dx = 0;
            }
        });
        //3,指定popupWindow的背景(设置上背景,点击返回按钮才会有响应)
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //一定要设置背景，这里设置为透明背景
        //4,指定popupWindow所在位置(挂载在哪个控件上,在父控件上的位置,偏离距离)
        popupWindow.showAsDropDown(view, popupWindow_dx, -view.getHeight() - 15); //在根布局上显示，正中间，距离正中间的x，y往右往下各偏离100
    }

    class ViewHolder {
        TextView tv_address;
        TextView tv_user_name_phone;
        ImageView iv_edit_address;
        RadioButton rb_set_default;
    }

    @Override
    protected void onDestroy() {
        manager.closeDB();
        super.onDestroy();
    }
}
