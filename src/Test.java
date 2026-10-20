import gol.GameOfLifePar;
import gol.GameOfLifeSeq;
import java.util.Arrays;
import javax.swing.text.StyledEditorKit.BoldAction;

public class Test {

    static final int CORES = Runtime.getRuntime().availableProcessors();

    static void check(String name, boolean ok) {
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

    static boolean[] blockInitExp(int n) {
        boolean[] g = new boolean[n * n];
        // @ @ _
        // @ @ _
        // _ _ _
        g[0 * n + 0] = g[0 * n + 1] = g[1 * n + 0] = g[1 * n + 1] = true;
        return g;
    }

    static boolean[] blinkerInit(int n) {
        boolean[] g = new boolean[n * n];
        // _ @ _
        // _ @ _
        // _ @ _
        g[0 * n + 1] = g[1 * n + 1] = g[2 * n + 1] = true;
        return g;
    }

    static boolean[] blinkerExp(int n) {
        boolean[] g = new boolean[n * n];
        // _ _ _
        // @ @ @
        // _ _ _
        g[1 * n + 0] = g[1 * n + 1] = g[1 * n + 2] = true;
        return g;
    }

    static void gliderSeqTest() {
        int n = 5;
        GameOfLifeSeq seq = new GameOfLifeSeq(n, n);
        System.arraycopy(gliderInit(n), 0, seq.grid, 0, n * n);
        for (int i = 0; i < 4; i++) seq.computeStep();
        check("glider seq", Arrays.equals(gliderExp(n), seq.grid));
    }

    static void blockSeqTest() {
        int n = 5;
        GameOfLifeSeq seq = new GameOfLifeSeq(n, n);
        System.arraycopy(blockInitExp(n), 0, seq.grid, 0, n * n);
        seq.computeStep();
        check("block seq", Arrays.equals(blockInitExp(n), seq.grid));
    }

    static void blinkerSeqTest() {
        int n = 5;
        GameOfLifeSeq seq = new GameOfLifeSeq(n, n);
        System.arraycopy(blinkerInit(n), 0, seq.grid, 0, n * n);
        seq.computeStep();
        check("blinker seq", Arrays.equals(blinkerExp(n), seq.grid));
    }

    static void gliderParTest() {
        int n = 5;
        GameOfLifePar par = new GameOfLifePar(n, n, CORES, CORES);
        System.arraycopy(gliderInit(n), 0, par.grid, 0, n * n);
        for (int i = 0; i < 4; i++) par.computeStep();
        check("glider par", Arrays.equals(gliderExp(n), par.grid));
        par.shutdown();
    }

    static void blockParTest() {
        int n = 5;
        GameOfLifePar par = new GameOfLifePar(n, n, CORES, CORES);
        System.arraycopy(blockInitExp(n), 0, par.grid, 0, n * n);
        par.computeStep();
        check("block par", Arrays.equals(blockInitExp(n), par.grid));
        par.shutdown();
    }

    static void blinkerParTest() {
        int n = 5;
        GameOfLifePar par = new GameOfLifePar(n, n, CORES, CORES);
        System.arraycopy(blinkerInit(n), 0, par.grid, 0, n * n);
        par.computeStep();
        check("blinker par", Arrays.equals(blinkerExp(n), par.grid));
        par.shutdown();
    }

    static void seqParTest() {
        int[] threadCounts = { 1, 2, 4, CORES, CORES * 2 };
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
        blockSeqTest();
        blinkerSeqTest();
        gliderParTest();
        blockParTest();
        blinkerParTest();
        System.out.println();
        seqParTest();
    }
}
