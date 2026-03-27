package airline;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static UserManager userManager = new UserManager();
    public static FlightManager flightManager = new FlightManager();

    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Airline Reservation System");

        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.centerOnScreen();

        // 🔥 OPTIONAL: simulate concurrent users (for testing only)
        // comment this out in production
        simulateConcurrentUsers();

        LoginScreen login = new LoginScreen(userManager, flightManager);
        login.show(primaryStage);
    }

    // 🔥 CONCURRENCY SIMULATION
    // private void simulateConcurrentUsers() {

    //     new Thread(() -> {

    //         try {
    //             Thread.sleep(2000); // wait for app to initialize
    //         } catch (InterruptedException e) {
    //             Thread.currentThread().interrupt();
    //         }

    //         // pick a flight
    //         Flight flight = flightManager.searchFlights("DEL", "BOM").get(0);

    //         Runnable user1 = () -> {
    //             boolean lock = flight.tryLockSeat("E1A");
    //             System.out.println("User1 trying E1A: " + lock);
    //         };

    //         Runnable user2 = () -> {
    //             boolean lock = flight.tryLockSeat("E1A");
    //             System.out.println("User2 trying E1A: " + lock);
    //         };

    //         Runnable user3 = () -> {
    //             boolean lock = flight.tryLockSeat("E1A");
    //             System.out.println("User3 trying E1A: " + lock);
    //         };

    //         new Thread(user1).start();
    //         new Thread(user2).start();
    //         new Thread(user3).start();

    //     }).start();
    // }

    public static void main(String[] args) {
        launch(args);
    }
}
