# Sudoku Solver & Player

An interactive, feature-packed Android application built with **Jetpack Compose** and **Material 3**, featuring a tactile **claymation aesthetic** with soft 3D plasticine bevels, engraved grid lines, and physics-based press animations.

---

## 🌟 What Was Built

### 1. High-Performance Sudoku Engine & Solver (`com.example.engine`)

* **Constraint Backtracking Solver with MRV:** Computes full solutions in milliseconds, even on extreme puzzles.
* **Solution Uniqueness & Validity Checker:** Distinguishes between unique solutions, multi-solution boards, and invalid placements.
* **Step-by-Step Logic Explainer:** Analyzes the board for human-understandable solving steps (Naked Singles, Hidden Singles across rows/columns/boxes) with detailed explanatory notes.
* **Visual Animated Solver:** Plays through the recursive solver trace visually on the board with adjustable speeds (Slow, Normal, Fast).
* **On-Device Puzzle Generator:** Generates fresh puzzles on demand across all difficulty tiers (**Easy**, **Medium**, **Hard**, **Expert**, **Evil**) guaranteeing unique solutions.
* **Curated Classics:** Pre-loaded with famous boards, from beginner warmup grids to Arto Inkala's hardest AI Sudoku.

### 2. Full-Featured Interactive Board & Keypad (`com.example.ui.components`)

* **Tactile Clay 9x9 Board:** Crafted with soft plasticine styling, selected cell highlighting, same-digit match highlighting, row/column/box crosshairs, and conflict alert badges.
* **Candidate Notes (Pencil Mode):** Supports placing and removing 1–9 candidate notes in mini 3x3 sub-grids inside each cell, with auto-clean logic when a number is placed.
* **Interactive Keypad:** Numbers 1 through 9 with live frequency counters, Erase/Backspace, Notes mode toggle, and an Undo/Redo stack.

### 3. Core Application Screens (`com.example.ui.screens`)

* **Solver Mode:** Input any custom board from a newspaper or book; validate instantly, solve with one tap, step through the logic, or watch the animated solver.
* **Play Mode:** Complete gameplay experience featuring a live timer (play/pause), mistake counter, smart hint button, and victory celebration dialog.
* **Library & History:** Local Room database integration (`SavedPuzzleEntity` & `SavedPuzzleDao`) to save boards, bookmark favorites, and track history.
* **Solving Guide / Techniques:** Comprehensive interactive manual explaining fundamental rules, Naked Singles, Hidden Singles, Pencil Marks, and more.

### 4. Visual Polish & Branding

* **Clay Animation Aesthetic:** Features soft 3D bevels, clay squish physics upon interaction, and a custom stop-motion clay mascot hero banner.
* **Theme Support:** Polished deep indigo and slate theme supporting both Light and Dark modes.
