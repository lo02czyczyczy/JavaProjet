package game;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code VirtualPlayer} class extends the {@code Player} class, providing a simple
 * Artificial Intelligence (AI) controlled player for the game. This virtual player
 * can place ships, choose command orders, and execute commands (expand, explore, exterminate)
 * without human input, using random choices and basic logic.
 * 
 * <p>
 * Key behaviors include:
 * <ul>
 *   <li>Random selection of sectors to place ships</li>
 *   <li>Shuffling command order choices</li>
 *   <li>Expanding fleets in controlled sectors</li>
 *   <li>Exploring adjacent sectors and moving fleets</li>
 *   <li>Exterminating enemy ships in neighboring sectors</li>
 * </ul>
 * 
 * Note: This is a basic AI implementation and may not reflect complex or strategic gameplay.
 */
public class VirtualPlayer extends Player {
    /**
     * Constructs a new VirtualPlayer instance with the given name, ID, and reference to the game.
     *
     * @param name the name of the virtual player
     * @param id   the unique identifier for this player
     * @param game the game instance this player participates in
     */
    public VirtualPlayer(String name, int id, Game game) {
        super(name, id, game);
    }

    /**
     * Places ships in one of the available sectors not yet occupied. This implementation:
     * <ul>
     *   <li>Finds available sectors that are not occupied.</li>
     *   <li>Selects a sector randomly.</li>
     *   <li>Places two ships in a randomly chosen Level I system within that sector.</li>
     * </ul>
     *
     * @param occupiedSectors a set of sectors already chosen or occupied
     */
    @Override
    public void placeShips(Set<String> occupiedSectors) {
        int shipsToPlace = 2;  
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();  
        
        // Filter out already occupied sectors
        List<String> availableSectors = cardHexes.keySet().stream()
                .filter(sector -> !occupiedSectors.contains(sector))
                .collect(Collectors.toList());

        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors for VirtualPlayer to place ships.");
            return;
        }

        // Randomly select a sector to occupy
        Random rand = new Random();
        String selectedSector = availableSectors.get(rand.nextInt(availableSectors.size()));
        occupiedSectors.add(selectedSector);  

