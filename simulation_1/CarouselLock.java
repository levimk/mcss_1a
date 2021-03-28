public class CarouselLock {
    private static CarouselLock instance;

    private CarouselLock() {};

    public static CarouselLock getInstance() {
        if (instance == null) {
            instance = new CarouselLock();
        }
        return instance;
    }

    public void whoHasTheLock() {
        System.out.println(Thread.currentThread().getName() + " has the lock");
    }
}
