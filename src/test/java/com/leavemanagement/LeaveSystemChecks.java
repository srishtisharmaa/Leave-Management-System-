package com.leavemanagement;

import com.leavemanagement.exception.DuplicateEmployeeException;
import com.leavemanagement.exception.EmployeeNotFoundException;
import com.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.leavemanagement.exception.InvalidLeaveRequestException;
import com.leavemanagement.exception.LeaveException;
import com.leavemanagement.exception.LeaveRequestNotFoundException;
import com.leavemanagement.model.Employee;
import com.leavemanagement.model.FullTimeEmployee;
import com.leavemanagement.model.Intern;
import com.leavemanagement.model.LeaveRequest;
import com.leavemanagement.model.Manager;
import com.leavemanagement.model.PartTimeEmployee;
import com.leavemanagement.service.LeaveManagementService;

import java.util.List;

public class LeaveSystemChecks {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        Employee full = new FullTimeEmployee("F1", "Full Timer", "IT", 20);
        Employee part = new PartTimeEmployee("P1", "Part Timer", "IT", 10);
        Employee intern = new Intern("I1", "Intern One", "IT", 3);
        Employee manager = new Manager("M1", "Manager One", "IT", 16, 5);

        check("apply leave reduces balance", () -> {
            full.applyLeave(4, "trip");
            return full.checkLeaveBalance() == 16;
        });
        check("cancel leave restores balance", () -> {
            full.cancelLeave(1);
            return full.checkLeaveBalance() == 20;
        });
        check("cancelling twice is rejected", () -> throwsException(() -> full.cancelLeave(1), InvalidLeaveRequestException.class));
        check("unknown request id is rejected", () -> throwsException(() -> full.cancelLeave(99), LeaveRequestNotFoundException.class));
        check("zero days is rejected", () -> throwsException(() -> full.applyLeave(0), InvalidLeaveRequestException.class));
        check("full-time limit is 10", () -> throwsException(() -> full.applyLeave(11), InvalidLeaveRequestException.class));
        check("part-time limit is 3", () -> throwsException(() -> part.applyLeave(4), InvalidLeaveRequestException.class));
        check("intern limit is 2", () -> throwsException(() -> intern.applyLeave(3, "x"), InvalidLeaveRequestException.class));
        check("intern needs a reason", () -> throwsException(() -> intern.applyLeave(1), InvalidLeaveRequestException.class));
        check("overloaded applyLeave works without reason", () -> {
            LeaveRequest r = part.applyLeave(2);
            return r.getReason().equals("not given") && part.checkLeaveBalance() == 8;
        });
        check("insufficient balance is rejected", () -> {
            intern.applyLeave(2, "exam");
            return throwsException(() -> intern.applyLeave(2, "again"), InsufficientLeaveBalanceException.class);
        });
        check("manager gets 8 bonus days", () -> manager.checkLeaveBalance() == 24);
        check("manager limit is 15", () -> {
            manager.applyLeave(15, "vacation");
            return manager.checkLeaveBalance() == 9
                    && throwsException(() -> manager.applyLeave(16, "too long"), InvalidLeaveRequestException.class);
        });
        check("polymorphism: policies differ per type", () -> {
            List<Employee> all = List.of(full, part, intern, manager);
            return all.stream().map(Employee::getLeavePolicy).distinct().count() == 4;
        });
        check("leave history cannot be edited from outside", () ->
                throwsUnsupported(() -> full.getLeaveRequests().clear()));
        check("blank name is rejected by constructor", () -> {
            try {
                new Intern("X", " ", "IT", 1);
                return false;
            } catch (IllegalArgumentException e) {
                return true;
            }
        });

        LeaveManagementService service = new LeaveManagementService();
        service.registerEmployee(full);
        check("duplicate id is rejected", () -> throwsException(() -> service.registerEmployee(full), DuplicateEmployeeException.class));
        check("unknown employee is rejected", () -> throwsException(() -> service.applyLeave("NOPE", 1), EmployeeNotFoundException.class));
        check("service applies leave through the employee", () -> {
            service.applyLeave("F1", 2, "check");
            return service.findEmployee("F1").checkLeaveBalance() == 18;
        });

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private interface Check { boolean run() throws Exception; }
    private interface Action { void run() throws Exception; }

    private static void check(String name, Check check) {
        boolean ok;
        try {
            ok = check.run();
        } catch (Exception e) {
            ok = false;
        }
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (ok) passed++; else failed++;
    }

    private static boolean throwsException(Action action, Class<? extends LeaveException> expected) {
        try {
            action.run();
            return false;
        } catch (Exception e) {
            return expected.isInstance(e);
        }
    }

    private static boolean throwsUnsupported(Action action) {
        try {
            action.run();
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
