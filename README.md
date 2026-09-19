# Media Rental System

## Overview

The Media Rental System is a Java command-line application for managing a
catalog of eBooks, movie DVDs, and music CDs.

The application demonstrates object-oriented domain modeling, validation,
file-based persistence, searching, sorting, rental state management, and
automated testing. Media records are stored as structured text files,
allowing persistence behavior to remain explicit and easy to inspect.

---

## Features

- Add eBooks, movie DVDs, and music CDs
- Search media by ID, title, or creator
- Rent and return media with availability enforcement
- Browse media by type and availability
- Sort browse results by ID, title, or year
- Case-insensitive ID lookup
- Input normalization for newly added media
- Validation of IDs, text fields, and release years
- File-based persistence of catalog and availability changes
- Graceful handling of malformed persisted records
- Rollback of in-memory availability when persistence fails
- Automated testing of domain, CLI, validation, and persistence behavior

---

## Application Structure

The application uses an object-oriented design centered around a shared
`Media` base class and specialized media types.

### Media

`Media` contains the attributes shared by all media items:

- ID
- Title
- Creator
- Release year
- Rental fee
- Availability

### Media Types

The application supports three concrete media types:

- `EBook`
- `MovieDVD`
- `MusicCD`

Each type provides its own rental fee and creator information. Movie DVDs
also store a director separately from the primary actor.

### Manager

`Manager` coordinates the application's main operations:

- Loading persisted media
- Adding media
- Searching
- Renting
- Returning
- Browsing and sorting
- Validation
- Persistence

### MediaRentalSystem

`MediaRentalSystem` provides the command-line entry point and main menu.

---

## Persistence

Media records are stored in the `data` directory using one text file per
media item.

Filenames identify the media type and ID:

```text
eBook-<ID>.txt
MovieDVD-<ID>.txt
MusicCD-<ID>.txt
```

eBooks and music CDs use five persisted fields:

```text
id,title,creator,year,availability
```

Movie DVDs use six fields so the actor and director are stored separately:

```text
id,title,actor,year,director,availability
```

Persisted records are validated when loaded. Malformed records are skipped
without preventing valid media from loading.

---

## Validation and Normalization

New media entries are validated before being persisted.

The application enforces:

- Exactly six alphanumeric characters for media IDs
- Unique media IDs
- Nonblank titles and creator names
- No commas in text fields because persistence uses an unquoted
  comma-delimited format
- Release years from `1000` through the current year
- Strict persisted boolean values for availability
- Matching persisted IDs and filenames

Newly entered IDs are normalized to uppercase. New text values are trimmed,
repeated internal spaces are collapsed, and capitalization is normalized
before persistence.

Existing persisted records are loaded without rewriting their descriptive
text.

---

## Rental State Management

Media items have two availability states:

```text
AVAILABLE -> RENTED
RENTED -> AVAILABLE
```

The application prevents invalid transitions, including:

- Renting an unavailable item
- Returning an item that is already available

Availability changes are persisted to the corresponding media file. If the
persistence operation fails, the in-memory state is rolled back so the
catalog remains consistent.

---

## Command-Line Interface

The main menu provides five workflows:

```text
1: Add Media
2: Find Media
3: Rent Media
4: Return Media
5: Browse Media
9: Quit
```

### Add Media

Creates and persists a new eBook, movie DVD, or music CD after validating
and normalizing the supplied information.

### Find Media

Searches the catalog by:

- Title
- ID
- Creator

Creator searches include authors, performers, actors, and movie directors.

### Rent Media

Locates an available media item, confirms the rental, updates its
availability, persists the change, and displays the rental fee.

### Return Media

Locates a rented media item, confirms the return, and persists the updated
availability.

### Browse Media

Filters the catalog by media type and availability and supports ascending
or descending sorting.

---

## Build and Test

### Prerequisites

- Java 21
- Maven 3.8 or later

### Run the automated tests

```bash
mvn clean test
```

The project currently contains **54 JUnit 5 tests** covering loading,
validation, normalization, searching, adding, renting, returning, browsing,
sorting, persistence, rollback behavior, and command-line output.

### Build the executable JAR

```bash
mvn clean package
```

The packaged application is created at:

```text
target/media-rental-system-1.0.0.jar
```

### Run the application

From the repository root:

```bash
java -jar target/media-rental-system-1.0.0.jar
```

The application automatically loads its catalog from the `data` directory
at startup.

---

## Tools and Technologies

- Java 21
- Maven
- JUnit 5
- Java NIO file APIs
- Object-oriented programming
- File-based persistence
- Command-line interface

---

## What This Project Demonstrates

This project demonstrates practical Java software engineering concepts,
including:

- Object-oriented design and inheritance
- Domain modeling
- Separation of application and domain responsibilities
- Defensive input validation
- Input normalization
- File parsing and persistence
- State-transition enforcement
- Failure recovery and state rollback
- Searching, filtering, and sorting
- Automated regression testing
- Filesystem-based integration testing
- Maven build and packaging workflows

---

## License

This project is licensed under the MIT License. See the
[LICENSE](LICENSE) file for details.
