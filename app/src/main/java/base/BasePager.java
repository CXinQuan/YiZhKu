package base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.example.lenovo.yizhku.R;

/**
 * Created by lenovo on 2018/7/12.
 */

public abstract class BasePager {
    public Context mCtx;
    FrameLayout flContent;
    public View root_view;

    public BasePager(Context mCtx) {
        this.mCtx = mCtx;
        initView();
    }

    // 初始化布局
    public void initView() {
        root_view = View.inflate(mCtx, R.layout.base_layout, null);
        flContent = (FrameLayout) root_view.findViewById(R.id.fl_content);
        View view = addToView();
        flContent.addView(view);
        initData();
    }

    // 初始化数据
    public abstract void initData();

    public abstract View addToView();


}
