package model;

import java.util.Objects;

public class Piece {
    private Color color;
    private int row;
    private int col;
    private PieceType type;
    // Game state can be used to track if the piece is a king or not
    private boolean isSelected; //UI selection state
    private int moveCount;

    // Constants for movement directions
    public static final int FORWARD_WHITE = -1; //Red moves up the board the row decreases
    public static final int BACKWARD_BLACK = 1;//Black moves down the board the row increases
    // Constructor to initialize a piece with its color and position
    public Piece(Color color, int row, int col)
    {
        this.color = color;
        this.type = PieceType.REGULAR; // Default type
        this.row = row;
        this.col = col;
        this.isSelected = false; // Default selection state
        this.moveCount = 0; // Initialize move count

    }
    public enum Color{
        BLACK, WHITE
    }
    public enum PieceType {
        REGULAR, // Regular piece
        KING // King piece
    }
    public Piece(Piece other)
    {
        this.color = other.color;
        this.type = other.type; // Default type
        this.row = other.row;
        this.col = other.col;
        this.isSelected = other.isSelected; // Default selection state
        this.moveCount = other.moveCount; // Initialize move count

    }
    // Getters and Setters
    public Color getColor() {
        return color;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    //public PieceType getType() {return type;}
    //public void setType(PieceType type) { this.type = type;}
    public boolean isSelected() {return isSelected;}
    
    public void setSelected(boolean selected) {this.isSelected = selected;}
    
    public int getMoveCount() {
        return moveCount;
    }
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    // Method to check if the piece is a king
    public boolean isKing(){
        return type == PieceType.KING;
    }
    public boolean isRegular(){
        return type == PieceType.REGULAR;
    }
    public boolean isBlack() {
        return color == Color.BLACK;
    }
    public boolean isWhite() {
        return color == Color.WHITE;
    }
    //promotion logic
    public boolean canBePromoted(){
        if (isWhite() && row == 0) {
            type = PieceType.KING;
            return true;
        } else if (isBlack() && row == 7) {
            type = PieceType.KING;
            return true;
        }
        return false;
    }

    public void promote(){
        if(canBePromoted()){
            this.type = PieceType.KING;
        }
    }
    public void  demote(){//For undo functionality
        this.type = PieceType.REGULAR;
        
    }
    //Move direction logic
    public int getForwardDirection() {
        return isWhite() ? FORWARD_WHITE : BACKWARD_BLACK;
    }

    public boolean canMoveInDirection(int direction) {
        if(isKing()){
            return direction == 1 || direction == -1; // Kings can move in both directions

        }else{
            return direction == getForwardDirection(); // Regular pieces can only move forward
        }
    }

    //Position validation logic
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8; //  an 8x8 board
    }
    public boolean isOnDarkSquare(){
        //in checkers dark squares are where the pieces are placed
        return (row + col) % 2 == 1; // Dark squares are where the sum of row and column indices is odd
    }

    //Movement validation logic
    public boolean canMoveTo(int newRow, int newCol){
        if(!isValidPosition(newRow, newCol)){
            return false; // Invalid position
        }
        if(!isOnDarkSqaure()){
            return false; // Can only move to dark squares
        }
        //Calculate the row difference
        int rowDiff = (newRow - row);
        int colDiff = Math.abs(newCol - col);
        //Must move diagonally
        if(Math.abs(rowDiff) != colDiff){
            return false; // Must move diagonally
        }
        //Check if the piece can move in the specified direction
        if(!canMoveInDirection(rowDiff)){
            
            return false; // Cannot move in the specified direction
        }
        int direction = rowDiff > 0 ? BACKWARD_BLACK : FORWARD_WHITE;
        return canMoveInDirection(direction); // Check if the piece can move in the specified direction


    }

    //Utility methods
    public void incrementMoveCount() {
        this.moveCount++;
    }

    public Piece clone() {
        return new Piece(this);
    }

    public double distanceTo(int targetRow, int targetCol) {
        return Math.sqrt(Math.pow(targetRow - row, 2) + Math.pow(targetCol - col, 2));
    }

    public boolean isAdjacentTo(int targetRow, int targetCol) {
        return Math.abs(targetRow - row) == 1 && Math.abs(targetCol - col) == 1;
    }

    // Override toString for better debugging

    @Override
    public String toString() {
        String colorStr = isBlack() ? "B" : "W";
        String typeStr = isKing() ? "K" : "R"; // K for King, R for Regular
        return colorStr + typeStr + "(" + row + ", " + col + ")";
    }
    
    public String getDisplayString(){
        if(isKing()){
            return isBlack() ? "WK" : "BK"; // White King or Black King
        } else {
            return isBlack() ? "WR" : "BR"; // White Regular or Black Regular
        }

    }

//equality and Hashing
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    
    Piece piece = (Piece) obj;
    return row == piece.row && 
           col == piece.col && 
           color == piece.color && 
           type == piece.type;
}

@Override
public int hashCode() {
    return Objects.hash(color, type, row, col);
}


}
