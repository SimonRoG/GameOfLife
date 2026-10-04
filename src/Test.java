import gol.GameOfLifePar;
import gol.GameOfLifeSeq;
import java.util.Arrays;

public class Test {

    static boolean passed = true;

    static void check(String name, boolean ok) {
        if (!ok) passed = false;
        System.out.println(name + ": " + (ok ? "OK" : "FAIL"));
    }

    static boolean[] gliderInit(int n) {
        boolean[] g = new boolean[n * n];
        // _ @ _ _
        // _ _ @ _
        // @ @ @ _
        // _ _ _ _
        g[0 * n + 1] = g[1 * n + 2] = g[2 * n + 0] = g[2 * n + 1] = g[2 * n + 2] = true;
        return g;
    }

    static boolean[] gliderExp(int n) {
        boolean[] g = new boolean[n * n];
        // _ _ _ _
        // _ _ @ _
        // _ _ _ @
        // _ @ @ @
        g[1 * n + 2] = g[2 * n + 3] = g[3 * n + 1] = g[3 * n + 2] = g[3 * n + 3] = true;
        return g;
    }

    static void gliderSeqTest() {
        int n = 5;
        GameOfLifeSeq seq = new GameOfLifeSeq(n, n);
        System.arraycopy(gliderInit(n), 0, seq.grid, 0, n * n);
        for (int i = 0; i < 4; i++) seq.computeStep();
        check("glider seq", Arrays.equals(gliderExp(n), seq.grid));
    }

    static void gliderParTest() {
        int n = 5;
        GameOfLifePar par = new GameOfLifePar(n, n, 4, 4);
        System.arraycopy(gliderInit(n), 0, par.grid, 0, n * n);
        for (int i = 0; i < 4; i++) par.computeStep();
        check("glider par", Arrays.equals(gliderExp(n), par.grid));
        par.shutdown();
    }

    static void seqParTest() {
        int cores = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = { 1, 2, 4, cores, cores * 2 };
        for (int t : threadCounts) {
            GameOfLifeSeq seq = new GameOfLifeSeq(1000, 1000, 1);
            GameOfLifePar par = new GameOfLifePar(1000, 1000, t, t, 1);
            for (int i = 0; i < 10; i++) {
                seq.computeStep();
                par.computeStep();
            }
            check("seq = par. t = " + t, Arrays.equals(seq.grid, par.grid));
            par.shutdown();
        }
    }

    public static void main(String[] args) {
        gliderSeqTest();
        gliderParTest();
        System.out.println();
        seqParTest();
    }
}
