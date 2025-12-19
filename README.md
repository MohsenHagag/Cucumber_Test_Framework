# Cucumber Test Framework

This is a skeletal framework structure extracted from `FAAINEX_ERP_QC`.

## Project Structure
- `src/main/java/config`: Configuration properties.
- `src/main/java/factory`: WebDriver factory.
- `src/main/java/pages/common`: BasePage and Repetitive (Locators).
- `src/main/java/utils`: Utilities for config and waiting.
- `src/test/java/hooks`: Setup and Teardown logic.
- `src/test/java/runners`: Test execution runner.
- `src/test/java/steps`: Step definitions (currently empty).
- `src/test/resources/features`: Cucumber feature files (currently empty).

## Setup
1. Open `src/main/java/config/config.properties` and set `baseUrl`, `username`, etc.
2. Update `src/main/java/pages/common/Repetitive.java` with actual locators and credentials if needed.

## Running Tests
Run via Maven:
```bash
mvn clean test
```
