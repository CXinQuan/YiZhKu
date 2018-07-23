package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetBroadcastReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("网络状态已经改变", "网络状态已经改变");
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Log.d("网络状态已经改变", "当前网络名称：" + name);
                if(name.trim().equals("mobile")){
                    name="当前网络为手机流量";
                }
                Toast.makeText(context, "当前网络为："+name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
                Log.d("mark", "没有可用网络");
            }
        }
    }
}


//    public void onReceive( Context context, Intent intent ) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if ( activeNetInfo != null ) {
//            Toast.makeText( context, "Active Network Type : " +
//                    activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
//        }
//        if( mobNetInfo != null ) {
//            Toast.makeText( context, "Mobile Network Type : " +
//                    mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
//        }
//    }