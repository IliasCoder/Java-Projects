package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Piece.Color;
/**
 * The {@code Move} class represents a move in a checkers game.
 * It contains comprehensive information about the move including:
 * - Start and end positions
 * - Captured pieces
 * - Move type (simple move, jump, multiple jump)
 * - Promotion information
 * - Move evaluation weight
 * 
 * @author Ilias Bahou
 * @version 2.0
 */
public class Move {
    
    // ========== CONSTANTS ==========
    // this sectuion defines constants used for move weights and types it helps the AI evaluate moves effectively.
    /** The weight corresponding to an invalid move. */
    public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;
    
    /** Default weight for a valid move. */
    public static final double WEIGHT_DEFAULT = 0.0;
    
    /** Weight bonus for capturing moves. */
    public static final double WEIGHT_CAPTURE_BONUS = 10.0;
    
    /** Weight bonus for king promotion. */
    public static final double WEIGHT_PROMOTION_BONUS = 15.0;
    
    // ========== MOVE TYPES ==========
    
    public enum MoveType {
        SIMPLE,         // Regular diagonal move
        SINGLE_JUMP,    // Jump over one piece
        MULTIPLE_JUMP,  // Jump over multiple pieces in sequence
        FORCED_JUMP     // Mandatory jump (when captures are available)
    }
    
    // ========== ATTRIBUTES ==========
    
    // Position information (legacy compatibility)
    private byte startIndex;
    private byte endIndex;
    
    // Enhanced position information
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    
    // Move characteristics
    private MoveType moveType;
    private double weight;
    
    // Captured pieces information
    private List<Piece> capturedPieces;
    private List<Point> capturePositions;
    
    // Piece information
    private Piece movingPiece;
    private boolean causesPromotion;
    private Piece promotedPiece;
    
    // Move sequence (for multiple jumps)
    private List<Point> moveSequence;
    
    // Game state information
    private long timestamp;
    private boolean isValid;
    
    // ========== CONSTRUCTORS ==========
    
    /**
     * Legacy constructor for compatibility
     */
    public Move(int startIndex, int endIndex) {
        this.startIndex = (byte) startIndex;
        this.endIndex = (byte) endIndex;
        this.weight = WEIGHT_DEFAULT;
        initializeFromIndices();
    }
    
    /**
     * Legacy constructor using Points
     */
    public Move(Point start, Point end) {
        this.startIndex = (byte) Board.toIndex(start);
        this.endIndex = (byte) Board.toIndex(end);
        this.fromRow = start.y;
        this.fromCol = start.x;
        this.toRow = end.y;
        this.toCol = end.x;
        initializeDefaults();
    }
    
