package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class MyBookingsScreen {
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    public MyBookingsScreen(User currentUser, UserManager userManager, FlightManager flightManager) {
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {
        stage.setTitle("SkyWings – My Bookings");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color:#1a237e;-fx-padding:12 20;");
        Label back = new Label("← Back to Home");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        back.setTextFill(Color.web("#90caf9"));
        back.setStyle("-fx-cursor:hand;");
        back.setOnMouseClicked(e -> new HomeScreen(currentUser, userManager, flightManager).show(stage));
        Label title = new Label("My Bookings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        topBar.getChildren().addAll(back, new Label("  "), title);

        VBox content = new VBox(14);
        content.setStyle("-fx-padding:20 30;");

        refreshContent(content, stage);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#f0f4ff;");

        VBox root = new VBox();
        root.setStyle("-fx-background-color:#f0f4ff;");
        root.getChildren().addAll(topBar, scroll);
        stage.setScene(new Scene(root, 740, 620));
        stage.setResizable(true);
        stage.show();
    }

    private void refreshContent(VBox content, Stage stage) {
        content.getChildren().clear();
        List<Booking> bookings = currentUser.getBookings();
        if (bookings.isEmpty()) {
            Label noBookings = new Label("You have no bookings yet.");
            noBookings.setFont(Font.font("Arial", 16));
            noBookings.setTextFill(Color.GRAY);
            noBookings.setPadding(new Insets(40));
            content.getChildren().add(noBookings);
            return;
        }
        long active    = bookings.stream().filter(b -> !b.isCancelled()).count();
        long cancelled = bookings.stream().filter(Booking::isCancelled).count();
        Label countLbl = new Label("Total: " + bookings.size()
                + "   Active: " + active + "   Cancelled: " + cancelled);
        countLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        countLbl.setTextFill(Color.web("#1a237e"));
        content.getChildren().add(countLbl);

        for (Booking b : bookings) {
            content.getChildren().add(buildBookingCard(b, content, stage));
        }
    }

    private VBox buildBookingCard(Booking b, VBox content, Stage stage) {
        VBox card = new VBox(10);
        boolean cancelled = b.isCancelled();
        String cardBg = cancelled ? "#fafafa" : "white";
        card.setStyle("-fx-background-color:" + cardBg + ";-fx-background-radius:10;" +
                "-fx-effect:dropshadow(gaussian,#c5cae9,6,0,0,2);-fx-padding:16;");

        // Row 1 – booking ID + class badge + status
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER_LEFT);
        Label bookingIdLbl = new Label("Booking: " + b.getBookingId());
        bookingIdLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        bookingIdLbl.setTextFill(cancelled ? Color.GRAY : Color.web("#1a237e"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label classLbl = new Label(b.getSeatClass());
        classLbl.setStyle("-fx-background-color:" +
                (b.getSeatClass().equals("BUSINESS") ? "#1a237e" : "#1565c0") +
                ";-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:20;-fx-font-size:11;-fx-font-weight:bold;");
        Label statusBadge = new Label(cancelled ? "  ✖ CANCELLED" : "  ✔ CONFIRMED");
        statusBadge.setStyle("-fx-background-color:" + (cancelled ? "#ef5350" : "#2e7d32") +
                ";-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:20;-fx-font-size:11;-fx-font-weight:bold;");
        row1.getChildren().addAll(bookingIdLbl, sp, classLbl, new Label("  "), statusBadge);

        // Row 2 – flight details
        HBox row2 = new HBox(24);
        row2.setAlignment(Pos.CENTER_LEFT);
        addInfo2(row2, "Flight",      b.getFlight().getFlightNumber());
        addInfo2(row2, "Route",       b.getFlight().getOrigin() + " → " + b.getFlight().getDestination());
        addInfo2(row2, "Departure",   b.getFlight().getDepartureTime());
        addInfo2(row2, "Seats",       b.getSeatId() + " (" + b.getSeatCount() + ")");
        addInfo2(row2, "Amount Paid", "₹ " + String.format("%,.0f", b.getPricePaid()));

        // Row 3 – action buttons
        HBox row3 = new HBox(10);
        row3.setAlignment(Pos.CENTER_LEFT);

        if (!cancelled) {
            Button viewBtn = new Button("View Ticket");
            viewBtn.setStyle("-fx-background-color:#e8eaf6;-fx-text-fill:#1a237e;-fx-font-weight:bold;" +
                    "-fx-cursor:hand;-fx-background-radius:5;-fx-padding:6 14;");
            viewBtn.setOnAction(e -> new ConfirmationScreen(b, currentUser, userManager, flightManager)
                    .show(new Stage()));

            Button cancelBtn = new Button("✖  Cancel Booking");
            cancelBtn.setStyle("-fx-background-color:#ffebee;-fx-text-fill:#c62828;-fx-font-weight:bold;" +
                    "-fx-cursor:hand;-fx-background-radius:5;-fx-padding:6 14;-fx-border-color:#ef9a9a;" +
                    "-fx-border-radius:5;");
            cancelBtn.setOnAction(e -> {
                boolean confirmed = showCancelDialog(b);
                if (confirmed) {
                    b.cancel();
                    refreshContent(content, stage);
                }
            });
            row3.getChildren().addAll(viewBtn, cancelBtn);

            // Refund info hint
            Label refundHint = new Label("  Full refund will be processed in 5–7 business days.");
            refundHint.setFont(Font.font("Arial", FontPosture.ITALIC, 11));
            refundHint.setTextFill(Color.web("#777"));
            row3.getChildren().add(refundHint);
        } else {
            Label cancelledNote = new Label("This booking has been cancelled. Seats have been released.");
            cancelledNote.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
            cancelledNote.setTextFill(Color.web("#999"));
            row3.getChildren().add(cancelledNote);
        }

        card.getChildren().addAll(row1, new Separator(), row2, row3);
        if (cancelled) card.setOpacity(0.65);
        return card;
    }

    private boolean showCancelDialog(Booking b) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Cancel Booking");

        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding:30;-fx-background-color:white;");
        box.setMinWidth(380);

        Label icon = new Label("⚠");
        icon.setFont(Font.font("Arial", 36));
        icon.setTextFill(Color.web("#f57c00"));
        Label msg = new Label("Are you sure you want to cancel booking\n" + b.getBookingId() + "?");
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        msg.setTextFill(Color.web("#333"));
        msg.setAlignment(Pos.CENTER);
        Label detail = new Label("Seats: " + b.getSeatId() + "\n"
                + b.getFlight().getOrigin() + " → " + b.getFlight().getDestination()
                + "  |  " + b.getFlight().getDepartureTime()
                + "\nAmount: ₹ " + String.format("%,.0f", b.getPricePaid()));
        detail.setFont(Font.font("Arial", 12));
        detail.setTextFill(Color.GRAY);
        detail.setAlignment(Pos.CENTER);

        HBox btns = new HBox(16);
        btns.setAlignment(Pos.CENTER);
        Button yesBtn = new Button("Yes, Cancel Booking");
        yesBtn.setStyle("-fx-background-color:#c62828;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-padding:10 20;-fx-cursor:hand;-fx-background-radius:6;");
        Button noBtn = new Button("Keep Booking");
        noBtn.setStyle("-fx-background-color:#1a237e;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-padding:10 20;-fx-cursor:hand;-fx-background-radius:6;");
        btns.getChildren().addAll(noBtn, yesBtn);

        final boolean[] result = {false};
        yesBtn.setOnAction(e -> { result[0] = true;  dialog.close(); });
        noBtn.setOnAction(e  -> { result[0] = false; dialog.close(); });

        box.getChildren().addAll(icon, msg, detail, btns);
        dialog.setScene(new Scene(box));
        dialog.showAndWait();
        return result[0];
    }

    private void addInfo2(HBox container, String key, String value) {
        VBox box = new VBox(2);
        Label k = new Label(key);
        k.setFont(Font.font("Arial", 11));
        k.setTextFill(Color.GRAY);
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        v.setTextFill(Color.web("#333"));
        v.setWrapText(true);
        box.getChildren().addAll(k, v);
        container.getChildren().add(box);
    }
}
