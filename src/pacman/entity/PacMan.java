package pacman.entity;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.ScoreManager;

public class PacMan extends Entity {
    private final ScoreManager scoreManager;
    private Direction pendingDirection;
    private boolean powerMode;
    private int powerTimer;
    private int invincibleTimer;
    private static final int INVINCIBLE_TICKS = 120;
    private static final String[] FRAMES = {"resources/pacman/0.png", "resources/pacman/1.png", "resources/pacman/2.png"};
    private int frameIndex;

    public PacMan(int row, int col) {
        super(row, col, 5, Direction.RIGHT);
        this.powerMode = false;
        this.powerTimer = 0;
        this.frameIndex = 0;
        this.setSprite(new Image(FRAMES[this.frameIndex]));
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        this.getSprite().makeVisible();
        this.scoreManager = new  ScoreManager();
    }

    public void activatePowerMode() {
        this.powerMode = true;
        this.powerTimer = 200;
    }

    public boolean isInvincible() {
        return this.invincibleTimer > 0;
    }

    public void activateInvincibility() {
        this.invincibleTimer = INVINCIBLE_TICKS;
    }

    @Override
    public void resetToSpawn() {
        super.resetToSpawn();
        this.pendingDirection = null;
        this.powerMode = false;
        this.powerTimer = 0;
        this.invincibleTimer = 0;
        this.frameIndex = 0;
    }

    public void setPendingDirection(Direction pendingDirection) {
        this.pendingDirection = pendingDirection;
    }

    public ScoreManager getScoreManager() {
        return this.scoreManager;
    }

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

    @Override
    public void move(Board board) {
        if (this.isMoving()) {
            return;
        }

        if (this.pendingDirection != null && this.canMove(this.pendingDirection, board)) {
            this.setDirection(this.pendingDirection);
            this.pendingDirection = null;
        }

        if (this.canMove(this.getDirection(), board)) {
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager);
            this.startMove(this.getDirection());
        } else {
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager);
        }
    }
}
