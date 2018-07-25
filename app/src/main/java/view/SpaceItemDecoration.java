package view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lenovo on 2018/7/24.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;
        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
//            outRect.left = mSpace;      //左边距
//            outRect.right = mSpace;     //右边距
//            outRect.bottom = mSpace;    // 下边距
            if (parent.getChildAdapterPosition(view) == 0) {  //如果是RecyclerView的第一个子项，则设置该子项上方的间距 为0
                outRect.top = 0;
            }else{
                outRect.top = mSpace;
            }

        }


}
