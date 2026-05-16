package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

public class InkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/inky";
    private static final Position SCATTER_CORNER = new Position(26, 29);

    public InkyGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, respawnCol, respawnRow, direction);
        this.setSprite(new Image(String.format("%s/%s0.png", SPRITE_DIR, direction.name())));
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        this.getSprite().makeVisible();
    }

    @Override
    public Direction calculateNextMove(Board board, Position pacmanPosition, Direction pacmanDirection, Position blinkyPosition) {
        if (this.getState() == pacman.util.GhostState.SCATTER) {
            return this.bfsNextDirection(board, this.boardPosition(), SCATTER_CORNER);
        }
        // CHASE: vector-based flanking
        // 1. Take the cell 2 tiles ahead of Pac-Man as pivot
        // 2. Draw a vector from Blinky to that pivot
        // 3. Double the vector —> the endpoint is Inky's target
        int pivotCol = pacmanPosition.getX() + pacmanDirection.dx() * 2;
        int pivotRow = pacmanPosition.getY() + pacmanDirection.dy() * 2;

        int targetCol = pivotCol + (pivotCol - blinkyPosition.getX());
        int targetRow = pivotRow + (pivotRow - blinkyPosition.getY());

        Position target = this.clampToBoard(targetCol, targetRow);
        return this.bfsNextDirection(board, this.boardPosition(), target);
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
