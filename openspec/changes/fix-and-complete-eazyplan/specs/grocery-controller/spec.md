# Grocery List Controller Specification

## Purpose

Defines the wiring of GroceryListController to GroceryListView.fxml, populating grocery list data and handling new list creation.

## Requirements

### Requirement: Grocery List Display

GroceryListController MUST bind to GroceryListView.fxml and display the user's grocery lists via GroceryListService.

#### Scenario: View loads with grocery data

- GIVEN the user has existing grocery lists
- WHEN GroceryListView is loaded
- THEN the controller displays the user's grocery lists

#### Scenario: No grocery lists for user

- GIVEN the user has no grocery lists
- WHEN GroceryListView loads
- THEN the view renders empty with no errors

### Requirement: New Grocery List Button

GroceryListController MUST handle the `handleNewGrocery` action from the `newGroceryButton` in GroceryListView.fxml.

#### Scenario: New grocery list button clicked

- GIVEN GroceryListView is displayed
- WHEN the user clicks the new grocery list button
- THEN the system initiates grocery list creation via GroceryListService and refreshes the display
