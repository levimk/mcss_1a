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
                        System.out.println(indentation + vial + " [  S -> c3 ]");
                        carousel.returnInspectedVial(vial);
                        vial = null;
                        Boolean didReturn = vial == null;
//                        System.out.println("Shuttle successfully returned vial? " + didReturn + " (vial = " + vial + ")");
                    } else {
                        vial = carousel.getVialForInspection();
//                        System.out.println("Shuttle picking up vial: " + vial);
                        System.out.println(indentation + vial + " [ c3 -> S  ]");

                        // move the shuttle
                        sleep(Params.SHUTTLE_TIME);
                        togglePosition();
                    }
                } else if (position.equals(Position.INSPECTION_BAY)) {
                    if (vial != null) {
//                        System.out.println("Delivering vial " + vial + " to inspection bay");
                        inspectionBay.putVial(vial);
                        System.out.println(indentation + vial + " [  S -> I  ]");
                        vial = null;
                    } else {
                        vial = inspectionBay.getVial();
//                        System.out.println("Picked up vial " + vial + " from inspection bay");
                        System.out.println(indentation + vial + " [  I -> S  ]");

                        // move the shuttle
                        sleep(Params.SHUTTLE_TIME);
                        togglePosition();
                    }
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
