# Persistence Contract

## Scope

This document defines the current persistence-layer contract for the TA Recruitment System.

Architecture boundary:

- `model -> persistence.repository -> persistence.json -> data/*.json`

Constraints:

- JSON is the only persistence source of truth.
- Do not introduce databases.
- Do not introduce Spring Boot or other heavy frameworks.
- Persistence is responsible for storage mechanics, not business workflow rules.

## Minimal Models

### Student

Current minimal fields:

- `id`
- `name`
- `userId`
- `studentNumber`
- `skillTags`
- `cvFilePath`

Current meaning:

- `id`: internal student record identifier, and the persistence primary key
- `userId`: reference to `User.id`
- `studentNumber`: business student number used in the profile form
- `skillTags`: student skill labels stored as a JSON string array
- `cvFilePath`: relative path to the stored PDF CV under `data/cvs/`

[待确认]:

- whether persistence should enforce `userId` uniqueness
- additional fields such as major, grade, and contact information

### Job

Current minimal fields:

- `id`
- `title`
- `description`
- `courseName`
- `requiredSkills`
- `weeklyHours`

Current meaning:

- `id`: job/post identifier, and the persistence primary key
- `title`: display title for the current Swing tables
- `courseName`: course name entered by the admin when publishing a job
- `requiredSkills`: required skill labels stored as a JSON string array
- `weeklyHours`: weekly workload integer for the runnable test version

[待确认]:

- whether `id` has a stricter business format
- additional fields such as teacher, quota, deadline, publisher, and status

### Application

Current minimal fields:

- `id`
- `studentId`
- `jobId`
- `status`

Current meaning:

- `id`: internal application record identifier, and the persistence primary key
- `studentId`: reference to `Student.id`
- `jobId`: reference to `Job.id`
- `studentId` and `jobId` are association fields, not a composite key

Current minimal status set:

- `SUBMITTED`
- `APPROVED`
- `REJECTED`

[待确认]:

- full status transition rules beyond `SUBMITTED -> APPROVED/REJECTED`
- additional fields such as submitted time, reviewed time, reviewer, and comments
- reference existence validation inside persistence itself

### User

Current minimal fields:

- `id`
- `username`
- `password`
- `role`

Current meaning:

- `id`: persistence primary key
- `username`: login name used by the Swing UI
- `password`: plain-text password for the runnable test version only
- `role`: current UI role switch, using `STUDENT` and `ADMIN`

[待确认]:

- whether `MO` should become a separate persisted role
- a safer password storage strategy
- additional profile fields

## JSON Files

Persistent files:

- `data/students.json`
- `data/jobs.json`
- `data/applications.json`
- `data/users.json`
- `data/cvs/*.pdf`

Shared rules:

- each file uses a JSON array as the root structure
- JSON field order is not a strong contract
- malformed JSON must fail clearly

Examples:

```json
[
  {
    "id": "S001",
    "name": "Alice",
    "userId": "U001",
    "studentNumber": "2024001",
    "skillTags": ["Java", "Communication"],
    "cvFilePath": "cvs/S001.pdf"
  }
]
```

```json
[
  {
    "id": "J001",
    "title": "Java Programming TA",
    "description": "Assist with labs",
    "courseName": "Java Programming",
    "requiredSkills": ["Java", "Communication"],
    "weeklyHours": 6
  }
]
```

```json
[
  {
    "id": "A001",
    "studentId": "S001",
    "jobId": "J001",
    "status": "SUBMITTED"
  }
]
```

```json
[
  {
    "id": "U001",
    "username": "student1",
    "password": "student123",
    "role": "STUDENT"
  }
]
```

## Repository Contract

Current repository style for `Student`, `Job`, `Application`, and `User`:

- `findAll()`
- `findById(String id)`
- `insert(T entity)`
- `update(T entity)`
- `deleteById(String id)`

Shared semantics:

- repository methods return domain objects, lists, `Optional<T>`, or `boolean`
- repositories do not return UI messages
- repository naming remains unchanged for now
- duplicate primary keys must not be silently overwritten
- updating a missing `id` must throw `DataAccessException`
- CV binary file storage is handled by a dedicated persistence entry point instead of embedding file bytes in JSON

## Exception Semantics

Persistence layer uses explicit exceptions:

- `DataAccessException`
- `JsonFormatException`

Current meanings:

- `DataAccessException`: general persistence failure such as file access failure, duplicate id, or missing target during update
- `JsonFormatException`: malformed JSON content or JSON mapping failure

## Service / UI Integration Rules

### Service layer

Service should:

- depend on repository interfaces, not JSON implementation details
- enforce business rules before calling persistence
- handle `DataAccessException` and `JsonFormatException`

Business rules reserved for service first:

- prevent duplicate applications for the same `studentId` and `jobId`
- validate referenced student/job existence
- enforce application status transitions
- validate login credentials against `users.json`
- enforce unique `studentNumber`
- validate profile completion before allowing a job application

### UI layer

UI should:

- call controller/service only
- not read or write JSON files directly
- not depend on repository/json implementations directly

## Current [待确认] Summary

- expanded fields for Student, Job, and Application
- whether `MO` should be separated from `ADMIN`
- a safer password storage strategy
- full Application status transition rules beyond the runnable test version
- whether repository interfaces should add more business-field lookup methods
