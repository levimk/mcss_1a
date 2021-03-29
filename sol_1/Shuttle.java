import java.util.Random;

/**
 * A shuttle moves between the carousel (compartment 3, C3, specifically) and the inspection bay. It moves defective
 * vials only from C3 to the inspection bay. Inversely, it moves inspected and tagged vials (tagged for destruction)
 * from the inspection bay back to C3.
 */
public class Shuttle extends VaccineHandlingThread {

    // the carousel to which the producer puts vials
    final private static String indentation = "                  ";
    protected Carousel carousel;
    protected InspectionBay inspectionBay;
    protected Vial vial;
    protected Position position;

    /**
     * Keep track of the where the shuttle is.
     */
    private enum Position {
        CAROUSEL,
        INSPECTION_BAY
    }

    /**
     * Create a new shuttle and connect it to a carousel and an inspection bay.
     */
    Shuttle(Carousel carousel, InspectionBay inspectionBay) {
        super();
        this.carousel = carousel;
        this.inspectionBay = inspectionBay;
        this.vial = null;
        position = Position.CAROUSEL;
    }

    /**
     * Switch between the two possible positions of the carousel
     */
    private void togglePosition() {
        if (position.equals(Position.CAROUSEL)) {
            position = Position.INSPECTION_BAY;
        } else {
            position = Position.CAROUSEL;
        }
    }

    /**
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
                    // When at the carousel the shuttle will either be empty or contain an inspected and tagged vial
                    if (vial != null) {
                        // It has an inspected and tagged vial, so try to put it back on the carousel
                        carousel.putVial(vial, 2);
                        System.out.println(indentation + vial + " [  S -> c3 ]");
                        vial = null;
                    } else {
                        // It does not have a vial yet so try to get a defective that is not tagged and inspected
                        // from compartment 3 of the carousel
                        vial = carousel.getVialForInspection();
                        System.out.println(indentation + vial + " [ c3 -> S  ]");
                        togglePosition();
                        sleep(Params.SHUTTLE_TIME);
                    }
                } else if (position.equals(Position.INSPECTION_BAY)) {
                    // When at the inspection bay the shuttle will either be empty or contain a defective vial that
                    // has not been inspected and tagged yet
                    if (vial != null) {
                        // The shuttle has a new defective vial that needs to be inspected and tagged
                        inspectionBay.putVial(vial);
                        System.out.println(indentation + vial + " [  S -> I  ]");
                        vial = null;
                    } else {
                        // It does not have a vial yet so try to get get the vial from the inspection bay once it has
                        // been tagged and inspected
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
        System.out.println("Shuttle terminated");
    }
}
