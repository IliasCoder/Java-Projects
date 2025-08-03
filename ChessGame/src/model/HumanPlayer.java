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
    
