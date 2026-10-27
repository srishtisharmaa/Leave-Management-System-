# Debugging and Refactoring Notes

The version committed as **"Added Manager"** worked, but reading it back showed several weak spots. They were fixed in the commit **"Refactored code"**. You can compare the two with:

```bash
git diff ":/Added Manager" ":/Refactored code" --stat
```
(or look at the two commits by their messages in `git log`).

Summary of what was found and changed:

| # | Problem found | Type | Fix |
|---|---|---|---|
| 1 | Same limit check copied into every subclass | duplicate code | one template method in `Employee` |
| 2 | Methods returned `boolean` and printed their own errors | exception handling | custom exception classes |
| 3 | `Employee` also did printing; no place for registering and looking up employees | class responsibilities | `LeaveManagementService`, printing only in `Main` |
| 4 | Leave history was a list of plain numbers; cancel by number of days | maintainability | `LeaveRequest` class, cancel by request id |
| 5 | Hard-coded balances and numbers inside constructors | naming, reusability | balance passed in, named constants |
| 6 | Bug: reason printed even when the request was rejected | debugging | reason stored only after validation passes |
| 7 | No validation of constructor data | exception handling | checks added in `Employee` constructor |
| 8 | `Manager` repeated full-time logic on top of `Employee` | reusability | `Manager extends FullTimeEmployee` |

The assignment asks for at least three. Items 1, 2 and 3 are the main ones; the others are smaller but real.

---

## 1. Duplicate code in the subclasses

**Before.** Four classes carried the same shape of method:

```java
// FullTimeEmployee (PartTimeEmployee, Intern and Manager looked the same, only the number changed)
@Override
public boolean applyLeave(int days) {
    if (days > 10) {
        System.out.println("Full-time employees can take max 10 days at once");
        return false;
    }
    return super.applyLeave(days);
}
```
A change in how requests are checked would have to be made four times.

**After.** The checking order exists once in `Employee.applyLeave()`. A subclass only states its number:

```java
@Override
public int getMaxDaysPerRequest() {
    return MAX_DAYS_PER_REQUEST;
}
```
This is the Template Method pattern. A new employee type is now a 20-line class.

---

## 2. Booleans and `println` instead of exceptions

**Before.** `applyLeave()` returned `false` for every kind of failure and printed a message. The caller could not tell "too many days" from "not enough balance" from "zero days" without reading console text.

**After.** Each failure has its own exception under one `LeaveException` parent: `InvalidLeaveRequestException`, `InsufficientLeaveBalanceException`, `LeaveRequestNotFoundException`, `EmployeeNotFoundException`, `DuplicateEmployeeException`. They are checked exceptions, so the compiler makes the caller handle them. `Main` turns each one into a `[REJECTED]` line, and the checks in `LeaveSystemChecks` assert the exact exception type.

---

## 3. Class responsibilities

**Before.** `Employee` stored data, enforced rules and wrote to the console. There was no class responsible for the group of employees; `Main` just held a list.

**After.**

| Class | Responsible for |
|---|---|
| `Employee` and subclasses | data and leave rules |
| `LeaveRequest` | one request and its status |
| `LeaveManagementService` | registering, finding and routing operations; rejects duplicate IDs |
| `Main` | demonstration and printing results |

The model classes now return results or throw, so they can be tested without parsing console output (which is what `LeaveSystemChecks` does).

---

## 4. Leave history and cancellation

**Before.**
```java
private List<Integer> leaveHistory = new ArrayList<>();

public boolean cancelLeave(int days) {
    if (!leaveHistory.remove(Integer.valueOf(days))) { ... }
    leaveBalance += days;
```
Two separate 3-day leaves were indistinguishable, and cancelling "3" removed whichever came first. There was also no record of reason or status.

**After.** Each leave is a `LeaveRequest` with an id, days, reason and status (`APPLIED` / `CANCELLED`). `cancelLeave(int requestId)` finds that exact request, refuses to cancel it twice, and restores precisely its days.

---

## 5. Hard-coded values and naming

**Before.**
```java
public FullTimeEmployee(String id, String name, String dept) {
    super(id, name, dept, 24);
}
...
super(id, name, dept, 32);   // Manager: where does 32 come from?
```
Every employee of a type started with the same balance, and the manager's 32 hid the rule behind it.

**After.**
- The starting balance is a constructor parameter, so two full-time employees can have different balances.
- Limits are named constants (`MAX_DAYS_PER_REQUEST`).
- The manager's benefit reads as what it is: `baseLeaveBalance + BONUS_LEAVE_DAYS`.
- Field and method names were made consistent (`getName()` instead of a mix of `getEmployeeName()` and `employeeName`).

---

## 6. A bug found while reading the output

**Before.**
```java
public boolean applyLeave(int days, String reason) {
    System.out.println("Reason: " + reason);
    return applyLeave(days);
}
```
Running the old demo showed `Reason: Personal work` printed even for requests that were then rejected (for example the intern asking for 4 days). The reason was reported before anyone knew if the request was valid.

**After.** The reason is part of the `LeaveRequest` and exists only if validation passed. Rejected requests leave no trace and no balance change.

---

## 7. Input validation

**Before.** `new FullTimeEmployee("", null, "IT")` worked.

**After.** The `Employee` constructor rejects blank ID, name and department, and a negative balance (`IllegalArgumentException`). `Manager` also rejects a negative team size. `applyLeave()` rejects zero or negative days. All of these are covered by checks in `LeaveSystemChecks`.

---

## 8. Reuse between `FullTimeEmployee` and `Manager`

**Before.** `Manager extends Employee` and wrote its own limit check, again duplicating the full-time pattern.

**After.** `Manager extends FullTimeEmployee`. A manager really is a full-time employee, so it inherits that class and overrides only the limit (15), the policy text and `displayDetails()`.

---

## What stayed the same on purpose
- The four methods the assignment names (`applyLeave`, `cancelLeave`, `displayDetails`, `checkLeaveBalance`) kept their names and purpose.
- The leave rules per type are unchanged, so the refactor changed structure, not behaviour. The only behaviour fix is item 6.

## Limitations (not done on purpose)
- Employees live in memory only; there is no file or database.
- Requests have no dates and no approval step.
- Balances do not reset every year.
These are outside the scope of this assignment, but the `LeaveRequest` class is where dates and approval would be added.