    /**
     * Enhanced constructor with row/column coordinates
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        
        // Calculate indices for legacy compatibility
        this.startIndex = (byte) Board.toIndex(new Point(fromCol, fromRow));
        this.endIndex = (byte) Board.toIndex(new Point(toCol, toRow));
        
        initializeDefaults();
    }
    
    /**
     * Complete constructor with piece information
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movingPiece) {
        this(fromRow, fromCol, toRow, toCol);
        this.movingPiece = movingPiece;
        determineMoveType();
    }
    
    /**
     * Constructor for capture moves
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol, 
                Piece movingPiece, List<Piece> capturedPieces) {
        this(fromRow, fromCol, toRow, toCol, movingPiece);
        this.capturedPieces = new ArrayList<>(capturedPieces);
        this.moveType = capturedPieces.size() > 1 ? MoveType.MULTIPLE_JUMP : MoveType.SINGLE_JUMP;
        calculateCapturePositions();
    }
    
    // ========== INITIALIZATION METHODS ==========
    
    private void initializeFromIndices() {
        Point start = Board.toPoint(startIndex);
        Point end = Board.toPoint(endIndex);
        this.fromRow = start.y;
        this.fromCol = start.x;
        this.toRow = end.y;
        this.toCol = end.x;
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        this.weight = WEIGHT_DEFAULT;
        this.moveType = MoveType.SIMPLE;
        this.capturedPieces = new ArrayList<>();
        this.capturePositions = new ArrayList<>();
        this.moveSequence = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.isValid = true;
        this.causesPromotion = false;
    }
    
    private void determineMoveType() {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        if (rowDiff == 1 && colDiff == 1) {
            this.moveType = MoveType.SIMPLE;
        } else if (rowDiff == 2 && colDiff == 2) {
            this.moveType = MoveType.SINGLE_JUMP;
        } else if (rowDiff > 2 && colDiff > 2) {
            this.moveType = MoveType.MULTIPLE_JUMP;
        }
    }
    
    private void calculateCapturePositions() {
        capturePositions.clear();
        for (Piece piece : capturedPieces) {
            capturePositions.add(new Point(piece.getCol(), piece.getRow()));
        }
    }
    
    // ========== LEGACY GETTERS/SETTERS (for compatibility) ==========
    
    public int getStartIndex() {
        return startIndex;
    }
    
    public void setStartIndex(int startIndex) {
        this.startIndex = (byte) startIndex;
        Point start = Board.toPoint(startIndex);
        this.fromRow = start.y;
        this.fromCol = start.x;
    }
    
    public int getEndIndex() {
        return endIndex;
    }
    
    public void setEndIndex(int endIndex) {
        this.endIndex = (byte) endIndex;
        Point end = Board.toPoint(endIndex);
        this.toRow = end.y;
        this.toCol = end.x;
    }
    
    public Point getStart() {
        return new Point(fromCol, fromRow);
    }
    
    public void setStart(Point start) {
        this.fromRow = start.y;
        this.fromCol = start.x;
        this.startIndex = (byte) Board.toIndex(start);
    }
    
    public Point getEnd() {
        return new Point(toCol, toRow);
    }
    
    public void setEnd(Point end) {
        this.toRow = end.y;
        this.toCol = end.x;
        this.endIndex = (byte) Board.toIndex(end);
    }
    
    // ========== ENHANCED GETTERS/SETTERS ==========
    
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    
    public void setFromPosition(int row, int col) {
        this.fromRow = row;
        this.fromCol = col;
        this.startIndex = (byte) Board.toIndex(new Point(col, row));
    }
    
    public void setToPosition(int row, int col) {
        this.toRow = row;
        this.toCol = col;
        this.endIndex = (byte) Board.toIndex(new Point(col, row));
    }
    
    public MoveType getMoveType() { return moveType; }
    public void setMoveType(MoveType moveType) { this.moveType = moveType; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public void changeWeight(double delta) { this.weight += delta; }
    
    public List<Piece> getCapturedPieces() { return new ArrayList<>(capturedPieces); }
    public void setCapturedPieces(List<Piece> capturedPieces) {
        this.capturedPieces = new ArrayList<>(capturedPieces);
        calculateCapturePositions();
    }
    
    public void addCapturedPiece(Piece piece) {
        this.capturedPieces.add(piece);
        this.capturePositions.add(new Point(piece.getCol(), piece.getRow()));
    }
    
    public List<Point> getCapturePositions() { return new ArrayList<>(capturePositions); }
    
    public Piece getMovingPiece() { return movingPiece; }
    public void setMovingPiece(Piece movingPiece) { this.movingPiece = movingPiece; }
    
    public boolean causesPromotion() { return causesPromotion; }
    public void setCausesPromotion(boolean causesPromotion) { this.causesPromotion = causesPromotion; }
    
    public Piece getPromotedPiece() { return promotedPiece; }
    public void setPromotedPiece(Piece promotedPiece) { this.promotedPiece = promotedPiece; }
    
    public List<Point> getMoveSequence() { return new ArrayList<>(moveSequence); }
    public void setMoveSequence(List<Point> moveSequence) {
        this.moveSequence = new ArrayList<>(moveSequence);
    }
    
    public long getTimestamp() { return timestamp; }
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { this.isValid = valid; }
    
    // ========== MOVE ANALYSIS METHODS ==========
    
    /**
     * Checks if this is a capture move
     */
    public boolean isCapture() {
        return !capturedPieces.isEmpty();
    }
    
    /**
     * Checks if this is a jump move (single or multiple)
     */
    public boolean isJump() {
        return moveType == MoveType.SINGLE_JUMP || moveType == MoveType.MULTIPLE_JUMP;
    }
    
    /**
     * Checks if this is a multiple jump move
     */
    public boolean isMultipleJump() {
        return moveType == MoveType.MULTIPLE_JUMP;
    }
    
    /**
     * Gets the number of pieces captured
     */
    public int getCaptureCount() {
        return capturedPieces.size();
    }
    
