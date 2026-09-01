# Repository Agent Rules

These rules apply to all automated coding agents and repository-wide maintenance work.

## Repository-wide cleanup and refactoring policy

When asked to audit, clean up, optimize, simplify, or refactor this project, perform a complete repository-wide review rather than limiting the work to recently changed files or obvious hotspots.

The primary goal is to make the codebase as small, simple, clean, and maintainable as reasonably possible while preserving 100% of the current application functionality, behavior, UI/UX, public interfaces, data formats, edge-case behavior, and documented capabilities.

### Required scope

Review the entire repository, including:

- application source code;
- tests;
- resources;
- build scripts;
- Gradle configuration;
- dependencies;
- CI/CD workflows;
- repository configuration;
- documentation;
- platform-specific integration points.

Before changing code, establish the actual architecture and the complete set of current application features and behaviors.

### Remove only when proven unnecessary

Actively look for and remove, when safe:

- dead or unreachable code;
- unused functions, classes, methods, variables, constants, types, interfaces, imports, files and resources;
- obsolete legacy code that no longer participates in application behavior;
- temporary workarounds that are no longer necessary;
- duplicated or near-duplicated code;
- unnecessary abstraction layers;
- unnecessary wrappers and helpers;
- redundant data conversions;
- redundant intermediate objects or state;
- defensive checks whose conditions are already guaranteed by types, architecture or earlier validation;
- repeated validation of the same condition;
- redundant fallbacks;
- unused dependencies and development dependencies;
- obsolete configuration options;
- unused feature flags;
- commented-out old code;
- obsolete TODO/FIXME items;
- stale compatibility branches that are no longer required by the supported platform range.

Do not treat a missing textual reference as sufficient proof that code is unused. Before removing anything, account for indirect usage through callbacks, events, reflection, dynamic loading, Android/framework conventions, resources, manifests, build configuration, CI, serialization, platform entry points and other non-obvious references.

When uncertain whether code is safe to remove, preserve it until its lack of use is proven.

### Simplify when objectively better

Look for opportunities to:

- shorten code without reducing readability;
- simplify control flow;
- reduce the number of states and branches;
- merge code that represents the same responsibility;
- replace custom code with standard Kotlin, Android or library facilities where that clearly reduces complexity;
- remove unnecessary loops, passes, allocations, copies and transformations;
- compute or transform data once instead of repeatedly;
- eliminate repeated business logic;
- centralize genuinely shared logic when doing so reduces total code and complexity;
- reduce coupling between components;
- remove premature generalization and architecture that exists only for hypothetical future requirements.

Do not change code merely to make it different or stylistically preferable. A refactor must provide an objective reduction in duplication, complexity, risk, maintenance burden or runtime cost.

Do not optimize for minimum line count at the expense of clarity. The target is minimum necessary complexity, not code golf.

### Architecture review

Check whether:

- historical layers or components are still necessary;
- abstractions are disproportionate to the actual problem;
- classes or modules can be safely merged or removed;
- there is premature generalization;
- there is architecture built for future scenarios that provide no current value;
- the complexity of the implementation matches the actual complexity of the application.

Prefer the simplest architecture that fully supports the current product.

### Performance review

Optimize performance only where there is practical value. Look for:

- repeated calculations;
- redundant reads;
- unnecessary allocations;
- unnecessary recomposition/re-render/rebuild/recompute;
- repeated parsing or transformation of unchanged data;
- inefficient data structures;
- work that can safely be performed once instead of repeatedly.

Do not introduce micro-optimizations that make the code harder to understand without an obvious or measurable benefit.

### Dependency review

For every dependency, determine whether it is actually used and justified.

- Remove unused dependencies.
- Remove duplicate libraries serving the same purpose.
- Prefer standard platform/language functionality when it clearly replaces a dependency with substantially less overall complexity.
- Do not replace a well-maintained library with custom code unless there is a concrete benefit in simplicity, safety, size or maintenance.

### Functional preservation is mandatory

After refactoring, the application must behave the same as before unless a separately requested bug fix explicitly requires a behavior change.

Do not:

- remove user-facing features;
- change existing UX;
- change business logic;
- change public APIs or contracts without absolute necessity;
- change persisted or exchanged data formats;
- change edge-case behavior intentionally;
- reduce functionality in order to reduce code;
- perform a large rewrite merely because another architecture appears cleaner;
- add unrelated features;
- add complexity only to satisfy generic "best practices".

The default decision rule is:

- if code can be safely deleted, delete it;
- if it can be objectively simplified, simplify it;
- if duplicate responsibilities can be safely merged, merge them;
- if a change only makes the implementation different rather than better, leave it unchanged.

### Required workflow

For repository-wide cleanup or deep refactoring:

1. Inspect the entire repository first.
2. Determine the current architecture and all existing application features and behaviors.
3. Build an internal list of candidates for deletion, merging, simplification and optimization.
4. Prove that each removal is safe, including indirect and framework-driven usage.
5. Make changes in small, logical groups.
6. After each significant group, run the relevant available tests, lint, build, static analysis and project-specific checks.
7. If critical behavior lacks sufficient coverage for a risky refactor, add the minimum regression tests required to capture existing behavior before changing it.
8. At the end, run the complete available validation suite, including a clean build where practical.
9. Perform a second full pass over the already-refactored repository and look again for dead code, duplication, unnecessary abstractions, redundant checks, unused dependencies and residual legacy code.

Pay particular attention to patterns such as:

- repeated null or bounds checks already guaranteed by earlier logic;
- repeated validation of already validated data;
- unnecessary try/catch blocks;
- wrapper-to-wrapper chains;
- unnecessary DTO/model conversions;
- redundant intermediate state;
- stale compatibility code left behind by previous refactors.

### Comments

Keep comments to the minimum necessary.

- Comments in source code must be in English.
- Remove stale, redundant and obvious comments.
- Do not add comments that merely restate what the code does.
- Keep comments only when they explain a non-obvious reason, constraint, workaround or important contract.

### Final report

After repository-wide cleanup or deep refactoring, report:

1. what was removed;
2. what was merged;
3. what was simplified;
4. which dependencies were removed;
5. which potential legacy components were found;
6. what was intentionally left unchanged and why;
7. which tests, builds, lint and static-analysis checks were run;
8. which areas could not be safely optimized without additional information or test coverage;
9. reliable before/after statistics when available, such as file count, source lines, dependency count, test count and production artifact size.

Recommendations alone are not sufficient when safe improvements can be applied directly. Implement the safe improvements in the repository.
