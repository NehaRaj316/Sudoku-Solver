package com.example.model

enum class Difficulty(val label: String, val cluesCount: Int) {
    EASY("Easy", 38),
    MEDIUM("Medium", 32),
    HARD("Hard", 28),
    EXPERT("Expert", 24),
    EVIL("Evil", 21)
}

enum class SolvingTechnique(val title: String) {
    NAKED_SINGLE("Naked Single"),
    HIDDEN_SINGLE("Hidden Single"),
    NAKED_PAIR("Naked Pair"),
    BOX_LINE_REDUCTION("Box / Line Reduction"),
    BACKTRACKING_DEDUCTION("Constraint Deduction"),
    INITIAL_CLUE("Initial Clue")
}

data class StepExplanation(
    val row: Int,
    val col: Int,
    val value: Int,
    val technique: SolvingTechnique,
    val explanation: String,
    val highlightedCells: Set<Pair<Int, Int>> = emptySet()
)

data class Cell(
    val row: Int,
    val col: Int,
    val value: Int = 0, // 0 = empty
    val isGiven: Boolean = false,
    val notes: Set<Int> = emptySet(),
    val isConflicted: Boolean = false,
    val isHighlightedValue: Boolean = false,
    val isSameRowOrColOrBox: Boolean = false,
    val isSelected: Boolean = false,
    val isStepTarget: Boolean = false
) {
    val isEmpty: Boolean get() = value == 0
}

enum class InputMode {
    NORMAL,
    PENCIL
}

enum class SolveStatus {
    IDLE,
    SOLVING,
    SOLVED,
    UNSOLVABLE,
    MULTIPLE_SOLUTIONS,
    INVALID_BOARD
}

data class Conflict(
    val type: String, // "row", "col", "box"
    val index: Int,
    val digit: Int,
    val cells: List<Pair<Int, Int>>
)
