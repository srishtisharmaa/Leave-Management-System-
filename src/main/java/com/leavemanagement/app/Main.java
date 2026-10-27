package com.leavemanagement.app;

import com.leavemanagement.exception.LeaveException;
import com.leavemanagement.model.Employee;
import com.leavemanagement.model.FullTimeEmployee;
import com.leavemanagement.model.Intern;
import com.leavemanagement.model.LeaveRequest;
import com.leavemanagement.model.Manager;
import com.leavemanagement.model.PartTimeEmployee;
import com.leavemanagement.service.LeaveManagementService;

public class Main {

    public static void main(String[] args) {
        LeaveManagementService service = new LeaveManagementService();

        heading("1. EMPLOYEE RECORDS");
        register(service, new FullTimeEmployee("E101", "Aarav Mehta", "Engineering", 18));
        register(service, new PartTimeEmployee("E102", "Diya Nair", "Support", 8));
        register(service, new Intern("E103", "Kabir Shah", "Design", 3));
        service.getAllEmployees().forEach(Employee::displayDetails);
        System.out.println("Balance check (E101): " + balanceOf(service, "E101") + " days");

        heading("2. APPLYING FOR LEAVE");
        apply(service, "E101", 3, "Family function");
        apply(service, "E102", 2, null);
        apply(service, "E103", 2, "Exam preparation");

        heading("3. LEAVE RULES BY EMPLOYEE TYPE");
        for (Employee employee : service.getAllEmployees()) {
            System.out.println(employee.getName() + ": " + employee.getLeavePolicy());
        }
        apply(service, "E101", 11, "Long trip");
        apply(service, "E102", 4, "Medical");
        apply(service, "E103", 3, "Festival");

        heading("4. OTHER REJECTED REQUESTS");
        apply(service, "E103", 1, null);
        apply(service, "E103", 2, "Workshop");
        apply(service, "E101", 0, "Test");
        apply(service, "E999", 1, "Unknown employee");
        register(service, new Intern("E101", "Duplicate Id", "Design", 3));

        heading("5. CANCELLING LEAVE");
        cancel(service, "E101", 1);
        cancel(service, "E101", 1);
        cancel(service, "E101", 42);

        heading("6. LEAVE HISTORY");
        for (Employee employee : service.getAllEmployees()) {
            System.out.println(employee.getName() + " (" + employee.getEmployeeId() + ")");
            for (LeaveRequest request : employee.getLeaveRequests()) {
                System.out.println("   " + request);
            }
        }

        heading("7. CHANGE REQUEST: MANAGER");
        Manager manager = new Manager("E104", "Ananya Rao", "Product", 16, 6);
        register(service, manager);
        manager.displayDetails();
        apply(service, "E104", 12, "Annual vacation");
        apply(service, "E104", 16, "Sabbatical");
        System.out.println();
        for (Employee employee : service.getAllEmployees()) {
            System.out.println(employee.getName() + ": " + employee.getLeavePolicy());
        }

        heading("8. FINAL BALANCES");
        for (Employee employee : service.getAllEmployees()) {
            System.out.printf("%-5s %-12s %-9s %2d day(s)%n", employee.getEmployeeId(), employee.getName(),
                    employee.getEmployeeType(), employee.checkLeaveBalance());
        }
    }

    private static void register(LeaveManagementService service, Employee employee) {
        try {
            service.registerEmployee(employee);
            System.out.println("[REGISTERED] " + employee.getEmployeeId() + " " + employee.getName());
        } catch (LeaveException e) {
            System.out.println("[REJECTED]   " + employee.getEmployeeId() + ": " + e.getMessage());
        }
    }

    private static void apply(LeaveManagementService service, String id, int days, String reason) {
        try {
            LeaveRequest request = (reason == null) ? service.applyLeave(id, days) : service.applyLeave(id, days, reason);
            System.out.printf("[APPROVED]   %s request %s | balance now %d%n", id, request, balanceOf(service, id));
        } catch (LeaveException e) {
            System.out.println("[REJECTED]   " + id + ": " + e.getMessage());
        }
    }

    private static void cancel(LeaveManagementService service, String id, int requestId) {
        try {
            LeaveRequest request = service.cancelLeave(id, requestId);
            System.out.printf("[CANCELLED]  %s request %s | balance now %d%n", id, request, balanceOf(service, id));
        } catch (LeaveException e) {
            System.out.println("[REJECTED]   " + id + ": " + e.getMessage());
        }
    }

    private static int balanceOf(LeaveManagementService service, String id) {
        try {
            return service.findEmployee(id).checkLeaveBalance();
        } catch (LeaveException e) {
            return -1;
        }
    }

    private static void heading(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
