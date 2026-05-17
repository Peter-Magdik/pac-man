package pacman.util;

/**
 * Enumerates all sound assets used in the game, each mapped to its WAV file path.
 */
public enum Sound {
    EATING_DOT("resources/soundEffects/eating_dot.wav"),
    EATING_GHOST("resources/soundEffects/eating_ghost.wav"),
    EATING_POWER_PELLET("resources/soundEffects/eating_power_pellet.wav"),
    FAIL("resources/soundEffects/fail.wav"),
    GHOST_MOVE("resources/soundEffects/ghost_move.wav"),
    GHOST_RETURN_TO_HOME("resources/soundEffects/ghost_return_to_home.wav"),
    MENU("resources/soundEffects/menu.wav");

    private final String path;

    /**
     * Creates a Sound constant with the given asset path.
     *
     * @param path relative path to the WAV file
     */
    Sound(String path) {
        this.path = path;
    }

    /**
     * Returns the relative file-system path to this sound's WAV asset.
     *
     * @return asset path string
     */
    public String path() {
        return this.path;
    }
}
