package pacman.board.cell;

import fri.shapesge.Circle;
import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

/**
 * A large power pellet cell that, when entered, awards bonus points and activates
 * Pac-Man's power mode, allowing him to eat ghosts temporarily.
 * Once eaten the sprite is hidden and the pellet is never collected again.
 */
public class PowerPelletCell extends Cell {
    private final Circle sprite;
    private boolean isEaten = false;

    /**
     * Creates a power pellet cell at the given board position.
     *
     * @param position grid position
     */
    public PowerPelletCell(Position position) {
        super(position);

        this.sprite = new Circle();
        this.sprite.changeSize(12);
        this.sprite.changeColor("peach");
        this.sprite.changePosition(this.windowPosition().getX() + CELL_SIZE / 2 - 6, this.windowPosition().getY() + CELL_SIZE / 2 - 6);

        this.draw();
    }

    /**
     * Creates a power pellet cell using grid coordinates.
     *
     * @param col column index
     * @param row row index
     */
    public PowerPelletCell(int col, int row) {
        this(new Position(col, row));
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
        scoreManager.addPowerPelletPoints();
        board.dotConsumed();
    }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
