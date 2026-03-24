package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.*;

public class SeatSelectionScreen {
    private Flight flight;
    private String seatClass;
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    // Multi-select state
    private Set<String> selectedSeats = new LinkedHashSet<>();
    private Map<String, Button> seatButtons = new HashMap<>();

    // Colours
    private static final String AVAIL_STYLE  = "-fx-background-color:#a5d6a7;-fx-text-fill:#1b5e20;-fx-background-radius:4;-fx-border-radius:4;-fx-cursor:hand;";
    private static final String BOOKED_STYLE = "-fx-background-color:#ef5350;-fx-text-fill:white;-fx-background-radius:4;-fx-border-radius:4;";
    private static final String SEL_STYLE    = "-fx-background-color:#ffa726;-fx-text-fill:#333;-fx-background-radius:4;-fx-border-radius:4;-fx-cursor:hand;-fx-border-color:#e65100;-fx-border-width:2;";

    public SeatSelectionScreen(Flight flight, String seatClass, User currentUser,
                                UserManager userManager, FlightManager flightManager) {
        this.flight = flight;
        this.seatClass = seatClass;
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {
        stage.setTitle("SkyWings – Select Seats");

        // ── Top bar ──
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color:#1a237e;-fx-padding:12 20;");
        Label backLbl = new Label("← Back");
        backLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backLbl.setTextFill(Color.web("#90caf9"));
        backLbl.setStyle("-fx-cursor:hand;");
        backLbl.setOnMouseClicked(e -> new HomeScreen(currentUser, userManager, flightManager).show(stage));
        Label title = new Label("Select Seats – " + seatClass + " Class");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        Label hint = new Label("(click multiple seats)");
        hint.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        hint.setTextFill(Color.web("#c5cae9"));
        topBar.getChildren().addAll(backLbl, new Label("  "), title, new Label("  "), hint);

        // ── Flight strip ──
        HBox flightStrip = new HBox(30);
        flightStrip.setAlignment(Pos.CENTER);
        flightStrip.setStyle("-fx-background-color:#e8eaf6;-fx-padding:10 20;");
        addInfo(flightStrip, "Flight", flight.getFlightNumber());
        addInfo(flightStrip, "Route", flight.getOrigin() + " → " + flight.getDestination());
        addInfo(flightStrip, "Departure", flight.getDepartureTime());
        addInfo(flightStrip, "Arrival", flight.getArrivalTime());
        double pricePerSeat = seatClass.equals("BUSINESS") ? flight.getBusinessPrice() : flight.getEconomyPrice();
        addInfo(flightStrip, "Price/Seat", "₹ " + String.format("%,.0f", pricePerSeat));

        // ── Legend ──
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.setStyle("-fx-padding:10 0 4 0;");
        legend.getChildren().addAll(
                legendItem("#a5d6a7", "Available"),
                legendItem("#ef5350", "Booked"),
                legendItem("#ffa726", "Selected")
        );

        // ── Plane body ──
        VBox planeContainer = new VBox(0);
        planeContainer.setAlignment(Pos.CENTER);
        planeContainer.setStyle("-fx-padding:0 30 10 30;");

        Label nose = new Label("  ✈  ");
        nose.setFont(Font.font("Arial", 32));
        nose.setTextFill(Color.web("#37474f"));

        Label fuselageTop = new Label("╔══════════════════════════════════╗");
        fuselageTop.setFont(Font.font("Courier New", 14));
        fuselageTop.setTextFill(Color.web("#546e7a"));

        VBox seatRows = new VBox(4);
        seatRows.setAlignment(Pos.CENTER);
        seatRows.setStyle("-fx-background-color:#eceff1;-fx-padding:10 30;" +
                "-fx-border-color:#546e7a;-fx-border-width:0 3 0 3;");

        if (seatClass.equals("BUSINESS")) {
            seatRows.getChildren().add(buildColHeader(new String[]{"A","B","","C","D"}));
            seatRows.getChildren().add(buildClassBanner("✦  BUSINESS CLASS  ✦", "#1a237e"));
            for (int r = 1; r <= 4; r++) seatRows.getChildren().add(buildBusinessRow(r));
        } else {
            seatRows.getChildren().add(buildColHeader(new String[]{"A","B","C","","D","E","F"}));
            seatRows.getChildren().add(buildClassBanner("✦  ECONOMY CLASS  ✦", "#1565c0"));
            for (int r = 1; r <= 20; r++) seatRows.getChildren().add(buildEconomyRow(r));
        }

        Label fuselageBot = new Label("╚══════════════════════════════════╝");
        fuselageBot.setFont(Font.font("Courier New", 14));
        fuselageBot.setTextFill(Color.web("#546e7a"));
        Label tail = new Label("  ▲  ");
        tail.setFont(Font.font("Arial", 18));
        tail.setTextFill(Color.web("#37474f"));

        planeContainer.getChildren().addAll(nose, fuselageTop, seatRows, fuselageBot, tail);

        ScrollPane scrollPane = new ScrollPane(planeContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:#f5f5f5;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ── Bottom panel ──
        HBox bottomPanel = new HBox(12);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color:white;-fx-padding:14 24;" +
                "-fx-effect:dropshadow(gaussian,#aaa,8,0,0,-2);");

        Label selectedLbl = new Label("No seats selected");
        selectedLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        selectedLbl.setTextFill(Color.web("#555"));

        Label totalLbl = new Label();
        totalLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        totalLbl.setTextFill(Color.web("#1a237e"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button clearBtn = new Button("Clear All");
        clearBtn.setStyle("-fx-background-color:#e8eaf6;-fx-text-fill:#1a237e;-fx-font-weight:bold;" +
                "-fx-cursor:hand;-fx-background-radius:6;-fx-padding:8 16;");
        clearBtn.setVisible(false);

        Button proceedBtn = new Button("PROCEED TO PAYMENT  →");
        proceedBtn.setStyle("-fx-background-color:#1a237e;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-font-size:14;-fx-padding:10 24;-fx-cursor:hand;-fx-background-radius:6;");
        proceedBtn.setDisable(true);

        bottomPanel.getChildren().addAll(selectedLbl, totalLbl, spacer, clearBtn, proceedBtn);

        // ── Seat click handler ──
        for (Map.Entry<String, Button> entry : seatButtons.entrySet()) {
            String seatId = entry.getKey();
            Button btn = entry.getValue();
            btn.setOnAction(e -> {
                if (selectedSeats.contains(seatId)) {
                    selectedSeats.remove(seatId);
                    btn.setStyle(AVAIL_STYLE);
                } else {
                    selectedSeats.add(seatId);
                    btn.setStyle(SEL_STYLE);
                }
                int count = selectedSeats.size();
                if (count == 0) {
                    selectedLbl.setText("No seats selected");
                    totalLbl.setText("");
                    proceedBtn.setDisable(true);
                    clearBtn.setVisible(false);
                } else {
                    selectedLbl.setText(count + " seat" + (count > 1 ? "s" : "") + " selected: " +
                            String.join(", ", selectedSeats));
                    totalLbl.setText("Total: ₹ " + String.format("%,.0f", pricePerSeat * count));
                    proceedBtn.setDisable(false);
                    clearBtn.setVisible(true);
                }
            });
        }

        clearBtn.setOnAction(e -> {
            for (String s : new ArrayList<>(selectedSeats)) {
                Button b = seatButtons.get(s);
                if (b != null) b.setStyle(AVAIL_STYLE);
            }
            selectedSeats.clear();
            selectedLbl.setText("No seats selected");
            totalLbl.setText("");
            proceedBtn.setDisable(true);
            clearBtn.setVisible(false);
        });

        proceedBtn.setOnAction(e -> {
            if (!selectedSeats.isEmpty()) {
                double total = pricePerSeat * selectedSeats.size();
                new PaymentScreen(flight, seatClass, new ArrayList<>(selectedSeats), total,
                        currentUser, userManager, flightManager).show(stage);
            }
        });

        VBox root = new VBox();
        root.setStyle("-fx-background-color:#f5f5f5;");
        root.getChildren().addAll(topBar, flightStrip, legend, scrollPane, bottomPanel);

        stage.setScene(new Scene(root, 700, 720));
        stage.setResizable(true);
        stage.show();
    }

    private HBox buildBusinessRow(int rowNum) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER);
        Label rowLbl = rowLabel(rowNum);
        Button btnA = makeSeatBtn("B" + rowNum + "A", true);
        Button btnB = makeSeatBtn("B" + rowNum + "B", true);
        Button btnC = makeSeatBtn("B" + rowNum + "C", true);
        Button btnD = makeSeatBtn("B" + rowNum + "D", true);
        row.getChildren().addAll(rowLbl, btnA, btnB, new Label("   "), btnC, btnD);
        return row;
    }

    private HBox buildEconomyRow(int rowNum) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER);
        Label rowLbl = rowLabel(rowNum);
        Button btnA = makeSeatBtn("E" + rowNum + "A", false);
        Button btnB = makeSeatBtn("E" + rowNum + "B", false);
        Button btnC = makeSeatBtn("E" + rowNum + "C", false);
        Button btnD = makeSeatBtn("E" + rowNum + "D", false);
        Button btnE = makeSeatBtn("E" + rowNum + "E", false);
        Button btnF = makeSeatBtn("E" + rowNum + "F", false);
        row.getChildren().addAll(rowLbl, btnA, btnB, btnC, new Label("  "), btnD, btnE, btnF);
        return row;
    }

    private Button makeSeatBtn(String seatId, boolean isBusiness) {
        boolean booked = flight.isSeatBooked(seatId);
        String col = seatId.substring(seatId.length() - 1);
        String rowPart = seatId.substring(1, seatId.length() - 1);

        Button btn = new Button(rowPart + col);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, isBusiness ? 11 : 10));
        btn.setMinWidth(isBusiness ? 46 : 34);
        btn.setMinHeight(isBusiness ? 34 : 27);

        if (booked) {
            btn.setStyle(BOOKED_STYLE);
            btn.setDisable(true);
        } else {
            btn.setStyle(AVAIL_STYLE);
            // actual click handler wired after all buttons created
        }
        seatButtons.put(seatId, btn);
        return btn;
    }

    private Label rowLabel(int n) {
        Label l = new Label(String.format("%2d", n));
        l.setFont(Font.font("Courier New", 12));
        l.setTextFill(Color.GRAY);
        l.setMinWidth(22);
        return l;
    }

    private HBox buildColHeader(String[] cols) {
        HBox hb = new HBox(6);
        hb.setAlignment(Pos.CENTER);
        Label sp = new Label("   ");
        sp.setMinWidth(22);
        hb.getChildren().add(sp);
        for (String c : cols) {
            if (c.isEmpty()) {
                hb.getChildren().add(new Label("   "));
            } else {
                Label lbl = new Label(c);
                lbl.setMinWidth(34);
                lbl.setAlignment(Pos.CENTER);
                lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                lbl.setTextFill(Color.web("#455a64"));
                hb.getChildren().add(lbl);
            }
        }
        return hb;
    }

    private HBox legendItem(String color, String label) {
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(16, 16);
        rect.setFill(Color.web(color));
        rect.setArcWidth(4); rect.setArcHeight(4);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", 12));
        HBox item = new HBox(6, rect, lbl);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private Label buildClassBanner(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(color));
        lbl.setPadding(new Insets(4));
        return lbl;
    }

    private void addInfo(HBox container, String key, String value) {
        VBox box = new VBox(2);
        Label k = new Label(key);
        k.setFont(Font.font("Arial", 11));
        k.setTextFill(Color.GRAY);
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        v.setTextFill(Color.web("#1a237e"));
        box.getChildren().addAll(k, v);
        container.getChildren().add(box);
    }
}
