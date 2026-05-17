package pacman.util;

import java.util.Objects;

/**
 * Immutable 2D position within the pacman grid.
 */
public class Position {
    private final int x;
    private final int y;

    /**
     * Creates a position with given coordinates.
     * @param x column index (0 = leftmost)
     * @param y row index (0 = topmost)
     */
    public Position(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Position coordinates must not be negative: x=" + x + ", y=" + y);
        }
        this.x = x;
        this.y = y;
    }

    /**
     * @return x-coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Creates a new position translated by the given direction.
     *
     * @param direction movement direction
     * @return translated position
     */
    public Position translate(Direction direction) {
        return new Position(this.x + direction.dx(), this.y + direction.dy());
    }

    /**
     * Creates a new position translated by the given direction, wrapping around
     * the board edges (used for tunnel cells).
     *
     * @param direction movement direction
     * @param cols total number of columns in the board
     * @param rows total number of rows in the board
     * @return wrapped translated position
     */
    public Position translateWrapped(Direction direction, int cols, int rows) {
        int newX = (this.x + direction.dx() + cols) % cols;
        int newY = (this.y + direction.dy() + rows) % rows;
        return new Position(newX, newY);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        Position position = (Position)object;

        return (this.x == position.x && this.y == position.y);
    }

    // Required for correct behavior in hash-based collections (maybe will come handy)
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return String.format("[%d; %d]", this.x, this.y);
    }
}