    /**
     * Gets the Manhattan distance of the move
     */
    public int getDistance() {
        return Math.abs(toRow - fromRow) + Math.abs(toCol - fromCol);
    }
    
    /**
     * Gets the diagonal distance of the move
     */
    public int getDiagonalDistance() {
        return Math.max(Math.abs(toRow - fromRow), Math.abs(toCol - fromCol));
    }
    
    /**
     * Checks if the move is a forward move for the given color
     */
    public boolean isForwardMove(Color color) {
        if (color == Color.WHITE) {
            return toRow < fromRow; // Red moves up (decreasing row)
        } else {
            return toRow > fromRow; // Black moves down (increasing row)
        }
    }
    
    /**
     * Calculates and updates the move weight based on various factors
     */
    public void calculateWeight() {
        double totalWeight = WEIGHT_DEFAULT;
        
        // Capture bonus
        if (isCapture()) {
            totalWeight += WEIGHT_CAPTURE_BONUS * getCaptureCount();
        }
        
        // Promotion bonus
        if (causesPromotion()) {
            totalWeight += WEIGHT_PROMOTION_BONUS;
        }
        
        // Multiple jump bonus
        if (isMultipleJump()) {
            totalWeight += 5.0; // Extra bonus for multiple jumps
        }
        
        // Center control bonus (squares closer to center are better)
        double centerDistance = Math.abs(3.5 - toRow) + Math.abs(3.5 - toCol);
        totalWeight += (7.0 - centerDistance) * 0.5;
        
        this.weight = totalWeight;
    }
    
    // ========== UTILITY METHODS ==========
    
    /**
     * Creates a deep copy of this move
     */
    public Move clone() {
        Move cloned = new Move(fromRow, fromCol, toRow, toCol);
        cloned.moveType = this.moveType;
        cloned.weight = this.weight;
        cloned.capturedPieces = new ArrayList<>(this.capturedPieces);
        cloned.capturePositions = new ArrayList<>(this.capturePositions);
        cloned.movingPiece = this.movingPiece;
        cloned.causesPromotion = this.causesPromotion;
        cloned.promotedPiece = this.promotedPiece;
        cloned.moveSequence = new ArrayList<>(this.moveSequence);
        cloned.isValid = this.isValid;
        return cloned;
    }
    
    /**
     * Reverses this move (for undo functionality)
     */
    public Move reverse() {
        Move reversed = new Move(toRow, toCol, fromRow, fromCol);
        reversed.moveType = this.moveType;
        reversed.weight = -this.weight; // Negative weight for reverse
        reversed.capturedPieces = new ArrayList<>(this.capturedPieces);
        reversed.movingPiece = this.movingPiece;
        return reversed;
    }
    
    // ========== COMPARISON METHODS ==========
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Move move = (Move) obj;
        return fromRow == move.fromRow &&
               fromCol == move.fromCol &&
               toRow == move.toRow &&
               toCol == move.toCol &&
               Objects.equals(capturedPieces, move.capturedPieces);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol, capturedPieces);
    }
    
    /**
     * Compares moves by weight (for sorting)
     */
    public int compareByWeight(Move other) {
        return Double.compare(this.weight, other.weight);
    }
    
    // ========== STRING REPRESENTATION ==========
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Move[");
        sb.append("(").append(fromRow).append(",").append(fromCol).append(")")
          .append("->(").append(toRow).append(",").append(toCol).append(")")
          .append(", type=").append(moveType)
          .append(", weight=").append(String.format("%.2f", weight));
        
        if (isCapture()) {
            sb.append(", captures=").append(getCaptureCount());
        }
        
        if (causesPromotion()) {
            sb.append(", promotes");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Returns a compact string representation of the move
     */
    public String toCompactString() {
        return String.format("(%d,%d)->(%d,%d)", fromRow, fromCol, toRow, toCol);
    }
    
    /**
     * Returns algebraic notation for the move (like chess notation)
     */
    public String toAlgebraicNotation() {
        char fromFile = (char)('a' + fromCol);
        char toFile = (char)('a' + toCol);
        int fromRank = 8 - fromRow;
        int toRank = 8 - toRow;
        
        String notation = "" + fromFile + fromRank + "-" + toFile + toRank;
        
        if (isCapture()) {
            notation += "x" + getCaptureCount();
        }
        
        if (causesPromotion()) {
            notation += "=";
        }
        
        return notation;
    }
}