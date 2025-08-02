package model;

import java.util.List;

public abstract class Player {
    private String name;
    private Piece.Color color;
    private int score;
    //stats and history can be added later
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private List<String> moveHistory; 
    // Timing & Control
    private long lastMoveTimeMillis;
    private long moveTimeoutMillis = 30000; // default 30 seconds
    private long turnStartTimeMillis;
    private long maxThinkingTimeMillis = 60000; // default 60 seconds

    // Unique player ID
    private final String playerId = java.util.UUID.randomUUID().toString();

    public Player(String name, Piece.Color color) {
        this.name = name;
        this.color = color;
        this.score = 0; // Initialize score to 0
    }

    public String getName() {
        return name;
    }

    public Piece.Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }
    public void resetScore() {
        this.score = 0;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setColor(Piece.Color color) {
        this.color = color;
    }
    public enum PlayerType {
        HUMAN, // Human player
        AI // AI player
    }
    public enum playerColor{
        BLACK,
        WHITE
    }

    //Game State
    private boolean isTurn; // Indicates if it's this player's turn
    private boolean isAI; // Indicates if this player is controlled by AI
    public boolean isTurn() {
        return isTurn;
    }
    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
    public boolean isAI() {
        return isAI;
    }
    public void setAI(boolean isAI) {
        this.isAI = isAI;
    }
    public boolean isReadyToPlay() {
        return isTurn && !isAI; // Player is ready if it's their turn and they are not AI
    }
    public boolean isReadyToPlayAI() {
        return isTurn && isAI; // Player is ready if it's their turn and they are AI
    }
    public boolean isActive() {
        return isTurn; // Player is active if it's their turn
    }
    //Connection Status for multiplayer games
    private boolean isConnected; // Indicates if the player is connected in a multiplayer game
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    public void toggleConnection() {
        this.isConnected = !this.isConnected; // Toggle connection status
    }
    public abstract void makeMove(); // Abstract method for making a move
    public abstract boolean isReady(); // Abstract method to check if the player is ready
    public abstract void initialize(); // Abstract method for initializing the player
    public abstract void cleanup(); // Abstract method for cleaning up resources
    public abstract void startTurn(); // Abstract method to start the player's turn
    public abstract void endTurn(); // Abstract method to end the player's turn
    public abstract void cancelMove(); // Abstract method to cancel the current move
    











}

   

