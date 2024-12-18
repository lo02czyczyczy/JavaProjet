package game;

import java.util.*;
import game.HexBoard.OccupationInfo;
import java.util.stream.Collectors;

/**
 * The {@code Player} class represents a single participant in the game. Each player has:
 * <ul>
 *   <li>A unique ID and name</li>
 *   <li>A set of ships they control</li>
 *   <li>A collection of sectors and command cards</li>
 *   <li>A current score and round score</li>
 *   <li>A chosen command order for each round (Expand, Explore, Exterminate)</li>
 * </ul>
 * <p>
 * Players interact with the game board through placing ships, expanding territory,
 * exploring new hexes, and exterminating enemy fleets.
 * <p>
 * Some actions require user input (for human players), and the class uses a global scanner to
 * read inputs from the console. For AI players, commands can be automated by overriding methods.
 */
public class Player {
    // The player's display name.
    private String name;
    // A list of ships owned and controlled by this player.
    public List<Ship> ships;
    // A list of command cards the player holds (not fully utilized in this example).
    private List<CommandCard> commandCards;
    // A list of sectors this player controls (not fully utilized in this example).
    private List<Sector> controlledSectors;
    // The player's total score.
    private int score;
    // The player's score in the current round.
    private int roundScore;
    // A unique identifier for the player (1, 2, or 3).
    public int id;
    // The player's color, determined by their ID.
    private String color;
    // A Scanner for reading user input (for human players).
    private static final Scanner scanner = new Scanner(System.in);
    // A reference to the HexBoard for this game.
    public HexBoard hexBoard;
    // A reference to the Game instance this player participates in.
    public Game game;
    // The chosen command order for the current round, a list of integers [1,2,3].
    public List<Integer> currentCommandOrder;
    // A set of available ship IDs for reuse if ships are destroyed (for efficient ID allocation).
    private Set<Integer> availableShipIds = new TreeSet<>();
    // A global incrementing number to assign IDs to newly created ships.
    private int nextShipNumber = 1;

    /**
     * Constructs a new Player instance with the given name, ID, and a reference to the game.
     *
     * @param name the player's name
     * @param id   the player's unique ID
     * @param game the game instance this player is participating in
     */
    public Player(String name, int id, Game game) {
        this.game = game;
        this.ships = new ArrayList<>();
        this.commandCards = new ArrayList<>();
        this.controlledSectors = new ArrayList<>();
        this.score = 0;
        this.name = name;
        this.id = id;
        this.color = assignColor(id);  
        this.hexBoard = game.getHexBoard(); 
        System.out.println("Player created: " + name + " with id: " + id);
    }

    /**
     * Assigns a color to the player based on their ID.
     *
     * @param id the player's ID
     * @return a string representing the player's color
     */
    private String assignColor(int id) {
        switch (id) {
            case 1:
                return "Red";
            case 2:
                return "Blue";
            case 3:
                return "Green";
            default:
                throw new IllegalArgumentException("Invalid player ID");
        }
    }

    /**
     * Returns the player's color.
     *
     * @return the player's color
     */
    public String getColor() {
        return color;
    }

    /**
     * Allows the player to choose a sector for initial ship placement.
     * The player must pick a sector from available sectors and then select a Level I system hex
     * within that sector. Two ships are placed at the chosen hex.
     *
     * @param occupiedSectors a set of sectors already chosen/occupied
     */
    public void placeShips(Set<String> occupiedSectors) {
        int shipsToPlace = 2; 
        Map<String, List<Hex>> cardHexes = this.hexBoard.generateCardHexes();

        System.out.println("Player " + name + " is placing ships with id: " + id);

        List<String> availableSectors = new ArrayList<>(cardHexes.keySet());
        availableSectors.removeAll(occupiedSectors);
        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors to choose for ship placement.");
            return;
        }

        System.out.println("Available sectors for ship placement: " + availableSectors);
        System.out.println("Please choose a sector card from the available sectors:");
        String selectedSector = scanner.nextLine().trim();

        while (!availableSectors.contains(selectedSector)) {
            System.out.println("Invalid sector card. Please choose again from: " + availableSectors);
            selectedSector = scanner.nextLine().trim();
        }

