package interface_package;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/7/19.
 */

public class MyObserverManager {

    private static MyObserverManager myObserverManager;

    public static MyObserverManager getInstance(){
        if(myObserverManager==null){
            synchronized (MyObserverManager.class){
                if(myObserverManager==null){
                    myObserverManager=new MyObserverManager();
                }
            }
        }
        return myObserverManager;
    }


    List<MyObserver> list=new ArrayList<MyObserver>();
    public void regiest(MyObserver myObserver){
        if(myObserver!=null){
            list.add(myObserver);
        }
    }

    public void unregiest(MyObserver myObserver){
        if(myObserver!=null&&list.contains(myObserver)){
            list.remove(myObserver);
        }
    }

    public void notifyAllObserver(float money){
        for (MyObserver myObserver:list){
            myObserver.update(money);
        }
    }


}
