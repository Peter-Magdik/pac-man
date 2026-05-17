package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

/**
 * Pinky - the pink ghost.
 * <p>
 * Chase behavior: targets 4 tiles ahead of Pac-Man's current direction, attempting to ambush.<br>
 * Scatter behavior: retreats to the top-left corner of the board.
 */
public class PinkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/pinky";
    private static final Position SCATTER_CORNER = new Position(1, 1);

    /**
     * Creates Pinky at the given board position.
     *
     * @param startCol starting column
     * @param startRow starting row
     * @param respawnCol respawn home column
     * @param respawnRow respawn home row
     * @param direction initial direction
     */
    public PinkyGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
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

        int targetCol = pacmanPosition.getX() + pacmanDirection.dx() * 4;
        int targetRow = pacmanPosition.getY() + pacmanDirection.dy() * 4;
        Position target = this.clampToBoard(targetCol, targetRow);
        return this.bfsNextDirection(board, this.boardPosition(), target);
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
