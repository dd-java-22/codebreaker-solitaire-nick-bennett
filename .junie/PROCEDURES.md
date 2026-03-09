## Discovery & Precedence (Mandatory)

**HALT:** If you have already read this file and aggregated guidelines during this session, **do not read this file again** and do not re-execute these steps.

1.  **Literal Path Access:** Access guidelines ONLY at these exact paths, and at locations explicity specified by the contents of these files. **Do not use `ls -R`** or any recursive search commands:
  
    - `./.junie/AGENTS.md` (Level 1: Project)
    -   `../.junie/AGENTS.md` (Level 2: Team)
    -   `../../.junie/AGENTS.md` (Level 3: Global)

2.  **Aggregation:** Read and combine instructions from the discovered files into a single set of active guidelines.

3.  **Precedence:** Level 1 (Highest) overrides Level 2; Level 2 overrides Level 3.

4.  **Verification:** State which specific files were found (e.g., "Guidelines aggregated from Level 1 and Level 2").

---

## Version Control Gate (Mandatory)

- Before any `git commit`, verify the specific formatting (Automatic vs. Prompted) required by the aggregated guidelines.