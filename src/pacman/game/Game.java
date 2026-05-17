package pacman.game;

import fri.shapesge.FontStyle;
import fri.shapesge.Manager;
import fri.shapesge.TextBlock;
import pacman.board.Board;
import pacman.entity.PacMan;
import pacman.entity.ghost.BlinkyGhost;
import pacman.entity.ghost.ClydeGhost;
import pacman.entity.ghost.Ghost;
import pacman.entity.ghost.InkyGhost;
import pacman.entity.ghost.PinkyGhost;
import pacman.entity.Entity;
import pacman.util.Direction;
import pacman.util.GameState;
import pacman.util.GhostState;
import pacman.util.Sound;
import pacman.util.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final PacMan pacMan;
    private final List<Ghost> ghosts;
    private GameState gameState;
    private int resetTimer;
    private static final int RESET_PAUSE_TICKS = 30;

    private final Overlay overlay;

    // stats
    private final TextBlock lives;
    private final TextBlock score;

    public Game() {
        this.board =  new Board();

        this.pacMan = new PacMan(14, 23, Direction.RIGHT);

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
        this.board.reset();
        this.resetEntitySprites(); // the entity sprites z-index would be behind boards elsewise
        this.pacMan.getScoreManager().reset();
        this.overlay.hide();
        this.resetRound();
    }

    private void resetEntitySprites() {
        this.pacMan.hide();
        this.pacMan.show();

        for (Ghost ghost : this.ghosts) {
            ghost.hide();
            ghost.show();
        }
    }

    public void pressedP() {
        if (this.gameState == GameState.RUNNING) {
            this.gameState = GameState.PAUSED;
            this.overlay.showPaused();
            SoundManager.stopLoop();
            SoundManager.playLoop(Sound.MENU);
            return;
        }

        if (this.gameState == GameState.PAUSED) {
            this.gameState = GameState.RUNNING;
            SoundManager.stopLoop();
            this.overlay.hide();
        }
    }

    public void tick() {
        switch (this.gameState) {
            case RUNNING -> {
                this.pacMan.update();
                if (!this.pacMan.isMoving()) {
                    this.pacMan.move(this.board);
                    this.updateStats();

                    if (this.board.isCleared()) {
                        this.gameState = GameState.WON;
                        this.overlay.showWin(this.pacMan.getScoreManager().getScore());
                        SoundManager.stopLoop();
                        SoundManager.playLoop(Sound.MENU);
                        return;
                    }
                }

                if (this.pacMan.getScoreManager().pollDotConsumed()) {
                    SoundManager.playOnce(Sound.EATING_DOT);
                }

                if (this.pacMan.getScoreManager().pollPowerPelletConsumed()) {
                    SoundManager.playOnce(Sound.EATING_POWER_PELLET);
                    this.pacMan.activatePowerMode();
                    for (Ghost ghost : this.ghosts) {
                        ghost.setFrightened();
                    }
                }

                this.updateLoopSound();
                SoundManager.tick();

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

                // this must be last because it can be game ending and sound effects will get messed up otherwise
                this.checkCollisions();
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
            if (!this.isCollision(this.pacMan, ghost)) {
                continue;
            }

            if (ghost.isFrightened()) {
                ghost.onCaught();
                this.pacMan.getScoreManager().addGhostEatenPoints();
                SoundManager.playOnce(Sound.EATING_GHOST);
            } else if (ghost.getState() != GhostState.RESPAWNING) {
                SoundManager.playOnce(Sound.FAIL);
                this.pacMan.getScoreManager().loseLife();
                if (this.pacMan.getScoreManager().isGameOver()) {
                    this.gameState = GameState.GAME_OVER;
                    this.overlay.showGameOver(this.pacMan.getScoreManager().getScore());
                    SoundManager.stopLoop();
                    SoundManager.playLoop(Sound.MENU);
                } else {
                    this.resetRound();
                }
                return;
            }
        }
    }

    private boolean isCollision(PacMan pac, Ghost ghost) {
        final int tolerance = 6;
        int px = pac.windowPosition().getX() + tolerance;
        int py = pac.windowPosition().getY() + tolerance;
        int ps = Entity.SIZE - tolerance * 2;
        int gx = ghost.windowPosition().getX() + tolerance;
        int gy = ghost.windowPosition().getY() + tolerance;
        int gs = Entity.SIZE - tolerance * 2;
        return px < gx + gs && px + ps > gx && py < gy + gs && py + ps > gy;
    }

    private void updateLoopSound() {
        boolean anyRespawning = false;
        for (Ghost ghost : this.ghosts) {
            if (ghost.getState() == GhostState.RESPAWNING) {
                anyRespawning = true;
                break;
            }
        }

        Sound soundToPlay;
        if (anyRespawning) {
            soundToPlay = Sound.GHOST_RETURN_TO_HOME;
        } else {
            soundToPlay = Sound.GHOST_MOVE;
        }
        SoundManager.playLoop(soundToPlay);
    }

    private void resetRound() {
        SoundManager.playLoop(Sound.GHOST_MOVE);
        this.updateStats();
        this.pacMan.resetToSpawn();
        for (Ghost ghost : this.ghosts) {
            ghost.resetToSpawn();
        }
        this.resetTimer = RESET_PAUSE_TICKS;
        this.gameState = GameState.RESETTING;
    }

    private void updateStats() {
        this.lives.changeText(String.format("Lives: %d", this.pacMan.getScoreManager().getLives()));
        this.score.changeText(String.format("Score: %d", this.pacMan.getScoreManager().getScore()));
    }
}
