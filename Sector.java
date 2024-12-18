package game;

/**
 * The {@code Sector} class represents a sector on the game's hex board.
 * Each sector has an associated level that may influence scoring, 
 * resource distribution, or strategic importance.
 * <p>
 * Sector levels are typically in the range of 0 to 3, with higher levels 
 * often being more valuable or contested.
 */
public class Sector {
    // The level of this sector (e.g., 0, 1, 2, or 3)
    private int level;  

    /**
     * Constructs a sector with the specified level.
     *
     * @param level the level of this sector
     */
    public Sector(int level) {
        this.level = level;
    }

    /**
     * Returns the sector's level.
     *
     * @return the sector level
     */
    public int getSector() {
        return level;
    }
}
