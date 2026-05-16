package pacman.board.cell;

import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

public abstract class Cell {
    public static final int CELL_SIZE = 20;
    private final Position windowPosition;

    public Cell(Position position) {
        this.windowPosition = new  Position(position.getX() * CELL_SIZE, position.getY() *  CELL_SIZE + 40);
    }

    public Position windowPosition() {
        return this.windowPosition;
    }

    public abstract void draw();

    public abstract void hide();

    public abstract void onEnter(ScoreManager scoreManager, Board board);

    public abstract boolean isWalkable();


}
