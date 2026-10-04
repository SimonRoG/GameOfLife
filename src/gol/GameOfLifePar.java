package gol;

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

    public void shutdown() {
        pool.shutdown();
    }
}
