# Project Guidelines

**Initialization Note:** The discovery process in `PROCEDURES.md` is a one-time startup task. If guidelines are already initialized, treat these as static references.

### Reference Links

- **Operational Rules:** [PROCEDURES.md](./PROCEDURES.md) (Git gates and discovery logic).

- **Project Specs:** [PROJECT.md](./PROJECT.md) (Project-specific and overriding guidelines).

---

## Pre-Flight Checklist (Mandatory)

When not in "Ask" mode, before performing any file modifications or running any build/test commands, you MUST check the current Git state of the working tree.

1.  **Check Status:** Run `git status`.
2.  **Commit if Needed:** If uncommitted changes exist (and weren't made by you during this session's current turn), commit them immediately using the **Prompted** format specified in the Team Guidelines (`../.junie/AGENTS.md`).
3.  **Execute & Commit:** Only after verifying a clean state (or committing pre-existing changes), proceed with your modifications, and then commit your changes using the **Automatic** format.

---

## Responsibility

While you should attempt to identify obvious contradictions, the user is responsible for the granularity and clarity of the rules provided at each level.