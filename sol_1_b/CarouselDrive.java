/**
 * A carousel drive rotates a carousel as often as possible, but only
 * when there is a vial on the carousel not in the final compartment.
 */

public class CarouselDrive extends VaccineHandlingThread {

    // the carousel to be handled
    protected Carousel carousel;

    /**
     * Create a new CarouselDrive with a carousel to rotate.
     */
    public CarouselDrive(Carousel carousel) {
        super();
        this.carousel = carousel;
    }

    /**
     * Move the carousel as often as possible, but only if there 
     * is a vial on the carousel which is not in the final compartment.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                // spend DRIVE_TIME milliseconds rotating the carousel
                Thread.sleep(Params.DRIVE_TIME);
                carousel.rotate();
            } catch (OverloadException e) {
                terminate(e);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }

        System.out.println("CarouselDrive terminated");
    }
}
