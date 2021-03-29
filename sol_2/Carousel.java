/**
 * The carousel holds vials of vaccine and rotates them from the compartment
 * at position 0, through to the scanner compartment, where they are 
 * scanned and potentially removed by a shuttle for further inspection, 
 * through to the final compartment where they are taken off the carousel.
 */
public class Carousel {

    // the items in the carousel segments
    protected Vial[] compartment;
    protected int size;

    // to help format output trace
    final protected static String indentation = "                  ";

    /**
     * Create a new, empty carousel with a specified number of compartments
     */
    public Carousel(int size) {
        compartment = new Vial[size];
        this.size = size;
        for (int i = 0; i < compartment.length; i++) {
            compartment[i] = null;
        }
    }

    /**
     * Create a new, empty carousel, initialised to be empty with the default number of compartments.
     */
    public Carousel() {
        compartment = new Vial[Params.CAROUSEL_SIZE];
        this.size = compartment.length;
        for (int i = 0; i < compartment.length; i++) {
            compartment[i] = null;
        }
    }

    /**
     * Get the number of compartments
     *
     * @return an integer representing the number of compartments
     */
    public int getSize() { return size; };

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
     * @param i : the compartment at i-1
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Vial getVial(int i) throws InterruptedException {

    	// the vial to be removed
        Vial vial;

        // while there is no vial in the specified compartment, block this thread
        while (compartment[i] == null) {
            wait();
        }

        // get the vial
        vial = compartment[i];
        compartment[i] = null;

        // print the removal message for the last
        if (i == size - 1) {
            // make a note of the event in output trace
            removeMessage(vial);
        }

        // notify any waiting threads that the carousel has changed
        notifyAll();
        return vial;
    }

    /**
     * Print a message indicating that a vial has been removed
     * @param vial : the vial to be printed in the message
     */
    protected void removeMessage(Vial vial) {
        System.out.println(indentation + indentation + vial + " removed");
    }

    /**
     * Get a vial specifically for inspection
     *
     * @param i : the vial at compartment i-1
     * @return : the vial to sent to the inspection subsystem
     */
    public synchronized Vial getVialForInspection(int i) throws InterruptedException {
        Vial vial;

        // Block this thread if there is no vial or if the vial does not need to be inspected
        while (compartment[i] == null || // the compartment is empty
                !compartment[i].isDefective() || // the vial in the compartment is not defective
                compartment[i].isInspected()) { // the vial in the compartment has been inspected
            wait();
        }

        // Take out the vial
        vial = compartment[i];
        compartment[i] = null;

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
        while (isEmpty() || // do NOT rotate if the carousel is empty
        		compartment[compartment.length-1] != null || // do NOT rotate if the final compartment contains a vial
                isVialInC3Defective() // do NOT rotate if compartment #3 has a DEFECTIVE vial
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
                        " [ c" + (i) + " -> c" + (i+1) + " ] main carousel");
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
    private synchronized Boolean isVialInC3Defective() {
        if (compartment[2] != null) {
            return compartment[2].isDefective();
        }
        return false;
    }
 
    /**
     * Check whether the carousel is currently empty.
     * @return true if the carousel is currently empty, otherwise false
     */
    protected boolean isEmpty() {
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
