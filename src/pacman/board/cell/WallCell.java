package pacman.board.cell;

import fri.shapesge.Square;
import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class WallCell extends Cell {
    private final Square square;

    public WallCell(Position position) {
        super(position);
        this.square = new Square(this.windowPosition().getX(), this.windowPosition().getY());
        this.square.changeSize(CELL_SIZE);
        this.square.changeColor("dark blue");

        this.draw();
    }

    public WallCell(int col, int row) {
        this(new Position(col, row));
    }

    @Override
    public void draw() {
        this.square.makeVisible();
    }

    @Override
    public void hide() {
    }

    @Override
    public void onEnter(ScoreManager scoreManager, Board board) {
        throw new IllegalStateException("can't enter wall");
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
