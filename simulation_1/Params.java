
/**
 * Parameters that influence the behaviour of the system.
 */

public class Params {

    // the number of compartments in this carousel
    public final static int CAROUSEL_SIZE = 5;
    
    // the maximum amount of time the producer waits
	public final static int PRODUCER_MAX_SLEEP = 3000;
	
	// the minimum amount of time the consumer waits
	public final static int CONSUMER_MIN_SLEEP = 500;
	
    // the maximum amount of time the consumer waits
	public final static int CONSUMER_MAX_SLEEP = 2800;
	
	// the amount of time it takes to move the belt
	public final static int DRIVE_TIME = 900;
	
	// the amount of time it takes the shuttle to move 
	// between the carousel and the inspection bay
	public final static int SHUTTLE_TIME = 900;
	
	// the amount of time it takes to inspect a vial
	public final static int INSPECT_TIME = 5000;
	
	// probability that a vial is defective
	public final static double DEFECT_PROB = 0.3;
	
}
