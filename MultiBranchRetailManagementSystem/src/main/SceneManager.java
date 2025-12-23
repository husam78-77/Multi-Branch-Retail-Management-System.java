package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.PermissionManager;
import util.SessionManager;

import java.util.HashMap;
import java.util.Map;
public class SceneManager {

    private static Stage mainStage;

    private static final Map<String, PermissionChecker> PROTECTED_SCENES = new HashMap<>();

    static {
        PROTECTED_SCENES.put("/view/BranchView.fxml", () -> PermissionManager.canManageBranches());
        PROTECTED_SCENES.put("/view/ProductView.fxml", () -> PermissionManager.canManageProducts());
        PROTECTED_SCENES.put("/view/ReportsMenuView.fxml", () -> PermissionManager.canViewReports());
        PROTECTED_SCENES.put("/view/ReportView.fxml", () -> PermissionManager.canViewReports());
        PROTECTED_SCENES.put("/view/ChartView.fxml", () -> PermissionManager.canViewCharts());
        PROTECTED_SCENES.put("/view/EmployeeView.fxml", () -> PermissionManager.canManageEmployees());
    }

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        if (!SessionManager.isLoggedIn() && !fxmlPath.equals("/view/LoginView.fxml")) {
            switchToLogin();
            return;
        }

        if (PROTECTED_SCENES.containsKey(fxmlPath)) {
            PermissionChecker checker = PROTECTED_SCENES.get(fxmlPath);
            if (!checker.hasPermission()) {
                showAccessDenied(title);
                return;
            }
        }

        try {
            Parent root = FXMLLoader.load(
                    SceneManager.class.getResource(fxmlPath)
            );
            mainStage.setTitle(title);
            mainStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load scene: " + title);
        }
    }

    public static void switchSceneUnsafe(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(
                    SceneManager.class.getResource(fxmlPath)
            );
            mainStage.setTitle(title);
            mainStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load scene: " + title);
        }
    }

    private static void showAccessDenied(String feature) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Insufficient Permissions");
        alert.setContentText(PermissionManager.getPermissionDeniedMessage(feature));
        alert.showAndWait();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void switchToLogin() {
        try {
            Parent root = FXMLLoader.load(
                    SceneManager.class.getResource("/view/LoginView.fxml")
            );
            mainStage.setTitle("Login");
            mainStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface PermissionChecker {
        boolean hasPermission();
    }
}