/**
 * The carousel holds vials of vaccine and rotates them from the compartments
 * at position 0, through to the scanner compartments, where they are
 * scanned and potentially removed by a shuttle for further inspection, 
 * through to the final compartments where they are taken off the carousel.
 */
public class Carousel {

    // the items in the carousel segments
    protected Vial[] compartments;
    protected Object lock;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public Carousel(Object lock) {
        this.lock = lock;
        compartments = new Vial[Params.CAROUSEL_SIZE];
        for (int i = 0; i < compartments.length; i++) {
            compartments[i] = null;
        }
    }

    /**
     * Insert a vial into the carousel.
     * 
     * @param vial
     *            the vial to insert into the carousel.
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void putVial(Vial vial, int compartmentI)
            throws InterruptedException {

    	// while there is another vial in the way, block this thread
        while (compartments[compartmentI] != null) lock.wait();

        // insert the element at the specified location
        compartments[compartmentI] = vial;

        // make a note of the event in output trace
        System.out.println(vial + " inserted");

        // notify any waiting threads that the carousel state has changed
        lock.notifyAll();
    }

    /**
     * Remove a vial from the final compartments of the carousel
     * 
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public Vial getVial(int compartmentI) throws InterruptedException {
        synchronized (lock) {
            // the vial to be removed
            Vial vial;

            // while there is no vial in the final compartments, block this thread
            if (compartments[compartmentI] == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // get the vial
            vial = compartments[compartmentI];
            compartments[compartmentI] = null;

            // make a note of the event in output trace
            System.out.print(indentation + indentation);
            System.out.println(vial + " removed");

            // notify any waiting threads that the carousel has changed
            lock.notifyAll(); // TODO: will this cause issues? What if the next thread to execute needs the vial first?
            return vial;
        }
    }
 
    /**
     * Rotate the carousel one position.
     * 
     * @throws OverloadException
     *             if a vial is rotated beyond the final compartments.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void rotate() 
            throws InterruptedException, OverloadException {
        // if there is a vial in the final compartments,
        // or if the carousel is empty,
        // or if a vial needs to be removed for inspection, do not move the carousel
        while (isEmpty() || 
        		compartments[compartments.length-1] != null) {
            lock.wait();
        }

        // double check that a vial cannot be rotated beyond the final compartments
        if (compartments[compartments.length-1] != null) {
            String message = "vial rotated beyond final compartments";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = compartments.length-1; i > 0; i--) {
            if (this.compartments[i-1] != null) {
                System.out.println(
                		indentation +
                		this.compartments[i-1] +
                        " [ c" + (i) + " -> c" + (i+1) +" ]");
            }
            compartments[i] = compartments[i-1];
        }
        compartments[0] = null;
        
        // notify any waiting threads that the carousel has changed
        lock.notifyAll();
    }
 
    /**
     * Check whether the carousel is currently empty.
     * @return true if the carousel is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < compartments.length; i++) {
            if (compartments[i] != null) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return java.util.Arrays.toString(compartments);
    }

}
