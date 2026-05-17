package pacman.util;

public enum Sound {
    EATING_DOT("resources/soundEffects/eating_dot.wav"),
    EATING_GHOST("resources/soundEffects/eating_ghost.wav"),
    EATING_POWER_PELLET("resources/soundEffects/eating_power_pellet.wav"),
    FAIL("resources/soundEffects/fail.wav"),
    GHOST_MOVE("resources/soundEffects/ghost_move.wav"),
    GHOST_RETURN_TO_HOME("resources/soundEffects/ghost_return_to_home.wav"),
    MENU("resources/soundEffects/menu.wav");

    private final String path;

    Sound(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
