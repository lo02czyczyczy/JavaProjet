package game;

import java.util.*;

public class Hex {
    public int q;  // x-axis in cube coordinates
    public int r;  // y-axis in cube coordinates
    public int s;  // z-axis in cube coordinates
    private Sector sector;  // the sector associated with this hex

    // Constructor
    public Hex(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("Cube coordinates must satisfy q + r + s = 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    // Get adjacent hexes based on cube coordinates
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
    
    // 检查是否为邻居的方法
    public boolean isNeighbor(Hex other) {
        // 在六边形网格中，邻居的定义是q, r, s坐标的差的绝对值之和为2且任意两个坐标的差的绝对值不超过1
        int dq = Math.abs(this.q - other.q);
        int dr = Math.abs(this.r - other.r);
        int ds = Math.abs(this.s - other.s);

        return (dq + dr + ds == 2) && (dq <= 1 && dr <= 1 && ds <= 1);
    }

    // Get the sector of this hex
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
