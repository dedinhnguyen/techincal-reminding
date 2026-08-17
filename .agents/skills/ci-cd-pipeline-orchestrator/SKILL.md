---
name: ci-cd-pipeline-orchestrator
description: Guides the agent to build and debug automated CI/CD pipeline configurations (GitHub Actions, GitLab CI) for testing, linting, and cloud deployments.
---

# CI/CD Pipeline Orchestrator Skill

This skill guides the AI Agent to design, write, and troubleshoot Continuous Integration and Continuous Deployment (CI/CD) pipelines. This ensures every code push automatically triggers code style checks, test suites, container builds, and deployment sequences.

## When to Use This Skill

- When setting up automated tests on push/pull requests.
- When configuring deployment configurations (e.g. Vercel, AWS S3, ECS, Kubernetes).
- When resolving broken pipelines (syntax or integration failures in YAML files).

## Workflow Integration Guidelines

### 1. Structure the Pipeline Stages
Implement standard workflow stages:
- **Lint**: Run code formatting and style checks (e.g., ESLint, Prettier, Black).
- **Test**: Execute the test suites and output coverage reports.
- **Build**: Compile assets or build container images (Docker).
- **Deploy**: Push build artifacts to hosting providers (Vercel, Netlify, AWS, GCP, Azure).

### 2. Formulate YAML Configurations
- **GitHub Actions**: Create `.github/workflows/main.yml` with actions triggers on `push` or `pull_request` to default branches. Use caching features for package managers (npm, pip, cargo) to speed up builds.
- **GitLab CI/CD**: Create `.gitlab-ci.yml` defining stages, jobs, and runner images.
- **Secret Management**: Reference environment variables using secure vault/secret repositories (e.g., `${{ secrets.DB_PASSWORD }}`) to prevent credentials leaks.
