/**
 * The carousel holds vials of vaccine and rotates them from the compartment
 * at position 0, through to the scanner compartment, where they are
 * scanned and potentially removed by a shuttle for further inspection,
 * through to the final compartment where they are taken off the carousel.
 */
public class InspectionBay extends VaccineHandlingThread {

    // the items in the carousel segments
    protected Vial vial;
    protected Carousel destroyerCarousel;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public InspectionBay(Carousel carousel) {
        vial = null;
        destroyerCarousel = carousel;
    }

    /**
     * Insert a vial into the carousel.
     *
     * @param newVial
     *            the vial to insert into the carousel.
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void putVial(Vial newVial)
            throws InterruptedException {

        // while there is another vial in the way, block this thread
        while (vial != null) {
            wait();
        }

        this.vial = newVial;

        // notify any waiting threads that the carousel state has changed
        notifyAll();
    }

    /**
     * Remove a vial from the final compartment of the carousel
     *
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Vial getVial() throws InterruptedException {
        // while there is no vial in the final compartment, block this thread
//        System.out.println("inspectionBay.getVial()");

        while (!isVialReadyForRemoval()) {
            wait();
        }
//        System.out.println("Removing vial from inspection bay.");
        Vial removedVial = vial;
        vial = null;

        // notify any waiting threads that the carousel has changed
        notifyAll();
        return removedVial;
    }

    private synchronized Boolean isVialReadyForRemoval() throws InterruptedException {
//        System.out.println("isReadyForRemoval: " + vial.isInspected() + " " + " " + vial.isTagged());
        if (!isEmpty()) {
            return vial.isInspected() && vial.isTagged();
        }
        return false;
    }

    private synchronized void tagAndInspectVial() throws InterruptedException {
        if (!isEmpty()) {
            vial.setTagged();
            vial.setInspected();
            notifyAll();
        }
//        vial.setTagged();
//        vial.setInspected();
//        notifyAll();
    }

    /**
     * Check whether the inspection bay is empty.
     * @return true if the inspection bay is empty, otherwise false
     */
    public synchronized boolean isEmpty() {
        return vial == null;
    }

    /**
     * Move the carousel as often as possible, but only if there
     * is a vial on the carousel which is not in the final compartment.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                if (!isEmpty() && !isVialReadyForRemoval()) {
                    Thread.sleep(Params.INSPECT_TIME);
                    tagAndInspectVial();
//                    System.out.println("InspectionBay complete " + vial);
                }
//                Thread.sleep(Params.INSPECT_TIME);
//                if (vial != null && isVialReadyForRemoval()) {
//                    System.out.println("Vial ready");
//                    Thread.sleep(Params.INSPECT_TIME);
//                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }

        System.out.println("CarouselDrive terminated");
    }

    public String toString() {
        return "Inspection Bar: " + vial.toString();
    }

}
