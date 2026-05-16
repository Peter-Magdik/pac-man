package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

public class BlinkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/blinky";
    private static final Position SCATTER_CORNER = new Position(25, 1);

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
        // CHASE: direct BFS — always targets Pac-Man's exact cell
        return this.bfsNextDirection(board, this.boardPosition(), pacmanPosition);
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
