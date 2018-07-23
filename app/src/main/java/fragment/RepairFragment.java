package fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.yizhku.R;

import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;

import bean.RepairBean;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import global.Constants;
import utils.ToastUtil;
import utils.UIUtils;
import utils.UriToPathGetter;

import static android.Manifest.*;

/**
 * Created by lenovo on 2018/7/14.
 */

public class RepairFragment extends BaseFragment implements NumberPicker.OnValueChangeListener { //implements NumberPicker.Formatter

    RepairBean repair = new RepairBean();//是继承了BmobObject的一个类

    ImageView iv_repair_photo;
    ImageView ivBack;
    Spinner spinnerRepairType;
    EditText etDormitoryAddress;
    EditText etPerson;
    EditText etPhone;
    NumberPicker npHour;
    NumberPicker npMinute;
    EditText etDescribe;
    Button btnSubmitRepair;
    String[] hours = new String[24];
    String[] minute = new String[60];

    String[] type_text = new String[]{"风扇", "电灯", "线路", "水龙头", "水管", "热水设备", "床", "窗户", "其他"};
    int[] type_image = new int[]{R.mipmap.fan, R.mipmap.light, R.mipmap.line, R.mipmap.water_tap,
            R.mipmap.water_pipe, R.mipmap.hot_water, R.mipmap.bed, R.mipmap.window,
            R.mipmap.other};

    String hour_select;
    String minute_select;

