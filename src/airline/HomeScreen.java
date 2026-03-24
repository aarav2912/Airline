package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class HomeScreen {
    private User currentUser;
    private UserManager userManager;
    private FlightManager flightManager;

    public HomeScreen(User user, UserManager userManager, FlightManager flightManager) {
        this.currentUser = user;
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {
        stage.setTitle("SkyWings – Search Flights");

        // ── Top Navigation Bar ──
        HBox navbar = new HBox();
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #1a237e; -fx-padding: 12 20;");
        Label brandLabel = new Label("✈ SkyWings");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        brandLabel.setTextFill(Color.WHITE);
        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);
        Label greeting = new Label("Hello, " + currentUser.getName() + "  |");
        greeting.setTextFill(Color.web("#90caf9"));
        greeting.setFont(Font.font("Arial", 13));
        Button myBookingsBtn = new Button("My Bookings");
        myBookingsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #90caf9; -fx-cursor: hand; -fx-font-size: 13;");
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12; -fx-background-radius: 4; -fx-padding: 5 12;");
        navbar.getChildren().addAll(brandLabel, navSpacer, greeting, myBookingsBtn, new Label("  "), logoutBtn);

        // ── Trip Type Toggle ──
        ToggleGroup tripTypeGroup = new ToggleGroup();
        ToggleButton oneWayBtn   = new ToggleButton("One Way");
        ToggleButton roundTripBtn = new ToggleButton("Round Trip");
        oneWayBtn.setToggleGroup(tripTypeGroup);
        roundTripBtn.setToggleGroup(tripTypeGroup);
        oneWayBtn.setSelected(true);

        String activeToggle   = "-fx-background-color: white; -fx-text-fill: #1a237e; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand; -fx-background-radius: 20; -fx-padding: 6 22; -fx-border-color: white; -fx-border-radius: 20;";
        String inactiveToggle = "-fx-background-color: transparent; -fx-text-fill: #c5cae9; -fx-font-size: 13; -fx-cursor: hand; -fx-background-radius: 20; -fx-padding: 6 22; -fx-border-color: #c5cae9; -fx-border-radius: 20;";
        oneWayBtn.setStyle(activeToggle);
        roundTripBtn.setStyle(inactiveToggle);

        oneWayBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) { oneWayBtn.setStyle(activeToggle); roundTripBtn.setStyle(inactiveToggle); }
        });
        roundTripBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) { roundTripBtn.setStyle(activeToggle); oneWayBtn.setStyle(inactiveToggle); }
        });

        HBox tripToggleBox = new HBox(0, oneWayBtn, roundTripBtn);
        tripToggleBox.setAlignment(Pos.CENTER);
        tripToggleBox.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 22; -fx-padding: 3;");

        // ── Hero Search Panel ──
        VBox hero = new VBox(14);
        hero.setAlignment(Pos.CENTER);
        hero.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #3949ab); -fx-padding: 28 40;");

        Label heroTitle = new Label("Find Your Perfect Flight");
        heroTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heroTitle.setTextFill(Color.WHITE);
        Label heroSub = new Label("Search across top airlines for the best deals");
        heroSub.setFont(Font.font("Arial", 13));
        heroSub.setTextFill(Color.web("#c5cae9"));

        // ── Search Row ──
        ComboBox<String> fromCB = styledCombo("From");
        ComboBox<String> toCB   = styledCombo("To");
        for (String[] ap : FlightManager.AIRPORTS) {
            fromCB.getItems().add(ap[0] + " – " + ap[1]);
            toCB.getItems().add(ap[0]   + " – " + ap[1]);
        }
        fromCB.setPrefWidth(200);
        toCB.setPrefWidth(200);

        Button swapBtn = new Button("⇄");
        swapBtn.setStyle("-fx-background-color: #e8eaf6; -fx-text-fill: #3949ab; -fx-cursor: hand; " +
                "-fx-font-size: 15; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12;");

        DatePicker departurePicker = new DatePicker(LocalDate.now());
        departurePicker.setPromptText("Departure");
        departurePicker.setPrefWidth(130);

        DatePicker returnPicker = new DatePicker();
        returnPicker.setPromptText("Return (optional)");
        returnPicker.setPrefWidth(140);
        returnPicker.setVisible(false);   // hidden until Round Trip selected
        returnPicker.setManaged(false);

        // Disable return dates before departure
        returnPicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (departurePicker.getValue() != null)
                    setDisable(empty || item.isBefore(departurePicker.getValue().plusDays(1)));
            }
        });
        // Disable past departure dates
        departurePicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        // Show/hide return picker based on trip type
        roundTripBtn.selectedProperty().addListener((obs, was, isNow) -> {
            returnPicker.setVisible(isNow);
            returnPicker.setManaged(isNow);
            if (!isNow) returnPicker.setValue(null);
        });

        Button searchBtn = new Button("SEARCH FLIGHTS");
        searchBtn.setStyle("-fx-background-color: #ff6f00; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 13; -fx-padding: 10 22; -fx-cursor: hand; -fx-background-radius: 6;");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER);
        searchRow.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18 22;");
        searchRow.setMaxWidth(820);
        searchRow.getChildren().addAll(fromCB, swapBtn, toCB, departurePicker, returnPicker, searchBtn);

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.web("#ffcdd2"));
        errorLbl.setFont(Font.font("Arial", 13));

        hero.getChildren().addAll(heroTitle, heroSub, tripToggleBox, searchRow, errorLbl);

        // ── Airport Reference ──
        TitledPane airportPane = new TitledPane();
        airportPane.setText("Airport Codes Reference");
        airportPane.setExpanded(false);
        GridPane apGrid = new GridPane();
        apGrid.setHgap(20); apGrid.setVgap(6);
        apGrid.setPadding(new Insets(10));
        int col = 0, row = 0;
        for (String[] ap : FlightManager.AIRPORTS) {
            Label lbl = new Label(ap[0] + " : " + ap[1]);
            lbl.setFont(Font.font("Arial", 12));
            apGrid.add(lbl, col, row);
            if (++col == 3) { col = 0; row++; }
        }
        airportPane.setContent(apGrid);

        // ── Results Area ──
        VBox resultsArea = new VBox(12);
        resultsArea.setStyle("-fx-padding: 16 24;");
        ScrollPane scroll = new ScrollPane(resultsArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f0f4ff;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // ── Swap action ──
        swapBtn.setOnAction(e -> {
            String f = fromCB.getValue(), t = toCB.getValue();
            fromCB.setValue(t);
            toCB.setValue(f);
        });

        // ── Search action ──
        searchBtn.setOnAction(e -> {
            errorLbl.setText("");
            resultsArea.getChildren().clear();

            String fromVal = fromCB.getValue();
            String toVal   = toCB.getValue();
            LocalDate depDate = departurePicker.getValue();
            boolean isRoundTrip = roundTripBtn.isSelected();
            LocalDate retDate = returnPicker.getValue();

            if (fromVal == null || toVal == null) {
                errorLbl.setText("Please select both origin and destination.");
                return;
            }
            if (depDate == null) {
                errorLbl.setText("Please select a departure date.");
                return;
            }
            if (isRoundTrip && retDate == null) {
                errorLbl.setText("Please select a return date for round trip.");
                return;
            }

            String fromCode = fromVal.split(" – ")[0];
            String toCode   = toVal.split(" – ")[0];
            if (fromCode.equals(toCode)) {
                errorLbl.setText("Origin and destination cannot be the same.");
                return;
            }

            // ── Outbound results ──
            List<Flight> outboundFlights = flightManager.searchFlights(fromCode, toCode);
            outboundFlights.forEach(f -> f.setFlightDate(depDate));

            Label outTitle = new Label("✈  Outbound: " + fromCode + " → " + toCode
                    + "   |   " + depDate
                    + (isRoundTrip ? "" : "   (" + outboundFlights.size() + " flights)"));
            outTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            outTitle.setTextFill(Color.web("#1a237e"));
            outTitle.setStyle("-fx-padding: 0 0 4 0;");
            resultsArea.getChildren().add(outTitle);

            if (outboundFlights.isEmpty()) {
                resultsArea.getChildren().add(noResultsLabel(fromCode, toCode));
            } else {
                for (Flight f : outboundFlights)
                    resultsArea.getChildren().add(buildFlightCard(f, stage, false));
            }

            // ── Return results (Round Trip) ──
            if (isRoundTrip) {
                Separator divider = new Separator();
                divider.setStyle("-fx-padding: 8 0;");
                resultsArea.getChildren().add(divider);

                List<Flight> returnFlights = flightManager.searchFlights(toCode, fromCode);
                returnFlights.forEach(f -> { f.setFlightDate(retDate); f.setRoundTripLeg(true); });

                Label retTitle = new Label("↩  Return: " + toCode + " → " + fromCode
                        + "   |   " + retDate
                        + "   (" + returnFlights.size() + " flights)");
                retTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                retTitle.setTextFill(Color.web("#1565c0"));
                retTitle.setStyle("-fx-padding: 8 0 4 0;");
                resultsArea.getChildren().add(retTitle);

                if (returnFlights.isEmpty()) {
                    resultsArea.getChildren().add(noResultsLabel(toCode, fromCode));
                } else {
                    for (Flight f : returnFlights)
                        resultsArea.getChildren().add(buildFlightCard(f, stage, true));
                }
            }
        });

        myBookingsBtn.setOnAction(e -> new MyBookingsScreen(currentUser, userManager, flightManager).show(stage));
        logoutBtn.setOnAction(e    -> new LoginScreen(userManager, flightManager).show(stage));

        VBox contentArea = new VBox();
        contentArea.getChildren().addAll(airportPane, scroll);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f0f4ff;");
        VBox.setVgrow(root, Priority.ALWAYS);
        root.getChildren().addAll(navbar, hero, contentArea);

        stage.setScene(new Scene(root, 900, 700));
        stage.setResizable(true);
        stage.show();
    }

    // ── Flight Card ──────────────────────────────────────────────────

    private VBox buildFlightCard(Flight f, Stage stage, boolean isReturn) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, #c5cae9, 6, 0, 0, 2); -fx-padding: 16;");

        // Row 1: Airline + flight number + duration + pricing label
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER_LEFT);
        Label airlineLbl = new Label(airlineEmoji(f.getAirline()) + "  " + f.getAirline());
        airlineLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        airlineLbl.setTextFill(Color.web("#1a237e"));
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        Label flightNum = new Label("Flight " + f.getFlightNumber());
        flightNum.setFont(Font.font("Arial", 12));
        flightNum.setTextFill(Color.GRAY);

        // Dynamic pricing badge
        String pLabel = f.getPricingLabel();
        Label pricingBadge = new Label("  " + pLabel + "  ");
        pricingBadge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        String badgeColor = pLabel.startsWith("🔥") || pLabel.startsWith("⚡") ? "#b71c1c"
                : pLabel.startsWith("📅") ? "#e65100"
                : pLabel.contains("Almost Full") ? "#b71c1c"
                : pLabel.contains("Filling Up") ? "#e65100"
                : "#2e7d32";
        pricingBadge.setStyle("-fx-background-color: " + badgeColor + "22; -fx-text-fill: " + badgeColor
                + "; -fx-background-radius: 20; -fx-border-color: " + badgeColor
                + "; -fx-border-radius: 20; -fx-padding: 2 8;");

        Label durationLbl = new Label("  ⏱ " + f.getDuration());
        durationLbl.setFont(Font.font("Arial", 12));
        durationLbl.setTextFill(Color.web("#555"));
        row1.getChildren().addAll(airlineLbl, sp1, pricingBadge, new Label("  "), flightNum, durationLbl);

        // Return trip tag
        if (isReturn) {
            Label returnTag = new Label("  ↩ RETURN  ");
            returnTag.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0; -fx-font-weight: bold; " +
                    "-fx-background-radius: 20; -fx-font-size: 11; -fx-padding: 2 8;");
            row1.getChildren().add(0, returnTag);
        }

        // Row 2: Departure / arrow / arrival
        HBox row2 = new HBox(20);
        row2.setAlignment(Pos.CENTER);
        Label depTime = new Label(f.getDepartureTime());
        depTime.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label fromCode = new Label(f.getOrigin());
        fromCode.setFont(Font.font("Arial", 13));
        fromCode.setTextFill(Color.GRAY);
        VBox depBox = new VBox(2, depTime, fromCode);
        depBox.setAlignment(Pos.CENTER);
        Label arrow = new Label("────✈────");
        arrow.setFont(Font.font("Arial", 14));
        arrow.setTextFill(Color.web("#90caf9"));
        Label arrTime = new Label(f.getArrivalTime());
        arrTime.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label toCode = new Label(f.getDestination());
        toCode.setFont(Font.font("Arial", 13));
        toCode.setTextFill(Color.GRAY);
        VBox arrBox = new VBox(2, arrTime, toCode);
        arrBox.setAlignment(Pos.CENTER);
        Region mid = new Region(); HBox.setHgrow(mid, Priority.ALWAYS);
        row2.getChildren().addAll(depBox, arrow, arrBox, mid);

        // Row 3: Price boxes + book buttons
        HBox row3 = new HBox(16);
        row3.setAlignment(Pos.CENTER_LEFT);
        VBox ecoBox = buildClassBox("ECONOMY",  f.getEconomyPrice(),  f.getAvailableEconomy(),  f.getBaseEconomyPrice());
        VBox bizBox = buildClassBox("BUSINESS", f.getBusinessPrice(), f.getAvailableBusiness(), f.getBaseBusinessPrice());
        Region spc = new Region(); HBox.setHgrow(spc, Priority.ALWAYS);
        Button bookEco = buildBookButton("Book Economy");
        Button bookBiz = buildBookButton("Book Business");
        bookEco.setOnAction(e -> new SeatSelectionScreen(f, "ECONOMY",  currentUser, userManager, flightManager).show(stage));
        bookBiz.setOnAction(e -> new SeatSelectionScreen(f, "BUSINESS", currentUser, userManager, flightManager).show(stage));
        row3.getChildren().addAll(ecoBox, bizBox, spc, bookEco, bookBiz);

        card.getChildren().addAll(row1, new Separator(), row2, row3);
        return card;
    }

    private VBox buildClassBox(String cls, double price, int seats, double basePrice) {
        Label clsLbl = new Label(cls);
        clsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        clsLbl.setTextFill(Color.web("#555"));

        Label priceLbl = new Label("₹ " + String.format("%,.0f", price));
        priceLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // If price is hiked, show it in red and strikethrough the base
        boolean hiked = price > basePrice * 1.03;
        priceLbl.setTextFill(hiked ? Color.web("#b71c1c") : Color.web("#1a237e"));

        VBox box = new VBox(2);
        box.getChildren().addAll(clsLbl, priceLbl);

        if (hiked) {
            Label baseLbl = new Label("₹ " + String.format("%,.0f", basePrice));
            baseLbl.setFont(Font.font("Arial", 10));
            baseLbl.setTextFill(Color.GRAY);
            baseLbl.setStyle("-fx-strikethrough: true;");
            box.getChildren().add(baseLbl);
        }

        Label seatsLbl = new Label(seats + " seats left");
        seatsLbl.setFont(Font.font("Arial", 11));
        seatsLbl.setTextFill(seats < 5 ? Color.RED : Color.web("#2e7d32"));
        box.getChildren().add(seatsLbl);

        box.setPadding(new Insets(6, 14, 6, 14));
        box.setStyle("-fx-background-color: " + (hiked ? "#ffebee" : "#e8eaf6") + "; -fx-background-radius: 6;");
        return box;
    }

    private Label noResultsLabel(String from, String to) {
        Label lbl = new Label("No flights found for " + from + " → " + to);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web("#888"));
        lbl.setPadding(new Insets(16));
        return lbl;
    }

    private Button buildBookButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 12; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 16;");
        return btn;
    }

    private ComboBox<String> styledCombo(String prompt) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle("-fx-font-size: 13;");
        return cb;
    }

    private String airlineEmoji(String airline) {
        switch (airline) {
            case "Air India": return "🔵";
            case "IndiGo":    return "🔷";
            case "SpiceJet":  return "🔴";
            case "Vistara":   return "🟣";
            default:          return "✈";
        }
    }
}
