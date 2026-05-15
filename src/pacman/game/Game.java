package pacman.game;

import fri.shapesge.Image;
import fri.shapesge.Manager;
import pacman.board.Board;
import pacman.entity.PacMan;
import pacman.entity.ghost.BlinkyGhost;
import pacman.entity.ghost.ClydeGhost;
import pacman.entity.ghost.Ghost;
import pacman.entity.ghost.InkyGhost;
import pacman.entity.ghost.PinkyGhost;
import pacman.util.Direction;
import pacman.util.GameState;
import pacman.util.GhostState;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final PacMan pacMan;
    private final List<Ghost> ghosts;
    private GameState gameState;
    private Image bg;

    public Game() {
        Manager manager = new Manager();
        manager.manageObject(this);

//        this.bg = new Image("resources/map.png");
//        this.bg.changePosition(0, 40);
//        this.bg.makeVisible();

        this.board =  new Board();
        this.pacMan = new PacMan(1, 1);
        this.ghosts = new ArrayList<>();

        // TESTING ------------------
        this.ghosts.add(new BlinkyGhost(3, 1, 7, 14, Direction.RIGHT));
        this.ghosts.add(new PinkyGhost(5, 1, 7, 14, Direction.DOWN));
        this.ghosts.add(new ClydeGhost(7, 1, 7, 14, Direction.UP));
        this.ghosts.add(new InkyGhost(9, 1, 7, 14, Direction.LEFT));

        this.ghosts.add(new BlinkyGhost(11, 1, 7, 14, Direction.LEFT));
        this.ghosts.get(4).setFrightened();

        this.pacMan.setDirection(Direction.DOWN);
        // --------------------------

        this.gameState = GameState.RUNNING;
    }

    public void up() {
        if (this.gameState == GameState.RUNNING) {
            this.pacMan.setPendingDirection(Direction.UP);
        }
    }

    public void down() {
        if (this.gameState == GameState.RUNNING) {
            this.pacMan.setPendingDirection(Direction.DOWN);
        }
    }

    public void left() {
        if (this.gameState == GameState.RUNNING) {
            this.pacMan.setPendingDirection(Direction.LEFT);
        }
    }

    public void right() {
        if (this.gameState == GameState.RUNNING) {
            this.pacMan.setPendingDirection(Direction.RIGHT);
        }
    }

    public void escape() {
        System.out.println("escape event triggered");
    }

    public void tick() {
        if (this.gameState == GameState.RUNNING) {
            this.pacMan.update();
            if (!this.pacMan.isMoving()) {
                this.pacMan.move(this.board);
                System.out.println(this.pacMan.boardPosition().toString());
            }

            if (this.pacMan.getScoreManager().pollPowerPelletConsumed()) {
                this.pacMan.activatePowerMode();
                for (Ghost ghost : this.ghosts) {
                    ghost.setFrightened();
                }
            }

            this.checkCollisions();

            this.pacMan.render();

            for (Ghost ghost : this.ghosts) {
                ghost.render();
                ghost.update();
            }
        }

    }

    private void checkCollisions() {
        for (Ghost ghost : this.ghosts) {
            if (!this.isTileCollision(this.pacMan, ghost)) {
                continue;
            }

            if (ghost.isFrightened()) {
                ghost.onCaught();
                this.pacMan.getScoreManager().addGhostEatenPoints();
            } else if (ghost.getState() != GhostState.RESPAWNING) {
                this.pacMan.getScoreManager().loseLife();
                if (this.pacMan.getScoreManager().isGameOver()) {
                    this.gameState = GameState.GAME_OVER;
                    System.out.println("GAME OVER — score: " + this.pacMan.getScoreManager().getScore());
                } else {
                    this.resetRound();
                }
            }
        }
    }

    private boolean isTileCollision(PacMan pac, Ghost ghost) {
        pacman.util.Position pp = pac.boardPosition();
        pacman.util.Position gp = ghost.boardPosition();
        return pp.equals(gp);
    }

    private void resetRound() {
        // TODO: restore spawn positions and animation state
        System.out.println("Life lost — lives remaining: " + this.pacMan.getScoreManager().getLives());
    }
}
