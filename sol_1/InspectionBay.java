/**
 * The carousel inspection bay receives a defective vial from the shuttle bay. It inspects it and tags it for
 * destruction. In this implementation, the shuttle bay (see ShuttleBay) takes the inspected and tagged vial. The
 * inspection bay only holds one vial at a time.
 */
public class InspectionBay extends VaccineHandlingThread {

    // the items in the carousel segments
    protected Vial vial;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public InspectionBay() {
        vial = null;
    }

    /**
     * Insert a vial into the carousel.
     *
     * @param newVial
     *            the vial to insert into the inspection bay.
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
     * Remove the vial from the inspection bay
     *
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Vial getVial() throws InterruptedException {
        // while there is no vial in the inspection bay, block this thread

        // block the thread while the vial is still being inspected and tagged
        while (!isVialReadyForRemoval()) {
            wait();
        }

        Vial removedVial = vial;
        vial = null;

        // notify any waiting threads that the carousel has changed
        notifyAll();
        return removedVial;
    }

    /**
     * Check if the vial is ready to be removed
     * @return true if the inspection bay is not empty and the vial is inspected and the vial is tagged, and
     *              false otherwise
     *
     * @throws InterruptedException
     *      *             if the thread executing is interrupted
     */
    private synchronized Boolean isVialReadyForRemoval() throws InterruptedException {
        if (!isEmpty()) {
            return vial.isInspected() && vial.isTagged();
        }
        return false;
    }

    /**
     * Tag and inspect the vial
     * @throws InterruptedException
     *      *             if the thread executing is interrupted
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

    /**
     * Tag and inspect the vial in the inspection (if there is one).
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                if (!isEmpty() && !isVialReadyForRemoval()) {
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
        return "Inspection Bar: " + vial.toString();
    }

}