        // Find Level I systems in the chosen sector
        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in selected sector for VirtualPlayer.");
            return;
        }

        // Randomly select one Level I system to place ships
        Hex selectedHex = levelIHexes.get(rand.nextInt(levelIHexes.size()));

        // Place the ships and update occupation
        for (int i = 1; i <= shipsToPlace; i++) {
            String shipId = generateShipId();
            Ship newShip = new Ship(this, hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(selectedHex, newShip);
            System.out.println("VirtualPlayer " + getName() + " placed a ship with ID " + shipId + " at Hex coordinates " + selectedHex);
        }
    }

    /**
     * Chooses a random order for the three commands (Expand, Explore, Exterminate).
     * This method shuffles the commands and returns the resulting order.
     *
     * @return a list of integers representing the shuffled command order
     */
    @Override
    public List<Integer> chooseCommandOrder() {
        List<Integer> commands = Arrays.asList(1, 2, 3);
        Collections.shuffle(commands);  // Shuffle the command order
        System.out.println("VirtualPlayer " + getName() + " chose command order: " + commands);
        return commands;
    }

    /**
     * Executes the "Expand" command, adding ships to controlled hexes.
     * <ul>
     *   <li>Calculates how many ships can be added based on command usage.</li>
     *   <li>Randomly selects controlled hexes to place new ships.</li>
     * </ul>
     *
     * @param game the current game instance
     */
    @Override
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1); 
        int shipsToAdd = Math.max(4 - usage, 1); 
        System.out.println(getName() + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // Get all hexes controlled by this player
        List<Hex> controlledHexes = hexBoard.getOccupationMap().entrySet().stream()
                .filter(entry -> entry.getValue().getPlayerId() == this.id)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (controlledHexes.isEmpty()) {
            System.out.println(getName() + " controls no hexes to expand.");
            return;
        }

        Random rand = new Random();

        // Add the ships to a random controlled hex
        while (shipsToAdd > 0) {
            Hex targetHex = controlledHexes.get(rand.nextInt(controlledHexes.size()));
            String shipId = generateShipId();
            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(targetHex, newShip);
            System.out.println(getName() + " added ship " + shipId + " to hex " + targetHex);
            shipsToAdd--;
        }
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Executes the "Explore" command, moving fleets to new hexes within a certain distance.
     * <ul>
     *   <li>Determines how many fleet movements are allowed based on usage.</li>
     *   <li>Finds fleets of ships that have not moved yet and tries to move them along valid paths.</li>
     *   <li>A path is valid if it does not cross enemy-owned hexes, boundary hexes, or improperly pass through Tri-Prime hexes.</li>
     * </ul>
     *
     * @param game the current game instance
     */
    @Override
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2);
        int fleetMovements = Math.max(4 - usage, 1); 
        System.out.println(getName() + " can make " + fleetMovements + " fleet movements using Explore command in round " + round);

        Random rand = new Random();

        // Group ships by their current hex, considering only those that haven't moved
        Map<Hex, List<Ship>> fleets = ships.stream()
                .filter(ship -> !ship.isMoved())
                .collect(Collectors.groupingBy(Ship::getShipLocation));

        List<Hex> triPrimeHexes = Arrays.asList(new Hex(0, 0, 0), new Hex(1, -1, 0), new Hex(0, -1, 1), new Hex(1, 0, -1));

        while (fleetMovements > 0 && !fleets.isEmpty()) {
            List<Hex> fleetLocations = new ArrayList<>(fleets.keySet());
            Hex currentHex = fleetLocations.get(rand.nextInt(fleetLocations.size()));
            List<Ship> fleetShips = fleets.get(currentHex);

            // Filter out ships that have already moved
            fleetShips = fleetShips.stream().filter(ship -> !ship.isMoved()).collect(Collectors.toList());
            if (fleetShips.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // Get all possible paths from the current hex within a max distance of 2
            List<List<Hex>> possiblePaths = getPossiblePaths(currentHex, 2);

            // Filter paths to those that are valid (no enemy-controlled hexes, no boundary hexes, proper handling of Tri-Prime)
            List<List<Hex>> validPaths = possiblePaths.stream()
                    .filter(path -> isValidPath(path, triPrimeHexes))
                    .collect(Collectors.toList());

            if (validPaths.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // Choose a random valid path and move all ships along it
            List<Hex> path = validPaths.get(rand.nextInt(validPaths.size()));
            Hex targetHex = path.get(path.size() - 1);

            // Move the fleet
            for (Ship ship : fleetShips) {
                Hex previousHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(previousHex, ship);
                ship.setMoved(true);
            }

            System.out.println(getName() + " moved fleet from " + currentHex + " to " + targetHex);
            fleets.remove(currentHex);
            fleetMovements--;
        }
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Generates all possible paths up to a certain maximum distance from a starting hex.
     * Used by the explore command to find potential routes for fleet movement.
     *
     * @param startHex    the starting hex
     * @param maxDistance the maximum number of steps allowed
     * @return a list of paths, each path represented as a list of hexes
     */
    private List<List<Hex>> getPossiblePaths(Hex startHex, int maxDistance) {
        List<List<Hex>> paths = new ArrayList<>();
        Queue<List<Hex>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(startHex));

        while (!queue.isEmpty()) {
            List<Hex> path = queue.poll();
            Hex lastHex = path.get(path.size() - 1);

            if (path.size() > maxDistance + 1) {
                continue;
            }

            if (path.size() > 1) {
                paths.add(new ArrayList<>(path));
            }

            // Explore neighbors for continued paths
            List<Hex> neighbors = lastHex.getNeighbors(hexBoard.getBoard());
            for (Hex neighbor : neighbors) {
                if (!path.contains(neighbor)) {
                    List<Hex> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return paths;
    }

    /**
     * Checks if a given path is valid for exploration:
     * <ul>
     *   <li>All intermediate hexes must be controlled by this player or unoccupied.</li>
     *   <li>No boundary hexes (level 0) allowed.</li>
     *   <li>If passing through a Tri-Prime hex, it must be the final destination.</li>
     * </ul>
     *
     * @param path         the path to validate
     * @param triPrimeHexes the list of Tri-Prime hex coordinates
     * @return true if the path is valid, false otherwise
     */
    private boolean isValidPath(List<Hex> path, List<Hex> triPrimeHexes) {
        for (int i = 1; i < path.size(); i++) {
            Hex hex = path.get(i);
            HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);

            // If the hex is occupied by another player, invalid
            if (info != null && info.getPlayerId() != this.id) {
                return false;
            }

            // If it's a boundary (level 0) sector, invalid
            if (hexBoard.getBoard().get(hex).getSector() == 0) {
                return false;
            }

            // If passing through a Tri-Prime hex, it must be the last hex in the path
            if (triPrimeHexes.contains(hex) && i != path.size() - 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the "Exterminate" command, which involves invading adjacent hexes occupied
     * by other players or unoccupied hexes. The virtual player:
     * <ul>
     *   <li>Determines how many invasions are allowed based on usage.</li>
     *   <li>Randomly selects targets and ships to move into those targets.</li>
     *   <li>Resolves invasions by destroying ships on both sides until one side remains or the hex is cleared.</li>
     * </ul>
     *
     * @param game the current game instance
     */
    @Override
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int invasionsAllowed = Math.max(4 - usage, 1);
        System.out.println(getName() + " can perform " + invasionsAllowed + " invasions using Exterminate command in round " + round);

        Random rand = new Random();
        Set<Ship> usedShips = new HashSet<>();

        while (invasionsAllowed > 0) {
            // Find potential target hexes for invasion
            List<Hex> potentialTargets = ships.stream()
                    .filter(ship -> !usedShips.contains(ship))
                    .flatMap(ship -> ship.getShipLocation().getNeighbors(hexBoard.getBoard()).stream()
                            .filter(hex -> {
                                HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
                                return (info == null || info.getPlayerId() != this.id) 
                                        && hexBoard.getBoard().get(hex).getSector() > 0;
                            })
                    )
                    .distinct()
                    .collect(Collectors.toList());

            if (potentialTargets.isEmpty()) {
                System.out.println(getName() + " has no targets to invade.");
                break;
            }

            // Randomly select a target hex to invade
            Hex targetHex = potentialTargets.get(rand.nextInt(potentialTargets.size()));
            HexBoard.OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);

            // Choose ships from neighboring hexes to invade
            List<Ship> availableShips = ships.stream()
                    .filter(ship -> !usedShips.contains(ship) && ship.getShipLocation().isNeighbor(targetHex))
                    .collect(Collectors.toList());

            if (availableShips.isEmpty()) {
                invasionsAllowed--;
                continue;
            }

            // Decide how many ships to move (at least one)
            int shipsToMove = rand.nextInt(availableShips.size()) + 1;
            List<Ship> invadingShips = availableShips.subList(0, shipsToMove);

            // Move ships to the target hex
            for (Ship ship : invadingShips) {
                Hex currentHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(currentHex, ship);
                usedShips.add(ship);
            }

            System.out.println(getName() + " is invading hex " + targetHex + " with " + invadingShips.size() + " ships.");

            // Resolve the invasion battle
            resolveInvasion(invadingShips.size(), targetInfo, targetHex);

            invasionsAllowed--;
        }
        System.out.println("**************************************************************************************************************************");
    }

    /**
     * Resolves an invasion battle on a target hex:
     * <ul>
     *   <li>Compares invading and defending ship counts.</li>
     *   <li>Destroys an equal number of ships from each side.</li>
     *   <li>Determines who remains in control of the hex after the battle.</li>
     * </ul>
     *
     * @param invadingShipsCount the number of ships that invaded
     * @param targetInfo         the occupation info of the target hex before invasion
     * @param targetHex          the target hex coordinates
     */
    private void resolveInvasion(int invadingShipsCount, HexBoard.OccupationInfo targetInfo, Hex targetHex) {
        int defendingShipsCount = (targetInfo != null) ? targetInfo.getNumberOfShips() : 0;
        int shipsToRemove = Math.min(invadingShipsCount, defendingShipsCount);

        // Remove defending ships
        if (targetInfo != null) {
            List<Ship> defendingShips = targetInfo.getOccupyingShips();
            for (int i = 0; i < shipsToRemove; i++) {
                Ship shipToRemove = defendingShips.get(0);
                defendingShips.remove(shipToRemove);
                hexBoard.clearOccupation(targetHex, shipToRemove);
                System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
            }

            // If defenders remain, update occupation
            if (!defendingShips.isEmpty()) {
                hexBoard.updateOccupation(targetHex, defendingShips.get(0));
            }
        }

        // Remove invading ships
        List<Ship> invadingShips = hexBoard.getOccupationInfo(targetHex).getOccupyingShips().stream()
                .filter(ship -> ship.getOwner() == this)
                .collect(Collectors.toList());

        for (int i = 0; i < shipsToRemove; i++) {
            Ship shipToRemove = invadingShips.get(0);
            invadingShips.remove(shipToRemove);
            ships.remove(shipToRemove);
            hexBoard.clearOccupation(targetHex, shipToRemove);
            System.out.println("Invading ship " + shipToRemove.getIdShip() + " has been destroyed.");
        }

        // Determine who controls the hex after the battle
        int remainingInvadingShips = invadingShips.size();
        int remainingDefendingShips = (targetInfo != null) ? targetInfo.getOccupyingShips().size() : 0;

        if (remainingInvadingShips > 0 && remainingDefendingShips == 0) {
            // Invaders take control
            System.out.println(getName() + " has taken control of hex " + targetHex);
        } else if (remainingDefendingShips > 0 && remainingInvadingShips == 0) {
            // Defenders retain control
            System.out.println("Defending player retains control of hex " + targetHex);
        } else {
            // Hex becomes unoccupied
            hexBoard.clearOccupation(targetHex, null);
            System.out.println("Hex " + targetHex + " is now unoccupied.");
        }
    }

    /**
     * Selects a sector card at random from the available cards.
     *
     * @param availableCards a list of sector card labels from which to choose
     * @return the chosen card label
     */
    @Override
    public String chooseSectorCard(List<String> availableCards) {
        Random rand = new Random();
        String chosenCard = availableCards.get(rand.nextInt(availableCards.size()));
        System.out.println(getName() + " chooses sector card: " + chosenCard);
        return chosenCard;
    }

}