        occupiedSectors.add(selectedSector);

        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> this.hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in the selected sector.");
            return;
        }

        System.out.println("Available Level I hexes for ship placement: " + levelIHexes);
        System.out.println("Please enter the coordinates of the Level I hex to place your ships (e.g., '1 -5 4'):");
        String input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+");

        while (parts.length != 3) {
            System.out.println("Invalid input. Please enter three coordinates (e.g., '1 -5 4'):");
            input = scanner.nextLine().trim();
            parts = input.split("\\s+");
        }

        try {
            int q = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);
            int s = Integer.parseInt(parts[2]);

            if (q + r + s != 0) {
                System.out.println("Invalid coordinates. q + r + s must equal 0.");
                return;
            }

            Hex selectedHex = new Hex(q, r, s);

            if (!levelIHexes.contains(selectedHex)) {
                System.out.println("The selected hex is not a Level I system in the chosen sector.");
                return;
            }

            // Place ships
            for (int i = 1; i <= shipsToPlace; i++) {
                String shipId = generateShipId();
                Ship newShip = new Ship(this, this.hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
                ships.add(newShip);
                this.hexBoard.updateOccupation(selectedHex, newShip);
                System.out.println("Placed ship with ID " + shipId + " at Hex coordinates " + selectedHex);
            }

            System.out.println(name + " now controls the following hexes after placement:");
            this.hexBoard.getOccupationMap().forEach((hex, occupationInfo) -> {
                if (occupationInfo.getPlayerId() == this.id) {
                    System.out.println("Controlled hex: " + hex);
                }
            });

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid integers for coordinates.");
        }
    }

    /**
     * Generates a unique ship ID for a newly created ship.
     * If there are reusable IDs available in the pool, it uses one of those; otherwise,
     * it increments the global counter.
     *
     * @return a string representing the new ship's ID, in the format "playerId-shipNumber"
     */
    public String generateShipId() {
        int shipNumber;
        if (!availableShipIds.isEmpty()) {
            shipNumber = availableShipIds.iterator().next();
            availableShipIds.remove(shipNumber);
        } else {
            shipNumber = nextShipNumber++;
        }
        return id + "-" + shipNumber;
    }

    /**
     * Resets each ship's moved status at the end of a player's turn, allowing them to move again in the next round.
     */
    public void endTurn() {
        ships.forEach(ship -> ship.setMoved(false));
    }

    /**
     * Prompts the player to choose the command order for this round: a sequence of three numbers (1, 2, 3)
     * representing the commands Expand(1), Explore(2), and Exterminate(3).
     *
     * @return a list of three integers representing the chosen command order
     */
    public List<Integer> chooseCommandOrder() {
        System.out.println("Choosing command order for " + getName());
        List<Integer> order = new ArrayList<>();
        boolean validInput = false;

        System.out.println("Enter the order of command cards (1 for EXPAND, 2 for EXPLORE, 3 for EXTERMINATE):");
        System.out.println("Available commands: 1 - EXPAND, 2 - EXPLORE, 3 - EXTERMINATE :");

        while (!validInput) {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.print("Enter command " + i + ": ");
                    String commandChoice = scanner.nextLine();
                    if (commandChoice.equalsIgnoreCase("CHEN")) {
                        System.out.print("Congratulations on discovering the Easter egg in this game!");
                        order.clear();
                        order.add(999); // Easter egg marker
                        validInput = true;
                        break;
                    }
                    int cmd = Integer.parseInt(commandChoice);
                    if (cmd < 1 || cmd > 3) {
                        throw new IllegalArgumentException("Invalid command choice. Please enter 1, 2, or 3.");
                    }
                    order.add(cmd);
                }
                if (order.size() == 3 || order.contains(999)) {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values only.");
                order.clear();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                order.clear();
            }
        }
        return order;
    }

    /**
     * Closes the global scanner. Should be called when input is no longer needed.
     */
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Executes the Expand command, allowing the player to add ships to controlled hexes.
     * The number of ships that can be added depends on command usage.
     *
     * @param game the current game instance
     */
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1);
        int shipsToAdd = Math.max(4 - usage, 1);
        System.out.println(name + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // Find all hexes controlled by this player
        List<Hex> controlledHexes = new ArrayList<>();
        hexBoard.getOccupationMap().forEach((hex, occupationInfo) -> {
            if (occupationInfo.getPlayerId() == this.id) {
                controlledHexes.add(hex);
            }
        });

        System.out.println("You control the following hexes:");
        for (Hex hex : controlledHexes) {
            System.out.println("Controlled hex: " + hex);
        }

        System.out.println("You can distribute " + shipsToAdd + " ships among the controlled hexes or type 'skip' to skip adding ships.");
        while (shipsToAdd > 0) {
            System.out.println("Enter hex coordinates (q r s) and number of ships to add, e.g., '0 0 0 2', or type 'skip':");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                System.out.println("Skipping ship addition for this round.");
                break;
            }
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input. Please enter three coordinates and a number.");
                continue;
            }
            try {
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                int count = Integer.parseInt(parts[3]);

                if (q + r + s != 0) {
                    System.out.println("Invalid coordinates. q + r + s must equal 0.");
                    continue;
                }

                if (count <= 0) {
                    System.out.println("Invalid ship count. Must be greater than 0.");
                    continue;
                }

                Hex targetHex = new Hex(q, r, s);
                boolean controlsHex = controlledHexes.stream().anyMatch(h -> h.q == targetHex.q && h.r == targetHex.r && h.s == targetHex.s);

                if (controlsHex && count <= shipsToAdd) {
                    for (int i = 0; i < count; i++) {
                        Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId());
                        ships.add(newShip);
                        hexBoard.updateOccupation(targetHex, newShip);
                    }
                    shipsToAdd -= count;
                    System.out.println("Added " + count + " ships to hex " + targetHex);
                } else {
                    System.out.println("Invalid hex or ship count. Make sure the hex is controlled and the ship count is correct.");
                }
                game.getOccupationDisplay().displayOccupation();

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for coordinates and ship count.");
            }
        }

        System.out.println("All ships placed or skipped for this round.");
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Executes the Explore command, allowing the player to move their ships around the board.
     * The number of movements per ship depends on command usage.
     *
     * @param game the current game instance
     */
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2);
        int movesPerShip = Math.max(4 - usage, 1);
        System.out.println(name + " can move each ship " + movesPerShip + " times using Explore command in round " + round);

        // Show current ship locations
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // Move ships
        for (Ship ship : ships) {
            int remainingMoves = movesPerShip;
            Hex currentHex = ship.getShipLocation();

            System.out.println("Move Ship ID: " + ship.getIdShip() + " starting at Hex coordinates " + currentHex);

            while (remainingMoves > 0) {
                System.out.println("Enter new coordinates for move (or type 'skip'):");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("skip")) {
                    break;
                }

                String[] parts = input.trim().split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Invalid input. Please enter three coordinates.");
                    continue;
                }

                try {
                    int q = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    int s = Integer.parseInt(parts[2]);

                    if (q + r + s != 0) {
                        System.out.println("Invalid coordinates. q + r + s must equal 0.");
                        continue;
                    }

                    Hex targetHex = new Hex(q, r, s);

                    if (isValidMove(currentHex, targetHex, remainingMoves)) {
                        ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                        hexBoard.updateOccupation(targetHex, ship);
                        hexBoard.clearOccupation(currentHex, ship);

                        currentHex = targetHex;
                        remainingMoves--;

                        System.out.println("Ship moved to " + targetHex);
                    } else {
                        System.out.println("Invalid move. Cannot move to the specified hex.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter valid integers for coordinates.");
                }
            }
            game.getOccupationDisplay().displayOccupation();
        }
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Checks if a move from one hex to another is valid.
     *
     * @param from           the starting hex
     * @param to             the target hex
     * @param remainingMoves the number of moves remaining for this ship
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(Hex from, Hex to, int remainingMoves) {
        if (!from.isNeighbor(to)) {
            System.out.println("Target hex is not adjacent.");
            return false;
        }

        OccupationInfo occupationInfo = hexBoard.getOccupationInfo(to);
        if (occupationInfo != null && occupationInfo.getPlayerId() != this.id) {
            System.out.println("Cannot move to a hex occupied by another player.");
            return false;
        }

        boolean toIsTriPrime = isTriPrimeHex(to);
        if (toIsTriPrime && remainingMoves > 1) {
            System.out.println("You must stop moving after entering a Tri-Prime system hex. Enter 'skip'.");
            return true; 
        }

        return true;
    }

    /**
     * Checks if a given hex is part of the Tri-Prime region.
     *
     * @param hex the hex to check
     * @return true if it's a Tri-Prime hex, false otherwise
     */
    private boolean isTriPrimeHex(Hex hex) {
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        );
        return triPrimeHexes.stream().anyMatch(t -> t.q == hex.q && t.r == hex.r && t.s == hex.s);
    }

    /**
     * Executes the Exterminate command, allowing the player to attack adjacent hexes.
     * The number of attacks depends on command usage.
     *
     * @param game the current game instance
     */
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int attacksAllowed = Math.max(4 - usage, 1);
        System.out.println(name + " has " + attacksAllowed + " total attacks using Exterminate command in round " + round);

        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        while (attacksAllowed > 0) {
            System.out.println("Enter ship ID and target hex coordinates to attack (e.g., '1 0 0 0') or type 'skip' to end attacks:");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                break;
            }

            String[] parts = input.trim().split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input. Please enter ship ID and three coordinates.");
                continue;
            }

            try {
                int shipId = Integer.parseInt(parts[0]);
                int q = Integer.parseInt(parts[1]);
                int r = Integer.parseInt(parts[2]);
                int s = Integer.parseInt(parts[3]);

                if (q + r + s != 0) {
                    System.out.println("Invalid coordinates. q + r + s must equal 0.");
                    continue;
                }

                Ship attackingShip = findShipById(shipId);
                if (attackingShip == null) {
                    System.out.println("Invalid ship ID.");
                    continue;
                }

                Hex targetHex = new Hex(q, r, s);

                if (isValidAttack(attackingShip.getShipLocation(), targetHex)) {
                    resolveAttack(attackingShip, targetHex);
                    attacksAllowed--;
                } else {
                    System.out.println("Invalid attack. Cannot attack the specified hex.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for ship ID and hex coordinates.");
            }

            game.getOccupationDisplay().displayOccupation();
        }
        System.out.println("All attacks have been executed.");
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Finds a ship controlled by this player given the ship ID number (the part after the player ID).
     *
     * @param shipId the ship ID number (not including the player's ID prefix)
     * @return the matching Ship, or null if not found
     */
    private Ship findShipById(int shipId) {
        for (Ship ship : ships) {
            String[] idParts = ship.getIdShip().split("-");
            if (idParts.length == 2) {
                try {
                    int id = Integer.parseInt(idParts[1]);
                    if (id == shipId) {
                        return ship;
                    }
                } catch (NumberFormatException e) {
                    // Ignore parse errors
                }
            }
        }
        return null;
    }

    /**
     * Checks if an attack from one hex to another is valid:
     * <ul>
     *   <li>Target hex must be adjacent</li>
     *   <li>Cannot attack own hex</li>
     * </ul>
     *
     * @param from the attacking ship's current hex
     * @param to   the target hex
     * @return true if the attack is valid, false otherwise
     */
    private boolean isValidAttack(Hex from, Hex to) {
        if (!from.isNeighbor(to)) {
            System.out.println("Target hex is not adjacent.");
            return false;
        }

        HexBoard.OccupationInfo occupationInfo = hexBoard.getOccupationInfo(to);
        if (occupationInfo == null) {
            System.out.println("Attacking an unoccupied hex.");
            return true;
        }

        if (occupationInfo.getPlayerId() == this.id) {
            System.out.println("Cannot attack your own hex.");
            return false;
        }

        return true;
    }

    /**
     * Resolves an attack on the target hex. If the hex is unoccupied, the attacking ship moves in.
     * If there are defending ships, one defending ship and the attacking ship are both destroyed.
     *
     * @param attackingShip the ship initiating the attack
     * @param targetHex     the hex being attacked
     */
    @SuppressWarnings("unused")
    private void resolveAttack(Ship attackingShip, Hex targetHex) {
        OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);

        if (targetInfo == null) {
            System.out.println("Hex " + targetHex + " is unoccupied. Moving ship to take control.");

            Hex currentHex = attackingShip.getShipLocation();
            hexBoard.clearOccupation(currentHex, attackingShip);

            attackingShip.move(targetHex, hexBoard.getBoard().get(targetHex));
            hexBoard.updateOccupation(targetHex, attackingShip);

            System.out.println("Ship " + attackingShip.getIdShip() + " moved to Hex " + targetHex);
            return;
        }

        List<Ship> defendingShips = targetInfo.getOccupyingShips();
        if (!defendingShips.isEmpty()) {
            Ship shipToRemove = defendingShips.get(0);
            defendingShips.remove(shipToRemove);
            hexBoard.clearOccupation(targetHex, shipToRemove);
            shipToRemove.getOwner().getShips().remove(shipToRemove);
            System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");

            ships.remove(attackingShip);
            hexBoard.clearOccupation(attackingShip.getShipLocation(), attackingShip);
            System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");

            if (!defendingShips.isEmpty()) {
                hexBoard.updateOccupation(targetHex, defendingShips.get(0));
            } else {
                System.out.println("No defending ships remain in Hex: " + targetHex + ". Clearing occupation.");
                hexBoard.clearOccupation(targetHex, null);
            }
        }
    }

    /**
     * Prompts the player to choose a sector card from the available cards for scoring.
     *
     * @param availableCards a list of available sector cards
     * @return the chosen sector card label
     */
    public String chooseSectorCard(List<String> availableCards) {
        System.out.println(getName() + ", choose a sector card from: " + availableCards);
        String chosenCard = scanner.nextLine().trim().toLowerCase();

        while (!availableCards.contains(chosenCard)) {
            System.out.println("Invalid card. Please choose again from: " + availableCards);
            chosenCard = scanner.nextLine().trim().toLowerCase();
        }

        return chosenCard;
    }

    /**
     * Calculates the player's score from a chosen sector card:
     * Adds up the sector levels of all hexes the player occupies in that sector.
     *
     * @param sectorCard the chosen sector card label
     * @param hexBoard   the current hex board state
     * @return the score obtained from that sector
     */
    public int calculatePlayerScore(String sectorCard, HexBoard hexBoard) {
        int score = 0;
        List<Hex> hexesInSector = hexBoard.generateCardHexes().get(sectorCard);

        for (Hex hex : hexesInSector) {
            OccupationInfo occupationInfo = hexBoard.getOccupationInfo(hex);
            if (occupationInfo != null && occupationInfo.getPlayerId() == this.id) {
                int systemLevel = hexBoard.getBoard().get(hex).getSector();
                score += systemLevel;
            }
        }

        System.out.println(getName() + " scored " + score + " points from sector " + sectorCard);
        return score;
    }

    /**
     * Calculates the final score at the end of the game. 
     * Generally doubles the score from all systems the player controls.
     *
     * @param hexBoard the current hex board state
     * @return the player's final scoring points
     */
    public int calculateFinalScore(HexBoard hexBoard) {
        int score = 0;

        for (Hex hex : hexBoard.getAllHexes()) {
            OccupationInfo occupationInfo = hexBoard.getOccupationInfo(hex);
            if (occupationInfo != null && occupationInfo.getPlayerId() == this.id) {
                int systemLevel = hexBoard.getBoard().get(hex).getSector();
                score += systemLevel * 2;
            }
        }

        System.out.println(getName() + " scored " + score + " points in final scoring.");
        return score;
    }

    /**
     * Sets the player's round score.
     *
     * @param score the new round score
     */
    public void setRoundScore(int score) {
        this.roundScore = score;
    }

    /**
     * Gets the player's round score.
     *
     * @return the round score
     */
    public int getRoundScore() {
        return this.roundScore;
    }

    /**
     * Adds to the player's total score and updates their round score.
     *
     * @param score the score to add
     */
    public void addScore(int score) {
        this.score += score;
        this.roundScore = score;
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's unique ID.
     *
     * @return the player's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of ships owned by the player.
     *
     * @return a list of Ship objects
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Sets the player's ships.
     *
     * @param ships the new list of ships
     */
    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    /**
     * Returns the player's command cards (not heavily used).
     *
     * @return a list of CommandCard objects
     */
    public List<CommandCard> getCommandCards() {
        return commandCards;
    }

    /**
     * Sets the player's command cards.
     *
     * @param commandCards the new list of command cards
     */
    public void setCommandCards(List<CommandCard> commandCards) {
        this.commandCards = commandCards;
    }

    /**
     * Returns the sectors controlled by the player (not heavily used).
     *
     * @return a list of Sector objects
     */
    public List<Sector> getControlledSectors() {
        return controlledSectors;
    }

    /**
     * Sets the player's controlled sectors.
     *
     * @param controlledSectors the new list of controlled sectors
     */
    public void setControlledSectors(List<Sector> controlledSectors) {
        this.controlledSectors = controlledSectors;
    }

    /**
     * Returns the player's total score.
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the player's total score.
     *
     * @param score the new total score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Prompts the player to choose their command order and updates the currentCommandOrder field.
     */
    public void setCommandOrder() {
        this.currentCommandOrder = chooseCommandOrder();
    }

    /**
     * Returns the player's current command order.
     *
     * @return a list of integers representing the command order
     */
    public List<Integer> getCurrentCommandOrder() {
        return this.currentCommandOrder;
    }

    /**
     * Removes a ship from the player's control. Frees up the ship ID for reuse.
     *
     * @param ship the ship to remove
     */
    public void removeShip(Ship ship) {
        String[] idParts = ship.getIdShip().split("-");
        if (idParts.length == 2) {
            try {
                int shipNumber = Integer.parseInt(idParts[1]);
                availableShipIds.add(shipNumber);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse ship ID: " + ship.getIdShip());
            }
        }
    }
}
