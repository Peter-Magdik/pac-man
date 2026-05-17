package pacman.util;

/**
 * Tracks the player's score and remaining lives for a single game session.
 * Score is accumulated by eating dots, power pellets, and ghosts.
 * The manager also exposes one-shot poll flags so the game loop can react
 * to consumption events without coupling cell logic to sound or state changes.
 */
public class ScoreManager {
    private final int startScore;
    private int score;
    private final int startLives;
    private int lives;
    private boolean powerPelletConsumed;
    private boolean dotConsumed;

    /**
     * Creates a manager with the default starting values (score 0, 3 lives).
     */
    public ScoreManager() {
        this.startScore = 0;
        this.score = 0;
        this.startLives = 3;
        this.lives = 3;
        this.powerPelletConsumed = false;
    }

    /**
     * Creates a manager with custom starting values (for save-game support).
     *
     * @param score initial score
     * @param lives initial number of lives
     */
    public ScoreManager(int score, int lives) {
        this.startScore = score;
        this.score = score;
        this.startLives = lives;
        this.lives = lives;
        this.powerPelletConsumed = false;
    }

    /**
     * Adds one point for eating a dot and sets the dot-consumed flag.
     */
    public void addDotPoints() {
        this.score++;
        this.dotConsumed = true;
    }

    /**
     * Adds ten points for eating a power pellet and sets the pellet-consumed flag.
     */
    public void addPowerPelletPoints() {
        this.score += 10;
        this.powerPelletConsumed = true;
    }

    /**
     * Adds two hundred points for eating a frightened ghost.
     */
    public void addGhostEatenPoints() {
        this.score += 200;
    }

    /**
     * Returns true and clears the flag if a dot was consumed since the last poll.
     */
    public boolean pollDotConsumed() {
        if (this.dotConsumed) {
            this.dotConsumed = false;
            return true;
        }
        return false;
    }

    /**
     * Returns {@code true} and clears the flag if a power pellet was consumed since the last poll.
     *
     * @return {@code true} if a power pellet was consumed
     */
    public boolean pollPowerPelletConsumed() {
        if (this.powerPelletConsumed) {
            this.powerPelletConsumed = false;
            return true;
        }
        return false;
    }

    /**
     * Deducts one life from the player, down to a minimum of zero.
     */
    public void loseLife() {
        if (this.lives > 0) {
            this.lives--;
        }
    }

    /**
     * Returns the number of lives remaining.
     *
     * @return remaining lives
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * Returns true when the player has no lives left.
     */
    public boolean isGameOver() {
        return this.lives <= 0;
    }

    /**
     * Resets score and lives back to their initial values for a new game.
     */
    public void reset() {
        this.score = this.startScore;
        this.lives = this.startLives;
        this.powerPelletConsumed = false;
        this.dotConsumed = false;
    }

    /**
     * @return current score
     */
    public int getScore() {
        return this.score;
    }
}
