package game;

import java.util.*;
import java.awt.EventQueue; 
import java.util.stream.Collectors;
import java.util.Scanner;

/**
 * The {@code Game} class orchestrates the entire flow of this strategy-based 
 * space warfare board game. It manages players, rounds, scoring, and the 
 * graphical components such as the hex board display and occupation display.
 *
 * <p>
 * Major responsibilities include:
 * <ul>
 *   <li>Initializing the game with players, sectors, and the hex board.</li>
 *   <li>Handling turn progression, including ship placement, command execution, and scoring.</li>
 *   <li>Updating visual components (hex board display, occupation display) after significant events.</li>
 *   <li>Determining the end of the game and the winner based on final scores.</li>
 * </ul>
 */
public class Game {
    // A list of all players participating in the game.
    private List<Player> players;
    // A list of sectors, not extensively used in current implementation but available for future expansions.
    private List<Sector> sectors;
    // The current round counter.
    private int turnCounter;
    // The maximum number of turns (rounds) to play before the game ends.
    private int maxTurns;
    // The player who starts the game.
    private Player startPlayer;
    // Manages scores, including round-by-round and final tally.
    private ScoreManager scoreManager; 
    // The scoreboard UI to display scores in a table format.
    private ScoreBoard scoreBoard;
    // The hex board representing the galaxy map.
    private HexBoard hexBoard;  
    // The graphical display of the hex board.
    private HexBoardDisplay hexBoardDisplay;
    // The UI for displaying which player occupies each hex.
    private OccupationDisplay occupationDisplay;
    // A 3x3 matrix tracking command usage per round (columns are rounds, rows are commands).
    // commandMatrix[0][x] = usage of "Expand" in round x+1
    // commandMatrix[1][x] = usage of "Explore" in round x+1
    // commandMatrix[2][x] = usage of "Exterminate" in round x+1
    private int[][] commandMatrix = new int[3][3];
    // Flag indicating if the game has ended.
    private boolean gameEnded = false;

    /**
     * Constructs a new Game instance with specified maximum turns, starting player, and a list of players.
     *
     * @param maxTurns    the maximum number of rounds
     * @param startPlayer the player who starts the game (may be null at initialization)
     * @param players     the list of players in the game
     */
    public Game(int maxTurns, Player startPlayer, List<Player> players) {
        this.players = players;  
        this.sectors = new ArrayList<>();
        this.turnCounter = 0;
        this.maxTurns = maxTurns;
        this.startPlayer = startPlayer;
        this.hexBoard = new HexBoard();
        this.hexBoardDisplay = new HexBoardDisplay(hexBoard);
        this.occupationDisplay = new OccupationDisplay(hexBoard);
        this.scoreManager = new ScoreManager(players, this);
    }

    /**
     * Starts the game by displaying the board, initiating ship placement,
     * and beginning the first round of the game.
     */
    public void startGame() {
        EventQueue.invokeLater(() -> hexBoardDisplay.setVisible(true));
        EventQueue.invokeLater(() -> occupationDisplay.displayOccupation());
        String cardInfo = hexBoard.getCardSectorInfo();
        System.out.println(cardInfo);
        this.turnCounter = 1;  
        this.scoreBoard = new ScoreBoard(scoreManager);

        System.out.println("Starting ship placement...");
        Set<String> occupiedSectors = new HashSet<>();

        // Clockwise ship placement
        for (Player player : players) {
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation();
        }

        // Counter-clockwise ship placement
        for (int i = players.size() - 1; i >= 0; i--) {
            Player player = players.get(i);
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation();
        }

        summarizeShipPlacement();

        System.out.println("Game started. It's " + startPlayer.getName() + "'s turn.");
        nextTurn();  
        scoreManager.calculateRoundScores(this); 
    }

    /**
     * Proceeds to the next turn (round) of the game by setting each player's command order,
     * updating the command usage matrix, executing the round's actions, and updating scores.
     */
    public void nextTurn() {
        System.out.println("Turn " + turnCounter + " starts.");

        // Players choose their command order for this turn
        for (Player player : players) {
            player.setCommandOrder();
            player.endTurn();
        }
        this.updateCommandMatrix();

        executeRound();  
        scoreManager.calculateRoundScores(this); 
        scoreBoard.updateScores(); 
        endRound();  
    }

