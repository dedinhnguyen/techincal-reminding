---
name: save-token
description: Guides the agent on specific workflows and techniques to minimize token consumption and quota usage in any software development task, across all LLM models.
---

# Save Token Skill

This skill provides a set of actionable workflows, coding practices, and tool usages to drastically reduce token consumption (both input and output) and maximize API quota efficiency during AI-driven software development.

## When to Use This Skill

- Whenever the user asks to "optimize tokens", "save quota", or "work efficiently".
- In large projects with high file counts and deep directory structures.
- When working with models that have strict rate limits or high costs per token.

## Developer Workflows for Token Optimization

### 1. Planning Mode First
- Before changing any code, **always** enter Planning Mode if the task involves more than 2 files or contains architectural ambiguity.
- Drafting an `implementation_plan.md` helps align requirements with the user *before* writing code. This avoids the "trial-and-error" loop, which is the single largest consumer of tokens.

### 2. Context Truncation & Ignore Lists
- Ensure that unnecessary directories are ignored by the IDE's indexing or git. 
- Create or verify a `.gitignore` containing:
  - `node_modules/`, `bower_components/`, `jspm_packages/`
  - `.git/`
  - `build/`, `dist/`, `.next/`, `out/`
  - `.cache/`, `tmp/`, `temp/`
  - Large media files, database dumps (`.sql`, `.sqlite`, `.db`).
- Ignoring these directories prevents the agent from scanning them during recursive operations.

### 3. Smart Code Replacement
- Avoid replacing large blocks of code when minor edits will suffice.
- **Do not** use `write_to_file` to replace a file if you only want to change a few lines.
- Master the use of `replace_file_content` for single contiguous changes, and `multi_replace_file_content` for multiple disjointed changes.
- Ensure `TargetContent` is unique and as small as possible while uniquely identifying the lines to edit.

### 4. Efficient Code Navigation
- Use `grep_search` with strict queries and file filters (e.g., `Includes=["*.ts"]`) instead of opening multiple files to trace a symbol.
- Use `codegraph` and `codebase-memory-mcp` skills to map paths rather than reading code files step-by-step.
- If you must read a file, read only the target section:
  ```json
  {
    "AbsolutePath": "/path/to/large_file.py",
    "StartLine": 120,
    "EndLine": 150
  }
  ```

### 5. Silent Background Execution
- When running commands (e.g., builds, test suites) in the background via `run_command`:
  - Do not call `manage_task status` repeatedly.
  - Set a `schedule` timer or let the system wake the agent up on completion. This prevents chat history inflation.
