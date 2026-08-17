---
name: code-refactoring-optimizer
description: Guides the agent to identify code smells, detect duplicate logic, and autonomously refactor code following design patterns and SOLID principles.
---

# Code Refactoring Optimizer Skill

This skill teaches the AI Agent how to identify structural weaknesses (code smells), reduce technical debt, eliminate code duplication, and refactor codebases safely following clean code standards (SOLID principles and Design Patterns).

## When to Use This Skill

- When a file grows too large or contains multiple responsibilities (Single Responsibility Principle violation).
- When duplicate logic is scattered across multiple classes or modules (DRY - Don't Repeat Yourself principle violation).
- When code is hard to test due to tightly coupled dependencies.
- Before introducing new features, to prepare the architecture (pre-refactoring).

## Refactoring Process

### Step 1: Detect Code Smells
Analyze source code files to search for:
- **Long Methods**: Functions exceeding 50 lines.
- **Large Classes**: Classes doing too many things.
- **Duplicate Code**: Mirror logic in different handlers.
- **Shotgun Surgery**: A single change requiring small edits across 10 different files.
- **Primitive Obsession**: Excessive use of primitive types instead of small, dedicated classes/objects.

### Step 2: Formulate Refactoring Recipe
Describe the structural change:
- **Extract Method**: Pull inline logic into a standalone, well-named function.
- **Extract Class**: Split a multi-purpose class into specialized classes.
- **Introduce Interface/Polymorphism**: Replace conditional statements (if-else, switch) with polymorphism.
- **Dependency Injection**: Inject dependencies instead of hardcoding them, allowing easier mocking in tests.

### Step 3: Run Tests Before Edits
- Ensure the current test suite passes successfully. Do not refactor code if it is already broken.

### Step 4: Apply Surgical Edits
- Edit the target files in small, incremental steps.
- Make use of `replace_file_content` to apply changes.
- Ensure all variable names remain self-explanatory.

### Step 5: Verify Behavior
- Run the test suite after each incremental refactoring step to ensure zero regression (no behavior changes).
- Re-run code linters to make sure code style remains compliant.
