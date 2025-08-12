package ui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ui.BoardPanel;
public class CheckersGameWindow extends JFrame {
    private GameController gameController;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel whitePiecesLabel;
    private JLabel blackPiecesLabel;
    private JButton newGameButton;
    private JButton undoButton;
    
    public CheckersGameWindow() {
        gameController = new GameController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Checkers Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create board panel
        boardPanel = new BoardPanel(gameController);
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create status panel
        JPanel statusPanel = createStatusPanel();
        
        // Add components to main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Pack and center the window
        pack();
        setLocationRelativeTo(null);
        
        // Update initial display
        updateDisplay();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // New Game button
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setMaximumSize(new Dimension(120, 30));
        
        // Undo button
        undoButton = new JButton("Undo Move");
        undoButton.addActionListener(e -> undoMove());
        undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        undoButton.setMaximumSize(new Dimension(120, 30));
        
        // Add some spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(newGameButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(undoButton);
        panel.add(Box.createVerticalStrut(10));
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Game Status"));
        
        // Status label
        statusLabel = new JLabel("White's turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        
        // White pieces count
        whitePiecesLabel = new JLabel("White: 12 pieces");
        whitePiecesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        whitePiecesLabel.setBorder(BorderFactory.createEtchedBorder());
        
        // Black pieces count
        blackPiecesLabel = new JLabel("Black: 12 pieces");
        blackPiecesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blackPiecesLabel.setBorder(BorderFactory.createEtchedBorder());
        
        panel.add(statusLabel);
        panel.add(whitePiecesLabel);
        panel.add(blackPiecesLabel);
        
        return panel;
    }
    
    private void startNewGame() {
        gameController.startNewGame();
        updateDisplay();
    }
    
    private void undoMove() {
        if (gameController.canUndo()) {
            gameController.undoMove();
            updateDisplay();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No moves to undo!", 
                "Undo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateDisplay() {
        // Update status
        statusLabel.setText(gameController.getGameStatus());
        
        // Update piece counts
        Board board = gameController.getBoard();
        int whitePieces = board.getPieceCount(Piece.Color.WHITE);
        int blackPieces = board.getPieceCount(Piece.Color.BLACK);
        int whiteKings = board.getKingCount(Piece.Color.WHITE);
        int blackKings = board.getKingCount(Piece.Color.BLACK);
        
        whitePiecesLabel.setText(String.format("White: %d pieces (%d kings)", whitePieces, whiteKings));
        blackPiecesLabel.setText(String.format("Black: %d pieces (%d kings)", blackPieces, blackKings));
        
        // Update undo button
        undoButton.setEnabled(gameController.canUndo());
        
        // Update board display
        boardPanel.updateDisplay();
        
        // Check for game over
        if (gameController.getGameState() == GameController.GameState.GAME_OVER) {
            String winner = gameController.getWinner() == Piece.Color.WHITE ? "White" : "Black";
            JOptionPane.showMessageDialog(this, 
                winner + " wins the game!", 
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show the game window
        SwingUtilities.invokeLater(() -> {
            CheckersGameWindow gameWindow = new CheckersGameWindow();
            gameWindow.setVisible(true);
        });
    }
}
