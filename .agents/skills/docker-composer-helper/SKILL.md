---
name: docker-composer-helper
description: Guides the agent to write optimized Dockerfiles, docker-compose.yml files, configure volumes and networks, and debug container runtime issues.
---

# Docker Composer Helper Skill

This skill guides the AI Agent in automating containerization workflows, writing production-ready Dockerfiles, configuring multi-container orchestrations with Docker Compose, and troubleshooting container logs.

## When to Use This Skill

- When containerizing a new application or service.
- When optimizing existing Docker images to reduce bundle size (using multi-stage builds).
- When configuring dev/prod environments with docker-compose.
- When container builds fail or crash loops occur.

## Key Containerization Guidelines

### 1. Optimized Dockerfile Construction
- Use official minimal base images (e.g. `node:alpine`, `python:3.10-slim`, `golang:1.20-alpine`).
- Leverage **Multi-Stage Builds** to separate compile-time tools from execution-time dependencies.
- Minimize layers by chaining commands with `&&` (e.g., `RUN apt-get update && apt-get install -y ... && rm -rf /var/lib/apt/lists/*`).
- Avoid running containers as `root` user for security compliance.

### 2. Multi-Container Orchestration (Docker Compose)
- Define `docker-compose.yml` specifying:
  - Container dependency order using `depends_on` with healthchecks.
  - Isolated bridge networks to allow secure communication between containers.
  - Persistent volume mounts for databases (e.g. Postgres, Redis).
  - Environment variable injection via `.env` files.

### 3. Log Diagnostics & Troubleshooting
- Use CLI commands to inspect crashes:
  - Check container status: `docker ps -a`
  - Fetch logs: `docker logs <container_name>`
  - Inspect state: `docker inspect <container_name>`
- Extract error stack traces from logs and match them against code modifications to apply code hotfixes.
