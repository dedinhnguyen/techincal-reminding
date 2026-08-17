---
name: performance-profiler-analyser
description: Guides the agent to identify application bottlenecks, optimize database queries (e.g. N+1 query problems), and implement caching strategies.
---

# Performance Profiler Analyser Skill

This skill guides the AI Agent to identify performance bottlenecks, optimize heavy computation algorithms, eliminate slow database queries, and establish efficient caching strategies.

## When to Use This Skill

- When server response times are slow or memory spikes occur.
- When database queries take too long under load.
- When designing high-throughput APIs.

## Optimization Strategies

### 1. Database Query Optimization
- Identify and resolve **N+1 Query Problems** by implementing eager loading (e.g. `JOIN` fetch in ORMs).
- Identify missing indexes on database tables based on `WHERE`, `ORDER BY`, or `JOIN` predicates.
- Limit query output payload size (always request only required fields instead of `SELECT *`).

### 2. Caching Implementations
- Evaluate cacheable endpoints (e.g. settings, static resources, slow search queries).
- Implement multi-tier caching:
  - **In-Memory Caching** (short-term, inside application memory).
  - **Distributed Caching** (Redis, Memcached) for shared, session-persistent, or heavy database outcomes.
- Apply Cache-Aside pattern with proper TTL (Time-To-Live) configurations.

### 3. Asynchronous Task Processing
- Offload long-running tasks (e.g., email sending, report generation, image compression) from request-response lifecycles.
- Set up background workers using message brokers or queue libraries (RabbitMQ, BullMQ, Celery).
