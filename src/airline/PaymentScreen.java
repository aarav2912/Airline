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

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color:#1a237e;-fx-padding:12 20;");
        Label back = new Label("← Back");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        back.setTextFill(Color.web("#90caf9"));
        back.setStyle("-fx-cursor:hand;");
        back.setOnMouseClicked(e ->
                new SeatSelectionScreen(flight, seatClass, currentUser, userManager, flightManager).show(stage));
        Label title = new Label("Secure Payment");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(back, new Label("  "), title, new Label("  "), new Label("🔒"));

        // ── Booking Summary ──
        VBox summary = new VBox(10);
        summary.setStyle("-fx-background-color:#e8eaf6;-fx-background-radius:10;-fx-padding:18;");
        summary.setMaxWidth(440);
        Label sumTitle = new Label("Booking Summary");
        sumTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        sumTitle.setTextFill(Color.web("#1a237e"));
        summary.getChildren().add(sumTitle);
        summary.getChildren().add(new Separator());
        addRow(summary, "Flight",     flight.getFlightNumber() + " – " + flight.getAirline());
        addRow(summary, "Route",      flight.getOrigin() + " → " + flight.getDestination());
        addRow(summary, "Departure",  flight.getDepartureTime() + "  |  Arrival: " + flight.getArrivalTime());
        addRow(summary, "Duration",   flight.getDuration());
        addRow(summary, "Class",      seatClass);
        addRow(summary, "Seats (" + seatIds.size() + ")", String.join(",  ", seatIds));
        double perSeat = seatClass.equals("BUSINESS") ? flight.getBusinessPrice() : flight.getEconomyPrice();
        addRow(summary, "Price/Seat", "₹ " + String.format("%,.0f", perSeat));
        summary.getChildren().add(new Separator());
        HBox totalRow = new HBox();
        Label totalKey = new Label("Total Amount  (" + seatIds.size() + " × ₹" + String.format("%,.0f", perSeat) + ")");
        totalKey.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label totalAmt = new Label("₹ " + String.format("%,.0f", totalPrice));
        totalAmt.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        totalAmt.setTextFill(Color.web("#1a237e"));
        totalRow.getChildren().addAll(totalKey, sp, totalAmt);
        summary.getChildren().add(totalRow);

        // ── Passenger Info ──
        VBox passengerBox = new VBox(10);
        passengerBox.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-padding:18;");
        passengerBox.setMaxWidth(440);
        Label passTitle = new Label("Passenger Information");
        passTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        passTitle.setTextFill(Color.web("#1a237e"));
        TextField nameField  = styledTF("Passenger Name");  nameField.setText(currentUser.getName());
        TextField emailField = styledTF("Email");           emailField.setText(currentUser.getEmail());
        TextField phoneField = styledTF("Phone");           phoneField.setText(currentUser.getPhone());
        passengerBox.getChildren().addAll(passTitle, nameField, emailField, phoneField);

        // ── Payment Method ──
        VBox paymentBox = new VBox(10);
        paymentBox.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-padding:18;");
        paymentBox.setMaxWidth(440);
        Label payTitle = new Label("Payment Method");
        payTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        payTitle.setTextFill(Color.web("#1a237e"));
        ToggleGroup tg = new ToggleGroup();
        RadioButton card = new RadioButton("💳  Credit / Debit Card");
        RadioButton upi  = new RadioButton("📱  UPI");
        RadioButton nb   = new RadioButton("🏦  Net Banking");
        card.setToggleGroup(tg); upi.setToggleGroup(tg); nb.setToggleGroup(tg);
        card.setSelected(true);
        card.setFont(Font.font("Arial", 14)); upi.setFont(Font.font("Arial", 14)); nb.setFont(Font.font("Arial", 14));
        VBox cardFields = new VBox(8);
        cardFields.getChildren().addAll(styledTF("Card Number (16 digits)"), styledTF("MM/YY"),
                styledTF("CVV"), styledTF("Name on Card"));
        card.setOnAction(e -> cardFields.setVisible(true));
        upi.setOnAction(e  -> cardFields.setVisible(false));
        nb.setOnAction(e   -> cardFields.setVisible(false));
        paymentBox.getChildren().addAll(payTitle, card, upi, nb, cardFields);

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.RED);
        errorLbl.setFont(Font.font("Arial", 13));
        errorLbl.setWrapText(true);

        Button payBtn = new Button("PAY  ₹ " + String.format("%,.0f", totalPrice));
        payBtn.setMaxWidth(440);
        payBtn.setStyle("-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-font-size:15;-fx-padding:12;-fx-cursor:hand;-fx-background-radius:8;");

        payBtn.setOnAction(e -> {
            boolean ok = flight.bookSeats(seatIds);
            if (!ok) {
                errorLbl.setText("One or more selected seats are no longer available. Please go back and reselect.");
                return;
            }
            Booking booking = new Booking(flight, seatIds, seatClass, totalPrice,
                    nameField.getText(), emailField.getText());
            currentUser.addBooking(booking);
            new ConfirmationScreen(booking, currentUser, userManager, flightManager).show(stage);
        });

        VBox content = new VBox(18, summary, passengerBox, paymentBox, errorLbl, payBtn);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding:20 30;");
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#f0f4ff;");

        VBox root = new VBox();
        root.setStyle("-fx-background-color:#f0f4ff;");
        root.getChildren().addAll(topBar, scroll);
        stage.setScene(new Scene(root, 700, 700));
        stage.setResizable(true);
        stage.show();
    }

    private void addRow(VBox box, String key, String value) {
        HBox row = new HBox();
        Label k = new Label(key + ":  ");
        k.setFont(Font.font("Arial", 12));
        k.setTextFill(Color.GRAY);
        k.setMinWidth(110);
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        v.setTextFill(Color.web("#333"));
        v.setWrapText(true);
        row.getChildren().addAll(k, v);
        box.getChildren().add(row);
    }

    private TextField styledTF(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#f5f5f5;-fx-border-color:#c5cae9;" +
                "-fx-border-radius:5;-fx-background-radius:5;-fx-padding:8;-fx-font-size:13;");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }
}
