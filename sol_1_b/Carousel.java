/**
 * The carousel holds vials of vaccine and rotates them from the compartment
 * at position 0, through to the scanner compartment, where they are 
 * scanned and potentially removed by a shuttle for further inspection, 
 * through to the final compartment where they are taken off the carousel.
 */
public class Carousel {

    // the items in the carousel segments
    protected Vial[] compartment;
    protected int length;
    protected volatile Boolean awaitingInspection;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public Carousel() {
        compartment = new Vial[Params.CAROUSEL_SIZE];
        length = compartment.length;
        awaitingInspection = false;
        for (int i = 0; i < compartment.length; i++) {
            compartment[i] = null;
        }
    }

    public int getLength() { return length; };

    /**
     * Insert a vial into the carousel.
     * 
     * @param vial
     *            the vial to insert into the carousel.
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void putVial(Vial vial, int i)
            throws InterruptedException {

    	// while there is another vial in the way, block this thread
        while (compartment[i] != null) {
//            System.out.println("Waiting to put " + vial + " in c" + (i+1));
            wait();
        }

        // insert the element at the specified location
        compartment[i] = vial;

        // make a note of the event in output trace
        System.out.println(vial + " inserted");


        // notify any waiting threads that the carousel state has changed
        notifyAll();
    }

    public synchronized void returnInspectedVial(Vial vial) throws InterruptedException {
        System.out.println("Returning vial: " + vial);

        // while there is another vial in the way, block this thread
        while (compartment[2] != null) {
//            System.out.println("Waiting for c2 to be empty to be able to return inspected vial");
            wait();
        }

        // insert the element at c3
        compartment[2] = vial;

        // make a note of the event in output trace
        System.out.println(compartment[2] + " inspected and returned to carousel");


        setAwaitingInspection(false);
//        System.out.println("Awaiting inspection? " + awaitingInspection);

        // notify any waiting threads that the carousel state has changed
        notifyAll();
//        System.out.println("Notified wait set");

    }

    /**
     * Remove a vial from the final compartment of the carousel
     * 
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Vial getVial(int i) throws InterruptedException {

    	// the vial to be removed
        Vial vial;

        // while there is no vial in the final compartment, block this thread
        while (compartment[i] == null) {
//            System.out.println("Waiting for a vial to arrive in c" + (i+1));
            wait();
        }

        // get the vial
        vial = compartment[i];
        compartment[i] = null;

        if (i == length - 1) {
            // make a note of the event in output trace
            System.out.println(indentation + indentation + vial + " removed");
        }

        // notify any waiting threads that the carousel has changed
        notifyAll();
        return vial;
    }

    public synchronized Vial getVialForInspection() throws InterruptedException {
        Vial vial;
        while (compartment[2] == null || // the compartment is empty
                !compartment[2].isDefective() || // the vial in the compartment is not defective
                compartment[2].isInspected()) { // the vial in the compartment has been inspected
            wait();
        }
        vial = compartment[2];
        compartment[2] = null;
        setAwaitingInspection(true);
//        System.out.println("Awaiting inspection? " + awaitingInspection);
        notifyAll();
        return vial;
    }

    /**
     * Rotate the carousel one position.
     * 
     * @throws OverloadException
     *             if a vial is rotated beyond the final compartment.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void rotate() 
            throws InterruptedException, OverloadException {
        // if there is in the final compartment, or the carousel is empty,
        // or a vial needs to be removed for inspection, do not move the carousel
        while (isEmpty() || // do NOT rotate if I'm empty
        		compartment[compartment.length-1] != null || // do NOT rotate if my last compartment contains a vial
                vialInC3RequiresInspection() || // do NOT rotate if compartment #3 has a DEFECTIVE vial
                isAwaitingInspection() // the carousel has been stopped (i.e. by the shuttle)
                ) { // TODO: call isStopped here!
//            System.out.println("Waiting to rotate");
            wait();
        }

        // double check that a vial cannot be rotated beyond the final compartment
        if (compartment[compartment.length-1] != null) {
            String message = "vial rotated beyond final compartment";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = compartment.length-1; i > 0; i--) {
            if (this.compartment[i-1] != null) {
                System.out.println(
                		indentation +
                		this.compartment[i-1] +
                        " [ c" + (i) + " -> c" + (i+1) +" ]");
            }
            compartment[i] = compartment[i-1];
        }
        compartment[0] = null;

        // notify any waiting threads that the carousel has changed
        notifyAll();
    }

    protected synchronized Boolean vialInC3RequiresInspection() {
        if (compartment[2] != null) {
            return compartment[2].isDefective() && !compartment[2].isInspected();
        }
        return false;
    }

    protected synchronized void setAwaitingInspection(Boolean awaitingInspection) {
        this.awaitingInspection = awaitingInspection;
//        notifyAll();
    }
    protected synchronized Boolean isAwaitingInspection() {
        return awaitingInspection;
    }
 
    /**
     * Check whether the carousel is currently empty.
     * @return true if the carousel is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < compartment.length; i++) {
            if (compartment[i] != null) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return java.util.Arrays.toString(compartment);
    }

}
