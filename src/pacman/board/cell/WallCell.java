package pacman.board.cell;

import fri.shapesge.Square;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class WallCell extends Cell {
    private Square square;

    public WallCell(Position position) {
        super(position);
        this.square = new Square(this.windowPosition().getX(), this.windowPosition().getY());
        this.square.changeSize(CELL_SIZE);

        this.draw();
    }

    @Override
    public void draw() {
        this.square.makeVisible();
    }

    @Override
    public void hide() {
    }

    @Override
    public void onEnter(ScoreManager scoreManager) {
        throw new IllegalStateException("can't enter wall");
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
