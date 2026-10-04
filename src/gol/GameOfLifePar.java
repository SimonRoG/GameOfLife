package gol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameOfLifePar extends LifeEngine {

    private final ExecutorService pool;
    private final int tasks;

    public GameOfLifePar(int rows, int cols, int tasks, int threads) {
        super(rows, cols);
        this.tasks = tasks;
        pool = Executors.newFixedThreadPool(threads);
    }

    public GameOfLifePar(int rows, int cols, int tasks, int threads, long seed) {
        this(rows, cols, tasks, threads);
        for (int r = 0; r < ROWS; r++) {
            Random rng = new Random(seed + r);
            for (int c = 0; c < COLS; c++) grid[r * COLS + c] = rng.nextBoolean();
        }
    }

    @Override
    public void computeStep() {
        int stripSize = (ROWS + tasks - 1) / tasks;
        List<Callable<Void>> taskList = new ArrayList<>(tasks);
        for (int s = 0; s < tasks; s++) {
            int from = s * stripSize;
            int to = Math.min(from + stripSize, ROWS);
            taskList.add(() -> { computeRows(from, to); return null; });
        }
        try {
            pool.invokeAll(taskList);
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
