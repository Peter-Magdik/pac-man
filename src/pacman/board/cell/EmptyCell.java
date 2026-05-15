package pacman.board.cell;

import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class EmptyCell extends Cell {
    public EmptyCell(Position position) {
        super(position);
    }

    public EmptyCell(int row, int col) {
        this(new  Position(col, row));
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
