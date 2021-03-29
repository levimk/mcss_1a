public class DestroyerCarousel extends Carousel {

    public DestroyerCarousel() {
        super(2);
    }

    @Override
    public synchronized void rotate()
            throws InterruptedException, OverloadException {
        // if there is in the final compartment, or the carousel is empty,
        // or a vial needs to be removed for inspection, do not move the carousel
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

        // notify any waiting threads that the carousel has changed
        notifyAll();
    }

    @Override
    protected void removeMessage(Vial vial) {
        System.out.println(indentation + indentation + vial + " destroyed");
    }
}
