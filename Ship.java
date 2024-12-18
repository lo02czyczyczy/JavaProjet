package game;

/**
 * The {@code Ship} class represents a ship controlled by a specific player and located on the
 * game's hexagonal board. Each ship is associated with a particular sector on the board and has
 * a unique ID, color, and position. Ships can move between sectors and engage in battles.
 */
public class Ship {
    // The owner (player) of this ship
    private Player owner;
    // The current sector where this ship is located
    private Sector currentSector;
    // The current hex coordinates of this ship on the board
    private Hex position;
    // A unique identifier for this ship
    private String idShip;
    // The color of this ship, typically associated with the owning player
    private String color;
    // Indicates whether this ship has moved during the current turn
    private boolean moved;

    /**
     * Constructs a new Ship with the given owner, sector, position, and ID.
     * The ship's color is derived from the owner's color.
     *
     * @param owner         the player who owns this ship
     * @param currentSector the sector where the ship is initially located
     * @param position      the hex coordinates of the ship
     * @param idShip        the unique ID for this ship
     */
    public Ship(Player owner, Sector currentSector, Hex position, String idShip) {
        this.owner = owner;
        this.currentSector = currentSector;
        this.position = position;
        this.idShip = idShip;
        this.color = owner.getColor(); // Obtain color from the player owner
        this.moved = false;
    }

    /**
     * Moves the ship to a new position and sector on the board.
     *
     * @param newPosition the new hex coordinates for the ship
     * @param newSector   the new sector in which the ship will be located
     */
    public void move(Hex newPosition, Sector newSector) {
        this.position = newPosition;
        this.currentSector = newSector;
        System.out.println("Ship " + idShip + " owned by " + owner.getName() 
                           + " moved to Hex coordinates (" + newPosition.q + ", " + newPosition.r 
                           + ", " + newPosition.s + ").");
    }

    /**
     * Returns whether this ship has moved during the current turn.
     *
     * @return true if the ship has moved, false otherwise
     */
    public boolean isMoved() {
        return moved;
    }

    /**
     * Sets the moved status of the ship for the current turn.
     *
     * @param moved true if the ship has moved this turn, false otherwise
     */
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    /**
     * Engages this ship in battle. The logic for battle resolution is handled elsewhere.
     */
    public void engageInBattle() {
        System.out.println("Ship " + idShip + " owned by " + owner.getName() + " is engaging in battle.");
    }

    /**
     * Returns the hex coordinates of the ship's current location.
     *
     * @return the current hex position of the ship
     */
    public Hex getShipLocation() {
        return position;
    }

    // Getters and Setters

    /**
     * Returns the owner (player) of this ship.
     *
     * @return the player who owns this ship
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the owner (player) of this ship.
     *
     * @param owner the new owner of the ship
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Returns the current sector of this ship.
     *
     * @return the sector where the ship is located
     */
    public Sector getCurrentSector() {
        return currentSector;
    }

    /**
     * Sets the current sector for this ship.
     *
     * @param currentSector the sector where the ship will be located
     */
    public void setCurrentSector(Sector currentSector) {
        this.currentSector = currentSector;
    }

    /**
     * Returns the unique ID of this ship.
     *
     * @return the ship's ID
     */
    public String getIdShip() {
        return idShip;
    }

    /**
     * Sets a new unique ID for this ship.
     *
     * @param idShip the new ID of the ship
     */
    public void setIdShip(String idShip) {
        this.idShip = idShip;
    }

    /**
     * Returns the color of this ship.
     *
     * @return the ship's color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of this ship.
     *
     * @param color the new color for the ship
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns a string representation of this ship, including its owner,
     * ID, color, position, and current sector level.
     *
     * @return a string describing this ship
     */
    @Override
    public String toString() {
        return "Ship{" +
                "owner=" + owner.getName() +
                ", idShip='" + idShip + '\'' +
                ", color='" + color + '\'' +
                ", position=(" + position.q + ", " + position.r + ", " + position.s + ")" +
                ", currentSectorLevel=" + currentSector.getSector() +
                '}';
    }
}
