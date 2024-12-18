package game;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The {@code HexBoardDisplay} class provides a graphical representation of the hexagonal board.
 * It creates a window with a hexagonal map drawn inside, illustrating each sector's level 
 * and coordinates, as well as a legend explaining the color coding of sector levels.
 */
public class HexBoardDisplay extends JFrame {
    // The map of Hex coordinates to Sector objects that represents the board's state
    private Map<Hex, Sector> board;

    /**
     * Constructs a new HexBoardDisplay window and initializes it with a given HexBoard.
     *
     * @param hexBoard the HexBoard whose layout will be displayed
     */
    public HexBoardDisplay(HexBoard hexBoard) {
        this.board = hexBoard.getBoard();
        setTitle("Hex Board Visualization");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(0, 0);      // Set the window at the top-left corner of the screen
        setAlwaysOnTop(true);   // Keep the window always on top
    }

    /**
     * Overrides the paint method of JFrame to draw the board and legend.
     *
     * @param g the Graphics context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother lines and edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHexBoard(g2d);
        drawLegend(g2d);
    }

    /**
     * Draws the hexagonal board based on the coordinates and levels stored in the board map.
     *
     * @param g the Graphics object used to draw
     */
    private void drawHexBoard(Graphics g) {
        int size = 30;         // The radius (in pixels) of each hex cell
        int offsetX = getWidth() / 3;  // Horizontal offset to center the board
        int offsetY = getHeight() / 2; // Vertical offset to center the board

        for (Map.Entry<Hex, Sector> entry : board.entrySet()) {
            Hex hex = entry.getKey();
            Sector sector = entry.getValue();

            // Calculate the pixel coordinates for the hex center based on its cube coordinates
            int x = offsetX + (int) ((3.0 / 2 * hex.q) * size);
            int y = offsetY + (int) ((Math.sqrt(3) * (hex.r + hex.q / 2.0)) * size);

            drawHex(g, x, y, size, sector.getSector(), hex);
        }
    }

    /**
     * Draws a single hex cell, including its outline, fill color based on sector level,
     * and the hex coordinates at its center.
     *
     * @param g     the Graphics object used for drawing
     * @param x     the x-coordinate of the hex center in pixels
     * @param y     the y-coordinate of the hex center in pixels
     * @param size  the radius of the hex cell
     * @param level the sector level of this hex
     * @param hex   the Hex object representing the hex's cube coordinates
     */
    private void drawHex(Graphics g, int x, int y, int size, int level, Hex hex) {
        int[] cx = new int[6];
        int[] cy = new int[6];

        // Compute the polygon points for the hex cell
        for (int i = 0; i < 6; i++) {
            cx[i] = (int) (x + size * Math.cos(i * Math.PI / 3));
            cy[i] = (int) (y + size * Math.sin(i * Math.PI / 3));
        }

        // Draw the hex outline
        g.setColor(Color.BLACK);
        g.drawPolygon(cx, cy, 6);

        // Determine the fill color based on the sector level
        Color color;
        switch (level) {
            case 0: color = Color.LIGHT_GRAY; break;
            case 1: color = Color.BLUE; break;
            case 2: color = Color.ORANGE; break;
            case 3: color = Color.GREEN; break;
            default: color = Color.BLACK;
        }
        g.setColor(color);
        g.fillPolygon(cx, cy, 6);

        // Draw the hex coordinates in the center as white text
        g.setColor(Color.WHITE);
        String coord = String.format("(%d,%d,%d)", hex.q, hex.r, hex.s);
        g.drawString(coord, x - g.getFontMetrics().stringWidth(coord) / 2, y + 5);
    }

    /**
     * Draws a legend on the right side of the window explaining the color coding for each sector level.
     *
     * @param g the Graphics object used for drawing
     */
    private void drawLegend(Graphics g) {
        int legendX = getWidth() - 250; // X-position for the legend area
        int legendY = 100;             // Y-position for the top of the legend
        int boxSize = 30;              // The size of the color boxes in the legend

        // Title for the legend
        g.setColor(Color.BLACK);
        g.drawString("Sector Levels:", legendX, legendY - 20);

        // Draw and label the Level 0 box
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(legendX, legendY, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY, boxSize, boxSize);
        g.drawString("Level 0", legendX + boxSize + 10, legendY + boxSize / 2 + 5);

        // Draw and label the Level 1 box
        g.setColor(Color.BLUE);
        g.fillRect(legendX, legendY + 50, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 50, boxSize, boxSize);
        g.drawString("Level 1", legendX + boxSize + 10, legendY + 50 + boxSize / 2 + 5);

        // Draw and label the Level 2 box
        g.setColor(Color.ORANGE);
        g.fillRect(legendX, legendY + 100, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 100, boxSize, boxSize);
        g.drawString("Level 2", legendX + boxSize + 10, legendY + 100 + boxSize / 2 + 5);

        // Draw and label the Level 3 box
        g.setColor(Color.GREEN);
        g.fillRect(legendX, legendY + 150, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 150, boxSize, boxSize);
        g.drawString("Level 3", legendX + boxSize + 10, legendY + 150 + boxSize / 2 + 5);
    }

    /**
     * The main method to run a demonstration of the HexBoardDisplay.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        HexBoard hexBoard = new HexBoard();
        EventQueue.invokeLater(() -> {
            new HexBoardDisplay(hexBoard).setVisible(true);
        });
    }
}
