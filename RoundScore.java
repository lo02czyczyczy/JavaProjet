package game;

/**
 * The {@code RoundScore} class represents the scoring information for a specific player 
 * during a particular round of the game.
 * <p>
 * Each instance of this class stores the player's score, name, ID, and the round number 
 * associated with that score. This information is useful for displaying round-by-round 
 * performance and determining the overall game results.
 */
public class RoundScore {
    // The player's score for the given round.
    private int score;
    // The player's name.
    private String playerName;
    // The player's unique ID.
    private int playerId;
    // The round number for which this score is recorded.
    private int roundNumber;

    /**
     * Constructs a new RoundScore instance.
     *
     * @param score       the score the player achieved during this round
     * @param playerName  the name of the player
     * @param playerId    the unique identifier of the player
     * @param roundNumber the number of the round to which this score corresponds
     */
    public RoundScore(int score, String playerName, int playerId, int roundNumber) {
        this.score = score;
        this.playerName = playerName;
        this.playerId = playerId;
        this.roundNumber = roundNumber;
    }

    /**
     * Returns the player's score for this round.
     *
     * @return the round's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the player's ID.
     *
     * @return the player's ID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Returns the round number associated with this score.
     *
     * @return the round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }
}
