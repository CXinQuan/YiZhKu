package page;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import base.BasePager;

/**
 * Created by lenovo on 2018/7/12.
 */

public class OrderPager extends BasePager {
    public OrderPager(Context mCtx) {
        super(mCtx);
    }

    @Override
    public void initData() {

    }

    @Override
    public View addToView() {
        TextView textView=new TextView(mCtx);
        textView.setText("订单页面");
        return textView;
    }

}
