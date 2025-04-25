package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel implements ActionListener {
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int CELL_SIZE = 30;
    private Timer timer;
    private boolean isFalling = false;
    private boolean isPaused = false;
    private boolean gameOver = false;
    private Color[][] board;
    private Tetromino currentPiece;
    private int score;
    private int linesCleared;

    public Tetris() {
        setPreferredSize(new Dimension(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE));
        setBackground(Color.BLACK);
        timer = new Timer(500, this);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        setFocusable(true);
        startGame();
    }

    private void startGame() {
        board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
        score = 0;
        linesCleared = 0;
        gameOver = false;
        currentPiece = new Tetromino();
        timer.stop();
        timer.setDelay(500);
        timer.start();
        isPaused = false;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused || gameOver) return;

        if (!movePieceDown()) {
            placePiece();
            clearFullRows();
            currentPiece = new Tetromino();
            if (!isPositionValid(currentPiece.shape, currentPiece.x, currentPiece.y)) {
                gameOver = true;
                timer.stop();
                repaint();
                JOptionPane.showMessageDialog(this, "Game Over! Puntaje: " + score);
                startGame();
                return;
            }
        }
        repaint();
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!gameOver) isPaused = !isPaused;
            repaint();
            return;
        }
        if (isPaused || gameOver) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> movePiece(-1, 0);
            case KeyEvent.VK_RIGHT -> movePiece(1, 0);
            case KeyEvent.VK_DOWN -> movePieceDown();
            case KeyEvent.VK_UP -> rotatePiece();
        }
        repaint();
    }

    private boolean movePiece(int dx, int dy) {
        if (isPositionValid(currentPiece.shape, currentPiece.x + dx, currentPiece.y + dy)) {
            currentPiece.x += dx;
            currentPiece.y += dy;
            return true;
        }
        return false;
    }

    private boolean movePieceDown() {
        return movePiece(0, 1);
    }

    private void rotatePiece() {
        int[][] rotated = currentPiece.getRotatedShape();
        if (isPositionValid(rotated, currentPiece.x, currentPiece.y)) {
            currentPiece.shape = rotated;
        }
    }

    private boolean isPositionValid(int[][] shape, int x, int y) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;
                    if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT || (newY >= 0 && board[newY][newX] != null)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placePiece() {
        for (int row = 0; row < currentPiece.shape.length; row++) {
            for (int col = 0; col < currentPiece.shape[row].length; col++) {
                if (currentPiece.shape[row][col] != 0) {
                    board[currentPiece.y + row][currentPiece.x + col] = currentPiece.color;
                }
            }
        }
    }

    private void clearFullRows() {
        int linesClearedThisMove = 0;
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int r = row; r > 0; r--) {
                    System.arraycopy(board[r - 1], 0, board[r], 0, BOARD_WIDTH);
                }
                board[0] = new Color[BOARD_WIDTH];
                score += 100;
                linesCleared++;
                linesClearedThisMove++;
                row++; // Revisar la misma fila otra vez
            }
        }
        if (linesClearedThisMove > 0 && linesCleared % 5 == 0) {
            int delay = Math.max(100, timer.getDelay() - 50);
            timer.setDelay(delay);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar el tablero
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Dibujar la pieza actual si no es game over
        if (!gameOver) {
            g.setColor(currentPiece.color);
            for (int row = 0; row < currentPiece.shape.length; row++) {
                for (int col = 0; col < currentPiece.shape[row].length; col++) {
                    if (currentPiece.shape[row][col] != 0) {
                        int drawX = (currentPiece.x + col) * CELL_SIZE;
                        int drawY = (currentPiece.y + row) * CELL_SIZE;
                        g.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                        g.setColor(currentPiece.color);
                    }
                }
            }
        }

        // Mostrar mensaje de pausa
        if (isPaused && !gameOver) {
            g.setColor(Color.WHITE);
            g.drawString("PAUSA", BOARD_WIDTH * CELL_SIZE / 2 - 20, BOARD_HEIGHT * CELL_SIZE / 2);
        }
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris game = new Tetris();
        
        JPanel sidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.drawString("Puntaje:", 9, 20);
                g.drawString(String.valueOf(game.getScore()), 10, 40);
                g.drawString("Líneas:", 9, 70);
                g.drawString(String.valueOf(game.getLinesCleared()), 10, 90);
                g.drawString("Velocidad:", 9, 120);
                g.drawString(String.valueOf(500 - game.timer.getDelay()) + "ms", 10, 140);
                g.drawString("Pausa: Esc", 9, 180);
            }
        };
        sidePanel.setPreferredSize(new Dimension(120, 600));
        sidePanel.setBackground(Color.DARK_GRAY);

        Timer repaintTimer = new Timer(100, e -> sidePanel.repaint());
        repaintTimer.start();

        frame.setLayout(new java.awt.BorderLayout());
        frame.add(game, java.awt.BorderLayout.CENTER);
        frame.add(sidePanel, java.awt.BorderLayout.EAST);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}