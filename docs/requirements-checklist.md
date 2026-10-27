# Requirements Checklist

Every point of the assignment brief, and where it is covered.

## Guidelines

| # | Requirement | Where it is covered |
|---|---|---|
| 1 | Identify main objects, data, behaviours, classes, relationships | [design.md](design.md) |
| 1 | Create a class diagram before implementation | [class-diagram.md](class-diagram.md), first plan in the first commit (`git show` on "Initial employee design") |
| 2 | `Employee` with ID, name, department, leave balance | `model/Employee.java` |
| 2 | `applyLeave()`, `cancelLeave()`, `displayDetails()`, `checkLeaveBalance()` | `model/Employee.java` |
| 2 | At least three employee objects, tested | `app/Main.java` (4 employees), `LeaveSystemChecks.java` |
| 3 | Encapsulation | [oop-concepts.md](oop-concepts.md) section 2 |
| 3 | Inheritance | section 4; `FullTimeEmployee`, `PartTimeEmployee`, `Intern` |
| 3 | Polymorphism | section 5; demo sections 3 and 7 in the output |
| 3 | Abstraction | section 3; `abstract class Employee` |
| 3 | Method overriding | section 6 |
| 3 | Method overloading | section 7; `applyLeave(int)` / `applyLeave(int, String)` |
| 3 | Access modifiers | section 8 |
| 3 | `FullTimeEmployee`, `PartTimeEmployee`, `Intern` extend `Employee` | `model/` |
| 3 | Different leave rules per type | limits 10 / 3 / 2, intern needs a reason; table in [design.md](design.md) |
| 4 | Add `Manager` with additional leave benefits | `model/Manager.java`: 8 bonus days, limit 15 |
| 4 | Keep the design reusable and maintainable | [oop-concepts.md](oop-concepts.md) section 14: only one new file |
| 5 | At least three improvements | [refactoring-notes.md](refactoring-notes.md): eight listed |
| 5 | Implement and document the changes | commit "Refactored code" and the notes file |
| 6 | Git with meaningful commits | `git log --oneline`, screenshot in `sample-output/screenshots/` |

## Suggested commits

| Suggested | Commit message used |
|---|---|
| Initial employee design | Initial employee design |
| Added Employee class | Added Employee class |
| Implemented leave management | Implemented leave management |
| Added inheritance | Added inheritance: FullTimeEmployee, PartTimeEmployee, Intern |
| Implemented polymorphism | Implemented polymorphism, abstraction and overloading |
| Added Manager | Added Manager |
| Refactored code | Refactored code: template method, custom exceptions, LeaveRequest, service layer |
| (extra) | Added self-check tests for leave rules |
| Final version | Final version: documentation, diagrams and sample output |

## Deliverables

| Deliverable | File or place |
|---|---|
| Java source code | `src/main/java/com/leavemanagement/` |
| Class diagram | `docs/class-diagram.md`, `docs/class-diagram.mmd`, `docs/images/class-diagram.png` |
| README file | `README.md` |
| Sample output (text) | `sample-output/console-output.txt`, `sample-output/test-output.txt` |
| Sample output (screenshots) | `sample-output/screenshots/` |
| GitHub repository link | top of `README.md` |
| Explanation of OOP concepts | `docs/oop-concepts.md` |
| Refactoring notes | `docs/refactoring-notes.md` |
