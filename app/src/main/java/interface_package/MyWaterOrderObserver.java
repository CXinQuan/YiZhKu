package interface_package;

import bean.Order;

/**
 * Created by lenovo on 2018/7/19.
 */

public interface MyWaterOrderObserver {
    public void update(Order order);
}
