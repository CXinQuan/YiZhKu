package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lenovo on 2018/7/22.
 */

public class ToastUtil {

    private static Toast toast;
    /**
     * 能够连续弹的吐司，不会等上个吐司消失
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text){
        if(toast==null){
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);//将text文本设置给吐司
        toast.show();
    }

    
}
