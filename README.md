# TA Recruitment System README

## 1. Project Overview

TA Recruitment System is a Java 17 desktop application for managing teaching assistant recruitment.
The system supports two roles:

- `TA`: create profile, upload CV, browse jobs, and submit applications
- `MO`: review applications, publish positions, and monitor workload

The current runnable program is the **Java Swing desktop version**.
The `webapp` and `ui-preview` folders are UI prototype/reference pages rather than the main runtime entry.

---

## 2. Project Location

Repository folder:

`C:\Users\Administrator\Documents\GitHub\Software-Engineering-45`

Main entry class:

`src/main/java/edu/bupt/tarecruitment/TaRecruitmentApplication.java`

---

## 3. Environment Requirements

Please make sure the following software is installed:

- Java JDK 17 or above
- Maven 3.9.x or above
- Windows environment recommended for local desktop testing

This project uses:

- Java Swing for the runtime UI
- Maven for build and dependency management
- Jackson for JSON persistence
- JUnit 5 for testing

---

## 4. Data and Configuration

The application uses the local `data` directory for runtime persistence:

- `data/users.json`
- `data/students.json`
- `data/jobs.json`
- `data/applications.json`
- `data/cvs/`

Important note:

- The program expects the working directory to be the project root
- `data` is resolved by the application as a relative path
- If the program is started outside the repository root, file loading may fail

---

## 5. Default Test Accounts

The current preset accounts in `data/users.json` are:

### TA account
- Username: `ta1`
- Password: `ta123`
- Role: `TA`

### MO account
- Username: `mo1`
- Password: `mo123`
- Role: `MO`

---

## 6. How to Run

### Option A: Run with Maven

Open a terminal and switch to the project directory:

```powershell
cd C:\Users\Administrator\Documents\GitHub\Software-Engineering-45
```

If `mvn` is already available in your environment, run:

```powershell
mvn compile exec:java "-Dexec.mainClass=edu.bupt.tarecruitment.TaRecruitmentApplication"
```

### Option B: Run with explicit local Maven path

If Maven is not configured in PATH, use:

```powershell
cd C:\Users\Administrator\Documents\GitHub\Software-Engineering-45
C:\Users\Administrator\tools\apache-maven-3.9.9\bin\mvn.cmd "-Dmaven.repo.local=C:\Users\Administrator\Documents\GitHub\Software-Engineering-45\.m2\repository" compile exec:java "-Dexec.mainClass=edu.bupt.tarecruitment.TaRecruitmentApplication"
```

After startup, the Swing application window will open.

---

## 7. How to Build Only

To verify the project compiles successfully:

```powershell
cd C:\Users\Administrator\Documents\GitHub\Software-Engineering-45
C:\Users\Administrator\tools\apache-maven-3.9.9\bin\mvn.cmd "-Dmaven.repo.local=C:\Users\Administrator\Documents\GitHub\Software-Engineering-45\.m2\repository" compile
```

---

## 8. How to Run Tests

```powershell
cd C:\Users\Administrator\Documents\GitHub\Software-Engineering-45
C:\Users\Administrator\tools\apache-maven-3.9.9\bin\mvn.cmd "-Dmaven.repo.local=C:\Users\Administrator\Documents\GitHub\Software-Engineering-45\.m2\repository" test
```

---

## 9. Main Functional Scope

### TA side
- Login as TA
- Create or update student profile
- Upload PDF CV
- Browse open jobs
- Submit applications
- Check application results

### MO side
- Login as MO
- Review submitted applications
- Open submitted CV files
- Publish positions
- View published jobs
- Monitor TA workload

---

## 10. Notes for Use

- Uploaded CV files must be in PDF format
- The application reads and writes JSON data locally
- Closing the program does not clear existing JSON data
- To reset the demo state, back up or replace the JSON files in `data`
- If the UI does not look updated after a change, close the old window completely and rerun the program

---

## 11. Related Folders

- `src/main/java/` : main source code
- `data/` : JSON data and CV files
- `docs/` : project documents
- `webapp/` : JSP-based prototype pages
- `ui-preview/` : static preview pages for web UI reference

---

## 12. Recommended Startup Procedure

1. Open terminal
2. Switch to the project root
3. Run Maven compile and start command
4. Log in using `ta1 / ta123` or `mo1 / mo123`
5. Verify JSON data is loaded correctly from the `data` folder

