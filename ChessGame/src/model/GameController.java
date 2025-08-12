package model;

import java.util.List;

public class GameController {
    private Board board;
    private HumanPlayer humanPlayer;
    private ComputerPlayer computerPlayer;
    private Player currentPlayer;
    private boolean gameRunning;
    private GameState gameState;

    public enum GameState {
        PLAYING,
        GAME_OVER,
        PAUSED
    }

    public GameController() {
        initializeGame();
    }

    private void initializeGame() {
        board = new Board();
        humanPlayer = new HumanPlayer("Player", Piece.Color.WHITE);
        computerPlayer = new ComputerPlayer("Computer", Piece.Color.BLACK);
        currentPlayer = humanPlayer;
        gameRunning = true;
        gameState = GameState.PLAYING;
    }

    public void startNewGame() {
        board.resetBoard();
        currentPlayer = humanPlayer;
        gameRunning = true;
        gameState = GameState.PLAYING;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isHumanTurn() {
        return currentPlayer == humanPlayer;
    }

    public List<Move> getValidMoves() {
        return board.getValidMoves(currentPlayer.getColor());
    }

    public boolean makeMove(Move move) {
        if (!gameRunning || move == null) {
            return false;
        }

        // Apply the move
        board.applyMove(move);
        
        // Check for game over
        if (board.isGameOver()) {
            gameRunning = false;
            gameState = GameState.GAME_OVER;
            return true;
        }

        // Switch players
        currentPlayer = (currentPlayer == humanPlayer) ? computerPlayer : humanPlayer;
        
        return true;
    }

    public void makeComputerMove() {
        if (!gameRunning || currentPlayer != computerPlayer) {
            return;
        }

        List<Move> validMoves = board.getValidMoves(computerPlayer.getColor());
        if (!validMoves.isEmpty()) {
            Move computerMove = computerPlayer.makeMove(board, validMoves);
            if (computerMove != null) {
                makeMove(computerMove);
            }
        }
    }

    public boolean canUndo() {
        return board.canUndo();
    }

    public void undoMove() {
        if (canUndo()) {
            board.undoLastMove();
            // Switch back to previous player
            currentPlayer = (currentPlayer == humanPlayer) ? computerPlayer : humanPlayer;
        }
    }

    public Piece.Color getWinner() {
        if (gameState == GameState.GAME_OVER) {
            return board.getWinner();
        }
        return null;
    }

    public String getGameStatus() {
        if (gameState == GameState.GAME_OVER) {
            Piece.Color winner = getWinner();
            if (winner != null) {
                return winner == Piece.Color.WHITE ? "White wins!" : "Black wins!";
            } else {
                return "Game ended in a draw!";
            }
        } else if (gameState == GameState.PLAYING) {
            return "Current player: " + (currentPlayer == humanPlayer ? "White" : "Black");
        }
        return "Game paused";
    }
}
