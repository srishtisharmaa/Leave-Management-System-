# Class Diagram

The diagrams below are Mermaid, so GitHub draws them directly in this page. The same diagrams are also saved as images in `docs/images/` for the PDF report. The raw class diagram source is in [class-diagram.mmd](class-diagram.mmd).

## 1. Class diagram

```mermaid
classDiagram
    direction TB

    class Employee {
        <<abstract>>
        -String employeeId
        -String name
        -String department
        -int leaveBalance
        -List~LeaveRequest~ leaveRequests
        -int nextRequestId
        #Employee(id, name, department, leaveBalance)
        +getEmployeeType()* String
        +getMaxDaysPerRequest()* int
        +getLeavePolicy() String
        #checkTypeSpecificRules(days, reason) void
        +applyLeave(days) LeaveRequest
        +applyLeave(days, reason) LeaveRequest
        +cancelLeave(requestId) LeaveRequest
        +checkLeaveBalance() int
        +displayDetails() void
        -validate(days, reason) void
        -findRequest(requestId) LeaveRequest
        +getLeaveRequests() List~LeaveRequest~
    }

    class FullTimeEmployee {
        -int MAX_DAYS_PER_REQUEST = 10
        +getEmployeeType() String
        +getMaxDaysPerRequest() int
    }

    class PartTimeEmployee {
        -int MAX_DAYS_PER_REQUEST = 3
        +getEmployeeType() String
        +getMaxDaysPerRequest() int
    }

    class Intern {
        -int MAX_DAYS_PER_REQUEST = 2
        +getEmployeeType() String
        +getMaxDaysPerRequest() int
        #checkTypeSpecificRules(days, reason) void
        +getLeavePolicy() String
    }

    class Manager {
        -int BONUS_LEAVE_DAYS = 8
        -int MAX_DAYS_PER_REQUEST = 15
        -int teamSize
        +getEmployeeType() String
        +getMaxDaysPerRequest() int
        +getLeavePolicy() String
        +displayDetails() void
    }

    class LeaveRequest {
        -int requestId
        -int days
        -String reason
        -LeaveStatus status
        ~cancel() void
        +isActive() boolean
    }

    class LeaveStatus {
        <<enumeration>>
        APPLIED
        CANCELLED
    }

    class LeaveManagementService {
        -Map~String, Employee~ employees
        +registerEmployee(employee) void
        +findEmployee(employeeId) Employee
        +applyLeave(employeeId, days) LeaveRequest
        +applyLeave(employeeId, days, reason) LeaveRequest
        +cancelLeave(employeeId, requestId) LeaveRequest
        +getAllEmployees() List~Employee~
    }

    class LeaveException {
        <<exception>>
    }
    class InvalidLeaveRequestException
    class InsufficientLeaveBalanceException
    class LeaveRequestNotFoundException
    class EmployeeNotFoundException
    class DuplicateEmployeeException

    class Main {
        +main(args) void
    }

    Employee <|-- FullTimeEmployee
    Employee <|-- PartTimeEmployee
    Employee <|-- Intern
    FullTimeEmployee <|-- Manager

    Employee "1" *-- "0..*" LeaveRequest : owns
    LeaveRequest --> LeaveStatus : status
    LeaveManagementService "1" o-- "0..*" Employee : keeps

    LeaveException <|-- InvalidLeaveRequestException
    LeaveException <|-- InsufficientLeaveBalanceException
    LeaveException <|-- LeaveRequestNotFoundException
    LeaveException <|-- EmployeeNotFoundException
    LeaveException <|-- DuplicateEmployeeException

    Employee ..> LeaveException : throws
    LeaveManagementService ..> LeaveException : throws
    Main ..> LeaveManagementService : uses
```

![Class diagram](images/class-diagram.png)

### How to read it
| Symbol | Meaning | Example here |
|---|---|---|
| `<\|--` | inheritance (is-a) | `Intern` is an `Employee` |
| `*--` | composition (owns, cannot live without the owner) | `Employee` owns its `LeaveRequest` objects |
| `o--` | aggregation (keeps, but the parts can exist alone) | the service keeps `Employee` objects |
| `..>` | dependency (uses or throws) | `Employee` throws `LeaveException` |
| `-` `#` `+` `~` | private, protected, public, package-private | `LeaveRequest.cancel()` is package-private |
| `*` after a method | abstract method | `getMaxDaysPerRequest()` |

### Notes on the design
- Only `Employee` has the leave workflow. The four subclasses only describe what is different about them.
- `Manager` extends `FullTimeEmployee`, not `Employee`, because a manager is a full-time employee with extra benefits. It overrides only the limit, the policy text and `displayDetails()`.
- `LeaveManagementService` knows only about `Employee`. Adding `Manager` needed no change in it.
- `LeaveRequest.cancel()` and its constructor are package-private, so only `Employee` can create or cancel a request. Other classes cannot change a request's status.

## 2. Sequence: applying for leave

```mermaid
sequenceDiagram
    participant M as Main
    participant S as LeaveManagementService
    participant E as Employee (actual subtype)
    participant R as LeaveRequest

    M->>S: applyLeave("E103", 2, "Exam")
    S->>S: findEmployee("E103")
    S->>E: applyLeave(2, "Exam")
    E->>E: validate(days, reason)
    E->>E: getMaxDaysPerRequest() (subtype answers)
    E->>E: checkTypeSpecificRules() (Intern needs a reason)
    alt a rule is broken
        E-->>S: throws LeaveException subclass
        S-->>M: exception passes up
    else all rules pass
        E->>R: new LeaveRequest(id, days, reason)
        E->>E: leaveBalance -= days
        E-->>S: LeaveRequest
        S-->>M: LeaveRequest
    end
```

![Sequence diagram](images/sequence-diagram.png)

## 3. Life of a leave request

```mermaid
stateDiagram-v2
    [*] --> APPLIED : applyLeave()
    APPLIED --> CANCELLED : cancelLeave()
    CANCELLED --> [*]
```

A cancelled request cannot be cancelled again, and cancelling restores the days to the balance exactly once.
