package pacman.board.cell;

import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

/**
 * A passable cell with no collectible content.
 * Used for open corridors, ghost-house interior, and tunnel edge columns
 * that have no associated sprite and trigger no game events on entry.
 */
public class EmptyCell extends Cell {
    /**
     * Creates an empty cell at the given board position.
     *
     * @param position grid position
     */
    public EmptyCell(Position position) {
        super(position);
    }

    /**
     * Creates an empty cell using grid coordinates.
     *
     * @param col column index
     * @param row row index
     */
    public EmptyCell(int col, int row) {
        this(new Position(col, row));
    }

    @Override
    public void draw() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void onEnter(ScoreManager scoreManager, Board board) { }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
