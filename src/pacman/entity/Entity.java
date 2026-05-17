package pacman.entity;

import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;
import fri.shapesge.Image;

/**
 * Base class for all movable entities in the game.
 * <p>
 * Provides:
 * <ul>
 *     <li>Grid-based movement</li>
 *     <li>Smooth interpolation between tiles</li>
 *     <li>Tunnel wrapping logic</li>
 *     <li>Spawn/reset system</li>
 *     <li>Rendering sprite support</li>
 * </ul>
 */
public abstract class Entity {
    public static final int SIZE = 20;
    public static final int BOARD_COLS = 28;
    public static final int BOARD_ROWS = 31;
    public static final int TUNNEL_ROW = 14;
    public static final int FRAMES_PER_TILE = 4;

    private Position boardPosition;
    private Position windowPosition;
    private Direction direction;
    private Image sprite;

    private final Position spawnPosition;
    private final Direction spawnDirection;

    private boolean moving;
    private float progress;
    private Position fromPosition;
    private Position toPosition;

    /**
     * Creates an entity at a given board position and direction.
     *
     * @param position starting grid position
     * @param direction initial movement direction
     */
    public Entity(Position position, Direction direction) {
        this.boardPosition = position;
        this.windowPosition = new Position(position.getX() * SIZE - 5, position.getY() * SIZE + 30);
        this.direction = direction;
        this.spawnPosition = position;
        this.spawnDirection = direction;
    }

    /**
     * Creates an entity using grid coordinates.
     *
     * @param startCol starting column
     * @param startRow starting row
     * @param direction initial movement direction
     */
    public Entity(int startCol, int startRow, Direction direction) {
        this(new Position(startCol, startRow), direction);
    }

    /**
     * Updates entity
     */
    public abstract void update();

    /**
     * Renders entity sprite state to screen.
     */
    public abstract void render();

    /**
     * Returns the sprite associated with this entity.
     *
     * @return sprite instance
     */
    public Image getSprite() {
        return this.sprite;
    }

    /**
     * Assigns a sprite to this entity.
     *
     * @param sprite image object
     */
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    /**
     * Hides entity sprite if available.
     */
    public void hide() {
        if (this.sprite != null) {
            this.sprite.makeInvisible();
        }
    }

    /**
     * Shows entity sprite if available.
     */
    public void show() {
        if (this.sprite != null) {
            this.sprite.makeVisible();
        }
    }

    /**
     * Returns current board position.
     *
     * @return grid position
     */
    public Position boardPosition() {
        return this.boardPosition;
    }

    /**
     * @param boardPosition sets new board position
     */
    public void setBoardPosition(Position boardPosition) {
        this.boardPosition = boardPosition;
    }

    /**
     * Returns current window/screen position.
     *
     * @return pixel position
     */
    public Position windowPosition() {
        return this.windowPosition;
    }

    /**
     * @param windowPosition sets new window position
     */
    public void setWindowPosition(Position windowPosition) {
        this.windowPosition = windowPosition;
    }

    /**
     * Returns current movement direction.
     *
     * @return direction
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * @param direction sets new direction of entity
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Checks if entity is currently in movement animation.
     *
     * @return true if moving between tiles
     */
    public boolean isMoving() {
        return this.moving;
    }

    /**
     * Resets entity to its spawn position and state.
     */
    public void resetToSpawn() {
        this.boardPosition = this.spawnPosition;
        this.windowPosition = new Position(this.spawnPosition.getX() * SIZE - 5, this.spawnPosition.getY() * SIZE + 30);
        this.direction = this.spawnDirection;
        this.moving = false;
        this.progress = 0f;
        this.fromPosition = null;
        this.toPosition = null;
    }

    /**
     * Starts movement animation toward next tile.
     *
     * @param dir movement direction
     */
    public void startMove(Direction dir) {
        this.fromPosition = new Position(this.windowPosition.getX(), this.windowPosition.getY());
        this.setBoardPosition(this.nextPosition(dir));
        this.toPosition = new Position(
            this.boardPosition.getX() * SIZE - 2,
            this.boardPosition.getY() * SIZE + 33
        );
        this.setDirection(dir);
        this.progress = 0f;
        this.moving = true;
    }

    /**
     * Advances movement by one tick.
     */
    public void tickMovement() {
        if (!this.moving) {
            return;
        }
        this.progress += 1f / FRAMES_PER_TILE;
        if (this.progress >= 1f) {
            this.progress = 1f;
            this.moving = false;
        }
        this.setWindowPosition(new Position(
            (int)(this.fromPosition.getX() + (this.toPosition.getX() - this.fromPosition.getX()) * this.progress),
            (int)(this.fromPosition.getY() + (this.toPosition.getY() - this.fromPosition.getY()) * this.progress)
        ));
    }

    /**
     * Checks whether movement wraps through tunnel edges.
     *
     * @param dir movement direction
     * @return true if tunnel wrap applies
     */
    public boolean isTunnelWrap(Direction dir) {
        int col = this.boardPosition.getX();
        int row = this.boardPosition.getY();
        return row == TUNNEL_ROW
            && ((col == 0 && dir == Direction.LEFT) || (col == BOARD_COLS - 1 && dir == Direction.RIGHT));
    }

    /**
     * Computes next board position for a movement direction.
     *
     * @param dir movement direction
     * @return next position
     */
    public Position nextPosition(Direction dir) {
        if (this.isTunnelWrap(dir)) {
            return this.boardPosition.translateWrapped(dir, BOARD_COLS, BOARD_ROWS);
        }
        return this.boardPosition.translate(dir);
    }

    /**
     * Checks whether movement in a direction is possible.
     *
     * @param dir movement direction
     * @param board game board
     * @return true if move is valid
     */
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
