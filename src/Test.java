import java.util.Arrays;

public class Test {

    static void gliderTest() {
        int n = 5;
        GameOfLifePar g = new GameOfLifePar(n, n, 4);
        // _ @ _ _
        // _ _ @ _
        // @ @ @ _
        // _ _ _ _
        g.grid[0 * n + 1] = g.grid[1 * n + 2] = g.grid[2 * n + 0] = g.grid[2 * n + 1] = g.grid[2 * n + 2] = true;
        for (int i = 0; i < 4; i++) g.computeStep();
        boolean[] exp = new boolean[n * n];
        // _ _ _ _
        // _ _ @ _
        // _ _ _ @
        // _ @ @ @
        exp[1 * n + 2] = exp[2 * n + 3] = exp[3 * n + 1] = exp[3 * n + 2] = exp[3 * n + 3] = true;
        System.out.println("glider test: " + (Arrays.equals(exp, g.grid) ? "OK" : "FAIL"));
        g.shutdown();
    }

    static void benchmarkTest() {
        int[] sizes = { 500, 1000, 2000, 4000, 8000 };
        int steps = 10;
        System.out.println("\n| N | T |");
        System.out.println("| --- | --- |");
        for (int n : sizes) {
            GameOfLifePar g = new GameOfLifePar(n, n, 4, 1);
            long total = 0;
            for (int i = 0; i < steps; i++) total += g.measureStep();
            System.out.println("| " + n + " | " + total / steps + " |");
            g.shutdown();
        }
    }

    public static void main(String[] args) {
        gliderTest();
        benchmarkTest();
    }
}
