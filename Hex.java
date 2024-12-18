package game;

import java.util.*;

/**
 * Represents a hexagonal tile in a cube-coordinate system.
 * Each hex is defined by three coordinates (q, r, s) where q + r + s must equal 0.
 */
public class Hex {
    
    /** The x-axis coordinate in cube space. */
    public int q;  

    /** The y-axis coordinate in cube space. */
    public int r;  

    /** The z-axis coordinate in cube space. */
    public int s;  

    /** The sector associated with this hex. */
    private Sector sector;  

    /**
     * Constructs a Hex with specified cube coordinates.
     *
     * @param q The x-axis coordinate.
     * @param r The y-axis coordinate.
     * @param s The z-axis coordinate.
     * @throws IllegalArgumentException If q + r + s != 0.
     */
    public Hex(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("Cube coordinates must satisfy q + r + s = 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    /**
     * Retrieves the neighboring hexes from the provided game board.
     *
     * @param board A map of hexes to their corresponding sectors.
     * @return A list of neighboring hexes that exist on the board.
     */
    public List<Hex> getNeighbors(Map<Hex, Sector> board) {
        List<Hex> neighbors = new ArrayList<>();
        Hex[] possibleNeighbors = {
            new Hex(q + 1, r - 1, s),  // Right neighbor
            new Hex(q - 1, r + 1, s),  // Left neighbor
            new Hex(q + 1, r, s - 1),  // Upper-right neighbor
            new Hex(q, r + 1, s - 1),  // Upper-left neighbor
            new Hex(q - 1, r, s + 1),  // Lower-left neighbor
            new Hex(q, r - 1, s + 1)   // Lower-right neighbor
        };
        for (Hex neighbor : possibleNeighbors) {
            if (board.containsKey(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
    
    /**
     * Determines whether another hex is a direct neighbor of this hex.
     *
     * @param other The hex to compare against.
     * @return true if the other hex is a neighbor, false otherwise.
     */
    public boolean isNeighbor(Hex other) {
        int dq = Math.abs(this.q - other.q);
        int dr = Math.abs(this.r - other.r);
        int ds = Math.abs(this.s - other.s);

        return (dq + dr + ds == 2) && (dq <= 1 && dr <= 1 && ds <= 1);
    }

    /**
     * Retrieves the sector associated with this hex.
     *
     * @return The current sector of this hex.
     */
    public Sector getSector() {
        return sector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hex hex = (Hex) o;
        return q == hex.q && r == hex.r && s == hex.s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ", " + s + ")";
    }
}