    /**
     * Ends the current round by:
     * <ul>
     *   <li>Sustaining ships and removing any that can't be maintained in a given hex.</li>
     *   <li>Allowing players (including any Tri-Prime controller) to choose sector cards for scoring.</li>
     *   <li>Calculating and displaying scores.</li>
     *   <li>Checking if the game has reached the maximum turns or if the game has ended.</li>
     * </ul>
     */
    public void endRound() {
        sustainShips();  
        chooseSectorCards();     
        calculateScores();
        scoreManager.calculateRoundScores(this);
        scoreBoard.updateScores();
        System.out.println("**************************************************************************************************************************");

        // Check if we've reached the max number of turns
        if (turnCounter >= maxTurns) {
            endGame();
            return;
        }

        // If the game hasn't ended, proceed to the next turn
        if (!gameEnded) {
            turnCounter++;
            nextTurn();
        }
    }

    /**
     * Summarizes the ship placement after the initial placement phase, listing where each player placed their ships.
     */
    public void summarizeShipPlacement() {
        System.out.println("\n**************************************************************************************************************************");
        System.out.println("\nSummary of Ship Placement:");

        for (Player player : players) {
            System.out.println(player.getName() + " placed ships at the following locations:");
            Map<String, Map<Hex, List<String>>> shipPlacementSummary = new HashMap<>();

            for (Ship ship : player.getShips()) {
                String sector = getSectorLabelForHex(ship.getShipLocation()); 
                Hex position = ship.getShipLocation();
                String shipId = ship.getIdShip();

                shipPlacementSummary.putIfAbsent(sector, new HashMap<>());
                Map<Hex, List<String>> sectorMap = shipPlacementSummary.get(sector);
                sectorMap.putIfAbsent(position, new ArrayList<>());
                sectorMap.get(position).add(shipId);
            }

            for (String sector : shipPlacementSummary.keySet()) {
                Map<Hex, List<String>> sectorMap = shipPlacementSummary.get(sector);
                for (Hex hex : sectorMap.keySet()) {
                    List<String> shipsAtHex = sectorMap.get(hex);
                    System.out.println("  - In sector " + sector + " at coordinates " + hex + ": " + String.join(", ", shipsAtHex));
                }
            }
            System.out.println();
        }
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Finds the sector label for a given hex by checking the card sets generated by the hex board.
     *
     * @param hex the hex whose sector label is needed
     * @return the sector label as a string, or "Unknown" if not found
     */
    private String getSectorLabelForHex(Hex hex) {
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();
        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            if (entry.getValue().contains(hex)) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    /**
     * Updates the command usage matrix based on the current command orders chosen by each player.
     * This helps in determining how often each command type was used in each position during the round.
     */
    public void updateCommandMatrix() {
        for (int i = 0; i < 3; i++) {
            Arrays.fill(commandMatrix[i], 0);
        }

        for (Player player : players) {
            List<Integer> commands = player.getCurrentCommandOrder();
            for (int position = 0; position < 3; position++) {
                int command = commands.get(position) - 1;
                commandMatrix[command][position]++;
            }
        }

        System.out.println("Command Usage Matrix (rows: commands, columns: command position in the round):");
        for (int i = 0; i < 3; i++) {
            System.out.println(Arrays.toString(commandMatrix[i]));
        }
    }

    /**
     * Returns the usage count for a specific command type in a given command position (round).
     *
     * @param round   the command position (1-based)
     * @param command the command type (1=Expand, 2=Explore, 3=Exterminate)
     * @return the number of times the command was used in that position
     */
    public int getCommandUsage(int round, int command) {
        return commandMatrix[command - 1][round - 1];
    }

    /**
     * Executes one full round by iterating through command positions (1,2,3) and 
     * having each player perform the command they assigned to that position.
     */
    public void executeRound() {
        System.out.println("Executing round for all players");
        for (int commandPos = 0; commandPos < 3; commandPos++) {
            for (Player player : players) {
                int commandIndex = player.getCurrentCommandOrder().get(commandPos);
                System.out.println("Executing command " + commandIndex + " for " + player.getName());
                executeCommands(player, commandIndex);
            }
        }
    }

    /**
     * Executes the specified command (Expand, Explore, or Exterminate) for a given player.
     *
     * @param player       the player executing the command
     * @param commandIndex the command type (1=Expand, 2=Explore, 3=Exterminate)
     */
    private void executeCommands(Player player, Integer commandIndex) {
        switch (commandIndex) {
            case 1:
                player.expand(this);
                break;
            case 2:
                player.explore(this);
                break;
            case 3:
                player.exterminate(this);
                break;
            default:
                System.out.println("Unknown command.");
                break;
        }
        this.getOccupationDisplay().displayOccupation();
    }

    /**
     * Sustains ships at the end of a round. If a hex is over capacity (more ships than allowed
     * by the sector level), excess ships are removed.
     */
    public void sustainShips() {
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();

        Iterator<Map.Entry<Hex, HexBoard.OccupationInfo>> it = occupationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Hex, HexBoard.OccupationInfo> entry = it.next();
            Hex hex = entry.getKey();
            HexBoard.OccupationInfo occupationInfo = entry.getValue();
            Sector sector = hexBoard.getBoard().get(hex);
            int maxShipsSustained = 1 + sector.getSector();

            List<Ship> ships = occupationInfo.getOccupyingShips();
            Player player = ships.get(0).getOwner();
            if (ships.size() > maxShipsSustained) {
                // Remove excess ships
                List<Ship> toRemove = new ArrayList<>(ships.subList(maxShipsSustained, ships.size()));
                for (Ship ship : toRemove) {
                    player.removeShip(ship);
                    System.out.println("Ship " + ship.getIdShip() + " removed due to overcapacity in Hex " + hex);
                }
                ships.removeAll(toRemove);
            }

            // If no ships remain, clear occupation
            if (ships.isEmpty()) {
                it.remove();
                hexBoard.clearOccupation(hex, null);
            }
        }
        this.getOccupationDisplay().displayOccupation();
    }

    /**
     * Allows each player to choose sector cards for scoring at the end of a round.
     * If a player controls Tri-Prime, they choose twice.
     * Scores from chosen sectors are then added to each player's total.
     */
    public void chooseSectorCards() {
        List<String> availableCards = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        System.out.println("Now pick a sector card to calculate the score for this round.");

        Player triPrimeController = null;

        // Identify the Tri-Prime controlling player, if any
        for (Player player : players) {
            if (hexBoard.isTriPrimeControlledBy(player.getId())) {
                triPrimeController = player;
                break;
            }
        }

        for (Player player : players) {
            int roundScore = 0; 
            System.out.println(player.getName() + "'s turn to choose a sector card.");

            if (player == triPrimeController) {
                // Tri-Prime controller chooses twice
                for (int i = 0; i < 2; i++) {
                    if (availableCards.isEmpty()) break;
                    String chosenCard = player.chooseSectorCard(availableCards);
                    availableCards.remove(chosenCard);

                    roundScore += player.calculatePlayerScore(chosenCard, hexBoard);
                    System.out.println(player.getName() + " scored from sector card: " + chosenCard);
                }
            } else {
                // Other players choose once
                if (!availableCards.isEmpty()) {
                    String chosenCard = player.chooseSectorCard(availableCards);
                    availableCards.remove(chosenCard);

                    roundScore += player.calculatePlayerScore(chosenCard, hexBoard);
                    System.out.println(player.getName() + " scored from sector card: " + chosenCard);
                }
            }

            player.addScore(roundScore);
            System.out.println(player.getName() + " scored a total of " + roundScore + " points this round.");
        }
    }

    /**
     * Calculates and displays scores after the current round, showing round-specific and cumulative totals.
     */
    public void calculateScores() {
        System.out.println("Scores for round " + turnCounter + ":");
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " scored " + player.getRoundScore() + " in round " + turnCounter);
        }

