---
name: codegraph
description: Helps the agent analyze, query, and construct code graphs (dependency graph, call graph, class inheritance) to understand large codebases efficiently without reading every file, saving token usage.
---

# Codegraph Skill

This skill guides the agent in using code graphs to navigate and understand complex codebases. By constructing a mental or digital model of call graphs, dependencies, and class hierarchies, the agent can pinpoint relevant code locations without consuming massive input tokens by reading irrelevant files.

## When to Use This Skill

- When joining a new or large codebase with many directories.
- When trace-debugging a bug that spans across multiple modules or service boundaries.
- When performing refactoring that affects class hierarchies or function calls.
- When you need to understand "who calls this function" or "what components depend on this module".

## Key Concepts

1. **Call Graph**: A directed graph representing calling relationships between subroutines in a computer program.
2. **Dependency Graph**: A directed graph representing dependencies of one class/module/package on another.
3. **Inheritance Tree**: A graph representing class hierarchy (parents, interfaces, child classes).

## Strategies for Token Optimization using Codegraph

### Step 1: Scan Structure (High-Level)
Instead of reading all source code files, start by scanning the structure:
- Map directory trees (using `list_dir` or similar).
- Read main entries like `package.json`, `build.gradle`, `pom.xml`, or `tsconfig.json` to understand package boundaries.

### Step 2: Use Grep Search Smartly
Instead of open-reading files to locate definitions:
- Query for class/function signatures using `grep_search` with regex (e.g., `class Foo`, `function bar`, `interface Baz`).
- Use grep to identify import/require statements to map dependencies.

### Step 3: Construct the Graph
Represent the relations:
- **Imports**: `A -> B` (A imports B).
- **Calls**: `ClassA.methodX -> ClassB.methodY` (Method X calls Method Y).
- **Inheritance**: `ChildClass -> ParentClass` (Child inherits from Parent).

### Step 4: Trace the Flow
When resolving a request:
- Traverse the graph from the entry point (e.g., controller/handler) to the leaf nodes (e.g., repository/database driver).
- Focus file reads (`view_file`) ONLY on the nodes along the execution path.
