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

Current meaning:

- `id`: student number / student identifier, and the persistence primary key
- `userId`: reference to `User.id`

[待确认]:

- whether persistence should enforce `userId` uniqueness
- additional fields such as major, grade, and contact information

### Job

Current minimal fields:

- `id`
- `title`
- `description`

Current meaning:

- `id`: job/post identifier, and the persistence primary key

[待确认]:

- whether `id` has a stricter business format
- additional fields such as course, teacher, quota, deadline, publisher, and status

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

- full status transition rules
- additional fields such as submitted time, reviewed time, reviewer, and comments
- reference existence validation

## JSON Files

Persistent files:

- `data/students.json`
- `data/jobs.json`
- `data/applications.json`
- `data/users.json`

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
    "userId": "U001"
  }
]
```

```json
[
  {
    "id": "J001",
    "title": "Java TA",
    "description": "Assist with labs"
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

## Repository Contract

Current repository style for `Student`, `Job`, and `Application`:

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

Current repository interfaces:

- `src/main/java/edu/bupt/tarecruitment/persistence/repository/StudentRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/JobRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/ApplicationRepository.java`

## Exception Semantics

Persistence layer uses explicit exceptions:

- `DataAccessException`
- `JsonFormatException`

Current meanings:

- `DataAccessException`: general persistence failure such as file access failure, duplicate id, or missing target during update
- `JsonFormatException`: malformed JSON content or JSON mapping failure

Shared behavior:

- `insert` with duplicate primary key throws `DataAccessException`
- `update` with missing target id throws `DataAccessException`
- invalid JSON throws `JsonFormatException`
- missing required JSON files fail clearly

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

### UI layer

UI should:

- call controller/service only
- not read or write JSON files directly
- not depend on repository/json implementations directly

## Current [待确认] Summary

- expanded fields for Student, Job, and Application
- full Application status transition rules
- whether persistence should enforce more uniqueness constraints
- whether repository interfaces should add business-field lookup methods
- final User model fields and authentication semantics
