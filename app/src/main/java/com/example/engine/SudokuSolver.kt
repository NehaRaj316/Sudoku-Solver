package com.example.engine

object SudokuSolver {

    data class SolveResult(
        val solvedGrid: Array<IntArray>?,
        val executionTimeMs: Long,
        val solutionCount: Int // 0, 1, or 2 (meaning 2+)
    )

    fun solve(grid: Array<IntArray>): SolveResult {
        val startTime = System.currentTimeMillis()
        if (!SudokuValidator.isValidBoard(grid)) {
            return SolveResult(null, System.currentTimeMillis() - startTime, 0)
        }

        val copy = Array(9) { r -> grid[r].clone() }
        val solutionCount = countSolutions(grid, maxCount = 2)

        if (solutionCount == 0) {
            return SolveResult(null, System.currentTimeMillis() - startTime, 0)
        }

        val success = solveBacktrackMRV(copy)
        val elapsed = System.currentTimeMillis() - startTime

        return SolveResult(if (success) copy else null, elapsed, solutionCount)
    }

    private fun solveBacktrackMRV(grid: Array<IntArray>): Boolean {
        var minCandidatesCount = 10
        var bestRow = -1
        var bestCol = -1
        var bestCandidates: Set<Int>? = null

        for (r in 0 until 9) {
            for (c in 0 until 9) {
                if (grid[r][c] == 0) {
                    val candidates = SudokuValidator.getCandidates(grid, r, c)
                    if (candidates.isEmpty()) {
                        return false // Dead end
                    }
                    if (candidates.size < minCandidatesCount) {
                        minCandidatesCount = candidates.size
                        bestRow = r
                        bestCol = c
                        bestCandidates = candidates
                        if (minCandidatesCount == 1) break
                    }
                }
            }
            if (minCandidatesCount == 1) break
        }

        if (bestRow == -1) {
            return true // All cells filled
        }

        for (candidate in bestCandidates!!) {
            grid[bestRow][bestCol] = candidate
            if (solveBacktrackMRV(grid)) {
                return true
            }
            grid[bestRow][bestCol] = 0
        }

        return false
    }

    fun countSolutions(grid: Array<IntArray>, maxCount: Int = 2): Int {
        val copy = Array(9) { r -> grid[r].clone() }
        var count = 0

        fun backtrack(): Boolean {
            var minCandidatesCount = 10
            var bestRow = -1
            var bestCol = -1
            var bestCandidates: Set<Int>? = null

            for (r in 0 until 9) {
                for (c in 0 until 9) {
                    if (copy[r][c] == 0) {
                        val candidates = SudokuValidator.getCandidates(copy, r, c)
                        if (candidates.isEmpty()) return false
                        if (candidates.size < minCandidatesCount) {
                            minCandidatesCount = candidates.size
                            bestRow = r
                            bestCol = c
                            bestCandidates = candidates
                            if (minCandidatesCount == 1) break
                        }
                    }
                }
                if (minCandidatesCount == 1) break
            }

            if (bestRow == -1) {
                count++
                return count >= maxCount
            }

            for (candidate in bestCandidates!!) {
                copy[bestRow][bestCol] = candidate
                if (backtrack()) return true
                copy[bestRow][bestCol] = 0
            }

            return false
        }

        backtrack()
        return count
    }

    /**
     * Generates a step-by-step trace of cell fills to power the animated solver.
     */
    fun generateAnimationTrace(grid: Array<IntArray>): List<Pair<Pair<Int, Int>, Int>> {
        val trace = mutableListOf<Pair<Pair<Int, Int>, Int>>()
        val copy = Array(9) { r -> grid[r].clone() }

        fun backtrack(): Boolean {
            var minCandidatesCount = 10
            var bestRow = -1
            var bestCol = -1
            var bestCandidates: Set<Int>? = null

            for (r in 0 until 9) {
                for (c in 0 until 9) {
                    if (copy[r][c] == 0) {
                        val candidates = SudokuValidator.getCandidates(copy, r, c)
                        if (candidates.isEmpty()) return false
                        if (candidates.size < minCandidatesCount) {
                            minCandidatesCount = candidates.size
                            bestRow = r
                            bestCol = c
                            bestCandidates = candidates
                            if (minCandidatesCount == 1) break
                        }
                    }
                }
                if (minCandidatesCount == 1) break
            }

            if (bestRow == -1) return true

            for (candidate in bestCandidates!!) {
                copy[bestRow][bestCol] = candidate
                trace.add(Pair(Pair(bestRow, bestCol), candidate))
                if (backtrack()) return true
                copy[bestRow][bestCol] = 0
                trace.add(Pair(Pair(bestRow, bestCol), 0)) // Backtrack step
            }

            return false
        }

        backtrack()
        return trace
    }
}
