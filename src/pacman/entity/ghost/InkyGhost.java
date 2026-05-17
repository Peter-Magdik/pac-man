package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

/**
 * Inky - the cyan ghost.
 * <p>
 * Chase behavior: vector-based flanking. Computes a pivot 2 tiles ahead of Pac-Man,
 * then doubles the vector from Blinky to that pivot to find the target cell.
 * This makes Inky's path depend on both Pac-Man's and Blinky's positions simultaneously.<br>
 * Scatter behavior: retreats to the bottom-right corner of the board.
 */
public class InkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/inky";
    private static final Position SCATTER_CORNER = new Position(26, 29);

    /**
     * Creates Inky at the given board position.
     *
     * @param startCol starting column
     * @param startRow starting row
     * @param respawnCol respawn home column
     * @param respawnRow respawn home row
     * @param direction initial direction
     */
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
