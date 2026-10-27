# Design: Leave Management System

## Objects identified
| Object | Data | Behaviour |
|---|---|---|
| Employee | id, name, department, leave balance | apply leave, cancel leave, check balance, display details |
| Leave request | id, days, reason, status | cancel |
| Leave rules | max days per request, yearly entitlement | differ per employee type |

## Relationships
- An `Employee` **has many** leave requests (composition).
- `FullTimeEmployee`, `PartTimeEmployee`, `Intern` **are an** `Employee` (inheritance).
- `Manager` **is a** `FullTimeEmployee` (inheritance, added later via change request).

## Initial plan
```
Employee
  - employeeId, employeeName, department, leaveBalance
  + applyLeave(), cancelLeave(), displayDetails(), checkLeaveBalance()
```
