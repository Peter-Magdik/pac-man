package pacman.entity;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.ScoreManager;

/**
 * Player-controlled entity representing Pac-Man.
 * <p>
 * Handles:
 * <ul>
 *     <li>Movement with buffered input direction</li>
 *     <li>Power mode state (ghost-eating ability)</li>
 *     <li>Temporary invincibility after respawn/hit</li>
 *     <li>Animation frame switching</li>
 *     <li>Score interaction via board cells</li>
 * </ul>
 */
public class PacMan extends Entity {
    private final ScoreManager scoreManager;
    private Direction pendingDirection;
    private boolean powerMode;
    private int powerTimer;
    private int invincibleTimer;
    private static final int INVINCIBLE_TICKS = 40;
    private static final String[] FRAMES = {"resources/pacman/0.png", "resources/pacman/1.png", "resources/pacman/2.png"};
    private int frameIndex;

    /**
     * Creates Pac-Man at a given board position and initial direction.
     *
     * @param col starting column
     * @param row starting row
     * @param direction initial movement direction
     */
    public PacMan(int col, int row, Direction direction) {
        super(col, row, direction);
        this.powerMode = false;
        this.powerTimer = 0;
        this.frameIndex = 0;
        this.setSprite(new Image(FRAMES[this.frameIndex]));
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        this.getSprite().makeVisible();
        this.scoreManager = new  ScoreManager();
    }

    /**
     * Activates power mode, enabling ghost consumption for a limited duration.
     */
    public void activatePowerMode() {
        this.powerMode = true;
        this.powerTimer = 200;
    }

    /**
     * Checks whether Pac-Man is currently invincible.
     *
     * @return true if invincibility timer is active
     */
    public boolean isInvincible() {
        return this.invincibleTimer > 0;
    }

    /**
     * Activates temporary invincibility.
     */
    public void activateInvincibility() {
        this.invincibleTimer = INVINCIBLE_TICKS;
    }

    /**
     * Resets Pac-Man to spawn state, clearing all temporary effects.
     */
    @Override
    public void resetToSpawn() {
        super.resetToSpawn();
        this.pendingDirection = null;
        this.powerMode = false;
        this.powerTimer = 0;
        this.invincibleTimer = 0;
        this.frameIndex = 0;
    }

    /**
     * Sets buffered movement direction, applied when movement becomes possible.
     *
     * @param pendingDirection desired next direction
     */
    public void setPendingDirection(Direction pendingDirection) {
        this.pendingDirection = pendingDirection;
    }

    /**
     * Returns the score manager associated with this entity.
     *
     * @return score manager instance
     */
    public ScoreManager getScoreManager() {
        return this.scoreManager;
    }

    /**
     * Renders Pac-Man sprite, orientation, and animation frame.
     */
    @Override
    public void render() {
        this.getSprite().changeImage(FRAMES[this.frameIndex]);
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        switch (this.getDirection()) {
            case RIGHT:
                this.getSprite().changeAngle(0);
                break;
            case LEFT:
                this.getSprite().changeAngle(180);
                break;
            case UP:
                this.getSprite().changeAngle(-90);
                break;
            case DOWN:
                this.getSprite().changeAngle(90);
                break;
            case NONE:
                System.out.println("PacMan has no direction!");
                break;
        }
    }

    /**
     * Updates animation and temporary states (power mode, invincibility),
     * then advances movement.
     */
    @Override
    public void update() {
        this.frameIndex = (this.frameIndex + 1) % FRAMES.length;

        if (this.powerMode) {
            this.powerTimer--;
            if (this.powerTimer <= 0) {
                this.powerMode = false;
            }
        }

        if (this.invincibleTimer > 0) {
            this.invincibleTimer--;
        }

        this.tickMovement();
    }

    /**
     * Attempts to move Pac-Man according to current and buffered direction.
     *
     * @param board game board used for collision and cell interactions
     */
    public void move(Board board) {
        if (this.isMoving()) {
            return;
        }

        if (this.pendingDirection != null && this.canMove(this.pendingDirection, board)) {
            this.setDirection(this.pendingDirection);
            this.pendingDirection = null;
        }

        if (this.canMove(this.getDirection(), board)) {
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager, board);
            this.startMove(this.getDirection());
        } else {
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager, board);
        }
    }
}
