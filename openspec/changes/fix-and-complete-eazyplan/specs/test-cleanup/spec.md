# Test Cleanup Specification

## Purpose

Defines the cleanup and correction of the test suite: removing duplicates, fixing references, and adding assertions.

## Requirements

### Requirement: Remove Duplicate Test Package

The system MUST NOT contain a duplicate test package. The `com.eazyplan` test package under `src/test/java/com/eazyplan/` MUST be deleted, keeping only `com.eazyplan.ia` tests.

#### Scenario: Only one test package exists

- GIVEN the duplicate com.eazyplan test package exists
- WHEN cleanup is applied
- THEN only com.eazyplan.ia test package remains
- AND no compilation errors arise from the deletion

### Requirement: Fix Test API References

All remaining tests MUST reference actual service and constructor APIs that exist in the codebase (e.g., ServiceLocator, actual method signatures).

#### Scenario: ServiceLocator exists and is usable

- GIVEN tests reference ServiceLocator.getUserService()
- WHEN tests run
- THEN ServiceLocator class exists with getUserService method returning UserService

#### Scenario: All test assertions match return types

- GIVEN test methods call service methods
- WHEN assertions execute
- THEN assertion types match actual return types (e.g., boolean vs int for existsByUsername)

### Requirement: Tests Pass

After cleanup, all tests MUST pass with `mvn test`.

#### Scenario: Full test suite passes

- GIVEN cleaned and corrected test suite
- WHEN mvn test runs
- THEN all tests pass with zero failures
