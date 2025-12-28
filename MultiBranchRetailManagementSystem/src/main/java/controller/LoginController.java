package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.husam.app.SceneManager;
import model.Employee;
import service.AuthService;
import service.AuditService;
import util.SessionManager;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();
    private final AuditService auditService = new AuditService();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        Employee employee = authService.authenticate(username, password);

        if (employee != null) {
            SessionManager.setCurrentUser(employee);
            
            auditService.logLogin();
            
            SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
        } else {
            messageLabel.setText("Invalid username or password");
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            
        }
    }
}