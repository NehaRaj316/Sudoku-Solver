package com.example.engine

import com.example.model.Difficulty

data class PresetPuzzle(
    val id: String,
    val title: String,
    val difficulty: Difficulty,
    val cluesString: String, // 81 chars, '0' or '.' for empty
    val description: String
) {
    fun toGrid(): Array<IntArray> {
        val grid = Array(9) { IntArray(9) }
        val clean = cluesString.replace(".", "0").filter { it.isDigit() }
        for (i in 0 until 81.coerceAtMost(clean.length)) {
            val r = i / 9
            val c = i % 9
            grid[r][c] = clean[i] - '0'
        }
        return grid
    }
}

object PuzzleLibrary {
    val presets: List<PresetPuzzle> = listOf(
        PresetPuzzle(
            id = "easy_1",
            title = "Gentle Breeze",
            difficulty = Difficulty.EASY,
            cluesString = "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
            description = "The classic textbook starter puzzle with clear intersections and naked singles."
        ),
        PresetPuzzle(
            id = "easy_2",
            title = "Morning Coffee",
            difficulty = Difficulty.EASY,
            cluesString = "000260701680070090190004500820100040004602900050003028009300074040050036703018000",
            description = "Great for quick morning practice with clear rows and column intersections."
        ),
        PresetPuzzle(
            id = "med_1",
            title = "The Lattice",
            difficulty = Difficulty.MEDIUM,
            cluesString = "100489006730000040000001295007120600500703008006095700914600000020000037800512004",
            description = "Balanced symmetry with hidden singles in boxes and columns."
        ),
        PresetPuzzle(
            id = "med_2",
            title = "Silver Spiral",
            difficulty = Difficulty.MEDIUM,
            cluesString = "000000085000210009960080100500800016000000000890006007009070052300054000480000000",
            description = "Requires careful pencil mark notes across central blocks."
        ),
        PresetPuzzle(
            id = "hard_1",
            title = "The Labyrinth",
            difficulty = Difficulty.HARD,
            cluesString = "000600000000000062089000100000006000002800005010000000004010600008700000000000350",
            description = "Challenging sparse layout requiring multi-step logical eliminations."
        ),
        PresetPuzzle(
            id = "hard_2",
            title = "Obsidian Gate",
            difficulty = Difficulty.HARD,
            cluesString = "000000012000000003002300400001800005060070800000009000008500000900040500470006000",
            description = "Sparse clues with locked candidates and box-line reductions."
        ),
        PresetPuzzle(
            id = "expert_1",
            title = "Golden Ratio",
            difficulty = Difficulty.EXPERT,
            cluesString = "000700000100000000000430200000000006000509000000000418000081000002000050040000300",
            description = "Expert test of patience requiring candidate chains."
        ),
        PresetPuzzle(
            id = "evil_inkala",
            title = "Arto Inkala's AI Sudoku",
            difficulty = Difficulty.EVIL,
            cluesString = "800000000003600000070090200050007000000045700000100030001000068008500010090000400",
            description = "Created by Finnish mathematician Arto Inkala, widely known as one of the world's most difficult puzzles."
        )
    )
}
