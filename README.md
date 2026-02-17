# Mable Bank

A Kotlin + Spring Boot application that models a simple banking service.

---

## Problem statement (implemented)

1. Load account balances for a single company (16-digit account numbers, starting balances).
2. Accept a CSV file of transfers (`from`, `to`, `amount`).
3. Apply transfers in a way that **never allows an account to go below $0**.

This solution models accounts and transfers explicitly, persists them, and processes transfers with a clear, auditable lifecycle.

---

Beyond a minimal implementation, the service includes:
- REST endpoints for accounts + transactions.
- CSV upload endpoint for daily transaction files.
- Asynchronous event-driven ingestion of uploaded files.
- Scheduled processing of pending transfers on a 30-second schedule.
- Persistence via Spring Data JPA (H2 database).
- Input validation, sanitization + defensive parsing.

---

## High-level design

### Domain model

- **Account**
  - `accountId` (unique 16-digit number stored as `Long`)
  - `balance` (`BigDecimal`, scaled to 2 decimal places)
  - Operations:
    - `withdraw(amount)` throws if insufficient funds
    - `deposit(amount)`

- **Transaction**
  - `sender` account
  - `receiver` account
  - `amount`
  - `state` (see below)
  - `createdAt` timestamp

- **TransactionState**
  - `PENDING` — ingested and queued for processing
  - `PROCESSED` — successfully applied
  - `DISHONOURED` — rejected due to insufficient funds (would go below $0)

### Processing pipeline

1. **Startup seed**
   - On application start, the service loads:
     - account balances from `src/main/resources/mable_account_balances.csv`
     - an example transaction file from `src/main/resources/mable_transactions.csv` (queued as `PENDING`)

2. **Daily batch ingestion**
   - A transaction CSV can be uploaded via the API.
   - The upload is saved to `src/main/resources/static/upload`.
   - An application event is published so ingestion can be handled asynchronously/decoupled from the controller request.

3. **Scheduled execution**
   - A scheduler periodically fetches all `PENDING` transactions (ordered by creation time) and applies them:
     - withdraw from sender
     - deposit to receiver
     - mark as `PROCESSED`
   - If withdrawal fails due to insufficient funds:
     - the transaction is marked `DISHONOURED`
     - balances are not modified

This creates an auditable ledger of attempted transfers without losing failed items.

---

## Notable features / extensions beyond the prompt

### 1) Transaction lifecycle and auditability
Instead of applying transfers directly on ingestion, the system persists them as `PENDING` and processes them later, recording the final state (`PROCESSED` or `DISHONOURED`).

This makes it easy to:
- Inspect what was attempted.
- Distinguish ingestion errors from business-rule failures.
- Reprocess/retry in future extensions.

### 2) Asynchronous event-driven batch ingestion
Uploads publish an application event, allowing ingestion logic to live outside the controller and keeping the HTTP request lightweight (returns `202 Accepted`).

### 3) Scheduled processing
Transfers are processed on a fixed schedule (configured in code), modeling a real "batch settlement" approach rather than immediate synchronous mutation.

### 4) Defensive CSV parsing + validation
- Blank/malformed lines are skipped safely (with warnings).
- Basic transaction constraints are enforced during parsing:
  - Sender and receiver must be different.
  - Amount must be positive.
  - Sender/receiver accounts must exist (otherwise skipped).
- Account-balance loading uses validation to avoid seeding invalid accounts.

### 5) Persistence-backed implementation
Using JPA repositories + H2 means the system is not just an in-memory script:
- State is queryable via REST.
- Behavior is testable at repository/controller boundaries.
- More closely resembles a production service structure.

---

## Running locally

### Prerequisites
- JDK **21**
- No external services required (runs as a typical Spring Boot app)

### Build
```shell script
./gradlew build
```

### Run
```shell script
./gradlew bootRun
```
The application uses an in-memory H2 database with schema auto-created on startup and dropped on shutdown.

### Test
```shell script
./gradlew test
```
The test suite includes coverage for:
- controllers (API behavior)
- repositories (persistence queries)
- core domain behaviors (e.g., account balance rules)
- orthogonal test cases (e.g., account ID business rules, conflict detection)

---

## Potential future improvements

If extending this further, good next steps would include:
- Idempotency keys / batch identifiers to prevent duplicate transaction ingestion.
- Stronger CSV schema validation with error reporting back to the client.
- Concurrency controls for processing (e.g., pessimistic locking on accounts).
- Configurable schedule and upload location via `application.yaml`.
- Multi-company support (tenant/company identifier on accounts and transactions).
- Streaming ingestion for very large CSV files.

---
