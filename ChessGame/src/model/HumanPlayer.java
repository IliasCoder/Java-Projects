package model;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * HumanPlayer represents a real person playing checkers through the UI.
 * This class handles user input, validates selections, and coordinates with the game interface.
 * 
 * @author Your Name
 * @version 2.0
 */
public class HumanPlayer extends Player {
    
    // Selection state enumeration
    public enum SelectionState {
        IDLE,               // Not currently selecting anything
        SELECTING_PIECE,    // Waiting for user to select a piece
        PIECE_SELECTED,     // Piece selected, waiting for destination
        MOVE_READY          // Move is complete and ready to execute
    }
    
    // ========== ATTRIBUTES ==========
    
    // Current selection state
    private SelectionState selectionState;
    
    // Selected piece and its valid moves
    private Piece selectedPiece;
    private List<Move> validMovesForSelectedPiece;
    
    // Pending move being constructed
    private Move pendingMove;
    
    // Input synchronization
    private CountDownLatch inputLatch;
    private final Object inputLock = new Object();
    private boolean waitingForInput;
    
    // UI interaction
    private List<int[]> highlightedSquares;
    private boolean showValidMoves;
    
    // Move timeout (optional - for timed games)
    private long moveTimeoutMs;
    private long turnStartTime;
    
    // ========== CONSTRUCTOR ==========
    
    /**
     * Creates a new HumanPlayer
     * @param name Player's display name
     * @param color Player's piece color (RED or BLACK)
     */
    public HumanPlayer(String name, Color color) {
        super(name, color, PlayerType.HUMAN);
        this.selectionState = SelectionState.IDLE;
        this.validMovesForSelectedPiece = new ArrayList<>();
        this.highlightedSquares = new ArrayList<>();
        this.showValidMoves = true;
        this.moveTimeoutMs = 0; // No timeout by default
        this.waitingForInput = false;
    }
    
    // ========== LEGACY COMPATIBILITY ==========
    
    @Override
    public boolean isHuman() {
        return true;
    }
    
    /**
     * Legacy method - HumanPlayer doesn't actively update the game
     * Game updates come through user input via handleSquareClick()
     */
    @Override
    public void updateGame(Game game) {
        // Human players don't actively update the game
        // Updates come through UI interactions
    }
    
    // ========== ABSTRACT METHOD IMPLEMENTATIONS ==========
    
    /**
     * Makes a move by waiting for user input through the UI
     * This method blocks until the user completes a valid move selection
     */
    @Override
    public Move makeMove(Board board, List<Move> validMoves) {
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }
        
        // Reset state for new move
        resetSelectionState();
        setActive(true);
        turnStartTime = System.currentTimeMillis();
        
        // Create latch to wait for user input
        inputLatch = new CountDownLatch(1);
        waitingForInput = true;
        
        try {
            // Start the selection process
            startMoveSelection(board, validMoves);
            
            // Wait for user to complete move selection
            if (moveTimeoutMs > 0) {
                boolean completed = inputLatch.await(moveTimeoutMs, TimeUnit.MILLISECONDS);
                if (!completed) {
                    // Timeout occurred - return random valid move or null
                    return handleMoveTimeout(validMoves);
                }
            } else {
                // Wait indefinitely for user input
                inputLatch.await();
            }
            
            // Return the completed move
            return pendingMove;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            // Clean up
            waitingForInput = false;
            setActive(false);
            clearHighlights();
        }
    }
    
