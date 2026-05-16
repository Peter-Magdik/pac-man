package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

public class ClydeGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/clyde";
    private static final Position SCATTER_CORNER = new Position(1, 29);
    private static final int CHASE_THRESHOLD = 4;

    public ClydeGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
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
        // CHASE: shy —> chases with BFS when far (>4 tiles), retreats to scatter corner when close
        Position target = this.manhattanDistance(this.boardPosition(), pacmanPosition) > CHASE_THRESHOLD
            ? pacmanPosition
            : SCATTER_CORNER;
        return this.bfsNextDirection(board, this.boardPosition(), target);
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
