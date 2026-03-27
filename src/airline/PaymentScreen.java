package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

public class PaymentScreen {

    private Flight flight;
    private String seatClass;
    private List<String> seatIds;
    private double totalPrice;
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    public PaymentScreen(Flight flight, String seatClass, List<String> seatIds, double totalPrice,
                         User currentUser, UserManager userManager, FlightManager flightManager) {

        this.flight = flight;
        this.seatClass = seatClass;
        this.seatIds = seatIds;
        this.totalPrice = totalPrice;
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {

        stage.setTitle("SkyWings – Payment");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label title = new Label("Payment");
        title.setFont(Font.font(20));

        Label info = new Label("Seats: " + String.join(", ", seatIds));
        Label total = new Label("Total: ₹ " + totalPrice);

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.RED);

        Button payBtn = new Button("Pay Now");

        payBtn.setOnAction(e -> {

            // 🔥 STEP 1: CHECK LOCK STILL VALID
            for (String seatId : seatIds) {
                long remaining = flight.getRemainingLockTime(seatId);

                if (remaining <= 0) {
                    errorLbl.setText("Seat hold expired. Please reselect seats.");

                    // release everything just in case
                    for (String s : seatIds) {
                        flight.releaseSeat(s);
                    }

                    return;
                }
            }

            // 🔥 STEP 2: FINAL CONFIRM BOOKING
            boolean success = flight.bookSeats(seatIds);

            if (!success) {
                errorLbl.setText("Seats already booked by another user!");

                // cleanup
                for (String s : seatIds) {
                    flight.releaseSeat(s);
                }

                return;
            }

            // ✅ SUCCESS
            Booking booking = new Booking(
                    flight,
                    seatIds,
                    seatClass,
                    totalPrice,
                    currentUser.getName(),
                    currentUser.getEmail()
            );

            currentUser.addBooking(booking);

            new ConfirmationScreen(
                    booking,
                    currentUser,
                    userManager,
                    flightManager
            ).show(stage);
        });

        root.getChildren().addAll(title, info, total, errorLbl, payBtn);

        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }
}
