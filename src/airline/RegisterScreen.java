package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class RegisterScreen {
    private UserManager userManager;
    private FlightManager flightManager;

    public RegisterScreen(UserManager userManager, FlightManager flightManager) {
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {
        stage.setTitle("SkyWings – Register");

        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #1a237e; -fx-padding: 20 0 15 0;");
        Label logo = new Label("✈ SkyWings");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        logo.setTextFill(Color.WHITE);
        Label sub = new Label("Create Your Account");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#90caf9"));
        header.getChildren().addAll(logo, sub);

        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-padding: 25 50 20 50;");

        TextField nameField = styledTF("Full Name");
        TextField emailField = styledTF("Email Address");
        TextField phoneField = styledTF("Phone Number (10 digits)");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password (min 8 chars, 1 capital, 1 digit)");
        passField.setStyle(fieldStyle());
        passField.setMaxWidth(Double.MAX_VALUE);
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setStyle(fieldStyle());
        confirmField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        Button regBtn = new Button("REGISTER");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14; -fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 5;");

        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1565c0; " +
                "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 13;");

        form.getChildren().addAll(nameField, emailField, phoneField, passField, confirmField, errorLabel, regBtn, backBtn);

        regBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }
            if (!phone.matches("\\d{10}")) {
                errorLabel.setText("Phone must be exactly 10 digits.");
                return;
            }
            if (pass.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters.");
                return;
            }
            if (!pass.matches(".*[A-Z].*")) {
                errorLabel.setText("Password must contain at least one capital letter.");
                return;
            }
            if (!pass.matches(".*\\d.*")) {
                errorLabel.setText("Password must contain at least one digit.");
                return;
            }
            if (!pass.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }
            if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                errorLabel.setText("Invalid email format.");
                return;
            }
            boolean ok = userManager.register(name, email, pass, phone);
            if (ok) {
                errorLabel.setTextFill(Color.web("#1b5e20"));
                errorLabel.setText("Registration successful! Please login.");
                regBtn.setDisable(true);
            } else {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText("Email already registered.");
            }
        });

        backBtn.setOnAction(e -> new LoginScreen(userManager, flightManager).show(stage));

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f0f4ff;");
        root.getChildren().addAll(header, form);

        stage.setScene(new Scene(root, 480, 580));
        stage.setResizable(false);
        stage.show();
    }

    private TextField styledTF(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private String fieldStyle() {
        return "-fx-background-color: white; -fx-border-color: #c5cae9; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 13;";
    }
}
