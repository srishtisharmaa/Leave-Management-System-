package com.leavemanagement.service;

import com.leavemanagement.exception.DuplicateEmployeeException;
import com.leavemanagement.exception.EmployeeNotFoundException;
import com.leavemanagement.exception.LeaveException;
import com.leavemanagement.model.Employee;
import com.leavemanagement.model.LeaveRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaveManagementService {
    private final Map<String, Employee> employees = new LinkedHashMap<>();

    public void registerEmployee(Employee employee) throws DuplicateEmployeeException {
        if (employees.containsKey(employee.getEmployeeId())) {
            throw new DuplicateEmployeeException(employee.getEmployeeId());
        }
        employees.put(employee.getEmployeeId(), employee);
    }

    public Employee findEmployee(String employeeId) throws EmployeeNotFoundException {
        Employee employee = employees.get(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return employee;
    }

    public LeaveRequest applyLeave(String employeeId, int days) throws LeaveException {
        return findEmployee(employeeId).applyLeave(days);
    }

    public LeaveRequest applyLeave(String employeeId, int days, String reason) throws LeaveException {
        return findEmployee(employeeId).applyLeave(days, reason);
    }

    public LeaveRequest cancelLeave(String employeeId, int requestId) throws LeaveException {
        return findEmployee(employeeId).cancelLeave(requestId);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }
}
