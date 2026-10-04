import gol.GameOfLifePar;
import gol.GameOfLifeSeq;

public class Bench {

    public static void main(String[] args) {
        GameOfLifeSeq warmup = new GameOfLifeSeq(1000, 1000, 1);
        for (int i = 0; i < 100; i++) warmup.computeStep();

        int[] sizes = { 500, 1000, 2000, 4000, 8000 };
        int steps = 20;
        System.out.println("|  N  |  T  |");
        System.out.println("| --- | --- |");
        for (int n : sizes) {
            GameOfLifeSeq seq = new GameOfLifeSeq(n, n, 1);
            long total = 0;
            for (int i = 0; i < steps; i++) total += seq.measureStep();
            System.out.println("| " + n + " | " + total / steps + " |");
        }
    }
}
