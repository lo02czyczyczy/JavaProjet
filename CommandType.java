package game;

/**
 * Defines the types of commands available in the game.
 * Each command represents a distinct action a player can take.
 */
enum CommandType {
    /** Represents the action of expanding a player's territory. */
    EXPAND, 

    /** Represents the action of exploring new sectors in the game. */
    EXPLORE, 

    /** Represents the action of exterminating opponents' units. */
    EXTERMINATE
}
