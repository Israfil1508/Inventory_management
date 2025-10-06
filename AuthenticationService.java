package com.company.inventory3.service;

import com.company.inventory3.dao.EmployeeDAO;
import com.company.inventory3.model.Employee;
import com.company.inventory3.utils.SessionManager;

public class AuthenticationService {
    private EmployeeDAO employeeDAO;

    public AuthenticationService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public boolean login(String email, String password) {
        Employee employee = employeeDAO.authenticate(email, password);
        if (employee != null) {
            SessionManager.setCurrentEmployee(employee);
            return true;
        }
        return false;
    }

    public void logout() {
        SessionManager.logout();
    }

    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    public boolean isAdmin() {
        return SessionManager.isAdmin();
    }
}