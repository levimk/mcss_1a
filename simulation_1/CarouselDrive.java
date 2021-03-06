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
                System.out.println("Driver state: " + Thread.currentThread().getState());
            } catch (OverloadException e){
                System.out.println("Terminating CarouselDriv");
                terminate(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.interrupt();
            }
        }

        System.out.println("CarouselDrive terminated");
    }
}
