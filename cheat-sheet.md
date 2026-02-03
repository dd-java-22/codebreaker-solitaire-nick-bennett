# AI architect cheat sheet: The _Codebreaker Solitaire_ client

This guide is designed to help you use an AI as a design partner. Your goal is to build a robust, professional architecture for a Java-based web service client. 

**Strict rule:** Do not let the AI write Java implementation code for you. You are prompting for specifications, class signatures, and architectural logic.

---

## 0. Setting the stage (The system prompt)

Start your session by defining the AI’s role. This prevents it from skipping straight to coding and forces it to focus on the "why" behind the design.

> **System prompt:**
> "Act as a senior software architect. We are designing a Java-based client for a RESTful API. Your goal is to help me articulate requirements, apply separation of concerns (SoC), and define a class hierarchy. 
> 
> **Constraints:**
> 1. Do not write implementation code.
> 2. Use pseudo-code or class signatures only.
> 3. Focus on a design that can migrate from console to JavaFX and Android.
> 4. If I ask for code, remind me we are in the design phase."

---

## Phase 1: Requirements and user stories

Focus on the "what." How does the game behave from the user's perspective?

* **Game loop:** "Based on the Codebreaker Solitaire API documentation, describe the lifecycle of a single game from creation to completion."
* **Unicode flexibility:** "The API allows for any Unicode characters in the pool and guess. How should the user interface explain this flexibility to the player?"
* **Error scenarios:** "Identify five technical or user-input errors (e.g., 404, 400, invalid characters) and describe how the application should react to each."

---

## Phase 2: Technical specifications and "Separation of Concerns"

Focus on the "where." How do we divide responsibilities using separation of concerns?

* **Library strategy:** "Compare the native `java.net.http.HttpClient` with `OkHttp`, and with higher-level libraries such as Retrofit2. Which one provides better long-term support for a project moving to Android?"
* **JSON mapping:** "The API uses JSON. Should our domain models be identical to the JSON structure, or should we create DTOs (Data Transfer Objects) to protect our internal logic from API changes?"
* **Defining the service:** "What are the essential methods for a `CodebreakerService` interface that handles all API communication? Ensure the methods are UI-agnostic."

---

## Phase 3: The bird’s-eye view (Class design)

Focus on the "structure." Define the nouns and verbs of your system.

* **Domain modeling:** "Identify the core classes needed. 
* **Class signatures:** "Provide the fields and method names for the `Game`, `Guess`, and `Score` classes. Do not include method bodies."
* **Visualizing logic:** "Generate a Mermaid.js class diagram that shows the relationship between the `TerminalUI`, the `GameController`, and the `RestCodebreakerService`."

---

## Phase 4: Future-proofing for GUI and mobile

Focus on the "tomorrow." How does this design survive a platform change?

* **Threading and async:** "In a console app, we can block the thread while waiting for an API. In JavaFX and Android, we cannot. How should our service methods be structured (e.g., using `CompletableFuture` or types from a third-party library such as RxJava3) to handle this?"
* **Decoupling:** "If I replace the `TerminalUI` with a JavaFX view, which parts of my current design will require zero changes? Why?"
* **Android constraints:** "What specific Android security or networking requirements should I consider now to ensure my `RestCodebreakerService` works on a mobile device later?"

---

## The “code-killer” prompts

Use these if the AI starts dumping blocks of Java code to keep the exercise focused on design:
* "Stop. I do not want the code. Explain the logic flow for that method instead."
* "I am focusing on architecture. Provide a bulleted list of responsibilities for this class rather than the implementation."
* "Represent that logic as a sequence diagram instead of code."