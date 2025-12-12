# Media Rental System

## Overview
The Media Rental System is a backend-focused Java application designed to manage a catalog of rentable media items, including eBooks, MovieDVDs, and MusicCDs. The system supports loading media records from files, querying media by title or attributes, renting and returning items, and persisting state changes back to storage.

This repository is presented as an engineering case study demonstrating object-oriented design, modular backend architecture, and reliable file-based persistence.

---

## Key Capabilities
- Polymorphic media hierarchy with shared and type-specific behavior
- File-based persistence using structured text files
- Menu-driven console interface
- Media search by title and attributes
- Rental and return workflows with availability tracking
- Input validation and exception-safe execution
- Deterministic state transitions with persistent updates

---

## System Architecture

### Media Hierarchy
- `Media` (base class)
- `EBook`
- `MovieDVD`
- `MusicCD`

Each media subtype extends the base class and overrides behavior where appropriate, enabling dynamic binding and consistent handling across media types.

### Manager Component
The `Manager` class encapsulates backend responsibilities:
- Loading media records from a user-specified directory
- Parsing files and instantiating media objects
- Searching for media by title or attributes
- Executing rental and return operations
- Persisting availability changes back to storage

### Controller / UI Layer
The `MediaRentalSystem` class manages:
- Menu-driven program flow
- User input and validation
- Error handling and feedback
- Program lifecycle and termination

This separation of concerns keeps UI logic independent from business rules and persistence logic.

---

## Design Goals
- Demonstrate correct use of inheritance, encapsulation, and polymorphism
- Maintain clean separation between UI, business logic, and persistence
- Ensure predictable and recoverable program behavior
- Validate input at system boundaries
- Provide clear error handling and user feedback
- Emphasize maintainability and readability

---

## Scope and Assumptions

### Included
- Console-based interaction
- Directory-based media file loading
- Persistent availability tracking
- Structured exception handling
- Deterministic backend workflows

### Excluded
- Database systems (intentionally file-based)
- Concurrent or multi-user access
- Networked or distributed execution
- Asynchronous processing

---

## Tools & Technologies
- Java (Object-Oriented Programming)
- File I/O (`java.io`)
- Exception handling and validation
- Console-based UI
- PDF-based specifications and test documentation

---

## Testing & Validation
System behavior is validated using structured test scenarios documented in the accompanying project report. These scenarios demonstrate:
- Successful and failed media loading
- Search functionality
- Rental and return workflows
- Availability enforcement
- Error handling for invalid input

---

## Artifacts
- Project specification PDF
- Final project report with labeled test cases
- Sample media data files
- Source code for all system components

---

## What This Project Demonstrates
- Backend architectural reasoning
- Object-oriented modeling and dynamic dispatch
- File-based persistence strategies
- Robust input validation and exception handling
- Clear separation of responsibilities across system components
- Ability to design and document a complete, testable backend system

---

## Notes
This repository is intended as a portfolio artifact to demonstrate backend engineering fundamentals and disciplined system design. It is not positioned as a production-ready rental platform.
