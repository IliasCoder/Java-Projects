package model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class ComputerPlayer extends Player {
    private boolean isConnected = false; // Connection status for networked players
    //pick a random move if multiple moves are available
    private final Random random = new Random();
    public ComputerPlayer(String name, Piece.Color color){

    
        super(name, color);
    }

    @Override
    public Move makeMove(Board board, List<Move> validMoves) {
        // Implement AI logic to select a move from validMoves
        // For now, just return the first valid move as a placeholder
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        for (Move move : validMoves) {
            Board boardCopy = board.clone(); // Assume Board has a clone method
            boardCopy.applyMove(move); // Assume Board has an applyMove method
            int value = minimax(Board boardCopy,int searchDepth-1, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        
        }
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    private int minimax(Board baord, int depth, boolean isMaximizing) {
        if (depth == 0) {
            return evaluateBoard(baord);
        }
        List<Move> validMoves = baord.getAllValidMoves(getColor());
        if (validMoves.isEmpty()) {
            return evaluateBoard(baord);
        }
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : validMoves) {
                Board boardCopy = baord.clone();
                boardCopy.applyMove(move);
                int eval = minimax(boardCopy, depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : validMoves) {
                Board boardCopy = baord.clone();
                boardCopy.applyMove(move);
                int eval = minimax(boardCopy, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private int evaluateBoard(Board board){
        //simple evaluation: difference in piece count
        int myPieces = board.countPieces(getColor());
        int opponentPieces = board.countPieces(getColor().opposite());
        return myPieces - opponentPieces;
    }



    @Override
    public boolean isReady() {
        return true; // AI is always ready
    }
    private int searchDepth =3; // Depth for AI search, can be adjusted for difficulty

    @Override
    public void initialize() {
        // Initialization logic for AI player if needed  
        // Reset or initialize AI state if needed
        // For example: reset any cached board analysis, clear move history, etc.
        // This is a good place to prepare the AI for a new game.
    }

    @Override
    public void cleanup() {
        // Cleanup resources if needed
    }

    @Override
    public void cancelMove() {
        // AI-specific cancel move logic if needed
    }

    @Override
    public void endTurn() {
        // AI-specific end turn logic if needed
    }

    @Override
    public void startTurn() {
        // AI-specific start turn logic if needed
    }
    
}
