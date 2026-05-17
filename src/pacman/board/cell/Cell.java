package pacman.board.cell;

import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

/**
 * Abstract base class for all board cells in the grid.
 * <p>
 * Each cell knows its pixel position on screen and declares how it behaves
 * when entered by Pac-Man (onEnter), whether it blocks movement
 * (isWalkable), and how it renders itself (draw, hide).
 */
public abstract class Cell {
    public static final int CELL_SIZE = 20;
    private final Position windowPosition;

    /**
     * Creates a cell at the given board position, computing the corresponding pixel position.
     *
     * @param position grid position of this cell
     */
    public Cell(Position position) {
        this.windowPosition = new  Position(position.getX() * CELL_SIZE, position.getY() *  CELL_SIZE + 35);
    }

    /**
     * Returns the pixel position of this cell on screen.
     *
     * @return window position
     */
    public Position windowPosition() {
        return this.windowPosition;
    }

    /** Makes this cell's sprite visible. */
    public abstract void draw();

    /** Makes this cell's sprite invisible. */
    public abstract void hide();

    /**
     * Called when Pac-Man enters this cell.
     * Subclasses implement scoring, power-up activation, or illegal-state guards.
     *
     * @param scoreManager score manager to apply point changes to
     * @param board game board, used to signal dot consumption
     */
    public abstract void onEnter(ScoreManager scoreManager, Board board);

    /**
     * Returns whether entities may move into this cell.
     *
     * @return true if the cell is passable
     */
    public abstract boolean isWalkable();


}
