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

public abstract class Ghost extends Entity {
    private static final int FRIGHTENED_TICKS = 200;
    private static final int SCATTER_TICKS = 420;   // ~7 s at 60 tps
    private static final int CHASE_TICKS = 1200;  // ~20 s at 60 tps
    public static final int NORMAL_FRAMES = 2;
    public static final int FRIGHTENED_FRAMES = 4;

    private static final Random RANDOM = new Random();

    private GhostState state;
    private final Position homePosition;
    private int frightenedTimer;
    private int modeTimer;
    private boolean inScatterPhase;
    private int frameIndex;

    public Ghost(int startCol, int startRow, int respawnCol, int respawnRow, Direction direction) {
        super(startCol, startRow, direction);
        this.homePosition = new  Position(respawnCol, respawnRow);
        this.state = GhostState.CHASE;
        this.modeTimer = CHASE_TICKS;
        this.inScatterPhase = false;
        this.frameIndex = 0;
    }

    public abstract Direction calculateNextMove(Board board, Position pacmanPosition, Direction pacmanDirection, Position blinkyPosition);

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

    public void setFrightened() {
        if (this.state != GhostState.RESPAWNING) {
            this.state = GhostState.FRIGHTENED;
            this.frightenedTimer = FRIGHTENED_TICKS;
        }
    }

    public void respawn() {
        this.state = this.inScatterPhase ? GhostState.SCATTER : GhostState.CHASE;
        this.frameIndex = 0;
    }

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

        int maxFrames = (this.state == GhostState.FRIGHTENED) ? FRIGHTENED_FRAMES : NORMAL_FRAMES;
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

    public abstract String getSpriteDir();

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

    protected int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    protected Position clampToBoard(int col, int row) {
        col = Math.max(0, Math.min(BOARD_COLS - 1, col));
        row = Math.max(0, Math.min(BOARD_ROWS - 1, row));
        return new Position(col, row);
    }

    public GhostState getState() {
        return this.state;
    }

    public boolean isFrightened() {
        return this.state == GhostState.FRIGHTENED;
    }
}
