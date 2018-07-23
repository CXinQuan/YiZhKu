package base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.example.lenovo.yizhku.R;

/**
 * Created by lenovo on 2018/7/14.
 */

public class BaseActivity extends Activity {
public Context mCtx=this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

}
