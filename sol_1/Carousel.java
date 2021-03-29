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
    protected volatile CarouselState state; // TODO: do I need volatile here?

    // to help format output trace
    final private static String indentation = "                  ";

    protected enum CarouselState {
        RUNNING,
        STOPPED
    }


    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public Carousel() {
        compartment = new Vial[Params.CAROUSEL_SIZE];
        length = compartment.length;
        state = CarouselState.RUNNING;
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
            wait();
        }

        // insert the element at the specified location
        compartment[i] = vial;

        // make a note of the event in output trace
        System.out.println(vial + " inserted");

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
    public synchronized Vial getVial(int i) throws InterruptedException {

    	// the vial to be removed
        Vial vial;

        // while there is no vial in the final compartment, block this thread
        while (compartment[i] == null) {
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

    public synchronized Vial getVialForInspection(int i) throws InterruptedException {
        Vial vial;
        while (compartment[i] == null || // the compartment is empty
                !compartment[i].isDefective() || // the vial in the compartment is not defective
                compartment[i].isInspected()) { // the vial in the compartment has been inspected
            wait();
        }
        vial = compartment[i];
        compartment[i] = null;
        notifyAll();
        return vial;
    }

//    public synchronized Boolean isVialNeedsInspection(int i) throws InterruptedException {
//
//        // while there is no vial in the final compartment, block this thread
//        while (compartment[i] == null &&
//               !compartment[i].isDefective() &&
//                !compartment[i].isInspected()) {
//            wait();
//        }
//
//        // get the vial
//        Boolean isDefective = compartment[i].isDefective();
//        Boolean isInspected = compartment[i].isInspected();
//
//        // notify any waiting threads that the carousel has changed
//        notifyAll();
//        return isDefective && !isInspected;
//    }

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
                isVialInC3Defective() // do NOT rotate if compartment #3 has a DEFECTIVE vial
                ) { // TODO: call isStopped here!
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
//        scan();
        
        // notify any waiting threads that the carousel has changed
        notifyAll();
    }

    protected synchronized Boolean isVialInC3Defective() {
        if (compartment[2] != null) {
            return compartment[2].isDefective();
        }
        return false;
    }

    // Do I even need these methods? Given that I have compartment[2].isDefective() guarding rotate()?
    protected synchronized void scan() {
        if (compartment[2].isDefective()) {
            stopCarousel();
        }
    }

    protected synchronized void stopCarousel() {
        state = CarouselState.STOPPED;
    }

    protected synchronized void restartCarousel() {
        state = CarouselState.RUNNING;
    }

    protected synchronized Boolean isStopped() {
        return state.equals(CarouselState.STOPPED);
    }

    protected synchronized Boolean isRunning() {
        return state.equals(CarouselState.RUNNING);
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
