package global;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.mob.MobSDK;

import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import bean.ErrorInfo;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lenovo on 2018/7/13.
 */
public class YiZhKuApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainThreadId;
    String path;
    File file;
    StringBuilder sb;
    ErrorInfo errorInfo;

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "5e0cad60e2c9ad3dae52d505a41b0dd8");
        MobSDK.init(this);
        x.Ext.init(this); //Xutils初始化
        context = getApplicationContext();
        handler = new Handler();
        errorInfo=new ErrorInfo();
        mainThreadId = android.os.Process.myTid();
//        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // 请求权限
//            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE   },
//                    EXTERNAL_STORAGE_REQ_CODE);
//        }

        path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "yizhku_error.txt";
        file = new File(path);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread thread, Throwable ex) {
//                if(!file.exists()){
//                    file.crea
//                }

                //在获取到了未捕获的异常后,处理的方法
                ex.printStackTrace();
                //  Log.i(tag, "捕获到了一个程序的异常");
                //将捕获的异常存储到sd卡中
//                PrintWriter printWriter;
//                try {
//                    printWriter = new PrintWriter(file);
//                    ex.printStackTrace(printWriter);
//
//                    printWriter.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                FileInputStream inputStream = null;
//                try {
//                    inputStream = new FileInputStream(file);
//                    int length=-1;
//                    byte[] bytes = new byte[1024];
//                    sb = new StringBuilder("");
//                    while ((length=inputStream.read(bytes))!=-1) {
//                        sb.append(new String(bytes, 0, length));
//                        Log.d("可以写入",""+sb.toString());
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }finally {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                //上传公司的服务器
                errorInfo.setErrorinfo(ex.toString());
                errorInfo.setPhone_info(getUserPhoneInfo());
                Log.d("###############错误信息  "+ex.toString(),""+getUserPhoneInfo());
                errorInfo.save(new SaveListener<String>() {
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            //结束应用
                            System.exit(0);
                        } else {
                            //结束应用
                            System.exit(0);
                        }
                    }
                });
            }
        });
    }

    public  String getUserPhoneInfo() {
        StringBuilder ua = new StringBuilder("OSChina.NET");
        ua.append('/' + getPackageInfo().versionName + '_'
                +  getPackageInfo().versionCode);// app版本信息
        ua.append("/Android");// 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
        ua.append("/" + android.os.Build.MODEL); // 手机型号
        return ua.toString();
    }

    PackageInfo info = null;
    public  PackageInfo getPackageInfo() {
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }


}