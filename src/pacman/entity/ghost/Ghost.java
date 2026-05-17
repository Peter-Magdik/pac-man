package pacman.entity.ghost;

import pacman.board.Board;
import pacman.entity.Entity;
import pacman.util.Direction;
import pacman.util.GhostState;
import pacman.board.GraphBuilder;
import pacman.util.Position;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all ghost entities.
 * <p>
 * Manages:
 * <ul>
 *     <li>Ghost state: CHASE, SCATTER, FRIGHTENED, RESPAWNING</li>
 *     <li>Timed mode alternation between chase and scatter phases</li>
 *     <li>Frightened and flash animation timing</li>
 *     <li>BFS-based pathfinding used by ghost subclasses</li>
 *     <li>Random movement while frightened</li>
 * </ul>
 */
public abstract class Ghost extends Entity {
    private static final int FRIGHTENED_TICKS = 200;
    private static final int FRIGHTENED_FLASH_TICKS = 60;
    private static final int SCATTER_TICKS = 420;
    private static final int CHASE_TICKS = 1200;
    public static final int NORMAL_FRAMES = 2;
    public static final int FRIGHTENED_FRAMES = 2;

    private static final Random RANDOM = new Random();

    private GhostState state;
    private final Position homePosition;
    private int frightenedTimer;
    private int modeTimer;
    private boolean inScatterPhase;
    private int frameIndex;

