---
name: api-docs-generator
description: Enables the agent to automatically scan source code routes, extract endpoint specifications, and generate or update Swagger/OpenAPI documentation.
---

# API Docs Generator Skill

This skill guides the AI Agent to automatically inspect back-end route controllers, extract endpoint parameters, request/response formats, and generate or update Swagger/OpenAPI spec files (YAML/JSON) without manual documentation writing.

## When to Use This Skill

- When creating new REST/GraphQL endpoints.
- When changing existing API payloads (parameters, headers, responses).
- When preparing API documentation for frontend developers or external integrators.

## Generation Workflow

### Step 1: Scan and Discover Endpoints
- Search the codebase for route handlers and controller decorators (e.g. `@Get`, `@Post`, `app.get`, `app.post`).
- Retrieve files defining endpoints and DTOs (Data Transfer Objects).

### Step 2: Extract Specification Details
Inspect endpoint methods to extract:
- **Path & Method**: (e.g., `POST /api/v1/auth/login`).
- **Query / Path Parameters**: Type, validation constraints (e.g., `email` must be string and valid email format).
- **Request Body**: JSON schema representing the payload model.
- **Headers**: Required headers (e.g. `Authorization: Bearer <token>`).
- **Response Payloads**: Success codes (e.g., `200 OK`, `201 Created`) and error states (e.g., `400 Bad Request`, `401 Unauthorized`).

### Step 3: Generate OpenAPI Spec (YAML/JSON)
- If an OpenAPI spec file (like `swagger.yaml` or `openapi.json`) already exists, locate it.
- Append or update the specific paths and components inside the spec using precise file replacement tools.
- If no spec exists, initialize a new `openapi.yaml` at the project root with basic info, version, and the first endpoint spec.

### Step 4: Validate Spec Accuracy
- Verify that the generated OpenAPI structure conforms to official specifications.
- Ensure all component references (`$ref`) resolve correctly to existing schemas.
