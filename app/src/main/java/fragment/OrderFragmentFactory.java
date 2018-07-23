package fragment;

import java.util.HashMap;

/**
 * Created by lenovo on 2018/7/13.
 */

public class OrderFragmentFactory {

    private static HashMap<Integer, BaseFragment> mFragmentMap = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment = mFragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new Order_Water_Fragment();
                    break;
                case 1:
                    fragment = new Order_Repair_Fragment();
                    break;
                default:
                    break;
            }
        }
        mFragmentMap.put(position, fragment);
        return fragment;
    }


}
