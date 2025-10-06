package com.company.inventory3.utils;

import com.company.inventory3.model.Employee;

public class SessionManager {
    private static Employee currentEmployee;

    public static void setCurrentEmployee(Employee employee) {
        currentEmployee = employee;
    }

    public static Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public static boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public static boolean isAdmin() {
        return currentEmployee != null &&
                currentEmployee.getRole() == Employee.Role.ADMIN;
    }

    public static void logout() {
        currentEmployee = null;
    }
}