    private void initViews(View view) {
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        spinnerRepairType = (Spinner) view.findViewById(R.id.spinner_repair_type);

        MySpinnerAdapter adapter = new MySpinnerAdapter();
        spinnerRepairType.setAdapter(adapter);
        spinnerRepairType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repair.setRepairType(type_text[position]);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etDormitoryAddress = (EditText) view.findViewById(R.id.et_dormitory_address);
        etPerson = (EditText) view.findViewById(R.id.et_person);
        etPhone = (EditText) view.findViewById(R.id.et_phone);
        npHour = (NumberPicker) view.findViewById(R.id.np_hour);
        npMinute = (NumberPicker) view.findViewById(R.id.np_minute);

        for (int i = 0; i < 60; i++) {
            if (i <= 23) {
                if (i <= 9) {
                    minute[i] = hours[i] = "0" + i;
                } else {
                    minute[i] = hours[i] = i + "";
                }
            } else {
                minute[i] = i + "";
            }
        }

        hour_select = hours[0];
        minute_select = minute[0];

        initNumberPicker(npHour, hours);
        initNumberPicker(npMinute, minute);

        etDescribe = (EditText) view.findViewById(R.id.et_describe);
        iv_repair_photo = (ImageView) view.findViewById(R.id.iv_repair_photo);
        //bmob上传图片的思路；
        // 1、打开图片库，选择图片，讲 返回来的 uri 存起来并将其转化为 图片的绝对路径（这里需要注意，手机需要开启本应用的存储权限）
        // 2、bmobFile.uploadblock  将图片单独先上传，上传成功会返回一个FileUrl, bmobFile.getFileUrl
        // 3.这个时候再将其bmobFile对象赋值给 Bean对象
        iv_repair_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断权限并开启权限
                final String[] PERMISSIONS_STORAGE = {
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"};
                if (ContextCompat.checkSelfPermission(getActivity(), permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有权限.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false)
                                .setMessage("应用需要存储权限来让您选择手机中的相片！")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getActivity(), "点击了取消按钮", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, Integer.parseInt(permission.WRITE_EXTERNAL_STORAGE));
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, Integer.parseInt(permission.WRITE_EXTERNAL_STORAGE));
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_PHOTO);
                    //必须是 getActivity().startActivityForResult ，不能没有getActivity()，没有的话，那么久无法接到返回的结果了
                }
            }
        });

        btnSubmitRepair = (Button) view.findViewById(R.id.btn_submit_repair);
        btnSubmitRepair.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (UIUtils.isEmtpy(etDormitoryAddress)) {
                    ToastUtil.showToast(getActivity(), "宿舍地址不能为空！");
                    return;
                }
                if (UIUtils.isEmtpy(etPerson)) {
                    ToastUtil.showToast(getActivity(), "联系人不能为空！");
                    return;
                }
                if (UIUtils.isEmtpy(etPhone)) {
                    ToastUtil.showToast(getActivity(), "联系电话不能为空！");
                    return;
                }

                if (UIUtils.isEmtpy(etDescribe)) {
                    ToastUtil.showToast(getActivity(), "请对此次维修进行描述！");
                    return;
                }
                repair.setAddress(etDormitoryAddress.getText().toString().trim());
                repair.setDescribe(etDescribe.getText().toString().trim());
                repair.setPhone(etPhone.getText().toString().trim());
                repair.setName(etPerson.getText().toString().trim());
                repair.setService_time(hour_select + ":" + minute_select);
                repair.setState(Constants.SUBMISSION);

                repair.save(new SaveListener<String>() {
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Log.d("bmob", "repairBean 保存成功");
                            Toast.makeText(getActivity(), "提交成功，受理编号为：" + repair.getObjectId(), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            //#############################  存入数据库


                        } else {
                            Log.d("bmob", "repairBean 保存失败：" + e.getMessage() + "," + e.getErrorCode());
                            Toast.makeText(getActivity(), "提交失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void initNumberPicker(NumberPicker mNumberPicker, String[] data) {

        mNumberPicker.setDisplayedValues(data);//设置需要显示的数组
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(data.length - 1);//这两行不能缺少,不然只能显示第一个，关联到format方法
        mNumberPicker.setWrapSelectorWheel(true);  //是否循环滚动
//        setPickerDividerColor(mNumberPicker);
//        setNumberPickerTextColor(mNumberPicker, Color.RED);
        mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPicker.setOnValueChangedListener(this);   //设置数值变化监听器
    }

    /**
     * 过反射改变  numberPicker  文字的颜色
     *
     * @param numberPicker
     * @param color
     * @return
     */
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("setTextColor", e);
                } catch (IllegalAccessException e) {
                    Log.w("setTextColor", e);
                } catch (IllegalArgumentException e) {
                    Log.w("setTextColor", e);
                }
            }
        }
        return false;
    }

    /**
     * 通过反射改变分割线颜色,
     */
    private void setPickerDividerColor(NumberPicker mNumberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(mNumberPicker, new ColorDrawable(Color.BLUE));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_repair, null);
        initViews(view);

        return view;
    }

    /**
     * 设置picker之间的间距
     */
    private void setPickerMargin(NumberPicker picker) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) picker.getLayoutParams();
        p.setMargins(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            p.setMarginStart(0);
            p.setMarginEnd(0);
        }
    }

    /**
     * 设置picker分割线的颜色
     */
    private void setDividerColor(NumberPicker picker) {
        Field field = null;
        try {
            field = NumberPicker.class.getDeclaredField("mSelectionDivider");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(picker, new ColorDrawable(Color.RED));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置picker分割线的宽度
     */
    private void setNumberPickerDivider(NumberPicker picker) {
        Field[] fields = NumberPicker.class.getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals("mSelectionDividerHeight")) {
                f.setAccessible(true);
                try {
                    f.set(picker, 10);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void loadData() {

    }

    public void upload_photo(Uri uri) {
        //  final BmobFile bmobFile = new BmobFile(new File(UriToPathGetter.getPath(getActivity(), uri)));
        String realFilePath = getFilePath(getActivity(), uri);
        final BmobFile bmobFile = new BmobFile(new File(realFilePath));
        bmobFile.uploadblock(new UploadFileListener() {
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    //     Toast.makeText(getActivity(), "上传图片成功:" + bmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                    //上传后，将图片显示出来
                    x.image().bind(iv_repair_photo, bmobFile.getFileUrl());

                    repair.setPhoto(bmobFile);

                } else {
                    Toast.makeText(getActivity(), "上传图片失败：" + e.getMessage() + "请重新上传！", Toast.LENGTH_SHORT).show();
                }
            }

            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    public static String getFilePath(Context context, Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        Cursor cursor = null;
        String path = "";
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            //获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
            path = cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.np_hour:
                hour_select = hours[newVal];
                break;
            case R.id.np_minute:
                minute_select = minute[newVal];
                break;
        }
    }

//    @Override
//    public String format(int value) {
//        return null;
//    }

    class MySpinnerAdapter extends BaseAdapter {

        public int getCount() {
            return type_text.length;
        }

        public Object getItem(int position) {
            return type_text[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getActivity(), R.layout.fragment_repair_type_item, null);
            ImageView iv_type = (ImageView) view.findViewById(R.id.iv_type);
            TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
            iv_type.setImageResource(type_image[position]);
            tv_type.setText(type_text[position]);
            return view;
        }
    }


}
