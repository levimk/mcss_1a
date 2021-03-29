import java.util.Random;

/**
 * A producer continually tries, at varying time intervals,
 * to put a vial onto a carousel
 */
public class Shuttle extends VaccineHandlingThread {

    // the carousel to which the producer puts vials
    final private static String indentation = "                  ";
    protected Carousel carousel;
    protected InspectionBay inspectionBay;
    protected Vial vial;
    protected Position position;
    private enum Position {
        CAROUSEL,
        INSPECTION_BAY
    }

    /**
     * Create a new producer to feed a given carousel.
     */
    Shuttle(Carousel carousel, InspectionBay inspectionBay) {
        super();
        this.carousel = carousel;
        this.inspectionBay = inspectionBay;
        this.vial = null;
        position = Position.CAROUSEL;
    }

    private void togglePosition() {
        if (position.equals(Position.CAROUSEL)) {
            position = Position.INSPECTION_BAY;
        } else {
            position = Position.CAROUSEL;
        }
    }



    /** TODO: finish this DOC
     * Continually attempt to either move defective vials back and forth between the carousel and the inspection bay.
     * The shuttle can be in one of four states:
     *   1. At the carousel removing up a new defective vial for inspection
     *   2. At the carousel to return a defective vial that has been inspected and tagged
     *   3. At the shuttle bay delivering a new defective vial for inspection and tagging
     *   4. At the shuttle waiting for a the defective vial to be inspected and tagged
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                if (position.equals(Position.CAROUSEL)) {
                    // When at the carousel the shuttle will always be empty

                    // Get the defective vial from compartment 3 of the carousel. . .
                    vial = carousel.getVialForInspection(2);

                    // . . . Then take it to the inspection bay
                    System.out.println(indentation + vial + " [ c3 -> S  ]");
                    sleep(Params.SHUTTLE_TIME);
                    togglePosition();
                } else if (position.equals(Position.INSPECTION_BAY)) {
                    // When at the inspection bay the shuttle will always contain a defective vial that has not
                    // yet been inspected and tagged

                    // Put the defective vial in the inspection bay. . .
                    inspectionBay.putVial(vial);
                    System.out.println(indentation + vial + " [  S -> I  ]");
                    vial = null;

                    // . . . Then head back to the carousel to get the next defective vial
                    sleep(Params.SHUTTLE_TIME);
                    togglePosition();
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Shuttle terminated");
    }
}
