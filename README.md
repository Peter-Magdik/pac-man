# Pac-Man

A faithful Java implementation of the classic arcade game Pac-Man, built as a semester project demonstrating object-oriented design with polymorphism across 10+ classes.

## Description

The player controls Pac-Man through a 28×31 maze, eating dots and power pellets while avoiding four ghosts — each with its own distinct AI. Eating a power pellet temporarily frightens all ghosts, making them edible. Clear the board of all dots to win.

## Requirements

- Java 17 or 21 LTS
- IntelliJ IDEA (recommended; project uses IntelliJ's native build system)

## Running the Project

1. Clone the repository
   ```bash
   git clone https://github.com/PeterSK-bit/pac-man.git
   ```
2. Open the project in IntelliJ IDEA via **File > Open** and select the project folder
3. Let IntelliJ index the project
4. Run `Main.java` via the green play button or `Shift + F10`

Alternatively, run the pre-built JAR directly:
```bash
java -jar Pac-man.jar
```

## Controls

| Key | Action |
|-----|--------|
| `↑` / `W` | Move up |
| `↓` / `S` | Move down |
| `←` / `A` | Move left |
| `→` / `D` | Move right |
| `P` | Pause / Unpause |
| `Escape` | Quit |

## Scoring

| Event | Points |
|-------|--------|
| Eat a dot | 1 |
| Eat a power pellet | 10 |
| Eat a frightened ghost | 200 |

The player starts with **3 lives**. Losing all lives ends the game.

## Ghost AI

Each ghost has a unique behaviour that switches between **Chase** and **Scatter** phases on a timer, and enters **Frightened** mode when Pac-Man eats a power pellet.

| Ghost | Colour | Chase | Scatter corner |
|-------|--------|-------|----------------|
| Blinky | Red | Directly pursues Pac-Man via BFS | Top-right |
| Pinky | Pink | Targets 4 tiles ahead of Pac-Man | Top-left |
| Inky | Cyan | Targets a point mirrored from Blinky through a spot 2 tiles ahead of Pac-Man | Bottom-right |
| Clyde | Orange | Chases Pac-Man when far; retreats to scatter corner when close | Bottom-left |

When eaten, a ghost enters **Respawning** mode and navigates home via BFS before rejoining the game.

## Project Structure

```
src/
└── pacman/
    ├── Main.java
    ├── board/
    │   ├── Board.java            # 28×31 cell grid, dot tracking, walkability
    │   ├── GraphBuilder.java     # Builds adjacency graph for BFS pathfinding
    │   └── cell/
    │       ├── Cell.java         # Abstract base — onEnter(), isWalkable(), draw()
    │       ├── WallCell.java
    │       ├── DotCell.java
    │       ├── PowerPelletCell.java
    │       └── EmptyCell.java
    ├── entity/
    │   ├── Entity.java           # Abstract base — position, movement, rendering
    │   ├── PacMan.java
    │   └── ghost/
    │       ├── Ghost.java        # Abstract ghost — BFS, frightened/scatter logic
    │       ├── BlinkyGhost.java
    │       ├── PinkyGhost.java
    │       ├── InkyGhost.java
    │       └── ClydeGhost.java
    ├── game/
    │   ├── Game.java             # Main game loop, input handling, collision detection
    │   └── Overlay.java          # Pause / Game Over / Win screen overlays
    └── util/
        ├── Direction.java
        ├── GameState.java
        ├── GhostState.java       # CHASE, SCATTER, FRIGHTENED, RESPAWNING, ...
        ├── Position.java
        ├── ScoreManager.java     # Score, lives, poll flags for dot/pellet events
        ├── Sound.java
        └── SoundManager.java

resources/
├── ghosts/           # Directional sprite frames per ghost + frightened/respawning
├── pacman/           # Pac-Man animation frames
└── soundEffects/     # WAV files for movement, eating, death, menu

libs/
└── shapesGE-2.1.0.jar   # School-provided rendering library (ShapesGE)
```

## Polymorphism

The project demonstrates polymorphism in two key places:

**Cell hierarchy** — `Board` calls `cell.onEnter(scoreManager, board)` without knowing the concrete type. `WallCell` throws an error, `DotCell` adds a point and decrements the dot counter, `PowerPelletCell` adds ten points and sets a flag, and `EmptyCell` does nothing.

**Ghost hierarchy** — `Game` calls `ghost.move(...)` on each `Ghost` reference. The abstract `calculateNextMove()` is overridden by each subclass to implement a different targeting strategy.

## Dependencies

- [ShapesGE 2.1.0](libs/shapesGE-2.1.0.jar) — school-provided library for window management, sprite rendering, keyboard input, and the game timer

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
