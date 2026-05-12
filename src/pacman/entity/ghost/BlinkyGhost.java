package pacman.entity.ghost;

import fri.shapesge.Image;
import pacman.board.Board;
import pacman.util.Direction;

public class BlinkyGhost extends Ghost {
    private static final String SPRITE_DIR = "resources/ghosts/blinky";

    public BlinkyGhost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, respawnCol, respawnRow, direction);
        this.setSprite(new Image(String.format("%s/%s0.png", SPRITE_DIR, direction.name())));
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
        this.getSprite().makeVisible();
    }

    @Override
    public Direction calculateNextMove(Board board) {
        return null;
    }

    @Override
    public String getSpriteDir() {
        return SPRITE_DIR;
    }
}
