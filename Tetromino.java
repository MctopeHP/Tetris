package Tetris;

import java.awt.Color;
import java.util.Random;

public class Tetromino {
    public int[][] shape;
    public Color color;
    public int x, y;

    private static final int[][][] SHAPES = {
        // I
        {
            {1, 1, 1, 1}
        },
        // O
        {
            {1, 1},
            {1, 1}
        },
        // T
        {
            {0, 1, 0},
            {1, 1, 1}
        },
        // S
        {
            {0, 1, 1},
            {1, 1, 0}
        },
        // Z
        {
            {1, 1, 0},
            {0, 1, 1}
        },
        // J
        {
            {1, 0, 0},
            {1, 1, 1}
        },
        // L
        {
            {0, 0, 1},
            {1, 1, 1}
        }
    };

    private static final Color[] COLORS = {
        Color.CYAN,    // I
        Color.YELLOW,  // O
        Color.MAGENTA, // T
        Color.GREEN,   // S
        Color.RED,     // Z
        Color.BLUE,    // J
        Color.ORANGE   // L
    };

    public Tetromino() {
        Random rand = new Random();
        int index = rand.nextInt(SHAPES.length);
        shape = SHAPES[index];
        color = COLORS[index];
        x = 3;
        y = -1; // Cambiado para que aparezca justo encima del tablero
    }

    public int[][] getRotatedShape() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                rotated[col][rows - 1 - row] = shape[row][col];
            }
        }
        return rotated;
    }
}