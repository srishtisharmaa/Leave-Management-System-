# OOP Concepts Used in This Project

This is a study note as much as project documentation. Each concept is explained in plain words, then tied to the exact place in this code, then followed by what would go wrong without it.

## Quick map

| Concept | Where in the project |
|---|---|
| Class and object | `Employee` is the class, `new Intern("E103", ...)` is an object |
| Encapsulation | private fields and controlled methods in `Employee` and `LeaveRequest` |
| Abstraction | `abstract class Employee` with abstract `getEmployeeType()` and `getMaxDaysPerRequest()` |
| Inheritance | `FullTimeEmployee`, `PartTimeEmployee`, `Intern` extend `Employee`; `Manager` extends `FullTimeEmployee` |
| Polymorphism | `Employee` references holding any subtype; same call, different behaviour |
| Method overriding | `getMaxDaysPerRequest()`, `getLeavePolicy()`, `checkTypeSpecificRules()`, `displayDetails()` |
| Method overloading | `applyLeave(int)` and `applyLeave(int, String)`, in `Employee` and in the service |
| Access modifiers | `private`, `protected`, `public`, package-private all used on purpose |
| Constructors and `super` | subclass constructors pass data up; `Manager` adds the bonus before calling `super` |
| Composition / aggregation | `Employee` owns `LeaveRequest`s; the service keeps `Employee`s |
| Exception handling | `LeaveException` hierarchy |
| Template Method pattern | `Employee.applyLeave()` with hooks for subclasses |

---

## 1. Class and object

A **class** is a blueprint; an **object** is one thing built from that blueprint, with its own data.

`Employee` describes what every employee has (ID, name, department, balance) and can do (apply, cancel, check). `new FullTimeEmployee("E101", "Aarav Mehta", "Engineering", 18)` is one real employee. Two objects of the same class do not share balances: when Aarav takes leave, Diya's balance stays as it was.

---

## 2. Encapsulation

**Idea:** keep data private and let the outside world change it only through methods that enforce the rules.

**In the project**
- All four fields of `Employee` are `private`. `leaveBalance` is the important one.
- The only ways to change the balance are `applyLeave()` (reduces it, after validation) and `cancelLeave()` (gives days back, once).
- There is no `setLeaveBalance()`. Nothing outside can write a negative number or give itself extra days.
- The constructor refuses blank ID/name/department and a negative balance.
- `getLeaveRequests()` returns `Collections.unmodifiableList(...)`. Without this, a caller could do `employee.getLeaveRequests().clear()` and wipe the history. A test in `LeaveSystemChecks` confirms that call throws.
- `employeeId`, `name`, `department` are `final`: they are set once.

**Without it:** `employee.leaveBalance = 500;` would be legal, and every leave rule in the system could be bypassed from any class.

---

## 3. Abstraction

**Idea:** show *what* something does and hide *how*, and let a class say "every subtype must fill this part in".

**In the project**
- `Employee` is `abstract`, so `new Employee(...)` does not compile. A generic "employee" with no type has no leave rules, so it should not exist.
- `getEmployeeType()` and `getMaxDaysPerRequest()` are abstract. A new subtype cannot compile until it answers both.
- Callers of `applyLeave()` do not know or care how validation works inside.

**Abstract class or interface?** An interface could also describe "something with leave rules". An abstract class was chosen because the types share state (ID, balance, request list) and a full working method (`applyLeave`). Interfaces cannot hold instance fields.

---

## 4. Inheritance

**Idea:** a subclass gets the parent's fields and methods and adds or changes only what is different ("is-a" relationship).

**In the project**
```
Employee
 ├── FullTimeEmployee
 │     └── Manager
 ├── PartTimeEmployee
 └── Intern
```
- Each of the three direct subclasses is a short class (roughly 20 to 30 lines). The validation, the request list, the balance logic and the display are inherited.
- `Manager extends FullTimeEmployee`, because a manager is a full-time employee with extra benefits. It inherits the full-time behaviour and changes three things.

**Without it:** the apply/cancel logic would be copied into four classes. That is exactly what the first version of this project did (see [refactoring-notes.md](refactoring-notes.md), item 1).

