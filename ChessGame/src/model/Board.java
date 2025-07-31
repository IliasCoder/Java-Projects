package model;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import model.Piece.Color;

public class Board{
    //Board configuration
    public static final int BOARD_SIZE = 8; // Standard checkers board size
    public static final int NUM_PIECES_PER_PLAYER = 12; // Each player starts with 12 pieces

    //Board state
    private Piece[][] board; // 2D array representing the board
    private List<Piece> capturedWhitePieces; // List of captured white pieces
    private List<Piece> capturedBlackPieces; // List of captured black pieces

    //Game state
    private int whitePiecesCount; // Count of white pieces on the board
    private int blackPiecesCount; // Count of black pieces on the board
    private int whiteKingsCount; // Count of white kings on the board
    private int blackKingsCount; // Count of black kings on the board

    
    private Stack<BoardState> moveHistory; // Stack to keep track of moves for undo functionality

    //board metadata
    private int moveCount; // Count of moves made in the game
    private Color lastMovedColor; // Color of the last player who made a move

    //For storing board state snapshots
    private static class BoardState {
        private Piece[][] boardSnapshot;
        private int whiteCount;
        private int blackCount;
        private int whiteKings;
        private int blackKings; 
        private List<Piece> capturedWhite;
        private List<Piece> capturedBlack;

        
    
    
    
    
    
    
    }


