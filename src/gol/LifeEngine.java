package gol;

public abstract class LifeEngine {

    final int ROWS, COLS;
    public boolean[] grid, next;

    LifeEngine(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        grid = new boolean[ROWS * COLS];
        next = new boolean[ROWS * COLS];
    }

    public abstract void computeStep();

    void computeRows(int from, int to) {
        for (int r = from; r < to; r++) {
            int up = (r == 0 ? ROWS - 1 : r - 1) * COLS,
                mid = r * COLS,
                down = (r == ROWS - 1 ? 0 : r + 1) * COLS;
            for (int c = 0; c < COLS; c++) {
                int n = neighbors(up, mid, down, c);
                next[mid + c] = grid[mid + c] ? n == 2 || n == 3 : n == 3;
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

    public double measureStep() {
        long t = System.nanoTime();
        computeStep();
        return (System.nanoTime() - t) / 1_000_000.0;
    }

    public boolean[] grid() {
        return grid;
    }

    public int rows() {
        return ROWS;
    }

    public int cols() {
        return COLS;
    }
}
