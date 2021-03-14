/**
 * The main class for the vaccine fill/finish system simulator.
 */

public class Sim {
    /**
     * The main method to run the simulator.
     */
    public static void main(String[] args) {
        
    	// Create system components
        Carousel carousel = new Carousel();

        Producer producer = new Producer(carousel);
        producer.setName("Producer Thread");

        Consumer consumer = new Consumer(carousel);
        consumer.setName("Consumer Thread");

        CarouselDrive driver = new CarouselDrive(carousel);
        driver.setName("Driver Thread");

        // TODO: InspectionBay

        // start threads
        consumer.start();
        producer.start();
        driver.start();

        // check all threads still live
        while (consumer.isAlive() && 
               producer.isAlive() && 
               driver.isAlive())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                VaccineHandlingThread.terminate(e);
            }

        // interrupt other threads
        consumer.interrupt();
        producer.interrupt();
        driver.interrupt();

        System.out.println("Sim terminating");
        System.out.println(VaccineHandlingThread.getTerminateException());
        System.exit(0);
    }
}
