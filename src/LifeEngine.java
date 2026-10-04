public abstract class LifeEngine {

    final int ROWS, COLS;
    boolean[] grid, next;

    LifeEngine(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        grid = new boolean[ROWS * COLS];
        next = new boolean[ROWS * COLS];
    }

    public abstract void computeStep();

    public long measureStep() {
        long t = System.currentTimeMillis();
        computeStep();
        return System.currentTimeMillis() - t;
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
