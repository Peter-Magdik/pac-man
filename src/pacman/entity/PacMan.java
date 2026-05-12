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
        this.setWindowPosition(new Position(this.boardPosition().getX() * SIZE, this.boardPosition().getY() * SIZE + 40));
    }

    @Override
    public void move(Board board) {
        if (this.pendingDirection != null && this.canMove(this.pendingDirection, board)) {
            this.setDirection(this.pendingDirection);
            this.pendingDirection = null;
        }

        if (this.canMove(this.getDirection(), board)) {
            this.setBoardPosition(this.boardPosition().translate(this.getDirection()));
            board.getCell(this.boardPosition().getX(), this.boardPosition().getY()).onEnter(this.scoreManager);
        }
    }
}
