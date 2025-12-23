package service;

import dao.EmployeeDAO;
import model.Employee;

public class AuthService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Employee authenticate(String username, String password) {
        return employeeDAO.login(username, password);
    }
}
