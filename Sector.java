package game;

public class Sector {
    private int level;  // Sector level (0, 1, 2, 3)

    public Sector(int level) {
        this.level = level;
    }

    public int getSector() {
        return level;
    }
}
