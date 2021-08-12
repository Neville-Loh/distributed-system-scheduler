package raspberry.scheduler.testlistener;

import java.util.Observable;

public class testObservable extends Observable {


    private int a, b;

    public testObservable(){
        super();
    }


    public void increment() throws InterruptedException {

        while(true) {
            Thread.sleep(500);
            setChanged();
            notifyObservers(a);
            a++;
        }
    }

    public void add(int number){
        a = number;
    }
}
