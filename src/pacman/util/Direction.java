package pacman.util;

/**
 * Represents movement directions with their corresponding
 * coordinate deltas.
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    private final int dx;
    private final int dy;

    /**
     * Creates a direction with the given coordinate deltas.
     *
     * @param dx horizontal delta
     * @param dy vertical delta
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns the direction that is exactly opposite to this one.
     *
     * @return opposite direction
     */
    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case NONE -> NONE;
        };
    }

    /**
     * Horizontal movement delta.
     *
     * @return x-axis delta
     */
    public int dx() {
        return this.dx;
    }

    /**
     * Vertical movement delta.
     *
     * @return y-axis delta
     */
    public int dy() {
        return this.dy;
    }
}
