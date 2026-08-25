E-Waste Collection & Recycling Management System

A desktop-based enterprise application built with JavaFX, SQLite, and Maven to manage the end-to-end lifecycle of electronic waste.

The system provides structured workflows for:

* E-waste submission
* Automated pickup prioritization
* Collector dispatching
* Recycling center inspection
* Outcome-based processing
* Reward point issuance
* Analytical reporting

⸻

Table of Contents

1. Project Overview
2. Technology Stack
3. Architecture & Design
4. Design Patterns Implementation
5. User Roles & Key Features
6. Database Schema
7. Team Work Division
8. Project Directory Structure
9. Getting Started & Installation
10. Running Automated Tests
11. Git Workflow & Branching Policy

⸻

1. Project Overview

Improper disposal of electronic devices poses significant environmental and health risks. The E-Waste Collection & Recycling Management System provides a digital logistics platform connecting consumers, field collectors, and certified recycling facilities.

Key Capabilities

* Lifecycle State Tracking: Enforces valid custody transitions from submission to final processing.
* Algorithmic Prioritization & Dispatch: Dynamically scores pickup urgency and optimizes collector assignment.
* Granular Processing Decisions: Records technical inspection results into five processing channels:
    * REUSE
    * REPAIR
    * REFURBISH
    * RECYCLE
    * HAZARDOUS_DISPOSAL
* Incentive Mechanism: Awards eco-points to consumers based on weight, device category, and physical condition.
* Decoupled Event Broadcasting: Sends notifications to relevant stakeholders when pickup statuses change.
* SQL Business Intelligence: Provides analytical reports using aggregate functions, joins, filtering, and other SQL operations.

⸻

2. Technology Stack

Technology	Purpose
Java 17+	Core programming language
JavaFX 21	Desktop graphical user interface
FXML	UI layout and view definition
CSS	JavaFX UI styling
Apache Maven	Build and dependency management
SQLite	Relational database
SQLite JDBC	Java-SQLite connectivity
JUnit 5	Automated unit testing
SLF4J / Logback	Application logging

Main Dependencies

* JavaFX 21
* SQLite JDBC 3.45.1.0 or higher
* JUnit 5 Jupiter
* SLF4J
* Logback
* Maven

⸻

3. Architecture & Design

The application follows a Layered Architecture to separate presentation, business logic, and data persistence.

┌──────────────────────────────────────────┐
│          Presentation Layer              │
│       JavaFX Views (.fxml) + CSS         │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│           Controller Layer               │
│    FXML Controllers & Event Binding      │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│        Facade / Service Layer            │
│ PickupFacade + Services + Domain Logic  │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│            Data Access Layer             │
│       CRUD Repositories & DAO Logic      │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│          Persistence Layer               │
│          SQLite Database File             │
│           e_waste_db.sqlite              │
└──────────────────────────────────────────┘

Architectural Principles

* Separation of concerns
* Low coupling
* High cohesion
* Single Responsibility Principle
* Open/Closed Principle
* Dependency abstraction
* Reusable business logic
* Testable service and domain layers

⸻

4. Design Patterns Implementation

The project demonstrates five major design patterns.

Pattern	Components	Purpose
Strategy	PriorityStrategy, AssignmentStrategy	Allows different prioritization and collector assignment algorithms to be selected dynamically.
State	PickupState and concrete state classes	Controls valid pickup lifecycle transitions and prevents invalid state changes.
Observer	PickupEventManager, UserObserver, CollectorObserver, AdminObserver	Decouples pickup events from notification receivers.
Factory	EWasteFactory	Centralizes creation of different e-waste subclasses.
Facade	PickupFacade	Provides a simplified interface for complex pickup operations.

Strategy Pattern

The Strategy pattern separates different algorithms from the main workflow.

Priority Strategies

* Hazardous Waste Priority
* Weight-Based Priority
* Waiting-Time Priority

Assignment Strategies

* Nearest Collector
* Least Busy Collector
* Specialist Collector

This allows new algorithms to be added without modifying the core pickup workflow.

⸻

State Pattern

The pickup lifecycle is represented through explicit states:

SUBMITTED
    ↓
REQUESTED
    ↓
ASSIGNED
    ↓
COLLECTED
    ↓
DELIVERED
    ↓
PROCESSING
    ↓
COMPLETED

A pickup can also transition to:

CANCELLED

The State pattern ensures that invalid transitions are rejected by the system.

⸻

Observer Pattern

The PickupEventManager broadcasts pickup status changes to relevant observers.

                PickupEventManager
                 /       |       \
                /        |        \
               ▼         ▼         ▼
        UserObserver  CollectorObserver  AdminObserver

This keeps notification logic independent from the pickup workflow.

⸻

Factory Pattern

EWasteFactory creates different types of electronic waste objects:

* LaptopWaste
* MobileWaste
* BatteryWaste
* DisplayWaste
* ApplianceWaste

The factory centralizes object creation and reduces direct dependency on concrete classes.

⸻

Facade Pattern

PickupFacade provides a single interface for complex pickup operations such as:

1. Input validation
2. Waste categorization
3. Priority calculation
4. Collector assignment
5. Database persistence
6. Event notification

This prevents controllers from directly coordinating multiple services and repositories.

⸻

5. User Roles & Key Features

Customer

Customers can:

* Register and authenticate
* Manage profile information
* Submit e-waste items
* Specify weight and condition
* Identify hazardous materials
* Schedule pickup requests
* Specify collection addresses and time windows
* Track pickup status
* View previous submissions
* View reward points
* View reward transaction history

⸻

Collector

Collectors can:

