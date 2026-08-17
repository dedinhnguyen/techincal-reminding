---
name: i18n-translator-manager
description: Guides the agent to extract hardcoded UI strings, structure locale files (JSON/YAML), and manage multi-language translation keys safely.
---

# i18n Translator Manager Skill

This skill teaches the AI Agent how to internationalize applications. It guides the extraction of hardcoded UI strings, formatting locale dictionary files (JSON, YAML), and maintaining consistent translation keys across multiple languages.

## When to Use This Skill

- When adding multi-language support (i18n) to a web or mobile application.
- When extracting hardcoded string literals into localization hooks (e.g. `t('key')`).
- When introducing a new target language locale dictionary.

## Localization Steps

### Step 1: Scan and Extract String Literals
- Identify hardcoded strings in template or view files (JSX, TSX, HTML, Vue, etc.).
- Replace raw text with internationalization methods or keys (e.g., `<p>Hello</p>` -> `<p>{t('common.hello')}</p>`).

### Step 2: Format Locale Dictionaries
- Structure translation key-value stores under `locales/` or `i18n/` directories.
- Keep translation keys nested logically (e.g., `dashboard.buttons.save`).
- Ensure all key structures are strictly identical across translation files (e.g., `en.json`, `vi.json`, `ja.json`).

### Step 3: Autocomplete Translations
- Translate locale string values into target languages while preserving placeholder formats (e.g. `Welcome, {{name}}`).
- Maintain technical terms, brands, and symbols correctly without modifications.
