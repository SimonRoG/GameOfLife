import java.util.Random;

public class GameOfLifeSeq implements LifeEngine {

    final int ROWS, COLS;
    boolean[] grid, next;

    public GameOfLifeSeq(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        grid = new boolean[ROWS * COLS];
        next = new boolean[ROWS * COLS];
    }

    public GameOfLifeSeq(int rows, int cols, long seed) {
        this(rows, cols);
        for (int r = 0; r < ROWS; r++) {
            Random rng = new Random(seed + r);
            for (int c = 0; c < COLS; c++) grid[r * COLS + c] = rng.nextBoolean();
        }
    }

    @Override
    public void computeStep() {
        computeRows(0, ROWS);
        boolean[] t = grid;
        grid = next;
        next = t;
    }

    void computeRows(int from, int to) {
        for (int r = from; r < to; r++) {
            int up = (r == 0 ? ROWS - 1 : r - 1) * COLS,
                mid = r * COLS,
                down = (r == ROWS - 1 ? 0 : r + 1) * COLS;
            for (int c = 0; c < COLS; c++) {
                int n = neighbors(up, mid, down, c);
                next[mid + c] = grid[mid + c] ? (n == 2 || n == 3) : (n == 3);
            }
        }
    }

    private int neighbors(int up, int mid, int down, int c) {
        int l = c == 0 ? COLS - 1 : c - 1,
            rt = c == COLS - 1 ? 0 : c + 1;
        return rowCount(up, l, c, rt) + rowCount(down, l, c, rt) + (grid[mid + l] ? 1 : 0) + (grid[mid + rt] ? 1 : 0);
    }

    private int rowCount(int row, int l, int c, int rt) {
        return (grid[row + l] ? 1 : 0) + (grid[row + c] ? 1 : 0) + (grid[row + rt] ? 1 : 0);
    }

    public long measureStep() {
        long t = System.currentTimeMillis();
        computeStep();
        return System.currentTimeMillis() - t;
    }

    @Override
    public boolean[] grid() {
        return grid;
    }

    @Override
    public int rows() {
        return ROWS;
    }

    @Override
    public int cols() {
        return COLS;
    }
}
