package pacman.board;

import pacman.board.cell.Cell;
import pacman.board.cell.WallCell;
import pacman.board.cell.DotCell;
import pacman.board.cell.PowerPelletCell;
import pacman.board.cell.EmptyCell;

/**
 * Represents the game board as a 2D grid of Cell instances.
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Initializing and owning the full 28×31 cell grid</li>
 *     <li>Providing walkability queries used by entity movement</li>
 *     <li>Tracking remaining dots to detect board clear</li>
 *     <li>Exposing the adjacency graph built by GraphBuilder</li>
 * </ul>
 */
public class Board {
    private Cell[][] grid;
    private int remainingDots;
    private int[][] graph;

    /**
     * Creates the board and initializes the cell grid.
     */
    public Board() {
        this.initGrid();
    }

    /**
     * Hides all current cells, then rebuilds the grid and adjacency graph from scratch.
     * Call this when starting a new game while keeping the same Board instance.
     */
    public void reset() {
        for (Cell[] row : this.grid) {
            for (Cell cell : row) {
                cell.hide();
            }
        }
        this.initGrid();
    }

    private void initGrid() {
        this.grid = new Cell[][]{
                {this.wall(0, 0), this.wall(1, 0), this.wall(2, 0), this.wall(3, 0), this.wall(4, 0), this.wall(5, 0), this.wall(6, 0), this.wall(7, 0), this.wall(8, 0), this.wall(9, 0), this.wall(10, 0), this.wall(11, 0), this.wall(12, 0), this.wall(13, 0), this.wall(14, 0), this.wall(15, 0), this.wall(16, 0), this.wall(17, 0), this.wall(18, 0), this.wall(19, 0), this.wall(20, 0), this.wall(21, 0), this.wall(22, 0), this.wall(23, 0), this.wall(24, 0), this.wall(25, 0), this.wall(26, 0), this.wall(27, 0)},
                {this.wall(0, 1), this.pellet(1, 1), this.dot(2, 1), this.dot(3, 1), this.dot(4, 1), this.dot(5, 1), this.dot(6, 1), this.dot(7, 1), this.dot(8, 1), this.dot(9, 1), this.dot(10, 1), this.dot(11, 1), this.dot(12, 1), this.wall(13, 1), this.wall(14, 1), this.dot(15, 1), this.dot(16, 1), this.dot(17, 1), this.dot(18, 1), this.dot(19, 1), this.dot(20, 1), this.dot(21, 1), this.dot(22, 1), this.dot(23, 1), this.dot(24, 1), this.dot(25, 1), this.pellet(26, 1), this.wall(27, 1)},
                {this.wall(0, 2), this.dot(1, 2), this.wall(2, 2), this.wall(3, 2), this.wall(4, 2), this.wall(5, 2), this.dot(6, 2), this.wall(7, 2), this.wall(8, 2), this.wall(9, 2), this.wall(10, 2), this.wall(11, 2), this.dot(12, 2), this.wall(13, 2), this.wall(14, 2), this.dot(15, 2), this.wall(16, 2), this.wall(17, 2), this.wall(18, 2), this.wall(19, 2), this.wall(20, 2), this.dot(21, 2), this.wall(22, 2), this.wall(23, 2), this.wall(24, 2), this.wall(25, 2), this.dot(26, 2), this.wall(27, 2)},
                {this.wall(0, 3), this.dot(1, 3), this.wall(2, 3), this.empty(3, 3), this.empty(4, 3), this.wall(5, 3), this.dot(6, 3), this.wall(7, 3), this.empty(8, 3), this.empty(9, 3), this.empty(10, 3), this.wall(11, 3), this.dot(12, 3), this.wall(13, 3), this.wall(14, 3), this.dot(15, 3), this.wall(16, 3), this.empty(17, 3), this.empty(18, 3), this.empty(19, 3), this.wall(20, 3), this.dot(21, 3), this.wall(22, 3), this.empty(23, 3), this.empty(24, 3), this.wall(25, 3), this.dot(26, 3), this.wall(27, 3)},
                {this.wall(0, 4), this.dot(1, 4), this.wall(2, 4), this.wall(3, 4), this.wall(4, 4), this.wall(5, 4), this.dot(6, 4), this.wall(7, 4), this.wall(8, 4), this.wall(9, 4), this.wall(10, 4), this.wall(11, 4), this.dot(12, 4), this.wall(13, 4), this.wall(14, 4), this.dot(15, 4), this.wall(16, 4), this.wall(17, 4), this.wall(18, 4), this.wall(19, 4), this.wall(20, 4), this.dot(21, 4), this.wall(22, 4), this.wall(23, 4), this.wall(24, 4), this.wall(25, 4), this.dot(26, 4), this.wall(27, 4)},
                {this.wall(0, 5), this.dot(1, 5), this.dot(2, 5), this.dot(3, 5), this.dot(4, 5), this.dot(5, 5), this.dot(6, 5), this.dot(7, 5), this.dot(8, 5), this.dot(9, 5), this.dot(10, 5), this.dot(11, 5), this.dot(12, 5), this.dot(13, 5), this.dot(14, 5), this.dot(15, 5), this.dot(16, 5), this.dot(17, 5), this.dot(18, 5), this.dot(19, 5), this.dot(20, 5), this.dot(21, 5), this.dot(22, 5), this.dot(23, 5), this.dot(24, 5), this.dot(25, 5), this.dot(26, 5), this.wall(27, 5)},
                {this.wall(0, 6), this.dot(1, 6), this.wall(2, 6), this.wall(3, 6), this.wall(4, 6), this.wall(5, 6), this.dot(6, 6), this.wall(7, 6), this.wall(8, 6), this.dot(9, 6), this.wall(10, 6), this.wall(11, 6), this.wall(12, 6), this.wall(13, 6), this.wall(14, 6), this.wall(15, 6), this.wall(16, 6), this.wall(17, 6), this.dot(18, 6), this.wall(19, 6), this.wall(20, 6), this.dot(21, 6), this.wall(22, 6), this.wall(23, 6), this.wall(24, 6), this.wall(25, 6), this.dot(26, 6), this.wall(27, 6)},
                {this.wall(0, 7), this.dot(1, 7), this.wall(2, 7), this.wall(3, 7), this.wall(4, 7), this.wall(5, 7), this.dot(6, 7), this.wall(7, 7), this.wall(8, 7), this.dot(9, 7), this.wall(10, 7), this.wall(11, 7), this.wall(12, 7), this.wall(13, 7), this.wall(14, 7), this.wall(15, 7), this.wall(16, 7), this.wall(17, 7), this.dot(18, 7), this.wall(19, 7), this.wall(20, 7), this.dot(21, 7), this.wall(22, 7), this.wall(23, 7), this.wall(24, 7), this.wall(25, 7), this.dot(26, 7), this.wall(27, 7)},
                {this.wall(0, 8), this.dot(1, 8), this.dot(2, 8), this.dot(3, 8), this.dot(4, 8), this.dot(5, 8), this.dot(6, 8), this.wall(7, 8), this.wall(8, 8), this.dot(9, 8), this.dot(10, 8), this.dot(11, 8), this.dot(12, 8), this.wall(13, 8), this.wall(14, 8), this.dot(15, 8), this.dot(16, 8), this.dot(17, 8), this.dot(18, 8), this.wall(19, 8), this.wall(20, 8), this.dot(21, 8), this.dot(22, 8), this.dot(23, 8), this.dot(24, 8), this.dot(25, 8), this.dot(26, 8), this.wall(27, 8)},
                {this.wall(0, 9), this.wall(1, 9), this.wall(2, 9), this.wall(3, 9), this.wall(4, 9), this.wall(5, 9), this.dot(6, 9), this.wall(7, 9), this.wall(8, 9), this.wall(9, 9), this.wall(10, 9), this.wall(11, 9), this.dot(12, 9), this.wall(13, 9), this.wall(14, 9), this.dot(15, 9), this.wall(16, 9), this.wall(17, 9), this.wall(18, 9), this.wall(19, 9), this.wall(20, 9), this.dot(21, 9), this.wall(22, 9), this.wall(23, 9), this.wall(24, 9), this.wall(25, 9), this.wall(26, 9), this.wall(27, 9)},
                {this.empty (0, 10), this.empty(1, 10), this.empty(2, 10), this.empty(3, 10), this.empty(4, 10), this.wall(5, 10), this.dot(6, 10), this.wall(7, 10), this.wall(8, 10), this.wall(9, 10), this.wall(10, 10), this.wall(11, 10), this.dot(12, 10), this.wall(13, 10), this.wall(14, 10), this.dot(15, 10), this.wall(16, 10), this.wall(17, 10), this.wall(18, 10), this.wall(19, 10), this.wall(20, 10), this.dot(21, 10), this.wall(22, 10), this.empty(23, 10), this.empty(24, 10), this.empty(25, 10), this.empty(26, 10), this.empty(27, 10)},
                {this.empty (0, 11), this.empty(1, 11), this.empty(2, 11), this.empty(3, 11), this.empty(4, 11), this.wall(5, 11), this.dot(6, 11), this.wall(7, 11), this.wall(8, 11), this.dot(9, 11), this.dot(10, 11), this.dot(11, 11), this.dot(12, 11), this.dot(13, 11), this.dot(14, 11), this.dot(15, 11), this.dot(16, 11), this.dot(17, 11), this.dot(18, 11), this.wall(19, 11), this.wall(20, 11), this.dot(21, 11), this.wall(22, 11), this.empty(23, 11), this.empty(24, 11), this.empty(25, 11), this.empty(26, 11), this.empty(27, 11)},
                {this.empty(0, 12), this.empty(1, 12), this.empty(2, 12), this.empty(3, 12), this.empty(4, 12), this.wall(5, 12), this.dot(6, 12), this.wall(7, 12), this.wall(8, 12), this.dot(9, 12), this.wall(10, 12), this.wall(11, 12), this.wall(12, 12), this.empty(13, 12), this.empty(14, 12), this.wall(15, 12), this.wall(16, 12), this.wall(17, 12), this.dot(18, 12), this.wall(19, 12), this.wall(20, 12), this.dot(21, 12), this.wall(22, 12), this.empty(23, 12), this.empty(24, 12), this.empty(25, 12), this.empty(26, 12), this.empty(27, 12)},
                {this.wall(0, 13), this.wall(1, 13), this.wall(2, 13), this.wall(3, 13), this.wall(4, 13), this.wall(5, 13), this.dot(6, 13), this.wall(7, 13), this.wall(8, 13), this.dot(9, 13), this.wall(10, 13), this.empty(11, 13), this.empty(12, 13), this.empty(13, 13), this.empty(14, 13), this.empty(15, 13), this.empty(16, 13), this.wall(17, 13), this.dot(18, 13), this.wall(19, 13), this.wall(20, 13), this.dot(21, 13), this.wall(22, 13), this.wall(23, 13), this.wall(24, 13), this.wall(25, 13), this.wall(26, 13), this.wall(27, 13)},
                {this.dot(0, 14), this.dot(1, 14), this.dot(2, 14), this.dot(3, 14), this.dot(4, 14), this.dot(5, 14), this.dot(6, 14), this.dot(7, 14), this.dot(8, 14), this.dot(9, 14), this.wall(10, 14), this.empty(11, 14), this.empty(12, 14), this.empty(13, 14), this.empty(14, 14), this.empty(15, 14), this.empty(16, 14), this.wall(17, 14), this.dot(18, 14), this.dot(19, 14), this.dot(20, 14), this.dot(21, 14), this.dot(22, 14), this.dot(23, 14), this.dot(24, 14), this.dot(25, 14), this.dot(26, 14), this.dot(27, 14)},
                {this.wall(0, 15), this.wall(1, 15), this.wall(2, 15), this.wall(3, 15), this.wall(4, 15), this.wall(5, 15), this.dot(6, 15), this.wall(7, 15), this.wall(8, 15), this.dot(9, 15), this.wall(10, 15), this.empty(11, 15), this.empty(12, 15), this.empty(13, 15), this.empty(14, 15), this.empty(15, 15), this.empty(16, 15), this.wall(17, 15), this.dot(18, 15), this.wall(19, 15), this.wall(20, 15), this.dot(21, 15), this.wall(22, 15), this.wall(23, 15), this.wall(24, 15), this.wall(25, 15), this.wall(26, 15), this.wall(27, 15)},
                {this.empty(0, 16), this.empty(1, 16), this.empty(2, 16), this.empty(3, 16), this.empty(4, 16), this.wall(5, 16), this.dot(6, 16), this.wall(7, 16), this.wall(8, 16), this.dot(9, 16), this.wall(10, 16), this.wall(11, 16), this.wall(12, 16), this.wall(13, 16), this.wall(14, 16), this.wall(15, 16), this.wall(16, 16), this.wall(17, 16), this.dot(18, 16), this.wall(19, 16), this.wall(20, 16), this.dot(21, 16), this.wall(22, 16), this.empty(23, 16), this.empty(24, 16), this.empty(25, 16), this.empty(26, 16), this.empty(27, 16)},
                {this.empty(0, 17), this.empty(1, 17), this.empty(2, 17), this.empty(3, 17), this.empty(4, 17), this.wall(5, 17), this.dot(6, 17), this.wall(7, 17), this.wall(8, 17), this.dot(9, 17), this.dot(10, 17), this.dot(11, 17), this.dot(12, 17), this.dot(13, 17), this.dot(14, 17), this.dot(15, 17), this.dot(16, 17), this.dot(17, 17), this.dot(18, 17), this.wall(19, 17), this.wall(20, 17), this.dot(21, 17), this.wall(22, 17), this.empty(23, 17), this.empty(24, 17), this.empty(25, 17), this.empty(26, 17), this.empty(27, 17)},
                {this.empty(0, 18), this.empty(1, 18), this.empty(2, 18), this.empty(3, 18), this.empty(4, 18), this.wall(5, 18), this.dot(6, 18), this.wall(7, 18), this.wall(8, 18), this.dot(9, 18), this.wall(10, 18), this.wall(11, 18), this.wall(12, 18), this.wall(13, 18), this.wall(14, 18), this.wall(15, 18), this.wall(16, 18), this.wall(17, 18), this.dot(18, 18), this.wall(19, 18), this.wall(20, 18), this.dot(21, 18), this.wall(22, 18), this.empty(23, 18), this.empty(24, 18), this.empty(25, 18), this.empty(26, 18), this.empty(27, 18)},
                {this.wall(0, 19), this.wall(1, 19), this.wall(2, 19), this.wall(3, 19), this.wall(4, 19), this.wall(5, 19), this.dot(6, 19), this.wall(7, 19), this.wall(8, 19), this.dot(9, 19), this.wall(10, 19), this.wall(11, 19), this.wall(12, 19), this.wall(13, 19), this.wall(14, 19), this.wall(15, 19), this.wall(16, 19), this.wall(17, 19), this.dot(18, 19), this.wall(19, 19), this.wall(20, 19), this.dot(21, 19), this.wall(22, 19), this.wall(23, 19), this.wall(24, 19), this.wall(25, 19), this.wall(26, 19), this.wall(27, 19)},
                {this.wall(0, 20), this.dot(1, 20), this.dot(2, 20), this.dot(3, 20), this.dot(4, 20), this.dot(5, 20), this.dot(6, 20), this.dot(7, 20), this.dot(8, 20), this.dot(9, 20), this.dot(10, 20), this.dot(11, 20), this.dot(12, 20), this.wall(13, 20), this.wall(14, 20), this.dot(15, 20), this.dot(16, 20), this.dot(17, 20), this.dot(18, 20), this.dot(19, 20), this.dot(20, 20), this.dot(21, 20), this.dot(22, 20), this.dot(23, 20), this.dot(24, 20), this.dot(25, 20), this.dot(26, 20), this.wall(27, 20)},
                {this.wall(0, 21), this.dot(1, 21), this.wall(2, 21), this.wall(3, 21), this.wall(4, 21), this.wall(5, 21), this.dot(6, 21), this.wall(7, 21), this.wall(8, 21), this.wall(9, 21), this.wall(10, 21), this.wall(11, 21), this.dot(12, 21), this.wall(13, 21), this.wall(14, 21), this.dot(15, 21), this.wall(16, 21), this.wall(17, 21), this.wall(18, 21), this.wall(19, 21), this.wall(20, 21), this.dot(21, 21), this.wall(22, 21), this.wall(23, 21), this.wall(24, 21), this.wall(25, 21), this.dot(26, 21), this.wall(27, 21)},
                {this.wall(0, 22), this.dot(1, 22), this.wall(2, 22), this.wall(3, 22), this.wall(4, 22), this.wall(5, 22), this.dot(6, 22), this.wall(7, 22), this.wall(8, 22), this.wall(9, 22), this.wall(10, 22), this.wall(11, 22), this.dot(12, 22), this.wall(13, 22), this.wall(14, 22), this.dot(15, 22), this.wall(16, 22), this.wall(17, 22), this.wall(18, 22), this.wall(19, 22), this.wall(20, 22), this.dot(21, 22), this.wall(22, 22), this.wall(23, 22), this.wall(24, 22), this.wall(25, 22), this.dot(26, 22), this.wall(27, 22)},
                {this.wall(0, 23), this.dot(1, 23), this.dot(2, 23), this.dot(3, 23), this.wall(4, 23), this.wall(5, 23), this.dot(6, 23), this.dot(7, 23), this.dot(8, 23), this.dot(9, 23), this.dot(10, 23), this.dot(11, 23), this.dot(12, 23), this.dot(13, 23), this.dot(14, 23), this.dot(15, 23), this.dot(16, 23), this.dot(17, 23), this.dot(18, 23), this.dot(19, 23), this.dot(20, 23), this.dot(21, 23), this.wall(22, 23), this.wall(23, 23), this.dot(24, 23), this.dot(25, 23), this.dot(26, 23), this.wall(27, 23)},
                {this.wall(0, 24), this.wall(1, 24), this.wall(2, 24), this.dot(3, 24), this.wall(4, 24), this.wall(5, 24), this.dot(6, 24), this.wall(7, 24), this.wall(8, 24), this.dot(9, 24), this.wall(10, 24), this.wall(11, 24), this.wall(12, 24), this.wall(13, 24), this.wall(14, 24), this.wall(15, 24), this.wall(16, 24), this.wall(17, 24), this.dot(18, 24), this.wall(19, 24), this.wall(20, 24), this.dot(21, 24), this.wall(22, 24), this.wall(23, 24), this.dot(24, 24), this.wall(25, 24), this.wall(26, 24), this.wall(27, 24)},
                {this.wall(0, 25), this.wall(1, 25), this.wall(2, 25), this.dot(3, 25), this.wall(4, 25), this.wall(5, 25), this.dot(6, 25), this.wall(7, 25), this.wall(8, 25), this.dot(9, 25), this.wall(10, 25), this.wall(11, 25), this.wall(12, 25), this.wall(13, 25), this.wall(14, 25), this.wall(15, 25), this.wall(16, 25), this.wall(17, 25), this.dot(18, 25), this.wall(19, 25), this.wall(20, 25), this.dot(21, 25), this.wall(22, 25), this.wall(23, 25), this.dot(24, 25), this.wall(25, 25), this.wall(26, 25), this.wall(27, 25)},
                {this.wall(0, 26), this.dot(1, 26), this.dot(2, 26), this.dot(3, 26), this.dot(4, 26), this.dot(5, 26), this.dot(6, 26), this.wall(7, 26), this.wall(8, 26), this.dot(9, 26), this.dot(10, 26), this.dot(11, 26), this.dot(12, 26), this.wall(13, 26), this.wall(14, 26), this.dot(15, 26), this.dot(16, 26), this.dot(17, 26), this.dot(18, 26), this.wall(19, 26), this.wall(20, 26), this.dot(21, 26), this.dot(22, 26), this.dot(23, 26), this.dot(24, 26), this.dot(25, 26), this.dot(26, 26), this.wall(27, 26)},
                {this.wall(0, 27), this.dot(1, 27), this.wall(2, 27), this.wall(3, 27), this.wall(4, 27), this.wall(5, 27), this.wall(6, 27), this.wall(7, 27), this.wall(8, 27), this.wall(9, 27), this.wall(10, 27), this.wall(11, 27), this.dot(12, 27), this.wall(13, 27), this.wall(14, 27), this.dot(15, 27), this.wall(16, 27), this.wall(17, 27), this.wall(18, 27), this.wall(19, 27), this.wall(20, 27), this.wall(21, 27), this.wall(22, 27), this.wall(23, 27), this.wall(24, 27), this.wall(25, 27), this.dot(26, 27), this.wall(27, 27)},
                {this.wall(0, 28), this.dot(1, 28), this.wall(2, 28), this.wall(3, 28), this.wall(4, 28), this.wall(5, 28), this.wall(6, 28), this.wall(7, 28), this.wall(8, 28), this.wall(9, 28), this.wall(10, 28), this.wall(11, 28), this.dot(12, 28), this.wall(13, 28), this.wall(14, 28), this.dot(15, 28), this.wall(16, 28), this.wall(17, 28), this.wall(18, 28), this.wall(19, 28), this.wall(20, 28), this.wall(21, 28), this.wall(22, 28), this.wall(23, 28), this.wall(24, 28), this.wall(25, 28), this.dot(26, 28), this.wall(27, 28)},
                {this.wall(0, 29), this.pellet(1, 29), this.dot(2, 29), this.dot(3, 29), this.dot(4, 29), this.dot(5, 29), this.dot(6, 29), this.dot(7, 29), this.dot(8, 29), this.dot(9, 29), this.dot(10, 29), this.dot(11, 29), this.dot(12, 29), this.dot(13, 29), this.dot(14, 29), this.dot(15, 29), this.dot(16, 29), this.dot(17, 29), this.dot(18, 29), this.dot(19, 29), this.dot(20, 29), this.dot(21, 29), this.dot(22, 29), this.dot(23, 29), this.dot(24, 29), this.dot(25, 29), this.pellet(26, 29), this.wall(27, 29)},
                {this.wall(0, 30), this.wall(1, 30), this.wall(2, 30), this.wall(3, 30), this.wall(4, 30), this.wall(5, 30), this.wall(6, 30), this.wall(7, 30), this.wall(8, 30), this.wall(9, 30), this.wall(10, 30), this.wall(11, 30), this.wall(12, 30), this.wall(13, 30), this.wall(14, 30), this.wall(15, 30), this.wall(16, 30), this.wall(17, 30), this.wall(18, 30), this.wall(19, 30), this.wall(20, 30), this.wall(21, 30), this.wall(22, 30), this.wall(23, 30), this.wall(24, 30), this.wall(25, 30), this.wall(26, 30), this.wall(27, 30)}
        };
        this.graph = GraphBuilder.build(this, 31, 28);
        this.remainingDots = this.countDots();
    }

