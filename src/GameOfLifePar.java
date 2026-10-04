import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameOfLifePar extends LifeEngine {

    private final ExecutorService pool;
    private final int threads;

    public GameOfLifePar(int rows, int cols, int threads) {
        super(rows, cols);
        this.threads = threads;
        pool = Executors.newFixedThreadPool(threads);
    }

    public GameOfLifePar(int rows, int cols, int threads, long seed) {
        this(rows, cols, threads);
        for (int r = 0; r < ROWS; r++) {
            Random rng = new Random(seed + r);
            for (int c = 0; c < COLS; c++) grid[r * COLS + c] = rng.nextBoolean();
        }
    }

    @Override
    public void computeStep() {
        int stripSize = (ROWS + threads - 1) / threads;
        List<Callable<Void>> tasks = new ArrayList<>(threads);
        for (int s = 0; s < threads; s++) {
            int from = s * stripSize;
            int to = Math.min(from + stripSize, ROWS);
            tasks.add(() -> {
                computeRows(from, to);
                return null;
            });
        }
        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    public void shutdown() {
        pool.shutdown();
    }
}
