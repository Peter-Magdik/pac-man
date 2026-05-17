package pacman.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that constructs the board's walkability adjacency graph.
 * <p>
 * The graph is a flat array where index row * cols + col stores the
 * indices of all walkable neighbors for that cell. A tunnel cross-edge is
 * manually appended between the two tunnel-row edge cells so BFS pathfinding
 * used by ghost AI handles tunnel wrapping transparently.
 */
public class GraphBuilder {

    /** Not instantiable — all methods are static. */
    private GraphBuilder() { }

    /**
     * Builds and returns the adjacency graph for the given board.
     *
     * @param board game board to query for walkability
     * @param rows total number of rows
     * @param cols total number of columns
     * @return adjacency list array; each entry lists the flat indices of walkable neighbors
     */
    public static int[][] build(Board board, int rows, int cols) {
        int totalCells = rows * cols;
        int[][] adjacency = new int[totalCells][];

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;

                if (!board.isWalkable(col, row)) {
                    adjacency[index] = new int[0];
                    continue;
                }

                List<Integer> neighbors = new ArrayList<>();
                for (int[] dir : directions) {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];

                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                            && board.isWalkable(newCol, newRow)) {
                        neighbors.add(newRow * cols + newCol);
                    }
                }

                adjacency[index] = neighbors.stream().mapToInt(Integer::intValue).toArray();
            }
        }

        int leftTunnel = GraphBuilder.toIndex(14, 0, cols);
        int rightTunnel = GraphBuilder.toIndex(14, 27, cols);
        adjacency[leftTunnel] = appendNeighbor(adjacency[leftTunnel],  rightTunnel);
        adjacency[rightTunnel] = appendNeighbor(adjacency[rightTunnel], leftTunnel);

        return adjacency;
    }

    private static int[] appendNeighbor(int[] existing, int neighbor) {
        int[] updated = new int[existing.length + 1];
        System.arraycopy(existing, 0, updated, 0, existing.length);
        updated[existing.length] = neighbor;
        return updated;
    }

    /**
     * Converts 2D grid coordinates to a flat array index.
     *
     * @param row  row index
     * @param col  column index
     * @param cols total number of columns
     * @return flat index
     */
    public static int toIndex(int row, int col, int cols) {
        return row * cols + col;
    }

    /**
     * Extracts the row component from a flat index.
     *
     * @param index flat cell index
     * @param cols  total number of columns
     * @return row index
     */
    public static int toRow(int index, int cols) {
        return index / cols;
    }

    /**
     * Extracts the column component from a flat index.
     *
     * @param index flat cell index
     * @param cols  total number of columns
     * @return column index
     */
    public static int toCol(int index, int cols) {
        return index % cols;
    }
}