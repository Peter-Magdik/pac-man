package pacman.util;

import fri.shapesge.Music;
import fri.shapesge.SoundEffect;

/**
 * Utility class responsible for managing all game audio playback.
 * Handles:
 * One-shot sound effects
 * Looped background/music tracks
 * Automatic loop restarting
 * This class is static-only and cannot be instantiated.
 */
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

    /**
     * Plays a non-looping sound effect once.
     * Only sound effects are handled here. Unsupported sounds are ignored.
     *
     * @param sound the sound effect to play
     */
    public static void playOnce(Sound sound) {
        switch (sound) {
            case EATING_DOT -> DOT_SFX.play();
            case EATING_GHOST -> GHOST_SFX.play();
            case EATING_POWER_PELLET -> PELLET_SFX.play();
            case FAIL -> FAIL_SFX.play();
            default -> { }
        }
    }

    /**
     * Starts playing a looped music track.
     * If another loop is already playing, it is stopped first.
     * If the requested loop is already active or unsupported,
     * the method does nothing.
     *
     * @param sound the looped soundtrack to play
     */
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

    /**
     * Stops the currently active looped music track.
     * If no loop is active, nothing happens.
     */
    public static void stopLoop() {
        if (currentLoop != null) {
            currentLoop.stop();
            currentLoop = null;
        }
    }

    /**
     * Updates loop playback state.
     * If the current loop stops playing, it is automatically restarted.
     * Intended to be called regularly from the game loop.
     */
    public static void tick() {
        if (currentLoop != null && !currentLoop.isPlaying()) {
            currentLoop.play();
        }
    }
}
