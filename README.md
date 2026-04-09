# TA Recruitment System

A lightweight Java 17 + Swing + JSON recruitment system for the course group project.

## Tech Stack

- Java 17
- Swing
- JSON persistence with Jackson
- Maven

## Runnable Test Version Scope

The current runnable test version covers the minimum core flow required for the intermediate demo:

1. Login with a role
2. Browse jobs as a student
3. Submit an application
4. Review an application as an admin
5. Persist changes to JSON files under `data/`

## Run

```bash
mvn compile
mvn exec:java -Dexec.mainClass=edu.bupt.tarecruitment.TaRecruitmentApplication
```

If the Maven exec plugin is unavailable in your environment, you can also run `TaRecruitmentApplication` directly from the IDE.

## Demo Accounts

- Student: `student1` / `student123`
- Admin: `admin1` / `admin123`

## Demo Flow

1. Login as `student1`
2. Select a job and submit an application
3. Logout
4. Login as `admin1`
5. Approve or reject the submitted application
6. Refresh or restart the program to confirm JSON persistence

## Notes

- `webapp/*.jsp` files remain prototype references only and are not the runnable test version.
- `ADMIN` currently covers the reviewer role in the test version.
- [待确认] Whether `MO` should later become a separate role from `ADMIN`.
- [待确认] Password storage is plain text only for the current test version.
