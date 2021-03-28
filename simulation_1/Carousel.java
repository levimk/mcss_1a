import java.util.Arrays;

/**
 * The carousel holds vials of vaccine and rotates them from the compartments
 * at position 0, through to the scanner compartments, where they are
 * scanned and potentially removed by a shuttle for further inspection, 
 * through to the final compartments where they are taken off the carousel.
 */
public class Carousel {

    // the items in the carousel segments
    protected Vial[] compartments;
    protected static CarouselLock lock;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty carousel, initialised to be empty.
     */
    public Carousel() {
        this.lock = CarouselLock.getInstance();
        compartments = new Vial[Params.CAROUSEL_SIZE];
        Arrays.fill(compartments, null);
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
        System.out.println("putVial(" + compartmentI + ") executing on:  " + Thread.currentThread().getName());
        System.out.println("Entering synchronised block - putVial() - state = " + Thread.currentThread().getState());
        synchronized (lock) {
            System.out.println("ENTERED synchronised block - putVial()");

            // while there is another vial in the way, block this thread
            while (compartments[compartmentI] != null) {
                try {
                    lock.whoHasTheLock();
                    System.out.println("WAIT " + Thread.currentThread().getName() + " from putVial(" + compartmentI + ")");
//                    lock.notifyAll();
                    System.out.println(Thread.currentThread().getName() + " about to wait - " + Thread.currentThread().getState());
                    lock.wait();
                    System.out.println("FINISHED WAITING " + Thread.currentThread().getName() + " from putVial(" + compartmentI + ")");
                    Thread.sleep(Params.CONSUMER_MIN_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // insert the element at the specified location
            compartments[compartmentI] = vial;

            // make a note of the event in output trace
            System.out.println(vial + " inserted");

            // notify any waiting threads that the carousel state has changed
            lock.notifyAll();
            System.out.println(Thread.currentThread().getName() + " NOTIFIED ALL from putVial()");
        }
    }

    /**
     * Remove a vial from the final compartments of the carousel
     * 
     * @return the removed vial
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public Vial getVial(int compartmentI) throws InterruptedException {
        System.out.println("getVial(" + compartmentI + ") executing on:  " + Thread.currentThread().getName());

        // the vial to be removed
        Vial vial;

        System.out.println("Entering synchronised block - getVial() - state = " + Thread.currentThread().getState());
        synchronized (lock) {
            System.out.println("ENTERED synchronised block - getVial()");
            // while there is no vial in the final compartments, block this thread
            while (compartments[compartmentI] == null) {
                try {
                    lock.whoHasTheLock();
                    System.out.println("WAIT "  + Thread.currentThread().getName() + " from getVial(" + compartmentI + ")");
                    lock.notifyAll();
                    System.out.println(Thread.currentThread().getName() + " about to wait - " + Thread.currentThread().getState());
                    lock.wait();
                    System.out.println("FINISHED WAITING "  + Thread.currentThread().getName() + " from getVial(" + compartmentI + ")");
                    Thread.sleep(Params.CONSUMER_MIN_SLEEP);
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
            // lock.notify();
            System.out.println(Thread.currentThread().getName() + " NOTIFIED ALL from getVial()");
        }
        return vial;
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
        System.out.println("rotate() executing on:  " + Thread.currentThread().getName());

        System.out.println("Entering synchronised block - rotate() - state = " + Thread.currentThread().getState());
        synchronized (lock) {
            System.out.println("Who has the lock? #1");
            lock.whoHasTheLock();
            while (isEmpty() ||
                    !isLastCompartmentEmpty()) { // TODO: here is the problem?
                try {
                    System.out.println("Who has the lock? #2");
                    lock.whoHasTheLock();
                    // if there is a vial in the final compartments,
                    // or if the carousel is empty,
                    // or if a vial needs to be removed for inspection, do not move the carousel
                    // TODO: compartment three case not being dealt with

                    System.out.println(Thread.currentThread().getName() + " about to wait - " + Thread.currentThread().getState());
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Rotating carousel");
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
    }
 
    /**
     * Check whether the carousel is currently empty.
     * @return true if the carousel is currently empty, otherwise false
     */
    private boolean isEmpty() {
        System.out.println("isEmpty() executing on:  " + Thread.currentThread().getName());
        for (Vial vial : compartments) {
            if (vial != null) {
                System.out.println("Carousel empty? false");
                return false;
            }
        }
//        for (int i = 0; i < compartments.length; i++) {
//            if (compartments[i] != null) {
//                return false;
//            }
//        }
        System.out.println("Carousel empty? true");
        return true;
    }

    public int getLength() {
        return compartments.length;
    }

    // TODO: implement
//    private Boolean isCompartmentThreeReady() {
//        return false;
//    }

    private Boolean isLastCompartmentEmpty() {
        Boolean check = compartments[compartments.length - 1] == null;
        System.out.println("Last compartment empty? " + check);
        return check;
    }
    
    public String toString() {
        return java.util.Arrays.toString(compartments);
    }

}
