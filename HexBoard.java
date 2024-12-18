package game;

import java.util.*;

/**
 * The {@code HexBoard} class represents the game's hexagonal grid board, 
 * containing sectors of varying levels and tracking the occupation of those sectors by ships.
 * 
 * <p>
 * This class manages a map of Hex coordinates to Sector objects, as well as a map for 
 * tracking which player controls (occupies) each hex and with how many ships.
 * </p>
 */
public class HexBoard {
    // A map storing the hex coordinates and their corresponding sector.
    private Map<Hex, Sector> board;
    // A map storing the occupation information of each hex, including the player and ships present.
    private Map<Hex, OccupationInfo> occupationMap;

    /**
     * Constructs a new HexBoard and initializes it with a predefined layout of sectors.
     */
    public HexBoard() {
        board = new HashMap<>();
        occupationMap = new HashMap<>();
        initializeBoard();
    }
    
    /**
     * The {@code OccupationInfo} class holds information about which player occupies a given hex,
     * as well as the ships present on that hex.
     */
    public static class OccupationInfo {
        private int playerId;
        private List<Ship> occupyingShips; 

        /**
         * Constructs an OccupationInfo instance for a specific player.
         * 
         * @param playerId the ID of the player occupying the hex
         */
        public OccupationInfo(int playerId) {
            this.playerId = playerId;
            this.occupyingShips = new ArrayList<>();
        }

        /**
         * Adds a ship to this occupation record.
         * 
         * @param ship the ship to add
         */
        public void addShip(Ship ship) {
            occupyingShips.add(ship);
        }

        /**
         * Removes a ship from this occupation record.
         * 
         * @param ship the ship to remove
         */
        public void removeShip(Ship ship) {
            occupyingShips.remove(ship);
        }

        /**
         * Returns the ID of the player occupying this hex.
         * 
         * @return the player ID
         */
        public int getPlayerId() {
            return playerId;
        }

        /**
         * Returns a list of ships occupying this hex.
         * 
         * @return a list of occupying ships
         */
        public List<Ship> getOccupyingShips() {
            return occupyingShips;
        }

        /**
         * Returns the number of ships currently occupying this hex.
         * 
         * @return the number of occupying ships
         */
        public int getNumberOfShips() {
            return occupyingShips.size();
        }
    }
    
    /**
     * Updates the occupation status of a given hex with a new ship. 
     * If the hex is not yet occupied, a new OccupationInfo record is created.
     * 
     * @param hex  the hex to update
     * @param ship the ship that occupies or moves into the hex
     */
    public void updateOccupation(Hex hex, Ship ship) {
        OccupationInfo occupationInfo = occupationMap.get(hex);
        if (occupationInfo == null) {
            occupationInfo = new OccupationInfo(ship.getOwner().id); 
            occupationMap.put(hex, occupationInfo);
        }
        occupationInfo.addShip(ship);
    }

    /**
     * Clears occupation information from a given hex by removing a specified ship.
     * If no ships remain, the hex is considered unoccupied and the record is removed.
     * 
     * @param hex  the hex to clear
     * @param ship the ship to remove; can be null if no specific ship is provided
     */
    public void clearOccupation(Hex hex, Ship ship) {
        if (occupationMap.containsKey(hex)) {
            OccupationInfo info = occupationMap.get(hex);
            if (ship != null) {
                info.removeShip(ship);
            }
            
            if (info.getNumberOfShips() == 0) {
                occupationMap.remove(hex);
            }
        }
    }

    /**
     * Retrieves the occupation info for a given hex.
     * 
     * @param hex the hex to query
     * @return the OccupationInfo of the hex, or null if it is unoccupied
     */
    public OccupationInfo getOccupationInfo(Hex hex) {
        return occupationMap.get(hex);
    }

    /**
     * Initializes the board with a predefined set of hexes (including the Tri-Prime area),
     * boundary hexes, and random sectors distributed via card hex sets.
     */
    private void initializeBoard() {
        // Define some fixed hexes, such as the Tri-Prime cluster
        Hex[] fixedHexes = {
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        };
        for (Hex hex : fixedHexes) {
            board.put(hex, new Sector(3));
        }

        // Set outer boundary hexes for the board
        setBoundaryHexes();

        // Generate sets of hex coordinates from "cards" and randomize their sectors
        Map<String, List<Hex>> cardMap = generateCardHexes();
        cardMap.forEach((cardLabel, hexList) -> randomizeSectors(hexList));
    }

    /**
     * Sets the boundary hexes on the board with lower-level (0) sectors.
     * These form the outer perimeter of the playable area.
     */
    private void setBoundaryHexes() {
        List<Hex> boundaryHexes = Arrays.asList(
            new Hex(0, -3, 3),  
            new Hex(-2, -1, 3),
            new Hex(1, -2, 1),
            new Hex(-1, 0, 1),
            new Hex(2, -1, -1),
            new Hex(0, 1, -1),
            new Hex(3, 0, -3),
            new Hex(1, 2, -3)
        );
        for (Hex hex : boundaryHexes) {
            board.put(hex, new Sector(0));  
        }
    }

