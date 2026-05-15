package pacman.board;

import java.util.ArrayList;
import java.util.List;

public class GraphBuilder {

    private GraphBuilder() { }

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

        int leftTunnel  = GraphBuilder.toIndex(14, 0, cols);
        int rightTunnel = GraphBuilder.toIndex(14, 27, cols);
        adjacency[leftTunnel]  = appendNeighbor(adjacency[leftTunnel],  rightTunnel);
        adjacency[rightTunnel] = appendNeighbor(adjacency[rightTunnel], leftTunnel);

        return adjacency;
    }

    private static int[] appendNeighbor(int[] existing, int neighbor) {
        int[] updated = new int[existing.length + 1];
        System.arraycopy(existing, 0, updated, 0, existing.length);
        updated[existing.length] = neighbor;
        return updated;
    }

    public static int toIndex(int row, int col, int cols) {
        return row * cols + col;
    }

    public static int toRow(int index, int cols) {
        return index / cols;
    }

    public static int toCol(int index, int cols) {
        return index % cols;
    }
}