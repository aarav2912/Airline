package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label greeting = new Label("Hello, " + currentUser.getName() + "  |");
        greeting.setTextFill(Color.web("#90caf9"));
        greeting.setFont(Font.font("Arial", 13));

        Button myBookingsBtn = new Button("My Bookings");
        myBookingsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #90caf9; " +
                "-fx-cursor: hand; -fx-font-size: 13;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-font-size: 12; -fx-background-radius: 4; -fx-padding: 5 12;");
        navbar.getChildren().addAll(brandLabel, spacer, greeting, myBookingsBtn, new Label("  "), logoutBtn);

        // ── Hero Search Panel ──
        VBox hero = new VBox(16);
        hero.setAlignment(Pos.CENTER);
        hero.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #3949ab); -fx-padding: 30 40;");

        Label heroTitle = new Label("Find Your Perfect Flight");
        heroTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heroTitle.setTextFill(Color.WHITE);

        Label heroSub = new Label("Search across top airlines for the best deals");
        heroSub.setFont(Font.font("Arial", 13));
        heroSub.setTextFill(Color.web("#c5cae9"));

        // Search box
        HBox searchBox = new HBox(12);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20 24;");
        searchBox.setMaxWidth(700);

        ComboBox<String> fromCB = styledCombo("From (Airport Code)");
        ComboBox<String> toCB = styledCombo("To (Airport Code)");

        for (String[] ap : FlightManager.AIRPORTS) {
            fromCB.getItems().add(ap[0] + " – " + ap[1]);
            toCB.getItems().add(ap[0] + " – " + ap[1]);
        }
        fromCB.setPrefWidth(220);
        toCB.setPrefWidth(220);

        // Swap button
        Button swapBtn = new Button("⇄");
        swapBtn.setStyle("-fx-background-color: #e8eaf6; -fx-text-fill: #3949ab; -fx-cursor: hand; " +
                "-fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12;");

        Button searchBtn = new Button("SEARCH");
        searchBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14; -fx-padding: 10 24; -fx-cursor: hand; -fx-background-radius: 6;");

        searchBox.getChildren().addAll(fromCB, swapBtn, toCB, searchBtn);

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.web("#ffcdd2"));
        errorLbl.setFont(Font.font("Arial", 13));

        hero.getChildren().addAll(heroTitle, heroSub, searchBox, errorLbl);

        // Airport quick-reference
        TitledPane airportPane = new TitledPane();
        airportPane.setText("Airport Codes Reference");
        airportPane.setExpanded(false);
        GridPane apGrid = new GridPane();
        apGrid.setHgap(20);
        apGrid.setVgap(6);
        apGrid.setPadding(new Insets(10));
        int col = 0, row = 0;
        for (String[] ap : FlightManager.AIRPORTS) {
            Label lbl = new Label(ap[0] + " : " + ap[1]);
            lbl.setFont(Font.font("Arial", 12));
            apGrid.add(lbl, col, row);
            col++;
            if (col == 3) { col = 0; row++; }
        }
        airportPane.setContent(apGrid);

        // Results area
        VBox resultsArea = new VBox(12);
        resultsArea.setStyle("-fx-padding: 16 24;");
        ScrollPane scroll = new ScrollPane(resultsArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f0f4ff;");

        // ── Swap action ──
        swapBtn.setOnAction(e -> {
            String from = fromCB.getValue();
            String to = toCB.getValue();
            fromCB.setValue(to);
            toCB.setValue(from);
        });

        // ── Search action ──
        searchBtn.setOnAction(e -> {
            errorLbl.setText("");
            resultsArea.getChildren().clear();

            String fromVal = fromCB.getValue();
            String toVal = toCB.getValue();
            if (fromVal == null || toVal == null) {
                errorLbl.setText("Please select both origin and destination.");
                return;
            }
            String fromCode = fromVal.split(" – ")[0];
            String toCode = toVal.split(" – ")[0];
            if (fromCode.equals(toCode)) {
                errorLbl.setText("Origin and destination cannot be same.");
                return;
            }

            List<Flight> flights = flightManager.searchFlights(fromCode, toCode);
            if (flights.isEmpty()) {
                Label noRes = new Label("No flights found for " + fromCode + " → " + toCode);
                noRes.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                noRes.setTextFill(Color.web("#555"));
                noRes.setPadding(new Insets(30));
                resultsArea.getChildren().add(noRes);
            } else {
                Label resTitle = new Label("Available Flights: " + fromCode + " → " + toCode
                        + "  (" + flights.size() + " found)");
                resTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                resTitle.setTextFill(Color.web("#1a237e"));
                resultsArea.getChildren().add(resTitle);
                for (Flight f : flights) {
                    resultsArea.getChildren().add(buildFlightCard(f, stage));
                }
            }
        });

        myBookingsBtn.setOnAction(e -> new MyBookingsScreen(currentUser, userManager, flightManager).show(stage));
        logoutBtn.setOnAction(e -> new LoginScreen(userManager, flightManager).show(stage));

        VBox contentArea = new VBox();
        VBox.setVgrow(scroll, Priority.ALWAYS);
        contentArea.getChildren().addAll(airportPane, scroll);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f0f4ff;");
        VBox.setVgrow(root, Priority.ALWAYS);
        root.getChildren().addAll(navbar, hero, contentArea);

        Scene scene = new Scene(root, 860, 700);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private VBox buildFlightCard(Flight f, Stage stage) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, #c5cae9, 6, 0, 0, 2); -fx-padding: 16;");

        // Row 1: Airline + Flight number + duration
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER_LEFT);
        Label airlineLbl = new Label(airlineEmoji(f.getAirline()) + "  " + f.getAirline());
        airlineLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        airlineLbl.setTextFill(Color.web("#1a237e"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label flightNumLbl = new Label("Flight " + f.getFlightNumber());
        flightNumLbl.setFont(Font.font("Arial", 12));
        flightNumLbl.setTextFill(Color.GRAY);
        Label durationLbl = new Label("⏱ " + f.getDuration());
        durationLbl.setFont(Font.font("Arial", 12));
        durationLbl.setTextFill(Color.web("#555"));
        row1.getChildren().addAll(airlineLbl, spacer, flightNumLbl, new Label("   "), durationLbl);

        // Row 2: times + route
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

        Region mid = new Region();
        HBox.setHgrow(mid, Priority.ALWAYS);
        row2.getChildren().addAll(depBox, arrow, arrBox, mid);

        // Row 3: prices + book buttons + seats
        HBox row3 = new HBox(16);
        row3.setAlignment(Pos.CENTER_LEFT);

        VBox ecoBox = buildClassBox("ECONOMY", f.getEconomyPrice(), f.getAvailableEconomy());
        VBox bizBox = buildClassBox("BUSINESS", f.getBusinessPrice(), f.getAvailableBusiness());

        Region spc = new Region();
        HBox.setHgrow(spc, Priority.ALWAYS);

        Button bookEco = buildBookButton("Book Economy");
        Button bookBiz = buildBookButton("Book Business");

        bookEco.setOnAction(e -> new SeatSelectionScreen(f, "ECONOMY", currentUser, userManager, flightManager).show(stage));
        bookBiz.setOnAction(e -> new SeatSelectionScreen(f, "BUSINESS", currentUser, userManager, flightManager).show(stage));

        row3.getChildren().addAll(ecoBox, bizBox, spc, bookEco, bookBiz);

        Separator sep = new Separator();
        card.getChildren().addAll(row1, sep, row2, row3);
        return card;
    }

    private VBox buildClassBox(String cls, double price, int seats) {
        Label clsLbl = new Label(cls);
        clsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        clsLbl.setTextFill(Color.web("#555"));
        Label priceLbl = new Label("₹ " + String.format("%,.0f", price));
        priceLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        priceLbl.setTextFill(Color.web("#1a237e"));
        Label seatsLbl = new Label(seats + " seats left");
        seatsLbl.setFont(Font.font("Arial", 11));
        seatsLbl.setTextFill(seats < 5 ? Color.RED : Color.web("#2e7d32"));
        VBox box = new VBox(2, clsLbl, priceLbl, seatsLbl);
        box.setPadding(new Insets(6 ,14, 6, 14));
        box.setStyle("-fx-background-color: #e8eaf6; -fx-background-radius: 6;");
        return box;
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
        cb.setEditable(false);
        return cb;
    }

    private String airlineEmoji(String airline) {
        switch (airline) {
            case "Air India": return "🔵";
            case "IndiGo": return "🔷";
            case "SpiceJet": return "🔴";
            case "Vistara": return "🟣";
            default: return "✈";
        }
    }
}
