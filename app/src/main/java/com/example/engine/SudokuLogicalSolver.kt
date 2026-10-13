package com.example.engine

import com.example.model.SolvingTechnique
import com.example.model.StepExplanation

object SudokuLogicalSolver {

    fun findNextStep(grid: Array<IntArray>): StepExplanation? {
        if (!SudokuValidator.isValidBoard(grid)) return null

        // 1. Check for Naked Singles
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (grid[r][c] == 0) {
                    val candidates = SudokuValidator.getCandidates(grid, r, c)
                    if (candidates.size == 1) {
                        val value = candidates.first()
                        val related = mutableSetOf<Pair<Int, Int>>()
                        for (i in 0 until 9) {
                            if (grid[r][i] != 0) related.add(Pair(r, i))
                            if (grid[i][c] != 0) related.add(Pair(i, c))
                        }
                        val startR = (r / 3) * 3
                        val startC = (c / 3) * 3
                        for (br in startR until startR + 3) {
                            for (bc in startC until startC + 3) {
                                if (grid[br][bc] != 0) related.add(Pair(br, bc))
                            }
                        }

                        return StepExplanation(
                            row = r,
                            col = c,
                            value = value,
                            technique = SolvingTechnique.NAKED_SINGLE,
                            explanation = "Cell R${r + 1}C${c + 1} has only one possible candidate: $value. Every other digit (1-9) is ruled out by intersecting numbers in its row, column, or 3x3 block.",
                            highlightedCells = related
                        )
                    }
                }
            }
        }

        // 2. Check for Hidden Singles in Rows
        for (r in 0 until 9) {
            for (digit in 1..9) {
                val possibleCols = mutableListOf<Int>()
                for (c in 0 until 9) {
                    if (grid[r][c] == 0 && SudokuValidator.isValidPlacement(grid, r, c, digit)) {
                        possibleCols.add(c)
                    }
                }
                if (possibleCols.size == 1) {
                    val c = possibleCols.first()
                    val related = (0 until 9).map { Pair(r, it) }.toSet()
                    return StepExplanation(
                        row = r,
                        col = c,
                        value = digit,
                        technique = SolvingTechnique.HIDDEN_SINGLE,
                        explanation = "In Row ${r + 1}, digit $digit can only fit in column ${c + 1}. All other empty cells in this row have conflicts elsewhere.",
                        highlightedCells = related
                    )
                }
            }
        }

        // 3. Check for Hidden Singles in Columns
        for (c in 0 until 9) {
            for (digit in 1..9) {
                val possibleRows = mutableListOf<Int>()
                for (r in 0 until 9) {
                    if (grid[r][c] == 0 && SudokuValidator.isValidPlacement(grid, r, c, digit)) {
                        possibleRows.add(r)
                    }
                }
                if (possibleRows.size == 1) {
                    val r = possibleRows.first()
                    val related = (0 until 9).map { Pair(it, c) }.toSet()
                    return StepExplanation(
                        row = r,
                        col = c,
                        value = digit,
                        technique = SolvingTechnique.HIDDEN_SINGLE,
                        explanation = "In Column ${c + 1}, digit $digit can only fit in row ${r + 1}. No other cell in this column allows a $digit.",
                        highlightedCells = related
                    )
                }
            }
        }

        // 4. Check for Hidden Singles in 3x3 Boxes
        for (boxIdx in 0 until 9) {
            val startR = (boxIdx / 3) * 3
            val startC = (boxIdx % 3) * 3
            for (digit in 1..9) {
                val possibleCells = mutableListOf<Pair<Int, Int>>()
                for (r in startR until startR + 3) {
                    for (c in startC until startC + 3) {
                        if (grid[r][c] == 0 && SudokuValidator.isValidPlacement(grid, r, c, digit)) {
                            possibleCells.add(Pair(r, c))
                        }
                    }
                }
                if (possibleCells.size == 1) {
                    val (r, c) = possibleCells.first()
                    val boxCells = mutableSetOf<Pair<Int, Int>>()
                    for (br in startR until startR + 3) {
                        for (bc in startC until startC + 3) {
                            boxCells.add(Pair(br, bc))
                        }
                    }
                    return StepExplanation(
                        row = r,
                        col = c,
                        value = digit,
                        technique = SolvingTechnique.HIDDEN_SINGLE,
                        explanation = "In 3x3 Block ${boxIdx + 1}, digit $digit can only be placed at R${r + 1}C${c + 1}.",
                        highlightedCells = boxCells
                    )
                }
            }
        }

        // 5. Fallback: Constraint deduction using full solver
        val solveResult = SudokuSolver.solve(grid)
        if (solveResult.solvedGrid != null) {
            // Find cell with fewest candidates
            var minCand = 10
            var bestR = -1
            var bestC = -1
            for (r in 0 until 9) {
                for (c in 0 until 9) {
                    if (grid[r][c] == 0) {
                        val cands = SudokuValidator.getCandidates(grid, r, c)
                        if (cands.size < minCand) {
                            minCand = cands.size
                            bestR = r
                            bestC = c
                        }
                    }
                }
            }
            if (bestR != -1 && bestC != -1) {
                val targetVal = solveResult.solvedGrid[bestR][bestC]
                return StepExplanation(
                    row = bestR,
                    col = bestC,
                    value = targetVal,
                    technique = SolvingTechnique.BACKTRACKING_DEDUCTION,
                    explanation = "Advanced constraint deduction: Cell R${bestR + 1}C${bestC + 1} must be $targetVal to maintain a globally valid solution.",
                    highlightedCells = setOf(Pair(bestR, bestC))
                )
            }
        }

        return null
    }
}
