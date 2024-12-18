package game;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code OccupationDisplay} class provides a graphical interface 
 * to display details about which players and ships occupy each hex in the game board.
 * <p>
 * It organizes hexes by card label and shows the sector level, as well as 
 * any occupying player and ships. The display is shown in a separate scrollable 
 * window for easy reading.
 */
public class OccupationDisplay {
    // The HexBoard instance providing the board state and occupation information.
    private HexBoard hexBoard;
    // A static reference to the JFrame used for displaying occupation details.
    private static JFrame frame;

    /**
     * Constructs a new OccupationDisplay for the given HexBoard.
     *
     * @param hexBoard the HexBoard whose occupation details are to be displayed
     */
    public OccupationDisplay(HexBoard hexBoard) {
        this.hexBoard = hexBoard;
        initializeUI();
    }

    /**
     * Initializes the user interface components for the occupation details window.
     * If the frame has not been created yet, it creates a new JFrame with a JTextArea
     * for displaying the occupation information.
     */
    private void initializeUI() {
        if (frame == null) {
            frame = new JFrame("Occupation Details");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(600, 1000); // Set the window width and height directly
            frame.setLocation(1100, 30); // Position the window on the screen
            frame.setAlwaysOnTop(true);  // Keep this window always on top

            JTextArea textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);
        }
    }

    /**
     * Displays the occupation details in a structured format. 
     * Hexes are grouped and sorted by their card label, and Tri-Prime and Boundary 
     * hexes are specifically positioned in the display order.
     */
    public void displayOccupation() {
        StringBuilder display = new StringBuilder();

        // Retrieve occupation information and all hexes from the board
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();
        Map<Hex, Sector> allHexes = hexBoard.getBoard();

        // Build a map associating each card label with its list of hexes
        Map<String, List<Hex>> cardHexesMap = new HashMap<>();

        // Determine the card label for each hex and group them
        for (Hex hex : allHexes.keySet()) {
            String cardLabel = findCardLabelForHex(hex);
            cardHexesMap.computeIfAbsent(cardLabel, k -> new ArrayList<>()).add(hex);
        }

        // Sort card labels for structured output
        List<String> sortedCardLabels = new ArrayList<>(cardHexesMap.keySet());
        Collections.sort(sortedCardLabels);

        // Move "Tri-Prime" and "Boundary" labels to the desired positions
        sortedCardLabels.remove("Tri-Prime");
        sortedCardLabels.remove("Boundary");
        sortedCardLabels.add(0, "Tri-Prime");
        sortedCardLabels.add("Boundary");

        // Iterate through each card label group in sorted order
        for (String cardLabel : sortedCardLabels) {
            List<Hex> hexes = cardHexesMap.get(cardLabel);

            // Sort hexes by their cube coordinates for consistency
            hexes.sort(Comparator.comparingInt((Hex h) -> h.q)
                    .thenComparingInt(h -> h.r)
                    .thenComparingInt(h -> h.s));

            // Print a header for this card label
            display.append("=== ").append(cardLabel).append(" ===\n");

            // For each hex, format and display its occupation info
            for (Hex hex : hexes) {
                HexBoard.OccupationInfo info = occupationMap.get(hex);
                display.append(formatOccupationInfo(cardLabel, hex, info)).append("\n");
            }
        }

        // Update the text area with the constructed occupation info
        JTextArea textArea = (JTextArea) ((JScrollPane) frame.getContentPane().getComponent(0)).getViewport().getView();
        textArea.setText(display.toString());

        // Show the frame on the EDT
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

    /**
     * Finds the card label associated with a given hex. 
     * Tri-Prime hexes are identified first. If not found in Tri-Prime or any card,
     * the hex is considered part of the boundary.
     *
     * @param hex the hex to categorize
     * @return the card label associated with this hex, or "Boundary" if not applicable
     */
    private String findCardLabelForHex(Hex hex) {
        // Check if the hex is part of the Tri-Prime region first
        if (isTriPrimeHex(hex)) {
            return "Tri-Prime";
        }

        // Otherwise, check each card set
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();

        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            String cardLabel = entry.getKey();
            List<Hex> hexes = entry.getValue();
            if (hexes.contains(hex)) {
                return cardLabel;
            }
        }

        // If not Tri-Prime or part of any card, consider it boundary
        return "Boundary";
    }

    /**
     * Checks if a given hex is part of the Tri-Prime region.
     *
     * @param hex the hex to check
     * @return true if the hex is one of the Tri-Prime hexes, false otherwise
     */
    private boolean isTriPrimeHex(Hex hex) {
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        );
        return triPrimeHexes.contains(hex);
    }

    /**
     * Formats a string representing the occupation status of a given hex.
     * Includes the sector level, and if occupied, the controlling player and ships.
     *
     * @param cardLabel the card label under which this hex falls
     * @param hex       the hex coordinates
     * @param info      the occupation info for this hex, may be null if unoccupied
     * @return a formatted string describing the occupation of the hex
     */
    private String formatOccupationInfo(String cardLabel, Hex hex, HexBoard.OccupationInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hex ").append(hex)
          .append(",sector ").append(hexBoard.getBoard().get(hex).getSector());

        if (info != null && info.getNumberOfShips() > 0) {
            sb.append(",Occupied by player ").append(info.getPlayerId())
              .append(",Ships: ");

            List<Ship> ships = info.getOccupyingShips();
            for (Ship ship : ships) {
                sb.append(ship.getIdShip()).append(", ");
            }

            // Remove trailing comma and space
            sb.setLength(sb.length() - 2);

            sb.append(",Number of ships: ").append(info.getNumberOfShips());
        } else {
            sb.append(",Unoccupied");
        }

        return sb.toString();
    }
}
