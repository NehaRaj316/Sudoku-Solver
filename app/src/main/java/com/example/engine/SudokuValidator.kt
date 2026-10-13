package com.example.engine

import com.example.model.Conflict

object SudokuValidator {

    fun isValidBoard(grid: Array<IntArray>): Boolean {
        return findConflicts(grid).isEmpty()
    }

    fun findConflicts(grid: Array<IntArray>): List<Conflict> {
        val conflicts = mutableListOf<Conflict>()

        // Check rows
        for (r in 0 until 9) {
            val seen = mutableMapOf<Int, MutableList<Int>>()
            for (c in 0 until 9) {
                val v = grid[r][c]
                if (v in 1..9) {
                    seen.getOrPut(v) { mutableListOf() }.add(c)
                }
            }
            for ((digit, cols) in seen) {
                if (cols.size > 1) {
                    conflicts.add(
                        Conflict(
                            type = "row",
                            index = r,
                            digit = digit,
                            cells = cols.map { c -> Pair(r, c) }
                        )
                    )
                }
            }
        }

        // Check cols
        for (c in 0 until 9) {
            val seen = mutableMapOf<Int, MutableList<Int>>()
            for (r in 0 until 9) {
                val v = grid[r][c]
                if (v in 1..9) {
                    seen.getOrPut(v) { mutableListOf() }.add(r)
                }
            }
            for ((digit, rows) in seen) {
                if (rows.size > 1) {
                    conflicts.add(
                        Conflict(
                            type = "col",
                            index = c,
                            digit = digit,
                            cells = rows.map { r -> Pair(r, c) }
                        )
                    )
                }
            }
        }

        // Check 3x3 boxes
        for (boxRow in 0 until 3) {
            for (boxCol in 0 until 3) {
                val seen = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
                for (r in boxRow * 3 until boxRow * 3 + 3) {
                    for (c in boxCol * 3 until boxCol * 3 + 3) {
                        val v = grid[r][c]
                        if (v in 1..9) {
                            seen.getOrPut(v) { mutableListOf() }.add(Pair(r, c))
                        }
                    }
                }
                for ((digit, cells) in seen) {
                    if (cells.size > 1) {
                        conflicts.add(
                            Conflict(
                                type = "box",
                                index = boxRow * 3 + boxCol,
                                digit = digit,
                                cells = cells
                            )
                        )
                    }
                }
            }
        }

        return conflicts
    }

    fun getConflictingCellCoordinates(grid: Array<IntArray>): Set<Pair<Int, Int>> {
        val result = mutableSetOf<Pair<Int, Int>>()
        for (conflict in findConflicts(grid)) {
            result.addAll(conflict.cells)
        }
        return result
    }

    fun isValidPlacement(grid: Array<IntArray>, row: Int, col: Int, value: Int): Boolean {
        if (value !in 1..9) return false

        // Row check
        for (c in 0 until 9) {
            if (c != col && grid[row][c] == value) return false
        }

        // Col check
        for (r in 0 until 9) {
            if (r != row && grid[r][col] == value) return false
        }

        // Box check
        val startR = (row / 3) * 3
        val startC = (col / 3) * 3
        for (r in startR until startR + 3) {
            for (c in startC until startC + 3) {
                if (r != row || c != col) {
                    if (grid[r][c] == value) return false
                }
            }
        }

        return true
    }

    fun getCandidates(grid: Array<IntArray>, row: Int, col: Int): Set<Int> {
        if (grid[row][col] != 0) return emptySet()
        val candidates = (1..9).toMutableSet()

        // Remove row candidates
        for (c in 0 until 9) {
            candidates.remove(grid[row][c])
        }

        // Remove col candidates
        for (r in 0 until 9) {
            candidates.remove(grid[r][col])
        }

        // Remove box candidates
        val startR = (row / 3) * 3
        val startC = (col / 3) * 3
        for (r in startR until startR + 3) {
            for (c in startC until startC + 3) {
                candidates.remove(grid[r][c])
            }
        }

        return candidates
    }

    fun isBoardComplete(grid: Array<IntArray>): Boolean {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (grid[r][c] == 0) return false
            }
        }
        return isValidBoard(grid)
    }
}
