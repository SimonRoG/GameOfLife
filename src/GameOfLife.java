import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class GameOfLife extends Canvas {

    private static final int ROWS = 100,
        COLS = 100,
        CELL = 12;
    private static final int BG = 0xFF1e1e1e,
        FG = 0xFFd4d4d4;

    private final GameOfLifeSeq seq = new GameOfLifeSeq(ROWS);
    volatile boolean running = false;
    private volatile int delay = 100;
    private Thread gameThread;
    private BufferedImage buffer;
    private int[] pixels;
    final JButton startBtn = new JButton("Start");

    public GameOfLife() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
        setBackground(new Color(BG));
        addMouseListener(
            new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    toggle(e);
                }
            }
        );
        addMouseMotionListener(
            new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    toggle(e);
                }
            }
        );
    }

    @Override
    public void addNotify() {
        super.addNotify();
        createBufferStrategy(2);
        buffer = new BufferedImage(COLS * CELL, ROWS * CELL, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
        java.util.Arrays.fill(pixels, BG);
        drawFrame();
    }

    private synchronized void toggle(MouseEvent e) {
        int r = e.getY() / CELL,
            c = e.getX() / CELL;
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            seq.grid[r * COLS + c] = !seq.grid[r * COLS + c];
            paintCell(r, c);
            drawFrame();
        }
    }

    private void paintCell(int r, int c) {
        int color = seq.grid[r * COLS + c] ? FG : BG,
            stride = COLS * CELL;
        for (int dy = 1; dy < CELL; dy++) for (int dx = 1; dx < CELL; dx++) pixels[(r * CELL + dy) * stride + c * CELL + dx] = color;
    }

    private void paintAll() {
        int stride = COLS * CELL;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            int color = seq.grid[r * COLS + c] ? FG : BG;
            for (int dy = 1; dy < CELL; dy++) for (int dx = 1; dx < CELL; dx++) pixels[(r * CELL + dy) * stride + c * CELL + dx] = color;
        }
    }

    private synchronized void drawFrame() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;
        do {
            do {
                Graphics g = bs.getDrawGraphics();
                g.drawImage(buffer, 0, 0, null);
                g.dispose();
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());
    }

    private synchronized void computeStep() {
        seq.computeStep();
        paintAll();
    }

    void startGame() {
        if (running) return;
        running = true;
        gameThread = new Thread(() -> {
            while (running) {
                long t = System.currentTimeMillis();
                computeStep();
                drawFrame();
                long s = delay - (System.currentTimeMillis() - t);
                if (s > 0) try {
                    Thread.sleep(s);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "game-loop");
        gameThread.setDaemon(true);
        gameThread.start();
    }

    void stopGame() {
        running = false;
        if (gameThread != null) gameThread.interrupt();
    }

    synchronized void singleStep() {
        computeStep();
        drawFrame();
    }

    synchronized void clear() {
        java.util.Arrays.fill(seq.grid, false);
        java.util.Arrays.fill(pixels, BG);
        drawFrame();
    }
}
