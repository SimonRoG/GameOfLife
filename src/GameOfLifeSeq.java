import java.util.Random;

public class GameOfLifeSeq implements LifeEngine {

    final int ROWS, COLS;
    boolean[] grid;

    public GameOfLifeSeq(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        grid = new boolean[ROWS * COLS];
    }

    public GameOfLifeSeq(int rows, int cols, long seed) {
        this(rows, cols);
        Random rng = new Random(seed);
        for (int i = 0; i < grid.length; i++) grid[i] = rng.nextBoolean();
    }

    @Override
    public void computeStep() {
        boolean[] next = new boolean[ROWS * COLS];
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            int n = neighbors(r, c);
            next[r * COLS + c] = grid[r * COLS + c] ? (n == 2 || n == 3) : (n == 3);
        }
        grid = next;
    }

    private int neighbors(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) continue;
            int nr = (r + dr + ROWS) % ROWS,
                nc = (c + dc + COLS) % COLS;
            if (grid[nr * COLS + nc]) count++;
        }
        return count;
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