    /**
     * Generates a map associating card labels with a list of hex coordinates. 
     * These represent different sets of hexes that will be randomized in sector level.
     * 
     * @return a map from card labels (e.g., "a", "b", "c") to a list of hex coordinates
     */
    public Map<String, List<Hex>> generateCardHexes() {
        Map<String, List<Hex>> cardHexes = new HashMap<>();

        cardHexes.put("a", Arrays.asList(new Hex(1, -5, 4), new Hex(0, -4, 4), new Hex(1, -4, 3), new Hex(2, -4, 2), new Hex(1, -3, 2)));
        cardHexes.put("b", Arrays.asList(new Hex(-1, -3, 4), new Hex(-2, -2, 4), new Hex(-1, -2, 3), new Hex(0, -2, 2), new Hex(-1, -1, 2)));
        cardHexes.put("c", Arrays.asList(new Hex(-3, -1, 4), new Hex(-4, 0, 4), new Hex(-3, 0, 3), new Hex(-2, 0, 2), new Hex(-3, 1, 2)));
        cardHexes.put("d", Arrays.asList(new Hex(2, -3, 1), new Hex(3, -3, 0), new Hex(2, -2, 0), new Hex(3, -2, -1)));
        cardHexes.put("e", Arrays.asList(new Hex(-2, 1, 1), new Hex(-1, 1, 0), new Hex(-2, 2, 0), new Hex(-1, 2, -1)));
        cardHexes.put("f", Arrays.asList(new Hex(4, -2, -2), new Hex(3, -1, -2), new Hex(4, -1, -3), new Hex(5, -1, -4), new Hex(4, 0,-4)));
        cardHexes.put("g", Arrays.asList(new Hex(2, 0, -2), new Hex(1, 1, -2), new Hex(2, 1, -3), new Hex(3, 1, -4), new Hex(2, 2, -4)));
        cardHexes.put("h", Arrays.asList(new Hex(0, 2, -2), new Hex(-1, 3, -2), new Hex(0, 3, -3), new Hex(1, 3, -4), new Hex(0, 4, -4)));

        return cardHexes;
    }

    /**
     * Randomizes the sectors assigned to a given list of hex coordinates. 
     * Some hexes get level 2 or 1 sectors, and others become level 0.
     * 
     * @param hexes the list of hex coordinates to randomize
     */
    private void randomizeSectors(List<Hex> hexes) {
        List<Sector> sectors = new ArrayList<>();
        // Predefine some higher-level sectors
        sectors.add(new Sector(2));
        sectors.add(new Sector(1));
        sectors.add(new Sector(1));
        // Fill the remainder with level 0 sectors
        while (sectors.size() < hexes.size()) {
            sectors.add(new Sector(0)); 
        }

        // Shuffle the sectors to randomize distribution
        Collections.shuffle(sectors); 

        // Assign shuffled sectors to each hex if not already occupied
        for (int i = 0; i < hexes.size(); i++) {
            Hex hex = hexes.get(i);
            if (!board.containsKey(hex)) { 
                board.put(hex, sectors.get(i)); 
            }
        }
    }
    
    /**
     * Returns the map representing the board, 
     * associating each Hex coordinate with its corresponding Sector.
     * 
     * @return the board map
     */
    public Map<Hex, Sector> getBoard() {
        return this.board;
    }
    
    /**
     * Returns the map of current occupation information.
     * 
     * @return the occupation map
     */
    public Map<Hex, OccupationInfo> getOccupationMap() {
        return occupationMap;
    }
    
    /**
     * Provides information on the distribution of sectors for each "card" category.
     * This method uses the cardHexes generated by {@link #generateCardHexes()}.
     * 
     * @return a string detailing how many hexes per card set correspond to each sector level
     */
    public String getCardSectorInfo() {
        Map<String, List<Hex>> cardHexes = generateCardHexes();  // Get all card Hex coordinates
        StringBuilder info = new StringBuilder();
        cardHexes.forEach((cardLabel, hexList) -> {
            info.append("Card ").append(cardLabel).append(" sector information:\n");
            Map<Integer, List<Hex>> sectorMap = new HashMap<>();
            for (Hex hex : hexList) {
                Sector sector = board.get(hex);
                int level = sector.getSector();  // Get the sector level
                if (!sectorMap.containsKey(level)) {
                    sectorMap.put(level, new ArrayList<>());
                }
                sectorMap.get(level).add(hex);
            }
            sectorMap.keySet().stream().sorted(Collections.reverseOrder()).forEach(level -> {
                info.append("  - ").append(sectorMap.get(level).size()).append(" coordinates are level ").append(level).append(" system:");
                sectorMap.get(level).forEach(hx -> info.append(" (").append(hx.q).append(", ").append(hx.r).append(", ").append(hx.s).append(")"));
                info.append("\n");
            });
            info.append("\n");
        });
        return info.toString();
    }
    
    /**
     * Checks if the Tri-Prime region (a set of key hexes) is controlled by a specified player.
     * 
     * @param playerId the player ID to check
     * @return true if the player controls at least one of the Tri-Prime hexes, false otherwise
     */
    public boolean isTriPrimeControlledBy(int playerId) {
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        );

        for (Hex hex : triPrimeHexes) {
            OccupationInfo info = getOccupationInfo(hex);
            if (info != null && info.getPlayerId() == playerId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a list of all hex coordinates present on the board.
     * 
     * @return a list of all hex keys in the board map
     */
    public List<Hex> getAllHexes() {
        return new ArrayList<>(board.keySet()); 
    }
}
