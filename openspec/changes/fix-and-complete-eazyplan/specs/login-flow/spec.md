# Login Flow Specification

## Purpose

Defines the login and registration flow: UI controller wired to FXML fields, validation, authentication via UserService, error display, and navigation to dashboard.

## Requirements

### Requirement: Login Form Field Binding

LoginController MUST bind to FXML fields defined in LoginView.fxml: `usernameField` (TextField), `passwordField` (PasswordField), `errorMessage` (Label), and buttons with `handleLogin`/`handleRegister` actions.

#### Scenario: Controller loads with FXML

- GIVEN LoginView.fxml specifies LoginController as fx:controller
- WHEN the view is loaded
- THEN all @FXML annotated fields are injected without null

### Requirement: Login Validation and Authentication

LoginController.handleLogin MUST validate that username and password are non-empty, authenticate via UserService.login, show errors inline, and navigate to dashboard on success.

#### Scenario: Successful login

- GIVEN valid username and password in the fields
- WHEN handleLogin is triggered
- THEN UserService.login is called and on success the scene navigates to the dashboard view

#### Scenario: Empty credentials

- GIVEN username or password field is empty
- WHEN handleLogin is triggered
- THEN errorMessage label becomes visible with a validation message and no navigation occurs

#### Scenario: Invalid credentials

- GIVEN credentials that do not match any user
- WHEN handleLogin is triggered
- THEN UserService.login throws and errorMessage displays the authentication failure

### Requirement: Registration via Login View

LoginController.handleRegister MUST validate all fields, call UserService.register, show errors inline, and navigate to dashboard on success.

#### Scenario: Successful registration

- GIVEN valid username, name, email, password in the fields
- WHEN handleRegister is triggered
- THEN UserService.register creates the user and the scene navigates to the dashboard

#### Scenario: Duplicate username on register

- GIVEN a username that already exists
- WHEN handleRegister is triggered
- THEN errorMessage displays the duplicate username error from UserService
