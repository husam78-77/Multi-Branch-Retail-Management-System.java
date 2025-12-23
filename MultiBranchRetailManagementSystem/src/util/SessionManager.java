package util;

import model.Employee;

public class SessionManager {

    private static Employee currentUser;

    public static void setCurrentUser(Employee employee) {
        currentUser = employee;
    }

    public static Employee getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
