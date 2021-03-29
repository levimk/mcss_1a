/**
 * The main class for the vaccine fill/finish system simulator.
 */

public class Sim {
    /**
     * The main method to run the simulator.
     */
    public static void main(String[] args) {
        
    	// Create system components
        Carousel mainCarousel = new Carousel(Params.CAROUSEL_SIZE);

        Producer producer = new Producer(mainCarousel);
        Consumer distributor = new Consumer(mainCarousel);

        Carousel destroyerCarousel = new Carousel(2);
        Consumer destroyer = new Consumer(destroyerCarousel);

        InspectionBay inspectionBay = new InspectionBay(destroyerCarousel);
        Shuttle shuttle = new Shuttle(mainCarousel, inspectionBay);


        CarouselDrive driver = new CarouselDrive(mainCarousel);

        // start threads
        distributor.start();
        producer.start();
        shuttle.start();
        inspectionBay.start();
        driver.start();

        // check all threads still live
        while (distributor.isAlive() &&
               producer.isAlive() &&
               shuttle.isAlive() &&
               driver.isAlive())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                VaccineHandlingThread.terminate(e);
            }

        // interrupt other threads
        distributor.interrupt();
        producer.interrupt();
        shuttle.interrupt();
        inspectionBay.interrupt();
        driver.interrupt();


        System.out.println("Sim terminating");
        System.out.println(VaccineHandlingThread.getTerminateException());
        System.exit(0);
    }
}
