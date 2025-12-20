# Media Rental System

## Overview
The Media Rental System is a modular Java backend designed to manage a catalog
of rentable media items across multiple media types, including ebooks,
movie DVDs, and music CDs.

The project focuses on backend domain modeling, validation, persistence,
and automated testing rather than UI concerns. All data is persisted using
a structured file-based storage model instead of a database, allowing the
system to simulate real backend responsibilities while remaining portable
and easy to test.

---

## Features
- Media catalog loading from a structured directory
- Media search by ID, title, or artist
- Rent and return workflows with availability enforcement
- Support for multiple media types using inheritance-based specialization
- Structured file-based persistence with deterministic loading and saving
- Centralized orchestration via a manager/service layer
- Explicit validation and controlled state transitions

---

## Architecture Overview
The system follows a layered, object-oriented architecture:

- **Manager / Service Layer**
  - Central controller coordinating media loading, searching,
    rental operations, validation, and persistence.

- **Media (Abstract Base Class)**
  - Encapsulates shared media attributes and behavior, including
    identification, descriptive metadata, and availability state.

- **Concrete Media Types**
  - `Ebook`
  - `MovieDVD`
  - `MusicCD`

- **Persistence Layer**
  - Structured text files with one file per media item.

- **Validation & Error Handling**
  - Guarded operations enforcing availability rules,
    input correctness, and file I/O integrity.

---

## Data Persistence Model
- One directory represents a media catalog
- One file per media item
- Filename prefix determines media type:
  - `eBook-<ID>.txt`
  - `MovieDVD-<ID>.txt`
  - `MusicCD-<ID>.txt`
- CSV-style structured text format per file:

  ```text
  id,title,artist,year,isAvailable

This approach simulates backend persistence while keeping storage logic
explicit, deterministic, and testable.

---

## Media State Management
Media items follow a controlled availability lifecycle:

- `AVAILABLE → RENTED`
- `RENTED → AVAILABLE`

Invalid transitions (e.g., renting an unavailable item or returning an item
that is not rented) are explicitly blocked through domain logic to preserve
system consistency.

---

## Error Handling Strategy
The system enforces correctness through explicit validation and guarded
operations, including:
- Invalid or malformed file data detection
- Missing or duplicate media records
- Illegal availability state transitions
- Load/save failures during file I/O

Failures are explicit, traceable, and suitable for automated testing.

---

## Build & Test

---

### Prerequisites
- Java 17+
- Maven 3.8+

---

### Run tests
```bash
mvn clean test
```

---

## Run (CLI)

This project primarily focuses on backend logic and automated testing.  
The CLI entry point is intentionally minimal.

```bash
mvn -q exec:java -Dexec.args="./data"
```

---

## Tools & Technologies
- **Language:** Java 17
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **Modeling:** UML
- **Persistence:** Structured text files
- **Design Artifacts:** Class diagrams, structured domain models

---

## Purpose
This project serves as a backend engineering case study demonstrating:
- Object-oriented system design
- Inheritance-based domain modeling
- Deterministic persistence strategies
- Controlled state transitions
- Validation-driven business logic
- Automated testing practices
- Translation of conceptual design into maintainable Java code

---

## License
This project is licensed under the MIT License.  
See the [LICENSE](LICENSE) file for details.

---