---

## 5. Polymorphism

**Idea:** one call, many behaviours, decided by the real type of the object.

**Runtime polymorphism (dynamic dispatch)** is the one used here:
```java
for (Employee employee : service.getAllEmployees()) {
    System.out.println(employee.getName() + ": " + employee.getLeavePolicy());
}
```
The variable type is `Employee`, but Java picks `getLeavePolicy()` from the real object: the intern prints the "must give a reason" line, the manager prints the bonus days line.

A more important case is inside `Employee.applyLeave()`. It calls `getMaxDaysPerRequest()` without knowing which subclass it is running for. When the object is an `Intern`, the answer is 2; when it is a `Manager`, 15. The service and `Main` never check `instanceof`.

**Compile-time polymorphism** is overloading (section 7).

**Without it:** the service would need `if (employee instanceof Intern) ... else if (employee instanceof Manager) ...`, and adding a type would mean editing that chain everywhere.

---

## 6. Method overriding

**Idea:** a subclass replaces an inherited method with its own version. Same name, same parameters, same return type; resolved at runtime.

| Method | Defined in | Overridden in | Purpose |
|---|---|---|---|
| `getEmployeeType()` | `Employee` (abstract) | all four types | name of the type |
| `getMaxDaysPerRequest()` | `Employee` (abstract) | all four types | the leave limit; `Manager` overrides `FullTimeEmployee`'s 10 with 15 |
| `getLeavePolicy()` | `Employee` (default text) | `Intern`, `Manager` | adds the extra rule to the sentence |
| `checkTypeSpecificRules()` | `Employee` (empty hook) | `Intern` | reason is compulsory |
| `displayDetails()` | `Employee` | `Manager` | calls `super.displayDetails()` then prints the team size |

All overrides carry `@Override`, so a typo in a method name becomes a compile error rather than a silent new method.

`Manager.displayDetails()` shows the other common pattern: extend the parent's behaviour with `super.method()` instead of replacing it entirely.

---

## 7. Method overloading

**Idea:** the same method name with different parameter lists. Which one runs is decided at compile time from the arguments.

```java
applyLeave(int days)                    // no reason
applyLeave(int days, String reason)     // with reason
```
The first simply calls the second with `null`. The service has the same pair. This is a real use: a normal employee may not bother with a reason, so the caller can leave it out.

### Overloading vs overriding

| | Overloading | Overriding |
|---|---|---|
| Where | same class (or parent and child) | child class replaces parent method |
| Signature | parameters must differ | must be identical |
| Resolved | at compile time | at runtime |
| Example here | `applyLeave(int)` / `applyLeave(int, String)` | `getMaxDaysPerRequest()` in `Intern` |

---

## 8. Access modifiers

| Modifier | Visible to | Used in this project for |
|---|---|---|
| `private` | the same class only | all fields; `validate()`, `findRequest()`, `requireText()` helpers |
| (package-private) | same package | `LeaveRequest` constructor and `cancel()`: only classes in `model` (i.e. `Employee`) can create or cancel a request |
| `protected` | same package and subclasses | `Employee` constructor; `checkTypeSpecificRules()` hook, meant to be overridden but not called from outside |
| `public` | everyone | `applyLeave()`, `cancelLeave()`, `checkLeaveBalance()`, `displayDetails()`, getters, the service API |

The rule followed: start as private and open up only when there is a reason.

---

## 9. Constructors and `super`

- A subclass constructor must call a parent constructor first. `Intern` passes ID, name, department and balance up to `Employee`, which validates them once for every type.
- `Manager` does a little extra before calling up: `super(id, name, department, baseLeaveBalance + BONUS_LEAVE_DAYS)`. This is how "additional leave benefits" is implemented: the manager object is born with the bonus already in the balance.
- `Employee`'s constructor is `protected`, so it can only be reached through a subclass. This backs up the "abstract" rule.

---

## 10. Relationships between objects