    private WallCell wall(final int x, final int y) {
        return new WallCell(x, y);
    }

    private DotCell dot(final int x, final int y) {
        return new DotCell(x, y);
    }

    private PowerPelletCell pellet(final int x, final int y) {
        return new PowerPelletCell(x, y);
    }

    private EmptyCell empty(final int x, final int y) {
        return new EmptyCell(x, y);
    }

    /**
     * Returns the cell at the given grid coordinates.
     *
     * @param col column index
     * @param row row index
     * @return cell at the specified position
     */
    public Cell getCell(int col, int row) {
        return this.grid[row][col];
    }

    /**
     * Returns whether the cell at the given coordinates can be entered by an entity.
     *
     * @param col column index
     * @param row row index
     * @return true if the cell is walkable
     */
    public boolean isWalkable(int col, int row) {
        return this.grid[row][col].isWalkable();
    }

    /**
     * Returns the adjacency graph of walkable cells, indexed by row * cols + col.
     * Each entry is an array of neighbor indices, including the tunnel cross-edge.
     *
     * @return adjacency list array
     */
    public int[][] getGraph() {
        return this.graph;
    }

    /**
     * Decrements the remaining-dot counter.
     * Called by DotCell and PowerPelletCell
     * when Pac-Man enters them for the first time.
     */
    public void dotConsumed() {
        this.remainingDots--;
    }

    /**
     * Returns whether all dots and power pellets have been consumed.
     *
     * @return true if no collectibles remain
     */
    public boolean isCleared() {
        return this.remainingDots == 0;
    }

    private int countDots() {
        int counter = 0;

        for (Cell[] cells : this.grid) {
            for (Cell cell : cells) {
                if (cell instanceof DotCell || cell instanceof PowerPelletCell) {
                    counter++;
                }
            }
        }

        return counter;
    }
}