    /**
     * Creates a ghost at a given start position with a designated respawn home.
     *
     * @param startCol starting column
     * @param startRow starting row
     * @param respawnCol column of the respawn/home target position
     * @param respawnRow row of the respawn/home target position
     * @param direction initial movement direction
     */
    public Ghost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, direction);
        this.homePosition = new  Position(respawnCol, respawnRow);
        this.state = GhostState.CHASE;
        this.modeTimer = CHASE_TICKS;
        this.inScatterPhase = false;
        this.frameIndex = 0;
    }

    /**
     * Calculates the preferred next movement direction for this ghost.
     * Called each tick when the ghost is in {@code CHASE} or {@code SCATTER} state.
     *
     * @param board game board for collision and graph queries
     * @param pacmanPosition current board position of Pac-Man
     * @param pacmanDirection current movement direction of Pac-Man
     * @param blinkyPosition current board position of Blinky (used by Inky's AI)
     * @return chosen direction; Direction.NONE if no valid move exists
     */
    public abstract Direction calculateNextMove(Board board, Position pacmanPosition, Direction pacmanDirection, Position blinkyPosition);

    /**
     * Advances ghost movement for one tick, choosing the appropriate strategy
     * based on the current state (respawning, frightened, or normal AI).
     *
     * @param board game board
     * @param pacmanPosition Pac-Man's current board position
     * @param pacmanDirection Pac-Man's current direction
     * @param blinkyPosition Blinky's current board position
     */
    public void move(Board board, Position pacmanPosition, Direction pacmanDirection, Position blinkyPosition) {
        if (this.isMoving()) {
            return;
        }

        if (this.state == GhostState.RESPAWNING) {
            if (this.boardPosition().equals(this.homePosition)) {
                this.respawn();
                return;
            }
            Direction dir = this.bfsNextDirection(board, this.boardPosition(), this.homePosition);
            if (dir != Direction.NONE) {
                this.startMove(dir);
            }
            return;
        }

        if (this.state == GhostState.FRIGHTENED) {
            Direction dir = this.randomDirection(board);
            if (dir != Direction.NONE) {
                this.startMove(dir);
            }
            return;
        }

        Direction dir = this.calculateNextMove(board, pacmanPosition, pacmanDirection, blinkyPosition);
        if (dir != Direction.NONE) {
            this.startMove(dir);
        }
    }

    private Direction randomDirection(Board board) {
        Direction current = this.getDirection();
        Direction opposite = current.opposite();
        List<Direction> options = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE || dir == opposite) {
                continue;
            }
            if (this.canMove(dir, board)) {
                options.add(dir);
            }
        }
        if (options.isEmpty()) {
            return opposite;
        }
        return options.get(RANDOM.nextInt(options.size()));
    }

    /**
     * Switches this ghost to the FRIGHTENED state unless it is currently respawning.
     * Resets the frightened timer to its full duration.
     */
    public void setFrightened() {
        if (this.state != GhostState.RESPAWNING) {
            this.state = GhostState.FRIGHTENED;
            this.frightenedTimer = FRIGHTENED_TICKS;
        }
    }

    /**
     * Called when this ghost reaches its home position after being eaten.
     * Transitions back to CHASE or SCATTER depending on the current phase.
     */
    public void respawn() {
        this.state = this.inScatterPhase ? GhostState.SCATTER : GhostState.CHASE;
        this.frameIndex = 0;
    }

    /**
     * Called when Pac-Man eats this ghost while in power mode.
     * Transitions the ghost to the RESPAWNING state so it navigates home.
     */
    public void onCaught() {
        this.state = GhostState.RESPAWNING;
        this.setDirection(Direction.RIGHT);
        this.frameIndex = 0;
    }

    @Override
    public void resetToSpawn() {
        super.resetToSpawn();
        this.state = GhostState.CHASE;
        this.frightenedTimer = 0;
        this.modeTimer = CHASE_TICKS;
        this.inScatterPhase = false;
        this.frameIndex = 0;
    }

    @Override
    public void update() {
        this.tickMovement();

        int maxFrames = NORMAL_FRAMES;

        if (this.state == GhostState.FRIGHTENED && this.frightenedTimer < FRIGHTENED_FLASH_TICKS) {
            maxFrames += FRIGHTENED_FRAMES;
        }

        this.frameIndex = (this.frameIndex + 1) % maxFrames;

        if (this.state == GhostState.FRIGHTENED) {
            this.frightenedTimer--;
            if (this.frightenedTimer <= 0) {
                this.state = this.inScatterPhase ? GhostState.SCATTER : GhostState.CHASE;
                this.frameIndex = 0;
            }
            return;
        }

        if (this.state == GhostState.RESPAWNING) {
            return;
        }

        this.modeTimer--;
        if (this.modeTimer <= 0) {
            if (this.inScatterPhase) {
                this.inScatterPhase = false;
                this.state = GhostState.CHASE;
                this.modeTimer = CHASE_TICKS;
            } else {
                this.inScatterPhase = true;
                this.state = GhostState.SCATTER;
                this.modeTimer = SCATTER_TICKS;
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

    /**
     * Returns the sprite resource directory for this ghost type.
     * Used by {@link #render()} to resolve frame image paths.
     *
     * @return path to the ghost's sprite folder (e.g. {@code "resources/ghosts/blinky"})
     */
    public abstract String getSpriteDir();

    /**
     * Runs BFS on the board graph from {@code from} toward {@code to} and returns
     * the first direction to take along the shortest path.
     *
     * @param board game board supplying the adjacency graph
     * @param from  starting board position
     * @param to    target board position
     * @return first direction along the shortest path, or {@link Direction#NONE} if unreachable
     */
    public Direction bfsNextDirection(Board board, Position from, Position to) {
        int cols = BOARD_COLS;
        int total = cols * BOARD_ROWS;

        int startIdx = GraphBuilder.toIndex(from.getY(), from.getX(), cols);
        int goalIdx  = GraphBuilder.toIndex(to.getY(),   to.getX(),   cols);

        if (startIdx == goalIdx) {
            return Direction.NONE;
        }

        int[][] graph = board.getGraph();
        int[] parent = new int[total];
        Arrays.fill(parent, -1);
        parent[startIdx] = startIdx;

        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(startIdx);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == goalIdx) {
                break;
            }
            for (int neighbor : graph[current]) {
                if (parent[neighbor] == -1) {
                    parent[neighbor] = current;
                    queue.add(neighbor);
                }
            }
        }

        if (parent[goalIdx] == -1) {
            return Direction.NONE;
        }

        // Walk back from goal to find the first step after start
        int step = goalIdx;
        while (parent[step] != startIdx) {
            step = parent[step];
        }

        int dCol = GraphBuilder.toCol(step, cols) - from.getX();
        int dRow = GraphBuilder.toRow(step, cols) - from.getY();

        if (dCol > 1) {
            dCol = -1;
        }
        if (dCol < -1) {
            dCol = 1;
        }

        for (Direction dir : Direction.values()) {
            if (dir.dx() == dCol && dir.dy() == dRow) {
                return dir;
            }
        }

        return Direction.NONE;
    }

    /**
     * Computes the Manhattan distance between two board positions.
     *
     * @param a first position
     * @param b second position
     * @return sum of absolute column and row differences
     */
    protected int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Clamps a target coordinate to the valid board bounds.
     * Used by ghost AI to prevent targeting out-of-bounds positions.
     *
     * @param col desired column, possibly out of range
     * @param row desired row, possibly out of range
     * @return nearest valid board position
     */
    protected Position clampToBoard(int col, int row) {
        col = Math.max(0, Math.min(BOARD_COLS - 1, col));
        row = Math.max(0, Math.min(BOARD_ROWS - 1, row));
        return new Position(col, row);
    }

    /**
     * Returns the current state of this ghost.
     *
     * @return current {@link GhostState}
     */
    public GhostState getState() {
        return this.state;
    }

    /**
     * Returns whether this ghost is currently in the {@code FRIGHTENED} state.
     *
     * @return {@code true} if frightened and edible by Pac-Man
     */
    public boolean isFrightened() {
        return this.state == GhostState.FRIGHTENED;
    }
}
