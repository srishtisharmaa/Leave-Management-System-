# Design

This was written before the code, as the first guideline asks.

## Problem in one paragraph
A company needs to keep employee records, let employees apply for and cancel leave, and apply different leave rules depending on the kind of employee. Later, a new employee type (Manager) has to be added without breaking what already works.

## Objects, data and behaviour
Picking out the nouns and verbs from the scenario:

| Object | Data it holds | Behaviour |
|---|---|---|
| Employee | ID, name, department, leave balance, list of leave requests | apply leave, cancel leave, check balance, display details |
| Full-time employee | same as Employee | own leave limit |
| Part-time employee | same as Employee | own leave limit |
| Intern | same as Employee | own leave limit, reason is compulsory |
| Manager | same as Employee + team size, bonus days | bigger leave limit, bonus days |
| Leave request | request id, days, reason, status | cancel |
| Leave management service | all registered employees | register, find, apply, cancel |

## Classes and their responsibility
| Class | Package | One-line responsibility |
|---|---|---|
| `Employee` (abstract) | model | shared data and the common leave workflow |
| `FullTimeEmployee`, `PartTimeEmployee`, `Intern` | model | supply their own leave rules |
| `Manager` | model | full-time employee with extra benefits |
| `LeaveRequest`, `LeaveStatus` | model | one request and its state (applied / cancelled) |
| `LeaveManagementService` | service | register employees, look them up, route operations |
| `LeaveException` and 5 subclasses | exception | report why an operation failed |
| `Main` | app | demonstration of the whole system |

## Relationships
- `FullTimeEmployee`, `PartTimeEmployee`, `Intern` are an `Employee` (inheritance).
- `Manager` is a `FullTimeEmployee` (inheritance, added by the change request).
- An `Employee` owns many `LeaveRequest` objects, which do not exist outside it (composition).
- `LeaveManagementService` keeps many `Employee` objects (aggregation) and works only with the `Employee` type, never a specific subclass.
- Every business-rule failure is a subclass of `LeaveException`.

The full diagram is in [class-diagram.md](class-diagram.md).

## Leave rules
The numbers below are example values chosen for this assignment, not a real company policy.

| Type | Max days in one request | Other rule |
|---|---|---|
| Full-Time | 10 | none |
| Part-Time | 3 | none |
| Intern | 2 | a reason must be given |
| Manager | 15 | 8 bonus days added to the starting balance |

Rules common to everyone: days must be above zero, and cannot be more than the current balance.

## Design decisions
1. **Abstract parent class instead of an interface.** The employee types share both data and the whole apply/cancel workflow, so a class that can hold state fits better than an interface.
2. **The workflow is written once, the rules are plugged in.** `Employee.applyLeave()` does the checking in a fixed order and asks the subclass only for the parts that differ.
3. **Failures are exceptions, not `false`.** A caller can tell the difference between "too many days", "not enough balance" and "unknown employee".
4. **Requests are objects with ids.** Cancelling by request id is unambiguous. Cancelling "3 days" is not.
5. **No printing inside the model or service.** Only `Main` and `displayDetails()` print, so the logic can be tested without reading the console.

## Assumptions
- Data is kept in memory only. Nothing is saved when the program ends.
- Dates, approval by a manager and yearly reset of balances are outside this assignment.
