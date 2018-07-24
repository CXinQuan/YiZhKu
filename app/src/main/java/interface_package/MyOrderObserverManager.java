package interface_package;

import java.util.ArrayList;
import java.util.List;

import bean.Order;
import bean.RepairBean;

/**
 * Created by lenovo on 2018/7/19.
 */

public class MyOrderObserverManager {

    private static MyOrderObserverManager myObserverManager;


    public static MyOrderObserverManager getInstance(){
        if(myObserverManager==null){
            synchronized (MyOrderObserverManager.class){
                if(myObserverManager==null){
                    myObserverManager=new MyOrderObserverManager();
                }
            }
        }
        return myObserverManager;
    }

    /**
     *  桶装水订单
     */

    List<MyWaterOrderObserver> list=new ArrayList<MyWaterOrderObserver>();
    public void regiestWaterObserver(MyWaterOrderObserver myObserver){
        if(myObserver!=null){
            list.add(myObserver);
        }
    }

    public void unregiestWaterObserver(MyWaterOrderObserver myObserver){
        if(myObserver!=null&&list.contains(myObserver)){
            list.remove(myObserver);
        }
    }

    public void notifyAllWaterObserver(Order order){
        for (MyWaterOrderObserver myObserver:list){
            myObserver.update(order);
        }
    }


    /**
     *  维修订单观察
     */

    List<MyRepairOrderObserver> list_repair=new ArrayList<MyRepairOrderObserver>();
    public void regiestRepair(MyRepairOrderObserver myObserver){
        if(myObserver!=null){
            list_repair.add(myObserver);
        }
    }

    public void unregiestRepairObserver(MyRepairOrderObserver myObserver){
        if(myObserver!=null&&list.contains(myObserver)){
            list_repair.remove(myObserver);
        }
    }

    public void notifyAllRepairObserver(RepairBean repairBean){
        for (MyRepairOrderObserver myObserver:list_repair){
            myObserver.update(repairBean);
        }
    }



}
