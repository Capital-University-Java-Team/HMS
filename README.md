# Hospital Management System (HMS)

A Java-based Hospital Management System domain model and test suite.

---

## 📁 Project Structure

```text
hms/
├── src/
│   └── app/
│       ├── domain/        # Core domain entities and enums (User, Appointment, Department, MedicalRecord, etc.)
│       └── tests/         # Entity unit tests and TestSupport utilities
└── out/                   # Compiled .class files (ignored by Git)
```

---

## Team Development Workflow

Please follow this workflow for every issue/entity you are assigned. **Do not push directly to `main`.**

### 1. Clone the Repository (First Time Only)
```bash
git clone <REPO_URL>
cd hms
```

### 2. Create a New Branch for Your Task
Always make sure your local `main` branch is up to date before creating a feature branch:
```bash
git checkout main
git pull origin main
git checkout -b feature/
```
*Example:* `git checkout -b feature/department-entity`

### 3. Implement Your Entity
* Create your class inside `src/app/domain/` (e.g., `Department.java`).
* Follow all acceptance criteria, field mutability (`final`), and validation rules specified in your GitHub issue.

### 4. Compile & Run Tests Locally
Before pushing your code, compile your entity along with its test file and `TestSupport.java`, then execute the test runner. Replace `<EntityName>` with the name of the entity you are working on (e.g., `Appointment`, `Department`, `MedicalRecord`):

```bash
javac -d out src/app/domain/*.java src/app/tests/<EntityName>Test.java src/app/tests/TestSupport.java
java -cp out app.tests.<EntityName>Test
```

*Example (for `Appointment`):*
```bash
javac -d out src/app/domain/*.java src/app/tests/AppointmentTest.java src/app/tests/TestSupport.java
java -cp out app.tests.AppointmentTest
```

> **Note:** Make sure all tests pass with zero compilation errors before opening a Pull Request.

### 5. Commit & Push Your Branch
```bash
git status
git add src/app/domain/<EntityName>.java
git commit -m "feat(domain): implement <EntityName> entity"
git push -u origin feature/
```

### 6. Open a Pull Request (PR)
1. Go to the repository on GitHub and click **Compare & pull request**.
2. Set **base:** `main` ← **compare:** `feature/`.
3. Reference your issue in the PR description (e.g., `Closes #3`) so it closes automatically upon merge.
4. Wait for code review and approval.

### 7. Sync After Your PR is Merged
Once your branch is merged into `main`, switch back to `main` and clean up your old local branch:
```bash
git checkout main
git pull origin main
git branch -d feature/
```

*(If another teammate's PR is merged while you are still working on your branch, update your branch with `git merge main`.)*

