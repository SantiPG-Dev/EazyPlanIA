# Repository Pattern Specification

## Purpose

Defines how JPA repository implementations manage EntityManager lifecycle and transaction boundaries for all persistence operations.

## Requirements

### Requirement: Transactional Save Operations

All repository implementations MUST wrap `save()` operations inside an active JPA transaction with explicit `begin()`, `commit()`, and `rollback` on failure. The EntityManager MUST be obtained fresh per operation from the EntityManagerFactory.

#### Scenario: Persist new entity

- GIVEN a new entity with null id
- WHEN save is called
- THEN a transaction begins, the entity is persisted, and the transaction commits
- AND the entity receives a generated id

#### Scenario: Merge existing entity

- GIVEN an entity with a non-null id
- WHEN save is called
- THEN a transaction begins, the entity is merged, and the transaction commits

#### Scenario: Save failure triggers rollback

- GIVEN a constraint violation during save
- WHEN the operation fails
- THEN the active transaction rolls back and the exception propagates

### Requirement: Transactional Delete Operations

All repository implementations MUST wrap `delete()` inside a transaction with `begin()`, `commit()`, and rollback on failure.

#### Scenario: Delete existing entity

- GIVEN an entity that exists in the database
- WHEN delete is called
- THEN a transaction begins, the entity is removed, and the transaction commits

#### Scenario: Delete nonexistent entity

- GIVEN an entity id that does not exist
- WHEN delete is called
- THEN an IllegalArgumentException is thrown and the transaction rolls back

### Requirement: Per-Operation EntityManager

Repository implementations MUST NOT hold EntityManager as a singleton field. Each mutating operation (save, delete) MUST create a new EntityManager from the EntityManagerFactory. Read-only operations (findById, findByUsername, findAll) SHOULD use a fresh EntityManager per call.

#### Scenario: Concurrent save operations

- GIVEN two save calls on the same repository
- WHEN both execute concurrently
- THEN each uses its own EntityManager within its own transaction