@Override
    public boolean isReady() {
        return !waitingForInput || selectionState == SelectionState.MOVE_READY;
    }
    
    @Override
    public void initialize() {
        resetSelectionState();
        setActive(false);
    }
    
    @Override
    public void cleanup() {
        resetSelectionState();
        if (inputLatch != null) {
            inputLatch.countDown(); // Release any waiting threads
        }
    }
    
    // ========== USER INPUT HANDLING ==========
    
    /**
     * Called when user clicks on a board square
     * @param row Row of the clicked square
     * @param col Column of the clicked square
     * @param board Current board state
     * @param validMoves List of all valid moves for this turn
     * @return true if the click was handled, false if invalid
     */
    public boolean handleSquareClick(int row, int col, Board board, List<Move> validMoves) {
        synchronized (inputLock) {
            if (!waitingForInput) {
                return false; // Not currently waiting for input
            }
            
            switch (selectionState) {
                case SELECTING_PIECE:
                    return handlePieceSelection(row, col, board, validMoves);
                    
                case PIECE_SELECTED:
                    return handleDestinationSelection(row, col, board, validMoves);
                    
                default:
                    return false;
            }
        }
    }
    
    /**
     * Handles selection of a piece
     */
    private boolean handlePieceSelection(int row, int col, Board board, List<Move> validMoves) {
        Piece clickedPiece = board.getPieceAt(row, col);
        
        // Check if clicked on a valid piece
        if (clickedPiece == null || clickedPiece.getColor() != getColor()) {
            return false; // Invalid selection
        }
        
        // Get valid moves for this piece
        List<Move> pieceMoves = getMovesForPiece(clickedPiece, validMoves);
        if (pieceMoves.isEmpty()) {
            return false; // Piece has no valid moves
        }
        
        // Select the piece
        selectPiece(clickedPiece, pieceMoves);
        return true;
    }
    
    /**
     * Handles selection of a destination square
     */
    private boolean handleDestinationSelection(int row, int col, Board board, List<Move> validMoves) {
        // Check if clicking on a different piece (changing selection)
        Piece clickedPiece = board.getPiece(row, col);
        if (clickedPiece != null && clickedPiece.getColor() == getColor()) {
            // User clicked on another piece - change selection
            return handlePieceSelection(row, col, board, validMoves);
        }
        
        // Check if destination is valid for selected piece
        Move attemptedMove = findMoveToDestination(row, col);
        if (attemptedMove != null) {
            // Valid destination - complete the move
            completeMove(attemptedMove);
            return true;
        }
        
        return false; // Invalid destination
    }
    
    // ========== SELECTION MANAGEMENT ==========
    
    /**
     * Selects a piece and highlights its valid moves
     */
    private void selectPiece(Piece piece, List<Move> pieceMoves) {
        selectedPiece = piece;
        validMovesForSelectedPiece = new ArrayList<>(pieceMoves);
        selectionState = SelectionState.PIECE_SELECTED;
        
        // Highlight the selected piece
        piece.setSelected(true);
        
        // Highlight valid destination squares
        if (showValidMoves) {
            highlightValidDestinations(pieceMoves);
        }
    }
    
    /**
     * Completes the move selection process
     */
    private void completeMove(Move move) {
        pendingMove = move;
        selectionState = SelectionState.MOVE_READY;
        
        // Clear selection
        if (selectedPiece != null) {
            selectedPiece.setSelected(false);
        }
        clearHighlights();
        
        // Signal that move is ready
        if (inputLatch != null) {
            inputLatch.countDown();
        }
    }
    
    /**
     * Resets all selection state
     */
    private void resetSelectionState() {
        selectionState = SelectionState.IDLE;
        
        if (selectedPiece != null) {
            selectedPiece.setSelected(false);
            selectedPiece = null;
        }
        
        validMovesForSelectedPiece.clear();
        pendingMove = null;
        clearHighlights();
    }
    
    // ========== MOVE VALIDATION AND UTILITIES ==========
    
    /**
     * Gets all valid moves for a specific piece
     */
    private List<Move> getMovesForPiece(Piece piece, List<Move> allValidMoves) {
        List<Move> pieceMoves = new ArrayList<>();
        
        for (Move move : allValidMoves) {
            if (move.getFromRow() == piece.getRow() && 
                move.getFromCol() == piece.getCol()) {
                pieceMoves.add(move);
            }
        }
        
        return pieceMoves;
    }
    
    /**
     * Finds a move that goes to the specified destination
     */
    private Move findMoveToDestination(int toRow, int toCol) {
        for (Move move : validMovesForSelectedPiece) {
            if (move.getToRow() == toRow && move.getToCol() == toCol) {
                return move;
            }
        }
        return null;
    }
    
    /**
     * Starts the move selection process
     */
    private void startMoveSelection(Board board, List<Move> validMoves) {
        selectionState = SelectionState.SELECTING_PIECE;
        
        // If only one piece can move, auto-select it
        if (hasOnlyOnePieceWithMoves(validMoves)) {
            Move firstMove = validMoves.get(0);
            Piece onlyPiece = board.getPieceAt(firstMove.getFromRow(), firstMove.getFromCol());
            selectPiece(onlyPiece, validMoves);
        }
    }
    
    /**
     * Checks if only one piece has valid moves
     */
    private boolean hasOnlyOnePieceWithMoves(List<Move> validMoves) {
        if (validMoves.isEmpty()) return false;
        
        Move firstMove = validMoves.get(0);
        int fromRow = firstMove.getFromRow();
        int fromCol = firstMove.getFromCol();
        
        for (Move move : validMoves) {
            if (move.getFromRow() != fromRow || move.getFromCol() != fromCol) {
                return false;
            }
        }
        
        return true;
    }
    
    // ========== HIGHLIGHTING AND UI SUPPORT ==========
    
    /**
     * Highlights valid destination squares for the selected piece
     */
    private void highlightValidDestinations(List<Move> moves) {
        clearHighlights();
        
        for (Move move : moves) {
            highlightedSquares.add(new int[]{move.getToRow(), move.getToCol()});
        }
    }
    
    /**
     * Clears all highlighted squares
     */
    private void clearHighlights() {
        highlightedSquares.clear();
    }
    
    /**
     * Gets the list of squares that should be highlighted in the UI
     */
    public List<int[]> getHighlightedSquares() {
        return new ArrayList<>(highlightedSquares);
    }
    
    // ========== TIMEOUT HANDLING ==========
    
    /**
     * Handles move timeout by returning a random valid move
     */
    private Move handleMoveTimeout(List<Move> validMoves) {
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // Return first valid move as default
        return validMoves.get(0);
    }
    
    /**
     * Sets the move timeout in milliseconds
     * @param timeoutMs Timeout in milliseconds (0 = no timeout)
     */
    public void setMoveTimeout(long timeoutMs) {
        this.moveTimeoutMs = timeoutMs;
    }
    
    /**
     * Gets remaining time for current move in milliseconds
     */
    public long getRemainingTime() {
        if (moveTimeoutMs <= 0 || turnStartTime == 0) {
            return -1; // No timeout set
        }
        
        long elapsed = System.currentTimeMillis() - turnStartTime;
        return Math.max(0, moveTimeoutMs - elapsed);
    }
    
    // ========== GETTERS AND SETTERS ==========
    
    public SelectionState getSelectionState() {
        return selectionState;
    }
    
    public Piece getSelectedPiece() {
        return selectedPiece;
    }
    
    public boolean isWaitingForInput() {
        return waitingForInput;
    }
    
    public void setShowValidMoves(boolean show) {
        this.showValidMoves = show;
    }
    
    public boolean isShowValidMoves() {
        return showValidMoves;
    }
    
    /**
     * Cancels the current move selection and resets state
     */
    public void cancelCurrentMove() {
        synchronized (inputLock) {
            resetSelectionState();
            if (inputLatch != null) {
                inputLatch.countDown();
            }
        }
    }
    
    // ========== STRING REPRESENTATION ==========
    
    @Override
    public String toString() {
        return String.format("HumanPlayer{name='%s', color=%s, state=%s, waiting=%s}", 
                           getName(), getColor(), selectionState, waitingForInput);
    }
}
