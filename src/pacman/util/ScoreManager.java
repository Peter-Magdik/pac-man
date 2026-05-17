package pacman.util;

public class ScoreManager {
    private final int startScore;
    private int score;
    private final int startLives;
    private int lives;
    private boolean powerPelletConsumed;
    private boolean dotConsumed;

    public ScoreManager() {
        this.startScore = 0;
        this.score = 0;
        this.startLives = 3;
        this.lives = 3;
        this.powerPelletConsumed = false;
    }

    // for future implementations of loading games
    public ScoreManager(int score, int lives) {
        this.startScore = score;
        this.score = score;
        this.startLives = lives;
        this.lives = lives;
        this.powerPelletConsumed = false;
    }

    public void addDotPoints() {
        this.score++;
        this.dotConsumed = true;
    }

    public void addPowerPelletPoints() {
        this.score += 10;
        this.powerPelletConsumed = true;
    }

    public void addGhostEatenPoints() {
        this.score += 200;
    }

    /** Returns true and clears the flag if a dot was just consumed. */
    public boolean pollDotConsumed() {
        if (this.dotConsumed) {
            this.dotConsumed = false;
            return true;
        }
        return false;
    }

    /** Returns true and clears the flag if a power pellet was just consumed. */
    public boolean pollPowerPelletConsumed() {
        if (this.powerPelletConsumed) {
            this.powerPelletConsumed = false;
            return true;
        }
        return false;
    }

    public void loseLife() {
        if (this.lives > 0) {
            this.lives--;
        }
    }

    public int getLives() {
        return this.lives;
    }

    public boolean isGameOver() {
        return this.lives <= 0;
    }

    public void reset()  {
        this.score = this.startScore;
        this.lives = this.startLives;
        this.powerPelletConsumed = false;
        this.dotConsumed = false;
    }

    public int getScore() {
        return this.score;
    }
}
