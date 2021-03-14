import java.util.Random;

/**
 * A producer continually tries, at varying time intervals, 
 * to put a vial onto a carousel
 */
public class Producer extends VaccineHandlingThread {

    // the carousel to which the producer puts vials
    protected Carousel carousel;
    protected Object lock;

    /**
     * Create a new producer to feed a given carousel.
     */
    Producer(Carousel carousel, Object lock) {
    	super();
        this.carousel = carousel;
        this.lock = lock;
    }

    /**
     * Continually tries to place vials on the carousel at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                // put a new vial in the carousel
                Vial vial = Vial.getInstance();
                carousel.putVial(vial);

                // sleep for a bit....
                Random random = new Random();
                int sleepTime = random.nextInt(Params.PRODUCER_MAX_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
