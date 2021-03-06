import java.util.Random;

/**
 * A producer continually tries, at varying time intervals, 
 * to put a vial onto a carousel
 */
public class Producer extends VaccineHandlingThread {

    // the carousel to which the producer puts vials
    protected Carousel carousel;

    /**
     * Create a new producer to feed a given carousel.
     */
    Producer(Carousel carousel) {
    	super();
        this.carousel = carousel;
    }

    /**
     * Continually tries to place vials on the carousel at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            System.out.println("Producer.run()");
            try {
                // put a new vial in the carousel
                Vial vial = Vial.getInstance();
                System.out.println("Producer: inserting new vial...");
                carousel.putVial(vial, 0);
                System.out.println("Producer: new vial inserted - state = " + Thread.currentThread().getState());

                // sleep for a bit....
                Random random = new Random();
                int sleepTime = random.nextInt(Params.PRODUCER_MAX_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                System.out.println("Producer: ~ interrupted ~");
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
