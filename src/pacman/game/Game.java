package pacman.game;

import fri.shapesge.FontStyle;
import fri.shapesge.Image;
import fri.shapesge.Manager;
import fri.shapesge.TextBlock;
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
import pacman.util.Position;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final PacMan pacMan;
    private final List<Ghost> ghosts;
    private GameState gameState;
    private Image bg;
    private int resetTimer;
    private static final int RESET_PAUSE_TICKS = 30;

    private final Overlay overlay;

    // stats
    private final TextBlock lives;
    private final TextBlock score;

    public Game() {
//        this.bg = new Image("resources/map.png");
//        this.bg.changePosition(0, 40);
//        this.bg.makeVisible();

        this.board =  new Board();

        this.pacMan = new PacMan(1, 1, Direction.DOWN);
        this.pacMan.setDirection(Direction.DOWN);

        this.ghosts = new ArrayList<>();
        this.ghosts.add(new BlinkyGhost(11, 13, 14, 14, Direction.RIGHT));
        this.ghosts.add(new PinkyGhost(11, 15, 14, 14, Direction.RIGHT));
        this.ghosts.add(new ClydeGhost(16, 13, 14, 14, Direction.RIGHT));
        this.ghosts.add(new InkyGhost(16, 15, 14, 14, Direction.LEFT));

        this.lives = new TextBlock("", 10, 25);
        this.lives.changeFont("Arial", FontStyle.BOLD, 20);
        this.lives.changeColor("blue");
        this.lives.makeVisible();

        this.score = new TextBlock("", 100, 25);
        this.score.changeFont("Arial", FontStyle.BOLD, 20);
        this.score.changeColor("blue");
        this.score.makeVisible();
        this.updateStats();

        this.overlay = new Overlay();
        this.gameState = GameState.RUNNING;

        // this needs to be last or manager starts sending tick messages to uninitialized game
        // and bad things will happen
        Manager manager = new Manager();
        manager.manageObject(this);
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
        System.exit(0);
    }

    public void reset() {
        // todo: need to get original board info
        this.resetRound();
    }

    public void pressedP() {
        if (this.gameState == GameState.RUNNING) {
            this.gameState = GameState.PAUSED;
            this.overlay.showPaused();
            return;
        }

        if (this.gameState == GameState.PAUSED) {
            this.gameState = GameState.RUNNING;
            this.overlay.hide();
        }
    }

    public void tick() {
        switch (this.gameState) {
            case WON, GAME_OVER -> {
                this.overlay.showGameOver(this.pacMan.getScoreManager().getScore());
            }
            case RUNNING -> {
                this.pacMan.update();
                if (!this.pacMan.isMoving()) {
                    this.pacMan.move(this.board);
                    this.updateStats();
                    if (this.board.isCleared()) {
                        this.gameState = GameState.WON;
                        this.overlay.showWin(this.pacMan.getScoreManager().getScore());
                    }
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
                    ghost.update();
                    ghost.move(
                        this.board,
                        this.pacMan.boardPosition(),
                        this.pacMan.getDirection(),
                        this.ghosts.get(0).boardPosition()
                    );
                    ghost.render();
                }
            }
            case PAUSED -> {

            }
            case RESETTING -> {
                this.resetTimer--;
                if (this.resetTimer <= 0) {
                    this.gameState = GameState.RUNNING;
                    this.pacMan.activateInvincibility();
                }
                this.pacMan.render();
                for (Ghost ghost : this.ghosts) {
                    ghost.render();
                }
            }
        }
    }

    private void checkCollisions() {
        if (this.pacMan.isInvincible()) {
            return;
        }

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
                    this.overlay.showGameOver(this.pacMan.getScoreManager().getScore());
                } else {
                    this.resetRound();
                }
                return;
            }
        }
    }

    // todo: inaccurate needs sprite collision
    private boolean isTileCollision(PacMan pac, Ghost ghost) {
        Position pp = pac.boardPosition();
        Position gp = ghost.boardPosition();
        return pp.equals(gp);
    }

    private void resetRound() {
        this.updateStats();
        this.pacMan.resetToSpawn();
        for (Ghost ghost : this.ghosts) {
            ghost.resetToSpawn();
        }
        this.resetTimer = RESET_PAUSE_TICKS;
        this.gameState = GameState.RESETTING;
    }

    private void restartGame() {
        this.overlay.hide();
        this.pacMan.resetToSpawn();
        this.pacMan.getScoreManager().reset();
        for (Ghost ghost : this.ghosts) {
            ghost.resetToSpawn();
        }
        this.gameState = GameState.RUNNING;
        this.updateStats();
    }

    private void updateStats() {
        this.lives.changeText(String.format("Lives: %d", this.pacMan.getScoreManager().getLives()));
        this.score.changeText(String.format("Score: %d", this.pacMan.getScoreManager().getScore()));
    }
}
