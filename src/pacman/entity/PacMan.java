package pacman.entity;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class PacMan extends Entity {
    private final ScoreManager scoreManager;
    private Direction pendingDirection;
    private boolean powerMode;
    private int powerTimer;
    private static final String[] FRAMES = {"resources/pacman/0.png", "resources/pacman/1.png", "resources/pacman/2.png"};
    private int frameIndex;

    // movement animation
    private float progress;
    private Position fromPosition;
    private Position toPosition;
    private boolean moving;

    private static final int FRAMES_PER_TILE = 4;

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

    public boolean isMoving() {
        return this.moving;
    }

    public boolean isPowerMode() {
        return this.powerMode;
    }

    public void activatePowerMode() {
        this.powerMode = true;
        this.powerTimer = 200;
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

        if (this.moving) {
            this.progress += 1f / FRAMES_PER_TILE;

            if (this.progress >= 1f) {
                this.progress = 1f;
                this.moving = false;
            }

            this.setWindowPosition(
                new Position(
                    (int)(this.fromPosition.getX() + (this.toPosition.getX() - this.fromPosition.getX()) * this.progress),
                    (int)(this.fromPosition.getY() + (this.toPosition.getY() - this.fromPosition.getY()) * this.progress)
                )
            );
        }
    }

    @Override
    public void move(Board board) {
        if (this.moving) {
            return;
        }

        if (this.pendingDirection != null && this.canMove(this.pendingDirection, board)) {
            this.setDirection(this.pendingDirection);
            this.pendingDirection = null;
        }

        if (this.canMove(this.getDirection(), board)) {
            this.fromPosition = new Position(this.windowPosition().getX(), this.windowPosition().getY());
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager);

            this.setBoardPosition(this.nextPosition(this.getDirection()));

            this.toPosition = new Position(this.boardPosition().getX() * SIZE, this.boardPosition().getY() * SIZE + 40);

            this.progress = 0f;
            this.moving = true;
        } else {
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager);
        }
    }
}
