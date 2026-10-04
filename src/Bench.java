import gol.GameOfLifePar;
import gol.GameOfLifeSeq;
import gol.LifeEngine;

public class Bench {

    static String measure(LifeEngine e, int steps) {
        double total = 0;
        for (int i = 0; i < steps; i++) total += e.measureStep();
        return String.format("%.2f", total / steps);
    }

    public static void main(String[] args) {
        GameOfLifeSeq warmup = new GameOfLifeSeq(1000, 1000, 1);
        for (int i = 0; i < 100; i++) warmup.computeStep();

        int[] sizes = { 500, 1000, 2000, 4000, 8000, 16000 };
        int[] threadCounts = { 1, 2, 4, 6, 12 };
        int steps = 20;

        StringBuilder header = new StringBuilder("| N | T (seq)");
        StringBuilder sep = new StringBuilder("| --- | ---");
        for (int t : threadCounts) {
            header.append(" | T (par, ").append(t).append(")");
            sep.append(" | ---");
        }
        System.out.println(header + " |");
        System.out.println(sep + " |");

        for (int n : sizes) {
            GameOfLifeSeq seq = new GameOfLifeSeq(n, n, 1);
            StringBuilder row = new StringBuilder("| " + n + " | " + measure(seq, steps));
            for (int t : threadCounts) {
                GameOfLifePar par = new GameOfLifePar(n, n, t, t, 1);
                row.append(" | ").append(measure(par, steps));
                par.shutdown();
            }
            System.out.println(row + " |");
        }

        System.out.println();

        int threads = 6;
        int[] taskMults = { 1, 2, 4, 6, 10, 20 };

        StringBuilder header2 = new StringBuilder("| N");
        StringBuilder sep2 = new StringBuilder("| ---");
        for (int m : taskMults) {
            header2.append(" | ").append(threads * m).append(" tasks");
            sep2.append(" | ---");
        }
        System.out.println(header2 + " |");
        System.out.println(sep2 + " |");

        for (int n : sizes) {
            StringBuilder row = new StringBuilder("| " + n);
            for (int m : taskMults) {
                GameOfLifePar par = new GameOfLifePar(n, n, threads * m, threads, 1);
                row.append(" | ").append(measure(par, steps));
                par.shutdown();
            }
            System.out.println(row + " |");
        }
    }
}
