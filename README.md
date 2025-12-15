# Clinic Appointment System (CAS)

The Clinic Appointment System (CAS) is a user-friendly application that simplifies healthcare management. Patients can schedule medical appointments with doctors, doctors can manage their schedules, and administrators oversee clinic operations, including doctor availability and patient data.

## Modules and Features

### User Registration and Login
- **Description**: Patients, doctors, and admins can sign up and log in with unique credentials.
- **Functionality**: Role-based access ensures each user type (Patient, Doctor, Admin) sees only relevant features.

### Doctor & Specialty Management
- **Description**: Admins and doctors manage doctor profiles, specialties, consultation hours, and availability.
- **Functionality**: Add/edit/remove doctors, set working hours, and associate doctors with specialties.

### Appointment Booking
- **Description**: Patients book appointments with doctors based on availability.
- **Functionality**: Appointment scheduling, date/time selection, preventing double bookings, and booking confirmation.

### Appointment Management
- **Description**: Doctors and admins manage appointment requests and schedules.
- **Functionality**: View daily/weekly schedules, approve/cancel/reschedule appointments, and block slots for leave.

### Billing & Payments
- **Description**: Handle consultation fees and payments.
- **Functionality**: Select payment method.

## Software Design Patterns

This project implements standard software design patterns to ensure scalability and maintainability:

1.  **Singleton Pattern**: Used in `LoginService`, `RegistrationService`, and `SessionManager` to ensure a single instance handles logic and state across the application.
2.  **Factory Pattern**: Implemented in `UserFactory` to centralize the creation of `User` objects (Patient, Doctor, Admin) based on role, promoting loose coupling.
3.  **MVC Architecture (Model-View-Controller)**: The project is structured to separate concerns:
    -   **Models** (`app.models`): Data representations (User, Patient, Doctor).
    -   **Views** (`app.ui`): User interface components (Dashboard, Forms).
    -   **Controllers** (`app.services`): Business logic handling interactions between models and views.
4.  **Observer Pattern**: Utilized implicitly via Java Swing's Event Delegation Model (e.g., `ActionListener` in buttons) to handle user interactions and update the UI.

## Technologies
-   **Language**: Java
-   **GUI**: Java Swing
-   **Build System**: Manual (javac) / GitHub Actions

## How to Run

### Prerequisites
-   Java Development Kit (JDK) 17 or higher.

### Compiling and Running
1.  Open a terminal in the project root.
2.  Compile the source code:
    ```bash
    javac -d out -sourcepath src src/Main.java
    ```
3.  Run the application:
    ```bash
    java -cp out Main
    ```
