package com.example.engine

import com.example.model.Difficulty
import java.util.Random

object SudokuGenerator {

    private val random = Random()

    fun generate(difficulty: Difficulty): Array<IntArray> {
        val fullBoard = generateSolvedBoard()
        val puzzle = Array(9) { r -> fullBoard[r].clone() }

        val targetClues = difficulty.cluesCount
        val totalToRemove = 81 - targetClues

        // Create list of all cell positions and shuffle them
        val positions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                positions.add(Pair(r, c))
            }
        }
        positions.shuffle(random)

        var removed = 0
        for (pos in positions) {
            if (removed >= totalToRemove) break

            val r = pos.first
            val c = pos.second
            val backup = puzzle[r][c]

            puzzle[r][c] = 0

            // Check if still unique solution
            val count = SudokuSolver.countSolutions(puzzle, maxCount = 2)
            if (count != 1) {
                // Not unique! Restore cell
                puzzle[r][c] = backup
            } else {
                removed++
            }
        }

        return puzzle
    }

    fun generateSolvedBoard(): Array<IntArray> {
        val grid = Array(9) { IntArray(9) }

        // Fill diagonal 3x3 blocks first (they don't conflict with each other)
        for (i in 0 until 9 step 3) {
            fill3x3Block(grid, i, i)
        }

        // Solve remaining board with randomized candidates
        solveRandomized(grid)
        return grid
    }

    private fun fill3x3Block(grid: Array<IntArray>, startRow: Int, startCol: Int) {
        val numbers = (1..9).toList().shuffled(random)
        var idx = 0
        for (r in 0 until 3) {
            for (c in 0 until 3) {
                grid[startRow + r][startCol + c] = numbers[idx++]
            }
        }
    }

    private fun solveRandomized(grid: Array<IntArray>): Boolean {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (grid[r][c] == 0) {
                    val numbers = (1..9).toList().shuffled(random)
                    for (num in numbers) {
                        if (SudokuValidator.isValidPlacement(grid, r, c, num)) {
                            grid[r][c] = num
                            if (solveRandomized(grid)) return true
                            grid[r][c] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
}
