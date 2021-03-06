import java.util.Random;

/**
 * A consumer continually, at random intervals, tries to take vials 
 * from the final compartment of a carousel.
 */

public class Consumer extends VaccineHandlingThread {

    // the carousel from which the consumer takes vials
    protected Carousel carousel;
    /**
     * Create a new Consumer that consumes from a carousel
     */
    public Consumer(Carousel carousel) {
        super();
        this.carousel = carousel;
    }

    /**
     * Loop indefinitely trying to get vials from the carousel
     */
    public void run() {
        Vial vial;
        while (!isInterrupted()) {
            try {
                vial = carousel.getVial(carousel.getLength() - 1);
                vial = null;
                System.out.println("Consumer state: " + Thread.currentThread().getState());
                // TODO: put them somewhere so I can inspect them?
                // let some time pass ...
                Random random = new Random();
                int sleepTime = Params.CONSUMER_MIN_SLEEP + 
                		random.nextInt(Params.CONSUMER_MAX_SLEEP - 
                				Params.CONSUMER_MIN_SLEEP);
                sleep(sleepTime);
//                throw new InterruptedException(); // TODO: clean up
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Consumer terminated");
    }
}
