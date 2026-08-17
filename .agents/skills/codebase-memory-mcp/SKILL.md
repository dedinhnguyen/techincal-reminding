---
name: codebase-memory-mcp
description: Teaches the agent to use MCP memory servers (knowledge graph, entities, relations) to store and retrieve architecture, design decisions, and database schemas, maintaining context across sessions while saving tokens.
---

# Codebase Memory MCP Skill

This skill teaches the agent how to leverage MCP memory servers to persist key facts, database schemas, architectural patterns, and structural details about the codebase. Storing this information in a persistent memory graph prevents context bloating and reduces input token usage on subsequent prompts.

## When to Use This Skill

- When a codebase has custom design patterns or specific architectural constraints that the agent needs to remember.
- When working on long-running tasks across multiple sessions where context needs to be preserved.
- When you want to document database schemas, endpoints, or API boundaries once and retrieve them on-demand.

## Key MCP Memory Operations

If an MCP memory server is available, the agent should interact with it using these patterns:

1. **Write Memory (Storing Context)**:
   - Identify key entities (e.g., `UserTable`, `AuthService`, `PaymentFlow`).
   - Store their definitions, structure, and constraints.
   - Example: Create an entity `AuthService` with observations about how JWT token generation and verification works.

2. **Establish Relations**:
   - Link entities to build a knowledge graph.
   - Example: Connect `AuthService` -> `uses` -> `UserTable` to record dependency.

3. **Search & Retrieve Memory**:
   - Before researching the codebase from scratch for a known component, query the memory server first: `search_memory` or `read_memory`.
   - Retrieve stored schemas or design summaries directly, saving tokens compared to reading the source files again.

## Fallback (Offline/No-Server Mode)

If an active MCP memory server is not running on the system, the agent should maintain a local markdown-based memory store in the project:
- **Location**: `docs/DOC_GENERATED/` or `.agents/memory/`
- **Tập tin**: `knowledge_graph.md` or `codebase_notes.md`
- **Tác vụ**: Append crucial design decisions and architectural states to this file, then reference it using `view_file` instead of parsing all source code directories.
