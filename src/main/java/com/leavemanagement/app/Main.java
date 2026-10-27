package com.leavemanagement.app;

import com.leavemanagement.model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new FullTimeEmployee("E101", "Aarav Sharma", "Engineering"));
        employees.add(new PartTimeEmployee("E102", "Meera Nair", "HR"));
        employees.add(new Intern("E103", "Rohan Das", "Finance"));
        employees.add(new Manager("E104", "Priya Menon", "Engineering", 8));

        // Polymorphism: same call, behaviour depends on the real object type
        for (Employee e : employees) {
            e.applyLeave(4, "Personal work");
        }
        for (Employee e : employees) {
            e.displayDetails();
        }
    }
}
