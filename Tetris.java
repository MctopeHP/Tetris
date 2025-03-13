package Game_Loop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel implements ActionListener, KeyListener {
    private static final int BOARD_WIDTH = 10; // Ancho del tablero
    private static final int BOARD_HEIGHT = 20; // Alto del tablero
    private static final int TILE_SIZE = 30; // Tamaño de cada bloque
    private Timer timer;
    private boolean[][] board; // Tablero del juego
    private Tetrimino currentPiece; // Pieza actual
    private int currentX, currentY; // Posición de la pieza actual
    private boolean gameOver; // Estado del juego

    // Constructor
    public Tetris() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(500, this); // Temporizador para la caída de las piezas
        spawnPiece();
        timer.start();
    }

    // Generar una nueva pieza
    private void spawnPiece() {
        currentPiece = Tetrimino.randomPiece();
        currentX = BOARD_WIDTH / 2 - 1;
        currentY = 0;

        if (collides(currentX, currentY, currentPiece)) {
            gameOver = true;
            timer.stop();
        }
    }

    // Verificar colisiones
    private boolean collides(int x, int y, Tetrimino piece) {
        for (int row = 0; row < piece.getShape().length; row++) {
            for (int col = 0; col < piece.getShape()[row].length; col++) {
                if (piece.getShape()[row][col]) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT || (newY >= 0 && board[newY][newX])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Fijar la pieza en el tablero
    private void fixPiece() {
        for (int row = 0; row < currentPiece.getShape().length; row++) {
            for (int col = 0; col < currentPiece.getShape()[row].length; col++) {
                if (currentPiece.getShape()[row][col]) {
                    board[currentY + row][currentX + col] = true;
                }
            }
        }
        clearLines();
        spawnPiece();
    }

    // Eliminar líneas completas
    private void clearLines() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean fullLine = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (!board[row][col]) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }
                board[0] = new boolean[BOARD_WIDTH];
            }
        }
    }

    // Dibujar el tablero y las piezas
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar el tablero
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col]) {
                    g.setColor(Color.CYAN);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Dibujar la pieza actual
     // Dibujar la pieza actual en el tablero
        if (currentPiece != null) {
            // Establecer el color de la pieza actual
            g.setColor(currentPiece.getColor());
            
            // Obtener la forma de la pieza actual
            boolean[][] shape = currentPiece.getShape();
            
            // Iterar sobre cada fila y columna de la forma de la pieza
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    // Si la celda está ocupada, dibujar un rectángulo en la posición correspondiente
                    if (shape[row][col]) {
                        int x = (currentX + col) * TILE_SIZE;
                        int y = (currentY + row) * TILE_SIZE;
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Dibujar "Game Over"
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", BOARD_WIDTH * TILE_SIZE / 2 - 30, BOARD_HEIGHT * TILE_SIZE / 2);
        }
    }

    // Lógica del temporizador
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (!collides(currentX, currentY + 1, currentPiece)) {
                currentY++;
            } else {
                fixPiece();
            }
            repaint();
        }
    }

    // Manejo de entrada del teclado
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (!collides(currentX - 1, currentY, currentPiece)) {
                    currentX--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!collides(currentX + 1, currentY, currentPiece)) {
                    currentX++;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (!collides(currentX, currentY + 1, currentPiece)) {
                    currentY++;
                }
                break;
            case KeyEvent.VK_UP:
                Tetrimino rotated = currentPiece.rotate();
                if (!collides(currentX, currentY, rotated)) {
                    currentPiece = rotated;
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Método principal
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris tetris = new Tetris();
        frame.add(tetris);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// Clase para representar las piezas (Tetriminos)
class Tetrimino {
    private boolean[][] shape;
    private Color color;

    public Tetrimino(boolean[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    // Rotar la pieza
    public Tetrimino rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        boolean[][] rotated = new boolean[cols][rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                rotated[col][rows - 1 - row] = shape[row][col];
            }
        }
        return new Tetrimino(rotated, color);
    }

    // Generar una pieza aleatoria
    public static Tetrimino randomPiece() {
        Tetrimino[] pieces = {
            new Tetrimino(new boolean[][]{
                {true, true, true, true}
            }, Color.CYAN), // I
            new Tetrimino(new boolean[][]{
                {true, true},
                {true, true}
            }, Color.YELLOW), // O
            new Tetrimino(new boolean[][]{
                {false, true, false},
                {true, true, true}
            }, Color.MAGENTA), // T
            new Tetrimino(new boolean[][]{
                {true, false, false},
                {true, true, true}
            }, Color.ORANGE), // L
            new Tetrimino(new boolean[][]{
                {false, false, true},
                {true, true, true}
            }, Color.BLUE), // J
            new Tetrimino(new boolean[][]{
                {false, true, true},
                {true, true, false}
            }, Color.GREEN), // S
            new Tetrimino(new boolean[][]{
                {true, true, false},
                {false, true, true}
            }, Color.RED) // Z
        };
        return pieces[(int) (Math.random() * pieces.length)];
    }
}