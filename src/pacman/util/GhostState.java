package pacman.util;

/**
 * Represents the behavioral state of a ghost.
 */
public enum GhostState {
    /** Ghost actively hunts the player using its targeting algorithm. */
    CHASE,
    /** Ghost retreats to its designated corner of the maze. */
    SCATTER,
    /** Ghost is vulnerable and flees from the player after a power pellet. */
    FRIGHTENED,
    /** Ghost has been eaten and is traveling back to the ghost house. */
    RESPAWNING
}