* View assigned collection tasks
* Filter tasks by operating area
* View pickup priority
* Access customer address information
* View package specifications
* Update pickup states
* Confirm collection
* Confirm delivery to recycling facilities

Typical lifecycle:

ASSIGNED → COLLECTED → DELIVERED

⸻

Administrator

Administrators can:

* Manage users
* Manage e-waste categories
* Manage collectors
* Manage recycling centers
* Monitor pickup requests
* Monitor in-transit waste
* Record technical inspections
* Record processing outcomes
* Monitor recycling center capacity
* View analytical reports
* Analyze recycling rates
* Analyze hazardous waste volumes
* Monitor collector KPIs

⸻

6. Database Schema

The system uses SQLite as its relational database.

User

User
├── user_id
├── name
├── email
├── password_hash
├── role_id
├── phone
└── created_at

Role

Role
├── role_id
└── role_name

E-Waste Category

EWasteCategory
├── category_id
├── name
├── description
└── hazardous_default

E-Waste Item

EWasteItem
├── item_id
├── user_id
├── category_id
├── brand
├── model
├── weight_kg
├── quantity
├── condition
├── is_hazardous
└── created_at

Pickup Request

PickupRequest
├── pickup_id
├── user_id
├── collector_id
├── address
├── preferred_date
├── preferred_time
├── status
├── priority_score
└── created_at

Other Entities

PickupItem
Collector
RecyclingCenter
ProcessingRecord
Reward
Notification

Entity Relationships

The major relationships are:

User
 │
 ├──────────────► EWasteItem
 │
 ├──────────────► PickupRequest
 │                      │
 │                      ├──► PickupItem
 │                      │       │
 │                      │       └──► EWasteItem
 │                      │
 │                      └──► ProcessingRecord
 │                                  │
 │                                  └──► RecyclingCenter
 │
 └──────────────► Reward
Collector ─────────────► PickupRequest
Role ──────────────────► User
PickupRequest ─────────► Notification

⸻

7. Project Directory Structure

e-waste-management-system/
│
├── pom.xml
├── README.md
│
├── database/
│   ├── schema.sql
│   └── seed_data.sql
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ewaste/
│   │   │           ├── App.java
│   │   │           ├── config/
│   │   │           │   ├── DatabaseManager.java
│   │   │           │   └── DatabaseSeeder.java
│   │   │           ├── controller/
│   │   │           ├── facade/
│   │   │           │   └── PickupFacade.java
│   │   │           ├── factory/
│   │   │           │   └── EWasteFactory.java
│   │   │           ├── model/
│   │   │           ├── observer/
│   │   │           ├── repository/
│   │   │           ├── service/
│   │   │           ├── state/
│   │   │           ├── strategy/
│   │   │           └── util/
│   │   │
│   │   └── resources/
│   │       ├── css/
│   │       ├── fxml/
│   │       └── images/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── ewaste/
│                   ├── auth/
│                   ├── pattern/
│                   ├── service/
│                   └── state/

⸻

8. Getting Started & Installation

Prerequisites

Make sure the following are installed:

* JDK 17 or higher
* Apache Maven 3.8 or higher
* Git

Verify the installations:

java -version
mvn -version
git --version

⸻

Step 1: Clone the Repository

git clone https://github.com/<your-organization>/e-waste-management-system.git
cd e-waste-management-system

Replace <your-organization> with the actual GitHub username or organization.

⸻

Step 2: Build the Project

Compile the source code and download the required Maven dependencies:

mvn clean compile

⸻

Step 3: Run the Application

Start the JavaFX desktop application:

mvn javafx:run

The database tables and sample data are automatically initialized on application startup through DatabaseSeeder.

⸻

9. Running Automated Tests

The project uses JUnit 5 for automated testing.

Run All Tests

mvn test

Run a Specific Test Class

For example:

mvn test -Dtest=PriorityStrategyTest

or:

mvn test -Dtest=PickupStateTransitionTest

Test Coverage Areas

Automated tests cover areas including:

* Authentication
* Input validation
* Pickup management
* Priority strategies
* Assignment strategies
* State transitions
* Processing logic
* Business services

⸻

10. Git Workflow & Branching Policy

The project follows a structured Git branching workflow.

Main Branch

main

Contains production-ready, stable, and tested code.

Direct commits to main are restricted.

⸻

Development Branch

develop

Contains integrated development work before release.

Feature branches are created from develop.

⸻

Feature Branches

Each feature should use a dedicated branch:

feature/auth-and-user
feature/ewaste-submission
feature/priority-strategy
feature/assignment-strategy
feature/state-pattern
feature/processing-inspection
feature/observer-notification

Recommended Workflow

main
 │
 └── develop
       │
       ├── feature/auth-and-user
       ├── feature/ewaste-submission
       ├── feature/priority-strategy
       └── feature/state-pattern

Pull Request Process

1. Create a feature branch from develop.
2. Implement the feature.
3. Write and run unit tests.
4. Commit changes using meaningful commit messages.
5. Push the feature branch.
6. Create a Pull Request to develop.
7. Review and test the changes.
8. Merge the Pull Request into develop.
9. Merge stable releases from develop into main.

⸻

Project Goals

The project aims to demonstrate how software design principles and design patterns can be applied to a realistic enterprise-style application.

The primary goals are:

* Build a maintainable JavaFX application.
* Apply object-oriented design principles.
* Demonstrate practical design pattern usage.
* Implement a normalized relational database.
* Separate application layers effectively.
* Provide automated testing.
* Practice collaborative Git-based development.
* Develop a realistic end-to-end e-waste management workflow.

⸻

License

This project was developed for academic purposes as part of the Design Patterns Lab at the University of Dhaka.
