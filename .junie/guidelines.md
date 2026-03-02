# Guidelines

## Version control

### Automatic commits

Whenever your execution plan, in response to a prompt from me, requires **you** to perform file creation, modification, or deletion, you must do the following (do not follow these steps if the execution plan consists only of read-only operations or if the only file changes are those already present in the workspace before you begin):

1. Before executing the plan, execute `git add` and `git commit` on the set of uncommitted changes (as discovered by `git status`), using this format for the commit message (note the blank line separating the first line from the rest of the message):

    > Commit by Junie: {short summary}
    > 
    > {long summary}

    - Replace {short summary} with a generated abbreviated summary of the changes, limited so that the total length of the first line does not exceed 72 characters.

    - Replace {long summary} with a generated summary of the changes, without regard for line length or overall length. Separate multiple paragraphs by blank lines.

    If there are no unsaved changes, then this item may be skipped.

2. Execute plan as usual.

3. **If the changes are within a Git repository**, commit the changes made by you, using this format for the commit message (note the blank lines separating message components):

    > Change by Junie: {short summary}
    > 
    > {long summary}
    > 
    > Prompt: {prompt} 

    - Replace {short summary} with a generated abbreviated summary of the changes, limited so that the total length of the first line does not exceed 72 characters.

    - Replace {long summary} with a generated summary of the changes, without regard for line length or overall length. Separate multiple paragraphs by blank lines.

    - Replace {prompt} with my prompt that resulted in the changes.

    **If the workspace or directory is not part of a Git repository, this step should be skipped.**

**Important:** Use this format ONLY for commits that are part of your automated execution plan. If the user explicitly asks you to commit, use the **Prompted commits** format below.

### Prompted commits

When I prompt you to commit (e.g., "Please commit my changes"):

1. Check (via `git status`) for uncommitted changes.

2. If there are uncommitted changes, `git add` and `git commit` them, using this format for the commit message (note the blank line separating the first line from the rest of the message and the **absence** of a `Prompt:` block):

    > Commit by Junie: {short summary}
    >
    > {long summary}

    - Replace {short summary} with a generated abbreviated summary of the changes, limited so that the total length of the first line does not exceed 72 characters.

    - Replace {long summary} with a generated summary of the changes, without regard for line length or overall length. Separate multiple paragraphs by blank lines.

    This section applies regardless of who made the changes or when they were made. If there are no uncommitted changes, then this item may be skipped.

## Android projects

For Android projects, check the `build.gradle.kts` script in the Android-specific Gradle subprojects (e.g., `app`):

### Dependency injection

If the Hilt plugin is included in the `plugins` configuration block, then all `Application` subclasses created by you must include the `@HiltAndroidApp` annotation; all `Activity` and `Fragment` subclasses created by you must include the `@AndroidEntryPoint` annotation; all viewmodel classes created by you must be subclasses of `ViewModel` (_not_ `AndroidViewModel`), must be annotated with the `@HiltViewModel` annotation, and must have a constructor annotated with `@Inject`.

### View binding

If `viewBinding` is enabled, then:

- All `Activity` and `Fragment` subclasses created by you must use the `inflate` method of the viewbinding class associated with the layout resource, and assign the result to a `private` field named `binding`, of the viewbinding class type.

- After inflating the layout resource, the `onCreate` method of all `Activity` subclasses created by you must include the statement `setContentView(binding.getRoot());`.

- After inflating the layout resource, the `onCreateView` method of all `Fragment` subclasses created by you must end with the statement `return binding.getRoot();`.

- To avoid memory leaks, the `onDestroyView` method of all `Fragment` subclasses created by you must include the statement `binding = null;`.

### Navigation

When creating or modifying navigation resources, in Android projects or subprojects where the navigation safeargs plugin is applied:

- All `<fragment>` elements that you create must use the lower_snake_case form of the fragment subclass name (including the "Fragment" suffix, if present in the class name) for the `android:id` attribute---that is, `@+id/{fragment_class_name}`, where `{fragment_class_name}` is replaced with the `lower_snake_case` form of the fragment subclass name.

- Any `<action>` elements that you create must have an `android:id` attribute value of the form `@+id/navigate_to_{destination_class_name}`, where `{destination_class_name}` is replaced with the `lower_snake_case` form of the destination fragment subclass name.

- When _modifying_ an existing `<fragment>` or `<action>` element, do not modify the `android:id` attribute value, _even if it violates the above guidelines_, unless I explicitly ask you to do so in the prompt.