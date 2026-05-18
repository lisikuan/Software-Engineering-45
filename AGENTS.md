# AGENTS.md

## Scope
This repository is for the TA Recruitment System.

Required stack:
- Java 17
- Swing
- JSON persistence

Hard constraints:
- Do not introduce databases.
- Do not introduce Spring Boot, ORM, or other heavy frameworks.
- Keep the implementation aligned with course requirements for a lightweight Java application.

## Architecture
Use strict 3-layer architecture:

presentation -> controller/service/validation -> persistence -> data/*.json

Allowed dependency direction only:
- presentation may call controller/service
- controller may call service
- service may call persistence.repository
- persistence.json may read/write data files

Forbidden:
- presentation directly reading or writing JSON files
- presentation directly depending on repository/json implementations
- service doing file I/O or hardcoding file paths
- persistence depending on presentation or controller
- business rules being hidden inside DAO/JSON utility code

## Directory Rules
Use this structure as the default:

- src/main/java/edu/bupt/tarecruitment/presentation/
- src/main/java/edu/bupt/tarecruitment/controller/
- src/main/java/edu/bupt/tarecruitment/service/
- src/main/java/edu/bupt/tarecruitment/validation/
- src/main/java/edu/bupt/tarecruitment/model/
- src/main/java/edu/bupt/tarecruitment/persistence/repository/
- src/main/java/edu/bupt/tarecruitment/persistence/json/
- src/main/java/edu/bupt/tarecruitment/common/
- data/

Do not place UI logic in persistence.
Do not place file persistence logic in presentation.
Do not bypass layers for convenience.

## Source of Truth
Persistent source of truth is JSON only:
- data/students.json
- data/jobs.json
- data/applications.json
- data/users.json

Core domain entities:
- Student
- Job
- Application
- User

If any field, status, schema, or model structure changes, update all affected parts together:
- model
- validation
- repository
- persistence implementation
- tests
- related docs/comments marked [待确认]

## Coding Rules
Naming:
- package names: lowercase only
- class names: PascalCase
- method names: clear verb-based names
- JSON filenames: lowercase plural names

Examples:
- StudentService
- ApplicationController
- JsonStudentRepository
- students.json

Avoid vague names like:
- Util2
- HandleData
- processThing

Use enums or constants for statuses. Do not scatter raw status strings across the codebase.

## Input / Output Rules
Controller layer:
- input: UI parameters or DTOs
- output: domain objects, result objects, or explicit exceptions

Service layer:
- input: DTOs, domain objects, primitive parameters
- output: domain objects, lists, computed results, or explicit exceptions
- do not return Swing components
- do not return raw JSON strings

Repository layer:
- input: domain objects, IDs, filter parameters
- output: domain objects, lists, Optional<T>, boolean, or explicit data exceptions
- do not return UI text messages

## Exception Rules
Do not use bare RuntimeException for normal flow.

Prefer explicit exception types:
- ValidationException
- BusinessException
- DataAccessException
- JsonFormatException

Do not swallow exceptions.
Error messages must help locate the failure.

## Persistence Rules
JSON is the only persistence format.

All writes must go through unified persistence entry points.
Use safe update flow:
1. read current data
2. validate
3. modify
4. write back safely

Requirements:
- no silent overwrite of duplicate IDs
- malformed or missing files must fail clearly
- avoid partial writes
- prefer backup or temp-file replacement when writing
- keep persistence logic single-responsibility

DAO/repository code is responsible for storage mechanics, not full business decisions.

## Development Order
Default implementation order:
1. model
2. persistence
3. service
4. presentation

Prioritize minimum usable core flow first:
- user profile
- job publish / browse
- application submit
- application status update

Do not prioritize advanced AI features before the core flow is stable.

## AI-Generated Content Rules
Anything not explicitly confirmed by course documents, team report, existing code, or tests must be marked:

[待确认]

Mark as [待确认] especially for:
- new business rules
- new status transitions
- new JSON fields
- speculative features
- unverified exception handling strategy
- untested code behavior

High-risk assumptions that must not be added silently:
- email notification
- user registration flow
- database migration
- Spring Boot refactor
- external AI API integration
- extra admin powers not already confirmed

## Output Format for Generated Work
When generating code, prefer this order:
1. file path
2. class responsibility
3. complete code
4. dependency notes
5. [待确认] items
6. minimum test suggestions

When generating schema or interface docs, include:
1. file name
2. structure
3. required fields
4. enum/status values
5. example
6. [待确认] items

## Final Rule
First obey course constraints.
Then obey layer boundaries.
Then implement the smallest working feature.
Do not invent requirements silently.
When uncertain, mark it [待确认].
