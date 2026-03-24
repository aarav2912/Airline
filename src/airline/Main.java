package airline;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static UserManager userManager = new UserManager();
    public static FlightManager flightManager = new FlightManager();

    // Define your application's "Standard" size here
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Airline Reservation System");

        // 1. Set a minimum size so the window never "shrinks" to fit small content
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);

        // 2. Set the initial size
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        // 3. Optional: Center it on the screen
        primaryStage.centerOnScreen();

        LoginScreen login = new LoginScreen(userManager, flightManager);
        
        // Pass the stage to your show method
        login.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}