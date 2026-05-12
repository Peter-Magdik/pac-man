package pacman.entity.ghost;

import pacman.board.Board;
import pacman.entity.Entity;
import pacman.util.Direction;
import pacman.util.GhostState;
import pacman.util.Position;

public abstract class Ghost extends Entity {
    public static final int CELL_SIZE = 20;
    private static final int SPEED = 2;
    private static final int FRIGHTENED_TICKS = 200;
    public static final int NORMAL_FRAMES = 2;
    public static final int FRIGHTENED_FRAMES = 4;

    private GhostState state;
    private final Position homePosition;
    private int frightenedTimer;
    private int frameIndex;

    public Ghost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, SPEED, direction);
        this.homePosition = new  Position(respawnCol, respawnRow);
        this.state = GhostState.CHASE;
        this.frameIndex = 0;
    }

    public abstract Direction calculateNextMove(Board board);

    @Override
    public void move(Board board) {
        if (this.state == GhostState.RESPAWNING) {
            this.moveToHome();
            return;
        }

        // TODO after implementation of graph
    }

    public void setFrightened() {
        if (this.state != GhostState.RESPAWNING) {
            this.state = GhostState.FRIGHTENED;
            this.frightenedTimer = FRIGHTENED_TICKS;
        }
    }

    public void respawn() {
        this.state = GhostState.CHASE;
        this.frameIndex = 0;
    }

    public void onCaught() {
        this.state = GhostState.RESPAWNING;
        this.setDirection(Direction.RIGHT);
        this.frameIndex = 0;
        this.moveToHome();
    }

    private void moveToHome() {
        if (this.boardPosition().getX() == this.homePosition.getX() && this.boardPosition().getY() == this.homePosition.getY()) {
            this.respawn();
        } else {
            System.out.println();
            // TODO pathfinding using graph
        }
    }

    @Override
    public void update() {
        int maxFrames = (this.state == GhostState.FRIGHTENED) ? FRIGHTENED_FRAMES : NORMAL_FRAMES;
        this.frameIndex = (this.frameIndex + 1) % maxFrames;

        if (this.state == GhostState.FRIGHTENED) {
            this.frightenedTimer--;
            if (this.frightenedTimer <= 0) {
                this.state = GhostState.CHASE;
                this.frameIndex = 0;
            }
        }
    }

    @Override
    public void render() {
        String imageName = switch (this.state) {
            case FRIGHTENED -> String.format("resources/ghosts/frightened/%d.png", this.frameIndex);
            case RESPAWNING -> "resources/ghosts/respawning/1.png";
            default -> String.format("%s/%s%d.png", this.getSpriteDir(), this.getDirection().name(), this.frameIndex);
        };
        this.getSprite().changeImage(imageName);
        this.getSprite().changePosition(this.windowPosition().getX(), this.windowPosition().getY());
    }

    public abstract String getSpriteDir();

    public GhostState getState() {
        return this.state;
    }

    public boolean isFrightened() {
        return this.state == GhostState.FRIGHTENED;
    }
}
