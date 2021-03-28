import java.util.Random;

/**
 * A producer continually tries, at varying time intervals,
 * to put a vial onto a carousel
 */
public class Shuttle extends VaccineHandlingThread {

    // the carousel to which the producer puts vials
    protected Carousel carousel;
    final private static String indentation = "                  ";

    /**
     * Create a new producer to feed a given carousel.
     */
    Shuttle(Carousel carousel) {
        super();
        this.carousel = carousel;
    }

    /**
     * Continually tries to place vials on the carousel at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                Boolean isDefective = carousel.isDefectiveVial(2);
                Boolean isInspected = carousel.isInspectedVial(2);

                if (isDefective && !isInspected) {
                    Vial vial;
                    vial = carousel.getVial(2);
                    System.out.println(indentation + vial + " [ c3 -> S  ]");
                    System.out.println(indentation + vial + " [  S -> I  ]");
                    sleep(Params.SHUTTLE_TIME);
                    sleep(Params.INSPECT_TIME);
                    vial.setInspected();
                    System.out.println(indentation + vial + " [  I -> S  ]");
                    sleep(Params.SHUTTLE_TIME);
                    System.out.println(indentation + vial + " [  S -> c3 ]");
                    carousel.putVial(vial, 2);
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
