# Media Rental System

---

## Overview
The Media Rental System is a modular Java backend designed to manage a catalog of rentable media items, including ebooks, movie DVDs, and music CDs.

The project emphasizes backend domain modeling, validation, persistence, and automated testing rather than UI concerns. All data is persisted using a structured file-based storage model instead of a database, allowing the system to simulate real backend responsibilities while remaining portable, deterministic, and easy to test.

---

## Features
- Media catalog loading from a structured directory
- Search media by ID, title, or artist
- Rent and return workflows with availability enforcement
- Support for multiple media types using inheritance-based specialization
- Deterministic file-based persistence per media item
- Centralized orchestration via a manager/service layer
- Explicit validation and controlled state transitions
- File I/O error handling and failure isolation

---

## Architecture Overview
The system follows a layered, object-oriented architecture:

---

### Application / Controller Layer
**MediaRentalSystem (CLI entry point)**  
Coordinates application startup, user input, and delegation to domain services.  
The CLI is intentionally minimal and not the focus of the project.

---

### Service Layer
**Manager / RentalService**  
Encapsulates business rules for searching, renting, returning, and persisting media items.  
Ensures validation, availability checks, and consistent state transitions.

---

### Domain Model
**Media (Abstract Base Class)**  
Defines shared attributes and behavior:
- ID
- title
- artist
- release year
- availability state

---

### Concrete Media Types
- `Ebook`
- `MovieDVD`
- `MusicCD`

Each subtype specializes behavior where appropriate while sharing common rental semantics.

---

### Persistence Layer
File-based persistence using structured text files:
- One file per media item
- Deterministic loading and saving
- No shared mutable global state

Persistence logic is isolated to allow testing without user interaction.

---

## Data Persistence Model
- One directory represents a media catalog
- One file per media item
- Filename prefix determines media type:
  - `eBook-<ID>.txt`
  - `MovieDVD-<ID>.txt`
  - `MusicCD-<ID>.txt`
- File contents use a simple CSV-style structure:






This approach simulates backend persistence while keeping storage logic explicit, predictable, and testable.

---

## Media State Management
Media items follow a controlled availability lifecycle:

- AVAILABLE → RENTED
- RENTED → AVAILABLE

Invalid transitions (e.g., renting an unavailable item or returning an item that is not rented) are explicitly blocked through domain logic to preserve system consistency.

---

## Error Handling Strategy
The system enforces correctness through explicit validation and guarded operations, including:
- Invalid or malformed file data detection
- Missing or duplicate media records
- Illegal availability transitions
- File load and save failures

Failures are surfaced clearly and are suitable for automated testing and debugging.

---

## Build & Test

---

### Prerequisites
- Java 17+
- Maven 3.8+

---

### Run Tests
```bash
mvn clean test
```

---

### Run CLI
```bash
mvn -q exec:java -Dexec.args="./data"
```

This project focuses on backend logic and automated testing.  
The CLI entry point is intentionally minimal.

---

## Tools & Technologies
- Language: Java 17
- Build Tool: Maven
- Testing: JUnit 5
- Persistence: Structured text files
- Design Focus: Object-oriented modeling, validation, testability

---

## Purpose
This project serves as a backend engineering case study demonstrating:
- Object-oriented system design
- Inheritance-based domain modeling
- Controlled state transitions
- Deterministic persistence strategies
- Validation-driven business logic
- Automated testing of backend behavior
- Translation of conceptual design into maintainable Java code

---

## License
This project is licensed under the MIT License.  
See the LICENSE file for details.

---