| Relationship | Example | Meaning |
|---|---|---|
| Inheritance (is-a) | `Intern` → `Employee` | one is a kind of the other |
| Composition (owns) | `Employee` → `LeaveRequest` | the requests are created inside the employee and make no sense without it |
| Aggregation (keeps) | `LeaveManagementService` → `Employee` | the service holds employees, but they can exist without the service |
| Dependency (uses) | `Main` → service, `Employee` → exceptions | one class uses or throws another |

---

## 11. Exception handling as part of the design

All business-rule failures extend one checked `LeaveException`:

| Exception | When |
|---|---|
| `InvalidLeaveRequestException` | zero or negative days, above the type's limit, missing reason, cancelling twice |
| `InsufficientLeaveBalanceException` | more days than are left |
| `LeaveRequestNotFoundException` | cancel with an id that does not exist |
| `EmployeeNotFoundException` | unknown employee id |
| `DuplicateEmployeeException` | registering an ID twice |

Two more points:
- They are **checked** exceptions, so the compiler forces `Main` to deal with every failure.
- Bad constructor data (blank name, negative balance) throws the built-in `IllegalArgumentException`, because that is a mistake by the programmer, not a normal business situation.

Because the subclasses share a parent, a caller can catch one specific kind, or just `LeaveException` for all of them. That is inheritance used inside the error handling.

---

## 12. Template Method pattern and the Open/Closed principle

`Employee.applyLeave()` is a fixed sequence:

1. days must be above zero
2. days must not exceed `getMaxDaysPerRequest()` (**subclass supplies the number**)
3. `checkTypeSpecificRules()` (**subclass may add a rule**)
4. days must not exceed the balance
5. create the request and deduct the days

The order lives in one place. Subclasses only fill the gaps. This is the Template Method pattern.

It makes the design **open for extension, closed for modification**: adding `Manager` meant writing one new class and changing no existing logic. (`FullTimeEmployee` and `Employee` were not edited for it.)

---

## 13. Other small details worth knowing

- `final` on fields means they can be assigned once. Constants are `private static final`, so the "magic numbers" have names (`MAX_DAYS_PER_REQUEST`, `BONUS_LEAVE_DAYS`).
- `Collections.unmodifiableList` gives a read-only view without copying.
- `LinkedHashMap` in the service keeps employees in the order they were registered, which keeps the console output stable.
- The type label comes from `getEmployeeType()` rather than `getClass().getSimpleName()`, so what is printed does not depend on the Java class name.

---

## 14. Walk-through: what the change request touched

The change request said: *add a Manager with additional leave benefits, keeping the design reusable.*

| Piece | Changed? |
|---|---|
| `Manager.java` | new file |
| `Employee.java` | no |
| `FullTimeEmployee.java` | no |
| `LeaveManagementService.java` | no |
| `Main.java` | only to show the manager in the demo |

The demo output proves it: the same `applyLeave()` loop that handled three types handles the fourth.

---

## 15. Questions that are often asked (with short answers)

**Why is `Employee` abstract?**
A plain "employee" has no leave rule, so it should not be creatable. Abstract also forces each subtype to define its limit.

**What is the difference between an abstract class and an interface?**
An abstract class can have fields, constructors and working methods, and a class can extend only one. An interface describes a contract; a class can implement many. Here the shared state and workflow made an abstract class the better fit.

**Where does polymorphism happen in your code?**
In `Employee.applyLeave()`, which calls `getMaxDaysPerRequest()`; and in the loop that calls `getLeavePolicy()` on a list of `Employee`.

**Can you override a private or static method?**
No. Private methods are not inherited; static methods are hidden, not overridden.

**Why does `Manager` extend `FullTimeEmployee`?**
Because a manager is a full-time employee; it reuses that class and overrides only what changes.

**Can a constructor be overridden?**
No. Constructors are not inherited. A subclass calls the parent's through `super(...)`.

**What does `super.displayDetails()` do in `Manager`?**
It runs the parent's version first, then the manager adds the team-size line.

**Why checked exceptions?**
The caller must handle them, so a failed leave request can never be ignored by accident.

**Why does cancelling use a request id instead of a number of days?**
Two 3-day requests look identical by days. An id points to exactly one request.

**Which design principle did the change request prove?**
Open/Closed: Manager was added by extension, not by editing existing classes.
