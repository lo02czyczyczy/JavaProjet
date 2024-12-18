package game;

import java.util.*;

/**
 * The {@code ScoreManager} class is responsible for managing and recording the scores of players 
 * across multiple rounds in the game. It keeps track of each player's round-by-round scores as well
 * as their total scores.
 * <p>
 * This class interacts closely with the {@code Game} and {@code Player} classes:
 * <ul>
 *   <li>It retrieves the current round number from the {@code Game} instance.</li>
 *   <li>It fetches round scores from each {@code Player}.</li>
 *   <li>It stores the scores in a data structure allowing easy retrieval for display purposes.</li>
 * </ul>
 */
public class ScoreManager {
    // A list of players participating in the game.
    private List<Player> players; 
    // A map keyed by player ID, where each value is another map keyed by round number and valued by the player's score in that round.
    // Example: roundScores.get(playerId).get(roundNumber) gives the score of the player with ID playerId in that round.
    private Map<Integer, Map<Integer, Integer>> roundScores;
    // A reference to the Game instance, providing access to the current round and overall game state.
    private Game game; 

    /**
     * Constructs a ScoreManager instance with a given list of players and a reference to the game.
     * Initializes the data structure for storing round scores for each player.
     *
     * @param players a list of the players in the game
     * @param game    the Game instance from which to retrieve round information
     */
    public ScoreManager(List<Player> players, Game game) {
        this.players = players;
        this.roundScores = new HashMap<>();
        this.game = game;

        // Initialize a round score map for each player
        players.forEach(player -> {
            roundScores.put(player.getId(), new HashMap<>());
        });
    }

    /**
     * Calculates the scores for the current round and stores them in the roundScores map.
     * It retrieves the current round number from the game and queries each player for their round score.
     *
     * @param game the Game instance from which to get the current round number
     */
    public void calculateRoundScores(Game game) {
        int currentRound = game.getTurnCounter(); // Get the current round number from the game
        for (Player player : players) {
            int playerId = player.getId();
            System.out.println("Initializing player with ID: " + playerId); // Debug output for player ID
            int roundScore = player.getRoundScore(); // Fetch the player's score for this round
            roundScores.get(playerId).put(currentRound, roundScore);
        }
    }

    /**
     * Displays the total scores of all players after the current round.
     * This can be used for debugging or tracking progress during the game.
     */
    public void displayScores() {
        System.out.println("Scores after round " + game.getTurnCounter() + ":");
        players.forEach(player ->
                System.out.println("Player " + player.getName() + " (ID: " + player.getId() + ") has total score: " + player.getScore())
        );
    }

    /**
     * Retrieves a map of round numbers to scores for a specific player.
     *
     * @param playerId the unique ID of the player
     * @return a map where the key is the round number and the value is the player's score in that round
     */
    public Map<Integer, Integer> getRoundScores(int playerId) {
        return roundScores.get(playerId);
    }

    /**
     * Gets the final (cumulative) score of a player identified by their player ID.
     *
     * @param playerId the unique ID of the player
     * @return the total score of the player, or 0 if the player is not found
     */
    public int getFinalScore(int playerId) {
        return players.stream()
                .filter(player -> player.getId() == playerId)
                .findFirst()
                .map(Player::getScore)
                .orElse(0);
    }

    /**
     * Returns the list of players managed by this ScoreManager.
     *
     * @return a list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the Game instance associated with this ScoreManager.
     *
     * @return the Game instance
     */
    public Game getGame() {
        return game;
    }
}
