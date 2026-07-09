# Arrive Wise

_by [Lukas Akermann](https://github.com/lakermann), July 2026_

> A Spring Boot application that uses AI to understand travel requests and recommend optimal connections based on
> arrival time preferences.

## Overview

Arrive Wise is an intelligent travel assistance tool designed for Swiss public transport. It combines natural
language processing with connection matching to help travelers find optimal train connections. The application uses
Spring AI with a local Ollama European LLM to interpret user travel intents in natural language and suggest the best
connections for your target arrival time within Switzerland.

## Prerequisites

Before running the application, ensure you have:

- Java 17+ installed
- Gradle (or use the included gradlew)
- Ollama installed and running with the mistral-small3.2 model

### Installing Ollama

1. Download Ollama from [ollama.ai](https://ollama.ai)
2. Install and start the Ollama service
3. Pull the required model:
   ```bash
   ollama pull mistral-small3.2
   ```
4. Verify Ollama is running on `http://localhost:11434`

## Setup & Installation

### 1. Start Ollama

Ensure Ollama is running with the mistral-small3.2 model:

```bash
ollama serve
```

The application expects Ollama to be available at `http://localhost:11434` (configurable in `application.properties`).

### 3. Build the Application

```bash
./gradlew build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

**Example Usage:**

```
Describe your trip, or type "exit" to quit.
> I'm in Thun and need to be in Bern by 09:00 for a client meeting

✓ Recommended
  08:04 Thun → 08:25 Bern
  35 min buffer

⚠ Latest feasible
  08:34 Thun → 08:56 Bern
  4 min buffer

To arrive in Bern by 09:00 for your client meeting, take the train departing
Thun at 08:04. This gives you a comfortable buffer of 35 minutes to reach your
destination on time. The later connection at 08:34 only provides a 4-minute
buffer, which is not recommended for reliable arrival.
```

## Testing and Code Quality

Run all standard verification tasks used by the CI pipeline:

```bash
./gradlew check
```

This runs:

* Unit tests
* Kotlin compilation checks
* ktlint code-style checks

Integration tests are not included in `check` and must be run explicitly.

### Unit Tests

Run only the unit tests:

```bash
./gradlew test
```

### Integration Tests

Run the integration tests separately:

```bash
./gradlew integrationTest
```

Integration tests require Ollama to be running locally.

### Kotlin Formatting

Check Kotlin code formatting without modifying files:

```bash
./gradlew ktlintCheck
```

Automatically fix supported formatting violations:

```bash
./gradlew ktlintFormat
```

Before committing, run:

```bash
./gradlew ktlintFormat
./gradlew check
```

To run all checks, including integration tests:

```bash
./gradlew check integrationTest
```
