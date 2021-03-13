import java.util.ArrayList;

public class MyCarousel {
    private static MyCarousel instance;
    private Object lock;
    private ArrayList<Vial> carousel;

    private MyCarousel() {
        this.lock = new Object();
    }

    public MyCarousel getInstance() {
        if (instance == null) {
            instance = new MyCarousel();
        }
        return instance;
    }

    public Object acquireLock() {
        return lock;
    }

    public void addVialAt(int compartment) {
    // TODO: wtf am I doing?
    }
}
