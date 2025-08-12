package ui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BoardPanel extends JPanel {
    private static final int SQUARE_SIZE = 60;
    private static final int BOARD_SIZE = 8;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);
    private static final Color SELECTED_COLOR = new Color(0, 255, 0, 100);
    
    private GameController gameController;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Move> validMoves;
    private List<int[]> highlightedSquares;
    
    public BoardPanel(GameController gameController) {
        this.gameController = gameController;
        this.validMoves = gameController.getValidMoves();
        this.highlightedSquares = gameController.getBoard().getAllPieces(Piece.Color.WHITE).stream()
            .map(p -> new int[]{p.getRow(), p.getCol()})
            .toList();
        
        setPreferredSize(new Dimension(BOARD_SIZE * SQUARE_SIZE, BOARD_SIZE * SQUARE_SIZE));
        setBackground(Color.BLACK);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }
    
    private void handleMouseClick(MouseEvent e) {
        if (!gameController.isHumanTurn()) {
            return;
        }
        
        int x = e.getX();
        int y = e.getY();
        
        int col = x / SQUARE_SIZE;
        int row = y / SQUARE_SIZE;
        
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            handleSquareClick(row, col);
        }
    }
    
    private void handleSquareClick(int row, int col) {
        Board board = gameController.getBoard();
        Piece clickedPiece = board.getPieceAt(row, col);
        
        // If no piece is selected, try to select a piece
        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.getColor() == gameController.getCurrentPlayer().getColor()) {
                selectedRow = row;
                selectedCol = col;
                updateValidMoves();
                repaint();
            }
        } else {
            // Check if clicking on the same piece (deselect)
            if (row == selectedRow && col == selectedCol) {
                selectedRow = -1;
                selectedCol = -1;
                highlightedSquares = List.of();
                repaint();
                return;
            }
            
            // Check if clicking on another piece of the same color (change selection)
            if (clickedPiece != null && clickedPiece.getColor() == gameController.getCurrentPlayer().getColor()) {
                selectedRow = row;
                selectedCol = col;
                updateValidMoves();
                repaint();
                return;
            }
            
            // Try to make a move
            Move selectedMove = findMoveToDestination(row, col);
            if (selectedMove != null) {
                gameController.makeMove(selectedMove);
                selectedRow = -1;
                selectedCol = -1;
                highlightedSquares = List.of();
                updateValidMoves();
                repaint();
                
                // Make computer move if it's computer's turn
                if (!gameController.isHumanTurn()) {
                    SwingUtilities.invokeLater(() -> {
                        gameController.makeComputerMove();
                        updateValidMoves();
                        repaint();
                    });
                }
            }
        }
    }
    
    private void updateValidMoves() {
        if (selectedRow != -1 && selectedCol != -1) {
            Board board = gameController.getBoard();
            Piece selectedPiece = board.getPieceAt(selectedRow, selectedCol);
            if (selectedPiece != null) {
                validMoves = board.getValidMoves(selectedPiece.getColor());
                highlightedSquares = validMoves.stream()
                    .filter(move -> move.getFromRow() == selectedRow && move.getFromCol() == selectedCol)
                    .map(move -> new int[]{move.getToRow(), move.getToCol()})
                    .toList();
            }
        } else {
            validMoves = gameController.getValidMoves();
            highlightedSquares = List.of();
        }
    }
    
    private Move findMoveToDestination(int toRow, int toCol) {
        if (selectedRow == -1 || selectedCol == -1) {
            return null;
        }
        
        for (Move move : validMoves) {
            if (move.getFromRow() == selectedRow && 
                move.getFromCol() == selectedCol &&
                move.getToRow() == toRow && 
                move.getToCol() == toCol) {
                return move;
            }
        }
        return null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBoard(g2d);
        drawPieces(g2d);
        drawHighlights(g2d);
    }
    
    private void drawBoard(Graphics2D g2d) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                
                Color squareColor = (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                g2d.setColor(squareColor);
                g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                
                // Draw border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
    
    private void drawPieces(Graphics2D g2d) {
        Board board = gameController.getBoard();
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    drawPiece(g2d, piece, row, col);
                }
            }
        }
    }
    
    private void drawPiece(Graphics2D g2d, Piece piece, int row, int col) {
        int x = col * SQUARE_SIZE + SQUARE_SIZE / 2;
        int y = row * SQUARE_SIZE + SQUARE_SIZE / 2;
        int radius = SQUARE_SIZE / 3;
        
        // Draw piece shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x - radius + 2, y - radius + 2, radius * 2, radius * 2);
        
        // Draw piece base
        Color pieceColor = piece.getColor() == Piece.Color.WHITE ? Color.WHITE : Color.BLACK;
        g2d.setColor(pieceColor);
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        
        // Draw piece border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        
        // Draw king crown if it's a king
        if (piece.isKing()) {
            g2d.setColor(piece.getColor() == Piece.Color.WHITE ? Color.BLACK : Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String crown = "♔";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x - fm.stringWidth(crown) / 2;
            int textY = y + fm.getAscent() / 2;
            g2d.drawString(crown, textX, textY);
        }
        
        // Highlight selected piece
        if (row == selectedRow && col == selectedCol) {
            g2d.setColor(SELECTED_COLOR);
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }
    
    private void drawHighlights(Graphics2D g2d) {
        g2d.setColor(HIGHLIGHT_COLOR);
        for (int[] square : highlightedSquares) {
            int row = square[0];
            int col = square[1];
            int x = col * SQUARE_SIZE;
            int y = row * SQUARE_SIZE;
            g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
    
    public void updateDisplay() {
        updateValidMoves();
        repaint();
    }
}
