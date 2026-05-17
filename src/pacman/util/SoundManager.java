package pacman.util;

import fri.shapesge.Music;
import fri.shapesge.SoundEffect;

public final class SoundManager {
    private static final Music GHOST_MOVE_MUSIC = new Music(Sound.GHOST_MOVE.path());
    private static final Music GHOST_RETURN_MUSIC = new Music(Sound.GHOST_RETURN_TO_HOME.path());
    private static final Music MENU_MUSIC = new Music(Sound.MENU.path());

    private static final SoundEffect DOT_SFX = new SoundEffect(Sound.EATING_DOT.path());
    private static final SoundEffect GHOST_SFX = new SoundEffect(Sound.EATING_GHOST.path());
    private static final SoundEffect PELLET_SFX = new SoundEffect(Sound.EATING_POWER_PELLET.path());
    private static final SoundEffect FAIL_SFX = new SoundEffect(Sound.FAIL.path());

    private static Music currentLoop = null;

    private SoundManager() { }

    public static void playOnce(Sound sound) {
        switch (sound) {
            case EATING_DOT -> DOT_SFX.play();
            case EATING_GHOST -> GHOST_SFX.play();
            case EATING_POWER_PELLET -> PELLET_SFX.play();
            case FAIL -> FAIL_SFX.play();
            default -> { }
        }
    }

    public static void playLoop(Sound sound) {
        Music next = switch (sound) {
            case GHOST_MOVE -> GHOST_MOVE_MUSIC;
            case GHOST_RETURN_TO_HOME -> GHOST_RETURN_MUSIC;
            case MENU -> MENU_MUSIC;
            default -> null;
        };
        if (next == null || next == currentLoop) {
            return;
        }
        if (currentLoop != null) {
            currentLoop.stop();
        }
        currentLoop = next;
        currentLoop.play();
    }

    public static void stopLoop() {
        if (currentLoop != null) {
            currentLoop.stop();
            currentLoop = null;
        }
    }

    public static void tick() {
        if (currentLoop != null && !currentLoop.isPlaying()) {
            currentLoop.play();
        }
    }
}
