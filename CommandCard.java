package game;

import java.util.*;

/**
 * Represents a command card in the game, defining an action type, its owner, and the execution order.
 * Each card is associated with a specific command type (Expand, Explore, or Exterminate).
 */
public class CommandCard {
    
    /** The type of command this card represents. */
    private CommandType commandType;

    /** The player owning this command card. */
    private Player player;

    /** The execution order of the commands. */
    private List<Integer> order;

    /**
     * Constructs a CommandCard with a specified command type, player, and command order.
     * The order is chosen by the player.
     *
     * @param commandType The type of command this card represents.
     * @param player      The player owning this card.
     * @param order       The initial execution order as a string (not used in logic).
     */
    public CommandCard(CommandType commandType, Player player, String order) {
        this.commandType = commandType;
        this.player = player;
        this.order = player.chooseCommandOrder();
    }

    /**
     * Executes the command associated with this card.
     * Implementation depends on the specific game logic.
     */
    public void execute() {
        // Implement command execution logic
    }

    /**
     * Checks if the command type is Expand.
     *
     * @return true if the command type is EXPAND, false otherwise.
     */
    public boolean isExpandable() {
        return commandType == CommandType.EXPAND;
    }

    /**
     * Checks if the command type is Explore.
     *
     * @return true if the command type is EXPLORE, false otherwise.
     */
    public boolean isExplorable() {
        return commandType == CommandType.EXPLORE;
    }

    /**
     * Checks if the command type is Exterminate.
     *
     * @return true if the command type is EXTERMINATE, false otherwise.
     */
    public boolean isExterminable() {
        return commandType == CommandType.EXTERMINATE;
    }

    // Getters and Setters

    /**
     * Gets the command type.
     *
     * @return The current command type.
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Sets the command type.
     *
     * @param commandType The new command type.
     */
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    /**
     * Gets the player owning this card.
     *
     * @return The owning player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player owning this card.
     *
     * @param player The new owning player.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the execution order of the commands.
     *
     * @return The list of command execution orders.
     */
    public List<Integer> getOrder() {
        return order;
    }

    /**
     * Sets the execution order of the commands.
     *
     * @param order The new list of command execution orders.
     */
    public void setOrder(List<Integer> order) {
        this.order = order;
    }
}
