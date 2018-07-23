package fragment;

import android.support.v4.app.Fragment;
import java.util.HashMap;

/**
 * Created by lenovo on 2018/7/13.
 */

public class FragmentFactory {

    private static HashMap<Integer, BaseFragment> mFragmentMap = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment = mFragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new DormitoryFragment();
                    break;
                case 1:
                    fragment = new RepairFragment();
                    break;
                case 2:
                    fragment = new LostFoundFragment();
                    break;
                default:
                    break;
            }
        }
        mFragmentMap.put(position, fragment);
        return fragment;
    }


}
