/**
 * The carousel holds vials of vaccine and rotates them from the compartment
 * at position 0, through to the scanner compartment, where they are 
 * scanned and potentially removed by a shuttle for further inspection, 
 * through to the final compartment where they are taken off the carousel.
 */
public class Carousel {
    protected int INSPECTION_COMPARTMENT = 2; // the inspection compartment is constant at C3

    // the items in the carousel segments
    protected Vial[] compartment;
    protected int length;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public Carousel() {
        compartment = new Vial[Params.CAROUSEL_SIZE];
        length = compartment.length;
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
     * @param i the index of the compartment
     *
     * @return the removed vial
     *
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

    /**
     * Remove the defective vials when they arrive in C3
     * @return the defective vial
     *
     * @throws InterruptedException
     *      *             if the thread executing is interrupted
     */
    public synchronized Vial getVialForInspection() throws InterruptedException {
        Vial vial;
        while (compartment[INSPECTION_COMPARTMENT] == null || // the compartment is empty
                !compartment[INSPECTION_COMPARTMENT].isDefective() || // the vial in the compartment is not defective
                compartment[INSPECTION_COMPARTMENT].isInspected()) { // the vial in the compartment has been inspected
            wait();
        }
        vial = compartment[INSPECTION_COMPARTMENT];
        compartment[INSPECTION_COMPARTMENT] = null;
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
        while (isEmpty() || // do NOT rotate the carousel is empty
        		compartment[compartment.length-1] != null || // do NOT rotate if the last compartment contains a vial
                isVialInC3Defective() // do NOT rotate if C3 has a defective vial
                ) {
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

    /**
     * Check whether the vial in C3 is defective
     * @return true if there is a vial in C3 and it is defective, otherwise false
     */
    protected synchronized Boolean isVialInC3Defective() {
        if (compartment[2] != null) {
            return compartment[2].isDefective();
        }
        return false;
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
