/**
 * The main class for the vaccine fill/finish system simulator.
 */

public class Sim {
    /**
     * The main method to run the simulator.
     */
    public static void main(String[] args) {
        
    	// Components of the main carousel subsystem
        Carousel mainCarousel = new Carousel();
        CarouselDrive mainDriver = new CarouselDrive(mainCarousel);
        Producer producer = new Producer(mainCarousel);
        Consumer distributor = new Consumer(mainCarousel);

        // Components of the destroyer carousel subsystem
        Carousel destroyerCarousel = new DestroyerCarousel();
        Consumer destroyer = new Consumer(destroyerCarousel);
        CarouselDrive destroyerDriver = new CarouselDrive(destroyerCarousel);

        // Components of the shuttle subsystem
        InspectionBay inspectionBay = new InspectionBay(destroyerCarousel);
        Shuttle shuttle = new Shuttle(mainCarousel, inspectionBay);



        // start threads
        distributor.start();
        producer.start();
        shuttle.start();
        inspectionBay.start();
        destroyer.start();
        destroyerDriver.start();
        mainDriver.start();

        // check all threads still live
        while (distributor.isAlive() &&
               producer.isAlive() &&
               shuttle.isAlive() &&
                inspectionBay.isAlive() &&
                destroyer.isAlive() &&
                mainDriver.isAlive() &&
                destroyerDriver.isAlive())
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
        mainDriver.interrupt();
        destroyerDriver.interrupt();
        destroyer.interrupt();


        System.out.println("Sim terminating");
        System.out.println(VaccineHandlingThread.getTerminateException());
        System.exit(0);
    }
}
