package pacman.board.cell;

import fri.shapesge.Circle;
import pacman.board.Board;
import pacman.util.Position;
import pacman.util.ScoreManager;

public class PowerPelletCell extends Cell {
    private final Circle sprite;
    private boolean isEaten = false;

    public PowerPelletCell(Position position) {
        super(position);

        this.sprite = new Circle();
        this.sprite.changeSize(12);
        this.sprite.changeColor("yellow");
        this.sprite.changePosition(this.windowPosition().getX() + CELL_SIZE / 2 - 6, this.windowPosition().getY() + CELL_SIZE / 2 - 6);

        this.draw();
    }

    public PowerPelletCell(int col, int row) {
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
