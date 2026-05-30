import java.util.Random;

public class GameOfLifeSeq {

    final int N;
    boolean[] grid;

    public GameOfLifeSeq(int n) {
        N = n;
        grid = new boolean[N * N];
    }

    public GameOfLifeSeq(int n, long seed) {
        this(n);
        Random rng = new Random(seed);
        for (int i = 0; i < grid.length; i++) grid[i] = rng.nextBoolean();
    }

    public void computeStep() {
        boolean[] next = new boolean[N * N];
        for (int r = 0; r < N; r++) for (int c = 0; c < N; c++) {
            int n = neighbors(r, c);
            next[r * N + c] = grid[r * N + c] ? (n == 2 || n == 3) : (n == 3);
        }
        grid = next;
    }

    private int neighbors(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) continue;
            int nr = (r + dr + N) % N,
                nc = (c + dc + N) % N;
            if (grid[nr * N + nc]) count++;
        }
        return count;
    }

    public long measureStep() {
        long t = System.currentTimeMillis();
        computeStep();
        return System.currentTimeMillis() - t;
    }
}
