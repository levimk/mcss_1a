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



    /**
     * Continually tries to place vials on the carousel at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                if (position.equals(Position.CAROUSEL)) {
                    if (vial != null) {
                        carousel.putVial(vial, 2);
                        System.out.println(indentation + vial + " [  S -> c3 ]");
                        vial = null;
                    } else {
//                        Boolean needsInspection = carousel.isVialNeedsInspection(2);
                        vial = carousel.getVialForInspection(2);
                        System.out.println(indentation + vial + " [ c3 -> S  ]");
                        togglePosition();
                        sleep(Params.SHUTTLE_TIME);
                    }
                } else if (position.equals(Position.INSPECTION_BAY)) {
                    if (vial != null) {
                        inspectionBay.putVial(vial);
                        System.out.println(indentation + vial + " [  S -> I  ]");
                        vial = null;
                    } else {
                        System.out.println("Getting vial from inspection bay");
                        vial = inspectionBay.getVial();
                        System.out.println(indentation + vial + " [  I -> S  ]");

                        togglePosition();
                        sleep(Params.SHUTTLE_TIME);
                    }
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
