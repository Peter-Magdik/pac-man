package pacman.util;

/**
 * Represents the states of the game loop.
 */
public enum GameState {
    /** The game is over. */
    GAME_OVER,
    /** Normal gameplay is in progress. */
    RUNNING,
    /** A brief pause between a death and resuming play. */
    RESETTING,
    /** The player has cleared all dots and won. */
    WON,
    /** The game is paused by the player. */
    PAUSED
}
