package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SavedPuzzleEntity
import com.example.engine.PresetPuzzle
import com.example.engine.PuzzleLibrary
import com.example.engine.SudokuGenerator
import com.example.engine.SudokuLogicalSolver
import com.example.engine.SudokuSolver
import com.example.engine.SudokuValidator
import com.example.model.Cell
import com.example.model.Difficulty
import com.example.model.InputMode
import com.example.model.SolveStatus
import com.example.model.StepExplanation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SOLVER,
    PLAY,
    LIBRARY,
    TECHNIQUES
}

class SudokuViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val puzzleDao = db.savedPuzzleDao()

    val savedPuzzles: StateFlow<List<SavedPuzzleEntity>> = puzzleDao.getAllPuzzles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation Screen
    private val _currentScreen = MutableStateFlow(AppScreen.SOLVER)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active 9x9 board state
    private val _cells = MutableStateFlow(createEmptyBoard())
    val cells: StateFlow<List<List<Cell>>> = _cells.asStateFlow()

    // Selection
    private val _selectedRow = MutableStateFlow(0)
    val selectedRow: StateFlow<Int> = _selectedRow.asStateFlow()

    private val _selectedCol = MutableStateFlow(0)
    val selectedCol: StateFlow<Int> = _selectedCol.asStateFlow()

    // Input mode
    private val _inputMode = MutableStateFlow(InputMode.NORMAL)
    val inputMode: StateFlow<InputMode> = _inputMode.asStateFlow()

    // Solver state
    private val _solveStatus = MutableStateFlow(SolveStatus.IDLE)
    val solveStatus: StateFlow<SolveStatus> = _solveStatus.asStateFlow()

    private val _solveTimeMs = MutableStateFlow(0L)
    val solveTimeMs: StateFlow<Long> = _solveTimeMs.asStateFlow()

    private val _solutionCount = MutableStateFlow(0)
    val solutionCount: StateFlow<Int> = _solutionCount.asStateFlow()

    private val _stepExplanation = MutableStateFlow<StepExplanation?>(null)
    val stepExplanation: StateFlow<StepExplanation?> = _stepExplanation.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating.asStateFlow()

    private val _animationSpeedMs = MutableStateFlow(80L)
    val animationSpeedMs: StateFlow<Long> = _animationSpeedMs.asStateFlow()

    private var animationJob: Job? = null

    // Undo / Redo Stacks
    private val undoStack = mutableListOf<List<List<Cell>>>()
    private val redoStack = mutableListOf<List<List<Cell>>>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Play Mode state
    private val _playDifficulty = MutableStateFlow(Difficulty.EASY)
    val playDifficulty: StateFlow<Difficulty> = _playDifficulty.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0L)
    val timerSeconds: StateFlow<Long> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _mistakesCount = MutableStateFlow(0)
    val mistakesCount: StateFlow<Int> = _mistakesCount.asStateFlow()

    private val _isGameWon = MutableStateFlow(false)
    val isGameWon: StateFlow<Boolean> = _isGameWon.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Start with a gentle starter preset on first launch
        loadPreset(PuzzleLibrary.presets.first(), false)
        startTimer()
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    private fun createEmptyBoard(): List<List<Cell>> {
        return List(9) { r ->
            List(9) { c ->
                Cell(row = r, col = c)
            }
        }
    }

    private fun saveUndoState() {
        undoStack.add(deepCopy(_cells.value))
        if (undoStack.size > 40) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    private fun deepCopy(board: List<List<Cell>>): List<List<Cell>> {
        return board.map { row ->
            row.map { it.copy(notes = it.notes.toSet()) }
        }
    }

    fun selectCell(row: Int, col: Int) {
        _selectedRow.value = row
        _selectedCol.value = col
        updateCellHighlights()
    }

    fun toggleInputMode() {
        _inputMode.value = if (_inputMode.value == InputMode.NORMAL) InputMode.PENCIL else InputMode.NORMAL
    }

    fun inputDigit(digit: Int) {
        val r = _selectedRow.value
        val c = _selectedCol.value
        val currentCell = _cells.value[r][c]

        // If in Play mode and cell is given clue, cannot edit
        if (_currentScreen.value == AppScreen.PLAY && currentCell.isGiven) {
            return
        }

        saveUndoState()

        val newBoard = _cells.value.map { it.toMutableList() }.toMutableList()

        if (_inputMode.value == InputMode.NORMAL) {
            val isCurrent = (currentCell.value == digit)
            val newValue = if (isCurrent) 0 else digit

            // In Play mode, check if placing a wrong digit causes a mistake
            if (_currentScreen.value == AppScreen.PLAY && newValue != 0) {
                val grid = toIntArrayGrid(_cells.value)
                val solved = SudokuSolver.solve(grid)
                if (solved.solvedGrid != null && solved.solvedGrid[r][c] != newValue) {
                    _mistakesCount.value += 1
                }
            }

            newBoard[r][c] = currentCell.copy(
                value = newValue,
                notes = emptySet()
            )

            // Auto clean notes from intersecting cells
            if (newValue != 0) {
                cleanIntersectingNotes(newBoard, r, c, newValue)
            }
        } else {
            // Pencil notes mode
            val newNotes = currentCell.notes.toMutableSet()
            if (newNotes.contains(digit)) {
                newNotes.remove(digit)
            } else {
                newNotes.add(digit)
            }
            newBoard[r][c] = currentCell.copy(
                value = 0,
                notes = newNotes
            )
        }

        _cells.value = newBoard
        updateCellHighlights()
        checkPlayWin()
    }

    private fun cleanIntersectingNotes(board: MutableList<MutableList<Cell>>, r: Int, c: Int, digit: Int) {
        // Row & Col
        for (i in 0 until 9) {
            if (board[r][i].notes.contains(digit)) {
                board[r][i] = board[r][i].copy(notes = board[r][i].notes - digit)
            }
            if (board[i][c].notes.contains(digit)) {
                board[i][c] = board[i][c].copy(notes = board[i][c].notes - digit)
            }
        }
        // Box
        val br = (r / 3) * 3
        val bc = (c / 3) * 3
        for (row in br until br + 3) {
            for (col in bc until bc + 3) {
                if (board[row][col].notes.contains(digit)) {
                    board[row][col] = board[row][col].copy(notes = board[row][col].notes - digit)
                }
            }
        }
    }

    fun eraseCurrentCell() {
        val r = _selectedRow.value
        val c = _selectedCol.value
        val currentCell = _cells.value[r][c]

        if (_currentScreen.value == AppScreen.PLAY && currentCell.isGiven) return
        if (currentCell.isEmpty && currentCell.notes.isEmpty()) return

        saveUndoState()

        val newBoard = _cells.value.map { it.toMutableList() }.toMutableList()
        newBoard[r][c] = currentCell.copy(value = 0, notes = emptySet())
        _cells.value = newBoard
        updateCellHighlights()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(deepCopy(_cells.value))
            val previous = undoStack.removeAt(undoStack.size - 1)
            _cells.value = previous
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
            updateCellHighlights()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(deepCopy(_cells.value))
            val next = redoStack.removeAt(redoStack.size - 1)
            _cells.value = next
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
            updateCellHighlights()
        }
    }

    fun solveInstantly() {
        stopAnimation()
        saveUndoState()

        _solveStatus.value = SolveStatus.SOLVING
        val currentGrid = toIntArrayGrid(_cells.value)

        val result = SudokuSolver.solve(currentGrid)
        _solveTimeMs.value = result.executionTimeMs
        _solutionCount.value = result.solutionCount

        if (result.solvedGrid != null) {
            val newBoard = _cells.value.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    val solvedVal = result.solvedGrid[r][c]
                    cell.copy(
                        value = solvedVal,
                        notes = emptySet()
                    )
                }
            }
            _cells.value = newBoard
            _solveStatus.value = SolveStatus.SOLVED
            _stepExplanation.value = null
        } else {
            _solveStatus.value = if (!SudokuValidator.isValidBoard(currentGrid)) SolveStatus.INVALID_BOARD else SolveStatus.UNSOLVABLE
        }

        updateCellHighlights()
    }

    fun requestStep() {
        stopAnimation()
        val currentGrid = toIntArrayGrid(_cells.value)
        val step = SudokuLogicalSolver.findNextStep(currentGrid)
        _stepExplanation.value = step

        if (step != null) {
            _selectedRow.value = step.row
            _selectedCol.value = step.col
            updateCellHighlights(stepTarget = Pair(step.row, step.col))
        }
    }

    fun applyStep() {
        val step = _stepExplanation.value ?: return
        saveUndoState()

        val newBoard = _cells.value.map { it.toMutableList() }.toMutableList()
        newBoard[step.row][step.col] = newBoard[step.row][step.col].copy(
            value = step.value,
            notes = emptySet()
        )
        cleanIntersectingNotes(newBoard, step.row, step.col, step.value)

        _cells.value = newBoard
        _stepExplanation.value = null
        updateCellHighlights()
        checkPlayWin()
    }

    fun dismissStep() {
        _stepExplanation.value = null
        updateCellHighlights()
    }

    fun toggleAnimation() {
        if (_isAnimating.value) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    fun setAnimationSpeed(ms: Long) {
        _animationSpeedMs.value = ms
    }

    private fun startAnimation() {
        val currentGrid = toIntArrayGrid(_cells.value)
        if (!SudokuValidator.isValidBoard(currentGrid)) {
            _solveStatus.value = SolveStatus.INVALID_BOARD
            return
        }

        saveUndoState()
        val trace = SudokuSolver.generateAnimationTrace(currentGrid)
        if (trace.isEmpty()) {
            _solveStatus.value = SolveStatus.UNSOLVABLE
            return
        }

        _isAnimating.value = true
        _solveStatus.value = SolveStatus.SOLVING

        animationJob = viewModelScope.launch {
            for (step in trace) {
                if (!_isAnimating.value) break
                val (coord, value) = step
                val (r, c) = coord

                val newBoard = _cells.value.map { it.toMutableList() }.toMutableList()
                newBoard[r][c] = newBoard[r][c].copy(value = value, notes = emptySet())
                _cells.value = newBoard
                _selectedRow.value = r
                _selectedCol.value = c
                updateCellHighlights()

                delay(_animationSpeedMs.value)
            }
            _isAnimating.value = false
            _solveStatus.value = SolveStatus.SOLVED
        }
    }

    fun stopAnimation() {
        _isAnimating.value = false
        animationJob?.cancel()
        animationJob = null
    }

    fun clearBoard(keepGivensOnly: Boolean = false) {
        stopAnimation()
        saveUndoState()
        _stepExplanation.value = null
        _solveStatus.value = SolveStatus.IDLE

        val newBoard = _cells.value.map { rowList ->
            rowList.map { cell ->
                if (keepGivensOnly && cell.isGiven) {
                    cell
                } else {
                    Cell(row = cell.row, col = cell.col)
                }
            }
        }
        _cells.value = newBoard
        updateCellHighlights()
    }

    fun loadPreset(preset: PresetPuzzle, isForPlayMode: Boolean = false) {
        stopAnimation()
        val grid = preset.toGrid()
        setBoardFromIntArray(grid, isGiven = true)
        _stepExplanation.value = null
        _solveStatus.value = SolveStatus.IDLE
        if (isForPlayMode) {
            _playDifficulty.value = preset.difficulty
            resetGameStats()
        }
    }

    fun generateNewPuzzle(difficulty: Difficulty, isForPlayMode: Boolean = true) {
        stopAnimation()
        viewModelScope.launch {
            val grid = SudokuGenerator.generate(difficulty)
            setBoardFromIntArray(grid, isGiven = true)
            _stepExplanation.value = null
            _solveStatus.value = SolveStatus.IDLE
            if (isForPlayMode) {
                _playDifficulty.value = difficulty
                resetGameStats()
            }
        }
    }

    private fun setBoardFromIntArray(grid: Array<IntArray>, isGiven: Boolean) {
        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false

        val newBoard = List(9) { r ->
            List(9) { c ->
                val v = grid[r][c]
                Cell(
                    row = r,
                    col = c,
                    value = v,
                    isGiven = isGiven && v != 0
                )
            }
        }
        _cells.value = newBoard
        _selectedRow.value = 0
        _selectedCol.value = 0
        updateCellHighlights()
    }

    private fun updateCellHighlights(stepTarget: Pair<Int, Int>? = null) {
        val r = _selectedRow.value
        val c = _selectedCol.value
        val activeVal = _cells.value.getOrNull(r)?.getOrNull(c)?.value ?: 0

        val grid = toIntArrayGrid(_cells.value)
        val conflictCoords = SudokuValidator.getConflictingCellCoordinates(grid)

        val updated = _cells.value.mapIndexed { rowIdx, rowList ->
            rowList.mapIndexed { colIdx, cell ->
                cell.copy(
                    isSelected = (rowIdx == r && colIdx == c),
                    isHighlightedValue = (activeVal in 1..9 && cell.value == activeVal),
                    isSameRowOrColOrBox = (rowIdx == r || colIdx == c || (rowIdx / 3 == r / 3 && colIdx / 3 == c / 3)),
                    isConflicted = conflictCoords.contains(Pair(rowIdx, colIdx)),
                    isStepTarget = (stepTarget != null && stepTarget.first == rowIdx && stepTarget.second == colIdx)
                )
            }
        }
        _cells.value = updated
    }

    private fun toIntArrayGrid(board: List<List<Cell>>): Array<IntArray> {
        return Array(9) { r ->
            IntArray(9) { c ->
                board[r][c].value
            }
        }
    }

    private fun checkPlayWin() {
        if (_currentScreen.value == AppScreen.PLAY) {
            val grid = toIntArrayGrid(_cells.value)
            if (SudokuValidator.isBoardComplete(grid)) {
                _isGameWon.value = true
                pauseTimer()
            }
        }
    }

    fun dismissWinDialog() {
        _isGameWon.value = false
    }

    private fun resetGameStats() {
        _mistakesCount.value = 0
        _timerSeconds.value = 0
        _isGameWon.value = false
        startTimer()
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000L)
                _timerSeconds.value += 1
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) pauseTimer() else startTimer()
    }

    fun saveCurrentBoard(title: String) {
        viewModelScope.launch {
            val currentGrid = toIntArrayGrid(_cells.value)
            val initStr = buildString {
                for (row in _cells.value) {
                    for (cell in row) {
                        append(if (cell.isGiven) cell.value else 0)
                    }
                }
            }
            val currStr = buildString {
                for (r in 0 until 9) {
                    for (c in 0 until 9) {
                        append(currentGrid[r][c])
                    }
                }
            }

            val entity = SavedPuzzleEntity(
                title = if (title.isBlank()) "Sudoku Puzzle #${System.currentTimeMillis() % 10000}" else title,
                initialBoard = initStr,
                currentBoard = currStr,
                difficulty = _playDifficulty.value.label,
                isSolved = SudokuValidator.isBoardComplete(currentGrid),
                timeSpentSeconds = _timerSeconds.value
            )
            puzzleDao.insertPuzzle(entity)
        }
    }

    fun loadSavedPuzzle(puzzle: SavedPuzzleEntity) {
        val grid = Array(9) { r ->
            IntArray(9) { c ->
                val idx = r * 9 + c
                if (idx < puzzle.currentBoard.length) puzzle.currentBoard[idx] - '0' else 0
            }
        }
        val initGrid = Array(9) { r ->
            IntArray(9) { c ->
                val idx = r * 9 + c
                if (idx < puzzle.initialBoard.length) puzzle.initialBoard[idx] - '0' else 0
            }
        }

        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false

        val newBoard = List(9) { r ->
            List(9) { c ->
                val currVal = grid[r][c]
                val initVal = initGrid[r][c]
                Cell(
                    row = r,
                    col = c,
                    value = currVal,
                    isGiven = (initVal != 0)
                )
            }
        }
        _cells.value = newBoard
        _timerSeconds.value = puzzle.timeSpentSeconds
        _selectedRow.value = 0
        _selectedCol.value = 0
        updateCellHighlights()
    }

    fun deleteSavedPuzzle(id: Long) {
        viewModelScope.launch {
            puzzleDao.deletePuzzleById(id)
        }
    }

    fun getDigitCounts(): Map<Int, Int> {
        val map = (1..9).associateWith { 0 }.toMutableMap()
        for (row in _cells.value) {
            for (cell in row) {
                if (cell.value in 1..9) {
                    map[cell.value] = (map[cell.value] ?: 0) + 1
                }
            }
        }
        return map
    }

    fun getCluesCount(): Int {
        var count = 0
        for (row in _cells.value) {
            for (cell in row) {
                if (cell.value in 1..9) count++
            }
        }
        return count
    }

    fun getConflictCount(): Int {
        val grid = toIntArrayGrid(_cells.value)
        return SudokuValidator.findConflicts(grid).size
    }
}
