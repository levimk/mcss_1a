/**
 * A subclass of the carousel, the destroyer is a specialised type of carousel for the defective vial recycling
 * subsystem. It takes defective vials that have been inspected and tagged by the inspection bay. It gives defective
 * vials to the destroyer consumer for destruction.
 */
public class DestroyerCarousel extends Carousel {

    /**
     * Create a new, empty destroyer carousel, initialised to be empty with default size of 2 compartments
     */
    public DestroyerCarousel() {
        super(2);
    }

    /**
     * Rotate the carousel one position.
     *
     * @throws OverloadException
     *             if a vial is rotated beyond the final compartment.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    @Override
    public synchronized void rotate()
            throws InterruptedException, OverloadException {
        // if there is a vial in the second compartment, or the carousel is empty,
        // do not move the carousel
        while (isEmpty() || // do NOT rotate if I'm empty
                compartment[compartment.length-1] != null ) { // do NOT rotate if my last compartment contains a vial
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
                                " [ c" + (i) + " -> c" + (i+1) + " ] destroyer carousel");
            }
            compartment[i] = compartment[i-1];
        }
        compartment[0] = null;

        // notify any waiting threads that the destroyer carousel has changed
        notifyAll();
    }

    /**
     * Print a message indicating that a vial has been removed for destruction
     * @param vial : the vial to be printed in the message
     */
    @Override
    protected void removeMessage(Vial vial) {
        System.out.println(indentation + indentation + vial + " removed (for destruction)");
    }
}
