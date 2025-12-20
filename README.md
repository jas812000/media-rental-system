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
- Automatic loading of the media catalog at application startup
- Media search by ID, title, or artist
- Rent and return workflows with availability enforcement
- Support for multiple media types using inheritance-based specialization
- Structured file-based persistence with deterministic loading
- Centralized manager/service layer coordinating domain logic
- Defensive parsing and graceful handling of malformed input
- Comprehensive JUnit test coverage for loading and state behavior

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
  - `eBook`
  - `MovieDVD`
  - `MusicCD`

- **Persistence Layer**
  - Structured text files with one file per media item.

- **Validation & Error Handling**
  - Explicit validation of parsed input
  - Graceful skipping of malformed media files
  - Controlled state transitions for rental operations

---

## Data Persistence Model
- One directory represents a media catalog
- One file per media item
- Filename prefix determines media type:
  - `eBook-<ID>.txt`
  - `MovieDVD-<ID>.txt`
  - `MusicCD-<ID>.txt`
- CSV-style structured text format per file:

  `id,title,artist,year,isAvailable`


This approach simulates backend persistence while keeping storage logic
explicit, deterministic, and testable.

---

## Media State Management
Media items follow a controlled availability lifecycle:

- `AVAILABLE → RENTED`
- `RENTED → AVAILABLE`

Invalid state transitions (e.g., renting an unavailable item or returning
an item that is already available) are explicitly blocked to preserve
system consistency.

---

## Error Handling Strategy
The system enforces correctness through defensive parsing and guarded
operations, including:
- Detection and skipping of malformed media records
- Validation of numeric and boolean fields
- Prevention of invalid rental state transitions
- Explicit handling of file I/O failures

Malformed files do not terminate the load process and are safely ignored.

---

## Build & Test

### Prerequisites
- Java 17+
- Maven 3.8+

### Run tests
```bash
mvn test
```
  
---


## Run (CLI)

The media catalog is automatically loaded from the ./data directory at
application startup. No manual loading or configuration is required.

```bash
mvn -q exec:java
```

The CLI menu provides options to:

- Add individual media items
- Search for media
- Rent media
- Return media

---

## Tools & Technologies
- **Language:** Java 21
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **Modeling:** UML
- **Persistence:** Structured text files
- **Testing Techniques:** Unit tests and filesystem-based integration tests
   
---
   
## Purpose
This project serves as a backend engineering case study demonstrating:
- Object-oriented system design
- Inheritance-based domain modeling
- Deterministic persistence strategies
- Defensive input validation
- Controlled state transitions
- Automated testing and regression prevention
- Translation of backend design concepts into working Java code

---

## License
This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.

---


