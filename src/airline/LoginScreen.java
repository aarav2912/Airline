package airline;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;

public class LoginScreen {
    private UserManager userManager;
    private FlightManager flightManager;

    public LoginScreen(UserManager userManager, FlightManager flightManager) {
        this.userManager = userManager;
        this.flightManager = flightManager;
    }

    public void show(Stage stage) {
        stage.setTitle("SkyWings – Login");

        // Header
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #1a237e; -fx-padding: 30 0 20 0;");
        Label logo = new Label("✈ SkyWings");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        logo.setTextFill(Color.WHITE);
        Label tagline = new Label("Your Journey Begins Here");
        tagline.setFont(Font.font("Arial", 14));
        tagline.setTextFill(Color.web("#90caf9"));
        header.getChildren().addAll(logo, tagline);

        // Form
        VBox form = new VBox(14);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-padding: 30 50 20 50;");
        form.setMaxWidth(400);

        Label title = new Label("Sign In");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1a237e"));

        TextField emailField = styledTextField("Email Address");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle(fieldStyle());
        passField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));

        Button loginBtn = new Button("LOGIN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14; -fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 5;");

        Separator sep = new Separator();
        Label noAccount = new Label("Don't have an account?");
        noAccount.setFont(Font.font("Arial", 13));
        Button registerBtn = new Button("Create Account");
        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1565c0; " +
                "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 13; -fx-border-color: #1565c0;" +
                "-fx-border-radius:5; -fx-background-radius:5; -fx-padding: 8 20;");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        // Demo hint
        Label hint = new Label("Demo: demo@fly.com / Demo@123");
        hint.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.ITALIC, 11));
        hint.setTextFill(Color.GRAY);

        form.getChildren().addAll(title, emailField, passField, errorLabel, loginBtn, sep, noAccount, registerBtn, hint);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = passField.getText();
            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please fill all fields.");
                return;
            }
            User user = userManager.login(email, pass);
            if (user != null) {
                new HomeScreen(user, userManager, flightManager).show(stage);
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });

        registerBtn.setOnAction(e -> new RegisterScreen(userManager, flightManager).show(stage));

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f0f4ff;");
        root.getChildren().addAll(header, form);

        Scene scene = new Scene(root, 480, 520);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private TextField styledTextField(String prompt) {
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
