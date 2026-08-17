---
name: git-workflow-automator
description: Guides the agent to automate git branch management, write conventional commits, draft pull request descriptions, and resolve local conflicts.
---

# Git Workflow Automator Skill

This skill guides the AI Agent to automate routine Git tasks and streamline the code integration lifecycle, ensuring clean histories and high-quality pull request documentation.

## When to Use This Skill

- When starting a new feature branch from main.
- When committing changes to ensure they follow coding standards and commit messages follow the Conventional Commits specification.
- When preparing to merge and drafting Pull Request (PR) or Merge Request (MR) descriptions.
- When resolving local merge conflicts.

## Key Automation Workflows

### 1. Feature Branch Initialization
- Check the current branch status (`git status`).
- Pull the latest changes from the remote tracking branch (`git pull origin main`).
- Create a new branch named according to standard naming conventions:
  - `feat/feature-name` for new features.
  - `fix/bug-name` for bug fixes.
  - `docs/document-name` for documentation.

### 2. Standardized Commit Generation
- Run `git diff` to review all staged and unstaged changes.
- Stage relevant changes (`git add <files>`).
- Generate commit messages following **Conventional Commits**:
  - Format: `<type>(<scope>): <short summary>`
  - Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`.
  - Example: `feat(auth): add JWT token refresh endpoint`

### 3. Autocomplete PR/MR Descriptions
Draft a pull request description template including:
- **Summary**: High-level explanation of the changes.
- **Related Issues**: Link to tickets/issues resolved.
- **Type of Change**: Feature, Fix, Refactoring, etc.
- **How Has This Been Tested**: List of automated tests run and manual verification steps.
- **Impact Analysis**: Any breaking changes or database migration requirements.

### 4. Conflict Resolution
If a merge conflict occurs during `git merge` or `git rebase`:
- Identify all files containing conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`).
- Inspect conflict blocks, determine the correct code state, and apply clean edits using surgical code replacement tools.
- Run tests to ensure resolved code builds successfully.
- Finalize the merge/rebase sequence (`git add` and `git commit` or `git rebase --continue`).
