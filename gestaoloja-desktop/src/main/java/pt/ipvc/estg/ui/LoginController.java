package pt.ipvc.estg.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void enterApplication() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if ("admin".equals(username) && "admin".equals(password)) {
            usernameField.clear();
            passwordField.clear();
            errorLabel.setText("");
            UiNavigator.showDashboard();
            return;
        }

        passwordField.clear();
        errorLabel.setText("Credenciais inválidas");
    }
}