        System.out.println("Total scores after " + turnCounter + " rounds:");
        for (Player player : players) {
            System.out.println("Player " + player.getName() + ": " + player.getScore());
        }
    }

    /**
     * Performs final scoring when the game ends, often by doubling control scores or applying other endgame rules.
     */
    public void calculateFinalScores() {
        System.out.println("Performing final scoring...");
        for (Player player : players) {
            int finalScore = player.calculateFinalScore(hexBoard);
            player.addScore(finalScore);
        }
        System.out.println("Final scoring completed.");
    }

    /**
     * Ends the game after reaching the maximum number of turns. Performs final scoring and determines the winner.
     */
    public void endGame() {
        gameEnded = true;
        System.out.println("Game ends after " + maxTurns + " turns.");
        calculateFinalScores();
        determineWinner();
    }

    /**
     * Determines the winner based on the highest total score. If there's a tie, all top-scoring players are announced.
     */
    public void determineWinner() {
        int highestScore = players.stream()
                                  .mapToInt(Player::getScore)
                                  .max()
                                  .orElse(Integer.MIN_VALUE);  

        List<Player> winners = players.stream()
                                      .filter(p -> p.getScore() == highestScore)
                                      .collect(Collectors.toList());

        if (winners.size() == 1) {
            System.out.println("The winner is " + winners.get(0).getName() + " with a score of " + highestScore + "!");
        } else {
            System.out.println("It's a tie!");
            for (Player winner : winners) {
                System.out.println(winner.getName() + " scored " + winner.getScore());
            }
        }
    }

    // Getters and Setters

    /**
     * Returns the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players in the game and updates the score manager accordingly.
     *
     * @param players the new list of players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
        this.scoreManager = new ScoreManager(players, this);
    }

    /**
     * Returns the list of sectors in the game (not heavily used here).
     *
     * @return the list of sectors
     */
    public List<Sector> getSectors() {
        return sectors;
    }

    /**
     * Sets the list of sectors.
     *
     * @param sectors the new list of sectors
     */
    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    /**
     * Returns the current turn counter.
     *
     * @return the turn counter
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Sets the current turn counter.
     *
     * @param turnCounter the new turn counter
     */
    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    /**
     * Returns the maximum number of turns the game will run.
     *
     * @return the maximum turns
     */
    public int getMaxTurns() {
        return maxTurns;
    }

    /**
     * Sets the maximum number of turns.
     *
     * @param maxTurns the new maximum turns
     */
    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    /**
     * Returns the player who starts the game.
     *
     * @return the start player
     */
    public Player getStartPlayer() {
        return startPlayer;
    }

    /**
     * Sets the player who starts the game.
     *
     * @param startPlayer the player who starts
     */
    public void setStartPlayer(Player startPlayer) {
        this.startPlayer = startPlayer;
    }

    /**
     * Returns the hex board of the game.
     *
     * @return the HexBoard
     */
    public HexBoard getHexBoard() {
        return this.hexBoard;
    }

    /**
     * Returns the occupation display UI component.
     *
     * @return the OccupationDisplay instance
     */
    public OccupationDisplay getOccupationDisplay() {
        return occupationDisplay;
    }

    /**
     * The main method to run the game. It prompts the user for the number of players and their details,
     * creates a Game instance, and starts the game.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        System.out.println("Welcome to the game! Please enter player details.");

        Game game = new Game(9, null, players);  

        Set<Integer> availableIds = new HashSet<>(Arrays.asList(1, 2, 3));

        Scanner scanner = new Scanner(System.in);
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 3) {
            try {
                System.out.print("Enter number of players (2 or 3): ");
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers < 2 || numPlayers > 3) {
                    System.out.println("Please enter a valid number of players (2 or 3).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        // Create players
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String name = scanner.nextLine().trim();

            int id = getAvailableId(availableIds, scanner);

            if (name.equalsIgnoreCase("VirtualPlayer")) {
                VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
                players.add(virtualPlayer);
            } else {
                Player player = new Player(name, id, game);
                players.add(player);
            }
        }

        // If less than 3 players, fill remaining spots with virtual players
        while (players.size() < 3) {
            int id = availableIds.iterator().next();
            availableIds.remove(id);
            VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
            players.add(virtualPlayer);
            System.out.println("Added VirtualPlayer" + id + " to the game.");
        }

        game.setPlayers(players);

        // Set the start player (with ID 1)
        Player startPlayer = players.stream()
                .filter(p -> p.getId() == 1)
                .findFirst()
                .orElse(null);

        if (startPlayer == null) {
            System.out.println("No player with ID 1 found, please ensure one of the players has ID 1.");
            return;
        }

        game.setStartPlayer(startPlayer);

        // Start the game
        game.startGame();
    }

    /**
     * Gets an available ID from the user for a new player.
     *
     * @param availableIds the set of IDs that can still be chosen
     * @param scanner      the Scanner for user input
     * @return a valid chosen ID
     */
    private static int getAvailableId(Set<Integer> availableIds, Scanner scanner) {
        int id = 0;
        while (id == 0) {
            try {
                System.out.print("Available IDs: " + availableIds + "\nEnter ID for this player: ");
                id = Integer.parseInt(scanner.nextLine());
                if (!availableIds.contains(id)) {
                    System.out.println("ID not available. Please choose from " + availableIds);
                    id = 0;
                } else {
                    availableIds.remove(id);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }
        return id;
    }
}
