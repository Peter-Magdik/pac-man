package pacman.entity;

import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;
import fri.shapesge.Image;

public abstract class Entity {
    public static final int SIZE = 20;
    public static final int BOARD_COLS = 28;
    public static final int BOARD_ROWS = 31;
    public static final int TUNNEL_ROW = 14;
    private Position boardPosition;
    private Position windowPosition;
    private int speed;
    private Direction direction;

    private Image sprite;

    public Entity(Position position, int speed, Direction direction) {
        this.boardPosition = position;
        this.windowPosition = new Position(position.getX() * SIZE, position.getY() * SIZE + 40);
        this.speed = speed;
        this.direction = direction;
    }

    public Entity(int startCol, int startRow, int speed, Direction direction) {
        this(new Position(startCol, startRow), speed, direction);
    }

    public abstract void move(Board board);
    public abstract void update();
    public abstract void render();

    public Image getSprite() {
        return this.sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public void hide() {
        if (this.sprite != null) {
            this.sprite.makeInvisible();
        }
    }

    public void show() {
        if (this.sprite != null) {
            this.sprite.makeVisible();
        }
    }

    public Position boardPosition() {
        return this.boardPosition;
    }

    public void setBoardPosition(Position boardPosition) {
        this.boardPosition = boardPosition;
    }

    public Position windowPosition() {
        return this.windowPosition;
    }

    public void setWindowPosition(Position windowPosition) {
        this.windowPosition = windowPosition;
    }

    public int getSpeed() {
        return this.speed;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    protected boolean isTunnelWrap(Direction dir) {
        int col = this.boardPosition.getX();
        int row = this.boardPosition.getY();
        return row == TUNNEL_ROW
            && ((col == 0 && dir == Direction.LEFT) || (col == BOARD_COLS - 1 && dir == Direction.RIGHT));
    }

    public Position nextPosition(Direction dir) {
        if (this.isTunnelWrap(dir)) {
            return this.boardPosition.translateWrapped(dir, BOARD_COLS, BOARD_ROWS);
        }
        return this.boardPosition.translate(dir);
    }

    public boolean canMove(Direction dir, Board board) {
        if (this.isTunnelWrap(dir)) {
            return true;
        }
        int nextCol = this.boardPosition.getX() + dir.dx();
        int nextRow = this.boardPosition.getY() + dir.dy();
        if (nextCol < 0 || nextCol >= BOARD_COLS || nextRow < 0 || nextRow >= BOARD_ROWS) {
            return false;
        }
        return board.isWalkable(nextCol, nextRow);
    }
}
