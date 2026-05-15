package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;
import pacman.util.Position;

public class PinkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/pinky";

    public PinkyGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, respawnCol, respawnRow, direction);
        this.setSprite(new Image(String.format("%s/%s0.png", SPRITE_DIR, direction.name())));
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        this.getSprite().makeVisible();
    }

    @Override
    public Direction calculateNextMove(Board board, Position pacmanPosition, Direction pacmanDirection, Position blinkyPosition) {
        // Pinky: ambush — targets 4 tiles ahead of Pac-Man's current direction
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
