package game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * The {@code ScoreBoard} class provides a graphical interface to display round-by-round and 
 * cumulative scores for all players in the game. It uses a table layout, with each row representing
 * a round and each column representing a player.
 * <p>
 * At the end of the table, a summary row shows the total (final) scores for each player.
 */
public class ScoreBoard {
    // The main frame to display the scoreboard
    private JFrame frame;
    // The JTable for displaying scores
    private JTable scoreTable;
    // The model used by the JTable to manage the data in a tabular format
    private DefaultTableModel tableModel;
    // The ScoreManager that provides player and score information
    private ScoreManager scoreManager;

    /**
     * Constructs a new ScoreBoard for the given ScoreManager.
     *
     * @param scoreManager the ScoreManager that provides game and scoring information
     */
    public ScoreBoard(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        initUI();
    }

    /**
     * Initializes the user interface (UI) components for the scoreboard.
     * <p>
     * This method sets up the JFrame with a table that displays the round number and each player's score.
     * It also configures the window to appear at the top of the screen and remain always on top.
     */
    private void initUI() {
        frame = new JFrame("Game Scoreboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);  // Set initial frame size
        frame.setLayout(new BorderLayout());

        // Position the frame at the top of the screen, centered horizontally
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getX(), 0);
        frame.setAlwaysOnTop(true);  // Keep the scoreboard always on top

        // Create the table model and define the first column as "Round"
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Round");

        // Add a column for each player, using their names as column headers
        for (Player player : scoreManager.getPlayers()) {
            tableModel.addColumn(player.getName());
        }

        // Create the table and set the model
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }

    /**
     * Updates the scores in the table to reflect the current state of the game.
     * <p>
     * This method clears any existing rows and re-populates the table with 
     * round-by-round scores and a final summary row. Each round row lists the 
     * score each player got that round. The final summary row displays the total scores.
     */
    public void updateScores() {
        // Clear any existing data
        tableModel.setRowCount(0);

        // Get the maximum number of rounds played so far
        int maxRounds = scoreManager.getGame().getTurnCounter();

        // Populate the table for each round
        for (int round = 1; round <= maxRounds; round++) {
            Object[] rowData = new Object[tableModel.getColumnCount()];
            rowData[0] = "Round " + round; // First column is the round number

            int columnIndex = 1; // Start filling player scores from the second column
            for (Player player : scoreManager.getPlayers()) {
                Integer score = scoreManager.getRoundScores(player.getId()).get(round);
                if (score != null) {
                    rowData[columnIndex] = score;
                } else {
                    rowData[columnIndex] = "-"; // No score available for this round
                }
                columnIndex++;
            }

            // Add the completed round row to the table
            tableModel.addRow(rowData);
        }

        // Add a final row showing the cumulative scores for each player
        Object[] finalScoreRow = new Object[tableModel.getColumnCount()];
        finalScoreRow[0] = "Sum";

        int columnIndex = 1;
        for (Player player : scoreManager.getPlayers()) {
            finalScoreRow[columnIndex] = scoreManager.getFinalScore(player.getId());
            columnIndex++;
        }

        // Add the summary row to the table
        tableModel.addRow(finalScoreRow);
    }
}
