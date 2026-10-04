import java.util.Random;

public class GameOfLifeSeq extends LifeEngine {

    public GameOfLifeSeq(int rows, int cols) {
        super(rows, cols);
    }

    public GameOfLifeSeq(int rows, int cols, long seed) {
        super(rows, cols);
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
}
