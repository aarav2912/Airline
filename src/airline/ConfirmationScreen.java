package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ConfirmationScreen {

    private Booking booking;
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    public ConfirmationScreen(Booking booking, User currentUser,
                              UserManager userManager, FlightManager flightManager) {
        this.booking = booking;
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.flightManager = flightManager;

        // 🔥 SAFETY: ensure no locks remain after confirmation
        for (String seatId : booking.getSeatIds()) {
            booking.getFlight().releaseSeat(seatId);
        }
    }

    public void show(Stage stage) {

        stage.setTitle("SkyWings – Booking Confirmed");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#f0f4ff;");

        // HEADER
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color:#1a237e;-fx-padding:16;");
        Label brand = new Label("✈ SkyWings");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        brand.setTextFill(Color.WHITE);
        header.getChildren().add(brand);

        // SUCCESS BANNER
        VBox banner = new VBox(8);
        banner.setAlignment(Pos.CENTER);
        banner.setStyle("-fx-background-color:#2e7d32;-fx-padding:20;");

        Label checkmark = new Label("✔");
        checkmark.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        checkmark.setTextFill(Color.WHITE);

        Label successLbl = new Label("Booking Confirmed!");
        successLbl.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        successLbl.setTextFill(Color.WHITE);

        Label bookingId = new Label("Booking ID: " + booking.getBookingId()
                + "   |   " + booking.getSeatCount() + " seat(s)");
        bookingId.setFont(Font.font("Arial", 15));
        bookingId.setTextFill(Color.web("#c8e6c9"));

        banner.getChildren().addAll(checkmark, successLbl, bookingId);

        VBox ticket = buildBoardingPass();

        // ACTION BUTTONS
        HBox actions = new HBox(16);
        actions.setAlignment(Pos.CENTER);
        actions.setStyle("-fx-padding:20;");

        Button homeBtn = new Button("🏠  Back to Home");
        homeBtn.setStyle("-fx-background-color:#1a237e;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-font-size:14;-fx-padding:10 24;-fx-cursor:hand;-fx-background-radius:6;");

        Button bookingsBtn = new Button("📋  My Bookings");
        bookingsBtn.setStyle("-fx-background-color:white;-fx-text-fill:#1a237e;-fx-font-weight:bold;" +
                "-fx-font-size:14;-fx-padding:10 24;-fx-cursor:hand;-fx-background-radius:6;" +
                "-fx-border-color:#1a237e;-fx-border-radius:6;");

        homeBtn.setOnAction(e ->
                new HomeScreen(currentUser, userManager, flightManager).show(stage));

        bookingsBtn.setOnAction(e ->
                new MyBookingsScreen(currentUser, userManager, flightManager).show(stage));

        actions.getChildren().addAll(homeBtn, bookingsBtn);

        ScrollPane scroll = new ScrollPane(new VBox(24, ticket, actions));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#f0f4ff;");

        root.getChildren().addAll(header, banner, scroll);

        stage.setScene(new Scene(root, 640, 660));
        stage.setResizable(true);
        stage.show();
    }

    private VBox buildBoardingPass() {

        VBox pass = new VBox(0);
        pass.setMaxWidth(540);
        pass.setStyle("-fx-effect:dropshadow(gaussian,#aaa,10,0,0,3);");
        pass.setAlignment(Pos.CENTER);

        VBox top = new VBox(12);
        top.setStyle("-fx-background-color:white;-fx-padding:24 30 20 30;-fx-background-radius:12 12 0 0;");
        top.setAlignment(Pos.CENTER);

        Label bpTitle = new Label("BOARDING PASS");
        bpTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        bpTitle.setTextFill(Color.web("#888"));

        Label airline = new Label(booking.getFlight().getAirline());
        airline.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        airline.setTextFill(Color.web("#1a237e"));

        HBox routeRow = new HBox(20);
        routeRow.setAlignment(Pos.CENTER);

        VBox fromBox = new VBox(2,
                bigCode(booking.getFlight().getOrigin()),
                smallLbl(flightManager.getAirportName(booking.getFlight().getOrigin())));
        fromBox.setAlignment(Pos.CENTER);

        Label arrow = new Label("✈");
        arrow.setFont(Font.font("Arial", 22));
        arrow.setTextFill(Color.web("#90caf9"));

        VBox toBox = new VBox(2,
                bigCode(booking.getFlight().getDestination()),
                smallLbl(flightManager.getAirportName(booking.getFlight().getDestination())));
        toBox.setAlignment(Pos.CENTER);

        routeRow.getChildren().addAll(fromBox, arrow, toBox);
        top.getChildren().addAll(bpTitle, airline, routeRow);

        // DETAILS GRID
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color:white;-fx-padding:16 30 20 30;");
        grid.setHgap(30);
        grid.setVgap(12);

        addCell(grid, "PASSENGER", booking.getPassengerName(), 0, 0);
        addCell(grid, "FLIGHT", booking.getFlight().getFlightNumber(), 1, 0);

        // 🔥 UPDATED DATE
        addCell(grid, "DATE", booking.getFlight().getFlightDate().toString(), 2, 0);

        addCell(grid, "DEPARTURE", booking.getFlight().getDepartureTime(), 0, 1);
        addCell(grid, "ARRIVAL", booking.getFlight().getArrivalTime(), 1, 1);
        addCell(grid, "DURATION", booking.getFlight().getDuration(), 2, 1);
        addCell(grid, "CLASS", booking.getSeatClass(), 0, 2);
        addCell(grid, "SEATS", booking.getSeatId(), 1, 2);
        addCell(grid, "AMOUNT PAID", "₹ " + String.format("%,.0f", booking.getPricePaid()), 2, 2);

        pass.getChildren().addAll(top, grid);

        VBox wrapper = new VBox(pass);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-padding:16 30;");
        return wrapper;
    }

    private void addCell(GridPane grid, String label, String value, int col, int row) {
        VBox cell = new VBox(2);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", 10));
        lbl.setTextFill(Color.GRAY);

        Label val = new Label(value);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        val.setTextFill(Color.web("#1a237e"));

        cell.getChildren().addAll(lbl, val);
        grid.add(cell, col, row);
    }

    private Label bigCode(String code) {
        Label l = new Label(code);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        l.setTextFill(Color.web("#1a237e"));
        return l;
    }

    private Label smallLbl(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 10));
        l.setTextFill(Color.GRAY);
        l.setWrapText(true);
        l.setAlignment(Pos.CENTER);
        return l;
    }
}
