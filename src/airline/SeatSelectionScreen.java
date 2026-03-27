package airline;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class SeatSelectionScreen {

    private Flight flight;
    private String seatClass;
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    private Set<String> selectedSeats = new LinkedHashSet<>();
    private Map<String, Button> seatButtons = new HashMap<>();

    private Label timerLbl = new Label();

    private static final String AVAIL_STYLE  = "-fx-background-color:#a5d6a7;";
    private static final String BOOKED_STYLE = "-fx-background-color:#ef5350;";
    private static final String SEL_STYLE    = "-fx-background-color:#ffa726;";

    public SeatSelectionScreen(Flight flight, String seatClass, User currentUser,
                              UserManager userManager, FlightManager flightManager) {
        this.flight = flight;
        this.seatClass = seatClass;
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Select Seats");
        title.setFont(Font.font(20));

        timerLbl.setTextFill(Color.RED);
        timerLbl.setFont(Font.font(14));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        // Create seats
        int rows = seatClass.equals("BUSINESS") ? 4 : 10;
        int cols = seatClass.equals("BUSINESS") ? 4 : 6;

        for (int i = 1; i <= rows; i++) {
            for (int j = 0; j < cols; j++) {

                String seatId = (seatClass.equals("BUSINESS") ? "B" : "E")
                        + i + (char)('A' + j);

                Button btn = new Button(seatId);
                btn.setMinSize(50, 40);

                if (flight.isSeatBooked(seatId)) {
                    btn.setStyle(BOOKED_STYLE);
                    btn.setDisable(true);
                } else {
                    btn.setStyle(AVAIL_STYLE);
                }

                btn.setOnAction(e -> handleSeatClick(seatId, btn));

                seatButtons.put(seatId, btn);
                grid.add(btn, j, i);
            }
        }

        Button proceedBtn = new Button("Proceed to Payment");
        proceedBtn.setDisable(true);

        proceedBtn.setOnAction(e -> {
            boolean success = flight.bookSeats(new ArrayList<>(selectedSeats));

            if (!success) {
                showAlert("Some seats were booked by another user!");
                clearSelection();
                return;
            }

            new PaymentScreen(flight, seatClass, new ArrayList<>(selectedSeats),
                    selectedSeats.size() * flight.getEconomyPrice(),
                    currentUser, userManager, flightManager).show(stage);
        });

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearSelection());

        HBox controls = new HBox(10, proceedBtn, clearBtn);

        root.getChildren().addAll(title, timerLbl, grid, controls);

        startTimer(proceedBtn);

        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    // 🔥 HANDLE CLICK
    private void handleSeatClick(String seatId, Button btn) {

        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            flight.releaseSeat(seatId);
            btn.setStyle(AVAIL_STYLE);
        } else {

            boolean locked = flight.tryLockSeat(seatId);

            if (!locked) {
                showAlert("Seat already taken or locked!");
                return;
            }

            selectedSeats.add(seatId);
            btn.setStyle(SEL_STYLE);
        }
    }

    // 🔥 CLEAR ALL
    private void clearSelection() {
        for (String s : selectedSeats) {
            flight.releaseSeat(s);
            seatButtons.get(s).setStyle(AVAIL_STYLE);
        }
        selectedSeats.clear();
    }

    // 🔥 TIMER UI
    private void startTimer(Button proceedBtn) {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {

                    if (!selectedSeats.isEmpty()) {

                        String seat = selectedSeats.iterator().next();

                        long ms = flight.getRemainingLockTime(seat);
                        long sec = ms / 1000;

                        timerLbl.setText("⏳ Hold expires in: " + sec + "s");

                        if (sec <= 0) {
                            timerLbl.setText("⚠️ Seats released!");
                            clearSelection();
                        }

                        proceedBtn.setDisable(false);
                    } else {
                        timerLbl.setText("");
                        proceedBtn.setDisable(true);
                    }
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
