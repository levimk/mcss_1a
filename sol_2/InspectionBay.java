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

    /**
     * Create a new, empty inspection bay, initialised to be empty. Connect it to a destroyer carousel.
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
        while (!isEmpty()) {
            wait();
        }

        this.vial = newVial;

        // notify any waiting threads that the carousel state has changed
        notifyAll();
    }

    /**
     * Check if the defective vial is ready for destruction
     *
     * @return true if there is a vial and it is ready to be destroyer, false otherwise
     * @throws InterruptedException
     */
    private synchronized Boolean isVialReadyForDestruction() throws InterruptedException {
        if (!isEmpty()) {
            return vial.isInspected() && vial.isTagged();
        }
        return false;
    }

    /**
     * Tag and inspect the vial then notify other threads of the change
     *
     * @throws InterruptedException
     */
    private synchronized void tagAndInspectVial() throws InterruptedException {
        if (!isEmpty()) {
            vial.setTagged();
            vial.setInspected();
            notifyAll();
        }
    }

    /**
     * Check whether the inspection bay is empty.
     * @return true if the inspection bay is empty, otherwise false
     */
    public synchronized boolean isEmpty() {
        return vial == null;
    }

    public synchronized void sendVialToDestroyer() throws InterruptedException {
        destroyerCarousel.putVial(vial, 0);
        vial = null;
        notifyAll();
    }

    /**
     * Continuously either (1) inspect and tag the vial or (2) send the vial off for destruction
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                if (isVialReadyForDestruction()) {
                    sendVialToDestroyer();
                } else {
                    Thread.sleep(Params.INSPECT_TIME);
                    tagAndInspectVial();
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }

        System.out.println("InspectionBay terminated");
    }

    public String toString() {
        return "Inspection Bay: " + vial.toString();
    }

}
