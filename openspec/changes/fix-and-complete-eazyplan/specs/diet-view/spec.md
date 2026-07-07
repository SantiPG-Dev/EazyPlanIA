# Diet View Specification

## Purpose

Defines the new DietView FXML and DietController for diet CRUD operations: view list, create, and delete diets.

## Requirements

### Requirement: Diet List View

The system MUST provide DietView.fxml and DietController that display the user's diets from DietService.

#### Scenario: Diet list loads with data

- GIVEN the user has existing diets
- WHEN DietView is loaded
- THEN the controller displays the user's diet list with relevant fields

#### Scenario: No diets for user

- GIVEN the user has no diets
- WHEN DietView loads
- THEN the view renders empty with no errors

### Requirement: Create Diet

DietController MUST provide a mechanism to create a new diet via DietService.

#### Scenario: Create new diet

- GIVEN DietView is displayed and the user provides diet details
- WHEN the create action is triggered
- THEN DietService.createDiet is called and the list refreshes to show the new diet

### Requirement: Delete Diet

DietController MUST provide a mechanism to delete a selected diet via DietService.

#### Scenario: Delete selected diet

- GIVEN a diet is selected in the list
- WHEN the delete action is triggered
- THEN DietService.deleteDiet is called and the list refreshes without the deleted diet
