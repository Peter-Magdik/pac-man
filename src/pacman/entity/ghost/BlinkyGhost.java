package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

/**
 * Blinky - the red ghost.
 * <p>
 * Chase behavior: directly pursues Pac-Man's current cell via BFS.<br>
 * Scatter behavior: retreats to the top-right corner of the board.
 */
public class BlinkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/blinky";
    private static final Position SCATTER_CORNER = new Position(26, 1);

    /**
     * Creates Blinky at the given board position.
     *
     * @param startCol starting column
     * @param startRow starting row
     * @param respawnCol respawn home column
     * @param respawnRow respawn home row
     * @param direction initial direction
     */
    public BlinkyGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
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

        return this.bfsNextDirection(board, this.boardPosition(), pacmanPosition);
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
