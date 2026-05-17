package pacman.board.cell;

import fri.shapesge.Circle;
import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class DotCell extends Cell {
    private final Circle sprite;
    private boolean isEaten = false;

    public DotCell(Position position) {
        super(position);
        this.sprite = new Circle();
        this.sprite.changeSize(6);
        this.sprite.changeColor("peach");
        this.sprite.changePosition(this.windowPosition().getX() + CELL_SIZE / 2 - 3, this.windowPosition().getY() + CELL_SIZE / 2 - 3);

        this.draw();
    }

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