    //Constructor to initialize the board
    public Board(){
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.capturedWhitePieces = new ArrayList<>();
        this.capturedBlackPieces = new ArrayList<>();
        this.moveHistory = new Stack<>();
        resetBoard();
    }
    public void resetBoard() {
        // Iclear the board and captured pieces
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = null; // Clear the board
            }
        }
        //reset all the counters variables
        whitePiecesCount = 0;
        blackPiecesCount = 0;
        whiteKingsCount = 0;
        blackKingsCount = 0;
        moveCount = 0;
        lastMovedColor = null;
    
        // Clear captured pieces
        capturedWhitePieces.clear();
        capturedBlackPieces.clear();
        moveHistory.clear();
        
        // Initialize starting positions
        initializeStartingPositions();
    }

    public void initializeStartingPositions()
    {
        //place black pieces on the board rows 0 to 2
        for (int row = 0; row < 3; row++) {
            for (int col = 0 ; col < BOARD_SIZE; col++) {
                if(isDarkSquare(row, col)) {
                    Piece blackPiece = new Piece(Piece.Color.BLACK, row, col);
                    board[row][col] = blackPiece;
                    blackPiecesCount++;
                } 
            }
        }

        //place white pieces on the board rows 5 to 7
        for (int row = 5; row < BOARD_SIZE; row++) {
            for (int col = 0 ; col < BOARD_SIZE; col++) {
                if(isDarkSquare(row, col)) {
                    Piece whitePiece = new Piece(Piece.Color.WHITE, row, col);
                    board[row][col] = whitePiece;
                    whitePiecesCount++;
                }
            }
        }


    }
    //Position validation methods
    public boolean isDarkSquare(int row, int col) {
        return (row + col) % 2 == 1; // Dark squares are where the sum of row and column indices is odd
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE; // Check if within board bounds
    }
    public boolean isEmptySquare(int row, int col) {
        return getPieceAt(row, col)==null; // Check if the square is empty
    }
    public boolean isOccupiedSquare(int row, int col) {
        return getPieceAt(row, col) != null; // Check if the square is occupied
    }
    public boolean hasEnemyPiece(int row, int col, Color playerColor) {
        Piece piece = getPieceAt(row, col);
        return piece != null && piece.getColor() != playerColor; // Check if the square has an enemy piece
    }
    public boolean hasFriendlyPiece(int row, int col, Color playerColor) {
        Piece piece = getPieceAt(row, col);
        return piece != null && piece.getColor() == playerColor; // Check if the square has a friendly piece
    }
    //Core Board Methods
    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null; // Invalid position
        }
        return board[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (!isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + col + ")");
        }else{
            board[row][col] = piece; // Set the piece at the specified position
            if (piece != null) {
                piece.setPosition(row, col); // Update the piece's position
                
        }
    }
    }
    public Piece removePiece(int row, int col)
    {
        Piece removedPiece = getPieceAt(row,col);
        if(removedPiece !=null){
            board[row][col] = null;
            updatePieceCount(removedPiece, false); // Decrease the piece count
        }
        return removedPiece;
    }
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
    Piece piece = removePiece(fromRow, fromCol);
    if (piece != null) {
        setPieceAt(toRow, toCol, piece);
        piece.incrementMoveCount();
        
        // Check for promotion
        if (piece.canBePromoted()) {
            promotePiece(piece);
        }
    }
    }
    //Piece Management
    public void capturePiece(int row, int col){
        Piece capturedPiece = removePiece(row, col);
        if(capturedPiece != null){
            if(capturedPiece.isWhite()){
                capturedWhitePieces.add(capturedPiece);
            } else
                {
                capturedBlackPieces.add(capturedPiece);
            }
            updatePieceCount(capturedPiece, false); // Decrease the piece count
        }
    }
    public void promotePiece(Piece piece) {
        if (piece.canBePromoted()) {
            piece.promote();
            if (piece.isWhite()) {
                whiteKingsCount++;
            } else {
                blackKingsCount++;
            }
        }
    }
    public void updatePieceCount(Piece piece, boolean isAdding) {
        int delta = isAdding ? 1 : -1;
        if (piece.isWhite()) {
            whitePiecesCount += delta;
            if (piece.isKing()) {
                whiteKingsCount += delta;
            }
        } else {
            blackPiecesCount += delta;
            if (piece.isKing()) {
                blackKingsCount += delta;
            }
        }
        
    }

    //Board Queries
    public List<Piece> getAllPieces(Color color) {
    List<Piece> pieces = new ArrayList<>();
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece piece = getPieceAt(row, col);
            if (piece != null && piece.getColor() == color) {
                pieces.add(piece);
            }
        }
    }
    return pieces;
}

    public List<Piece> getAllKings(Color color) {
        return getAllPieces(color).stream()
                .filter(Piece::isKing)
                .collect(Collectors.toList());
    }

    public List<Piece> getAllRegularPieces(Color color) {
        return getAllPieces(color).stream()
                .filter(Piece::isRegular)
                .collect(Collectors.toList());
    }

    public int getPieceCount(Color color) {
        return color == Color.WHITE ? whitePiecesCount : blackPiecesCount;
    }

    public int getKingCount(Color color) {
        return color == Color.WHITE ? whiteKingsCount : blackKingsCount;
    
    }
    public List<Piece> getCapturedPieces(Color color) {
        return color == Color.WHITE ? 
            new ArrayList<>(capturedWhitePieces) : 
            new ArrayList<>(capturedBlackPieces);
    }

    //Board State Management
    public void saveState(){
        BoardState state = new BoardState();
        state.boardSnapshot = cloneBoardArray();
        state.whiteCount = whitePiecesCount;
        state.blackCount = blackPiecesCount;
        state.whiteKings = whiteKingsCount;
        state.blackKings = blackKingsCount;
        state.captuWHITEWhite = new ArrayList<>(captuWhitePieces);
        state.captuWHITEBlack = new ArrayList<>(captuBlackPieces);
        
        moveHistory.push(state);
    }
    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    public void undoLastMove(){
        if (canUndo()) {
            BoardState previousState = moveHistory.pop();
            restoreState(previousState);
            moveCount--; // Decrease move count on undo
        }
    }
private void restoreState(BoardState state){
    //restore board
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[row][col] = state.boardSnapshot[row][col];
        }
    }
    // Restore counters
    whitePiecesCount = state.whiteCount;
    blackPiecesCount = state.blackCount;
    whiteKingsCount = state.whiteKings;
    blackKingsCount = state.blackKings;
    
    // Restore captuWHITE pieces
    captuWhitePieces.addAll(state.captuWHITEWhite);
    captuWhitePieces.clear();
    captuBlackPieces.clear();
    captuBlackPieces.addAll(state.captuWHITEBlack);
}

