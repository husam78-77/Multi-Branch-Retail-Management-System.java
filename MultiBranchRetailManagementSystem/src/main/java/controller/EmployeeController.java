package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Branch;
import com.husam.app.SceneManager;
import model.Employee;
import service.AuditService;
import service.BranchService;
import service.EmployeeService;
import util.AccessDeniedException;
import util.PermissionManager;
import util.Role;
import util.SessionManager;

import java.util.List;


public class EmployeeController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colUsername;
    @FXML private TableColumn<Employee, String> colRole;
    @FXML private TableColumn<Employee, Integer> colBranch;

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<Branch> branchComboBox;
    @FXML private TextField searchField;

    @FXML private Label userInfoLabel;
    @FXML private Label roleHintLabel;

    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    private final EmployeeService employeeService = new EmployeeService();
    private final BranchService branchService = new BranchService();
    private final AuditService auditService = new AuditService();

    private Employee selectedEmployee = null;

    @FXML
    public void initialize() {
        // Verify permissions
        if (!PermissionManager.canManageEmployees()) {
            showError("Access Denied", "You don't have permission to manage employees");
            handleBack();
            return;
        }

        setupTableColumns();
        setupRoleComboBox();
        setupBranchComboBox();
        setupUserInfo();
        loadEmployees();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        // Selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadEmployeeToForm(newSelection);
                    }
                }
        );
    }

    /**
     * Setup role combo box based on current user's role
     */
    private void setupRoleComboBox() {
        if (PermissionManager.isAdmin()) {
            // ADMIN can only add MANAGERS
            roleComboBox.getItems().addAll("MANAGER");
            roleComboBox.setValue("MANAGER");
            roleComboBox.setDisable(true); // Lock to MANAGER
            
            if (roleHintLabel != null) {
                roleHintLabel.setText("💡 As ADMIN, you can only manage MANAGER employees");
                roleHintLabel.setStyle("-fx-text-fill: #3498db;");
            }
        } else if (PermissionManager.isManager()) {
            // MANAGER can only add CASHIERS
            roleComboBox.getItems().addAll("CASHIER");
            roleComboBox.setValue("CASHIER");
            roleComboBox.setDisable(true); // Lock to CASHIER
            
            if (roleHintLabel != null) {
                roleHintLabel.setText("💡 As MANAGER, you can only manage CASHIER employees");
                roleHintLabel.setStyle("-fx-text-fill: #3498db;");
            }
        }
    }

    /**
     * Setup branch combo box based on role
     */
    private void setupBranchComboBox() {
        model.Employee currentUser = SessionManager.getCurrentUser();

        if (PermissionManager.isAdmin()) {
            // ADMIN: Can assign MANAGERS to any branch
            try {
                List<Branch> branches = branchService.getAllBranches();
                branchComboBox.setItems(FXCollections.observableArrayList(branches));
                branchComboBox.setDisable(false);
            } catch (AccessDeniedException e) {
                showError("Error", "Failed to load branches");
            }
        } else if (PermissionManager.isManager()) {
            // MANAGER: Locked to their branch (cannot change)
            branchComboBox.setDisable(true);
            // Branch will be auto-set when adding/updating
        }

        // Custom display for ComboBox
        branchComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Branch item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : 
                    item.getBranchName() + " (ID: " + item.getBranchId() + ")");
            }
        });
        branchComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Branch item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : 
                    item.getBranchName() + " (ID: " + item.getBranchId() + ")");
            }
        });
    }

    /**
     * Display user info and restrictions
     */
    private void setupUserInfo() {
        model.Employee user = SessionManager.getCurrentUser();
        
        if (PermissionManager.isAdmin()) {
            userInfoLabel.setText("👑 ADMIN - Manage MANAGERS across all branches");
            userInfoLabel.setStyle("-fx-text-fill: #51cf66; -fx-font-weight: bold;");
        } else if (PermissionManager.isManager()) {
            userInfoLabel.setText("👨‍💼 MANAGER - Manage CASHIERS for Branch " + user.getBranchId());
            userInfoLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        }
    }

    /**
     * Load employees with role-based filtering
     */
    private void loadEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            employeeTable.setItems(FXCollections.observableArrayList(employees));
        } catch (AccessDeniedException e) {
            showError("Access Denied", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to load employees: " + e.getMessage());
        }
    }

    /**
     * Load selected employee into form
     */
    private void loadEmployeeToForm(Employee employee) {
        selectedEmployee = employee;
        
        nameField.setText(employee.getFullName());
        usernameField.setText(employee.getUsername());
        passwordField.clear(); 
        
        // Set role
        roleComboBox.setValue(employee.getRole());
        
        // Set branch if ADMIN
        if (PermissionManager.isAdmin()) {
            for (Branch branch : branchComboBox.getItems()) {
                if (branch.getBranchId() == employee.getBranchId()) {
                    branchComboBox.setValue(branch);
                    break;
                }
            }
        }
    }

    /**
     * Handle add employee
     */
    @FXML
    private void handleAdd() {
        try {
            // Validation
            if (nameField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty()) {
                showWarning("Validation Error", "Please fill in all fields");
                return;
            }

            // Check if username exists
            if (employeeService.isUsernameExists(usernameField.getText().trim())) {
                showWarning("Username Exists", "This username is already taken");
                return;
            }

            // Determine branch ID
            int branchId;
            model.Employee currentUser = SessionManager.getCurrentUser();
            String role = roleComboBox.getValue();

            if (PermissionManager.isAdmin()) {
                // ADMIN: Must select a branch for MANAGER
                Branch selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    showWarning("Validation Error", "Please select a branch for the MANAGER");
                    return;
                }
                branchId = selectedBranch.getBranchId();
            } else {
                // MANAGER: Use their branch for CASHIER
                branchId = currentUser.getBranchId();
            }

            // Create employee (without ID - DAO will generate)
            Employee employee = new Employee(
                    0, // Will be generated by database
                    nameField.getText().trim(),
                    usernameField.getText().trim(),
                    role,
                    branchId,
                    false
            );
            
            // Set password
            employee.setPassword(passwordField.getText().trim());

            // Add through service
            employeeService.addEmployee(employee);
            
            // Log the action
            auditService.logEmployeeAdd(
                employee.getFullName(),
                role,
                branchId
            );
            
            showSuccess("Employee added successfully!");
            clearForm();
            loadEmployees();
            
        } catch (AccessDeniedException e) {
            auditService.logAccessDenied("add employee");
            showError("Access Denied", e.getMessage());
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle update employee
     */
    @FXML
    private void handleUpdate() {
        if (selectedEmployee == null) {
            showWarning("No Selection", "Please select an employee to update");
            return;
        }

        try {
            // Validation
            if (nameField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty()) {
                showWarning("Validation Error", "Please fill in all required fields");
                return;
            }

            // Check if username changed and if new username exists
            if (!usernameField.getText().trim().equals(selectedEmployee.getUsername())) {
                if (employeeService.isUsernameExists(usernameField.getText().trim())) {
                    showWarning("Username Exists", "This username is already taken");
                    return;
                }
            }

            // Determine branch ID
            int branchId;
            model.Employee currentUser = SessionManager.getCurrentUser();
            String role = roleComboBox.getValue();

            if (PermissionManager.isAdmin()) {
                // ADMIN: Can change MANAGER's branch
                Branch selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    showWarning("Validation Error", "Please select a branch");
                    return;
                }
                branchId = selectedBranch.getBranchId();
            } else {
                // MANAGER: Keep CASHIER in their branch
                branchId = currentUser.getBranchId();
            }

            // Create updated employee
            Employee updatedEmployee = new Employee(
                    selectedEmployee.getEmployeeId(),
                    nameField.getText().trim(),
                    usernameField.getText().trim(),
                    role,
                    branchId,
                    false
            );

            // Update through service
            employeeService.updateEmployee(updatedEmployee);
            
            // Log the action
            auditService.logEmployeeUpdate(
                updatedEmployee.getFullName(),
                role
            );
            
            showSuccess("Employee updated successfully!");
            clearForm();
            loadEmployees();
            
        } catch (AccessDeniedException e) {
            auditService.logAccessDenied("update employee");
            showError("Access Denied", e.getMessage());
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to update employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle delete employee
     */
    @FXML
    private void handleDelete() {
        if (selectedEmployee == null) {
            showWarning("No Selection", "Please select an employee to delete");
            return;
        }

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Employee");
        confirmAlert.setContentText("Are you sure you want to delete: " + 
                                   selectedEmployee.getFullName() + "?");

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            // Delete through service
            employeeService.deleteEmployee(selectedEmployee.getEmployeeId());
            
            // Log the action
            auditService.logEmployeeDelete(
                selectedEmployee.getFullName(),
                selectedEmployee.getRole()
            );
            
            showSuccess("Employee deleted successfully!");
            clearForm();
            loadEmployees();
            
        } catch (AccessDeniedException e) {
            auditService.logAccessDenied("delete employee");
            showError("Access Denied", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to delete employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle search
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadEmployees();
            return;
        }

        try {
            List<Employee> results = employeeService.searchEmployees(keyword);
            employeeTable.setItems(FXCollections.observableArrayList(results));
        } catch (AccessDeniedException e) {
            showError("Access Denied", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to search employees: " + e.getMessage());
        }
    }

    /**
     * Clear form fields
     */
    private void clearForm() {
        selectedEmployee = null;
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        
        // Reset role to default
        if (PermissionManager.isAdmin()) {
            roleComboBox.setValue("MANAGER");
        } else {
            roleComboBox.setValue("CASHIER");
        }
        
        if (PermissionManager.isAdmin()) {
            branchComboBox.setValue(null);
        }
        
        employeeTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    // Alert methods
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}