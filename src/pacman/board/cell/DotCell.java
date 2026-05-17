package pacman.board.cell;

import fri.shapesge.Circle;
import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

/**
 * A collectible dot cell worth standard points.
 * Once eaten the sprite is hidden and the dot is never collected again.
 */
public class DotCell extends Cell {
    private final Circle sprite;
    private boolean isEaten = false;

    /**
     * Creates a dot cell at the given board position.
     *
     * @param position grid position
     */
    public DotCell(Position position) {
        super(position);
        this.sprite = new Circle();
        this.sprite.changeSize(6);
        this.sprite.changeColor("peach");
        this.sprite.changePosition(this.windowPosition().getX() + CELL_SIZE / 2 - 3, this.windowPosition().getY() + CELL_SIZE / 2 - 3);

        this.draw();
    }

    /**
     * Creates a dot cell using grid coordinates.
     *
     * @param col column index
     * @param row row index
     */
    public DotCell(int col, int row) {
        this(new  Position(col, row));
    }

    @Override
    public void draw() {
        if (!this.isEaten) {
            this.sprite.makeVisible();
        }
    }

    @Override
    public void hide() {
        this.sprite.makeInvisible();
    }

    @Override
    public void onEnter(ScoreManager scoreManager, Board board) {
        if (this.isEaten) {
            return;
        }
        this.isEaten = true;
        this.hide();
        scoreManager.addDotPoints();
        board.dotConsumed();
    }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