private Piece[][] cloneBoardArray() {
    Piece[][] clone = new Piece[BOARD_SIZE][BOARD_SIZE];
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece original = board[row][col];
            clone[row][col] = original != null ? original.clone() : null;
        }
    }
    return clone;
}
    public boolean hasValidMoves(Color color) {
    List<Piece> pieces = getAllPieces(color);
    for (Piece piece : pieces) {
        if (hasValidMovesForPiece(piece)) {
            return true;
        }
    }
    return false;
}

private boolean hasValidMovesForPiece(Piece piece) {
    int row = piece.getRow();
    int col = piece.getCol();
    
    // Check all four diagonal directions
    int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    
    for (int[] dir : directions) {
        if (piece.canMoveInDirection(dir[0])) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            // Simple move
            if (isValidPosition(newRow, newCol) && isEmptySquare(newRow, newCol)) {
                return true;
            }
            
            // Jump move
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];
            if (isValidPosition(jumpRow, jumpCol) && 
                isEmptySquare(jumpRow, jumpCol) &&
                hasEnemyPiece(newRow, newCol, piece.getColor())) {
                return true;
            }
        }
    }
    
    return false;
}

public boolean isGameOver() {
    return getPieceCount(Color.WHITE) == 0 || 
           getPieceCount(Color.BLACK) == 0 ||
           !hasValidMoves(Color.WHITE) || 
           !hasValidMoves(Color.BLACK);
}

public Color getWinner() {
    if (getPieceCount(Color.WHITE) == 0 || !hasValidMoves(Color.WHITE)) {
        return Color.BLACK;
    } else if (getPieceCount(Color.BLACK) == 0 || !hasValidMoves(Color.BLACK)) {
        return Color.WHITE;
    }
    return null; // Game not over
}

    public Board clone() {
    Board clonedBoard = new Board();
    
    // Clone board array
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece original = this.board[row][col];
            clonedBoard.board[row][col] = original != null ? original.clone() : null;
        }
    }
    
    // Clone other attributes
    clonedBoard.whitePiecesCount = this.whitePiecesCount;
    clonedBoard.blackPiecesCount = this.blackPiecesCount;
    clonedBoard.whiteKingsCount = this.whiteKingsCount;
    clonedBoard.blackKingsCount = this.blackKingsCount;
    clonedBoard.moveCount = this.moveCount;
    clonedBoard.lastMovedColor = this.lastMovedColor;
    
    // Clone captuWhite pieces
    clonedBoard.captuWhitePieces.addAll(this.captuWhitePieces);
    clonedBoard.captuWhitePieces.addAll(this.captuWhitePieces);
    return clonedBoard;
}

@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("  0 1 2 3 4 5 6 7\n");
    
    for (int row = 0; row < BOARD_SIZE; row++) {
        sb.append(row).append(" ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece piece = getPieceAt(row, col);
            if (piece != null) {
                sb.append(piece.getDisplayString());
            } else if (isDarkSquare(row, col)) {
                sb.append("□");
            } else {
                sb.append("■");
            }
            sb.append(" ");
        }
        sb.append("\n");
    }
    
    sb.append("White: ").append(whitePiecesCount).append(" pieces, ")
      .append(whiteKingsCount).append(" kings\n");
    sb.append("Black: ").append(blackPiecesCount).append(" pieces, ")
      .append(blackKingsCount).append(" kings\n");
    
    return sb.toString();
}

// Getters
public int getMoveCount() { return moveCount; }
public Color getLastMovedColor() { return lastMovedColor; }
public void setLastMovedColor(Color color) { this.lastMovedColor = color; }
public void incrementMoveCount() { this.moveCount++; }







}
