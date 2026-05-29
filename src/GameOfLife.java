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

    private boolean[] grid = new boolean[ROWS * COLS];
    private volatile boolean running = false;
    private volatile int delay = 100;
    private Thread gameThread;
    private BufferedImage buffer;
    private int[] pixels;
    private final JButton startBtn = new JButton("Start");

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
            grid[r * COLS + c] = !grid[r * COLS + c];
            paintCell(r, c);
            drawFrame();
        }
    }

    private void paintCell(int r, int c) {
        int color = grid[r * COLS + c] ? FG : BG,
            stride = COLS * CELL;
        for (int dy = 1; dy < CELL; dy++) for (int dx = 1; dx < CELL; dx++) pixels[(r * CELL + dy) * stride + c * CELL + dx] = color;
    }

    private void paintAll() {
        int stride = COLS * CELL;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            int color = grid[r * COLS + c] ? FG : BG;
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
        boolean[] next = new boolean[ROWS * COLS];
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            int n = neighbors(r, c);
            next[r * COLS + c] = grid[r * COLS + c] ? (n == 2 || n == 3) : (n == 3);
        }
        grid = next;
        paintAll();
    }

    private int neighbors(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) continue;
            int nr = (r + dr + ROWS) % ROWS,
                nc = (c + dc + COLS) % COLS;
            if (grid[nr * COLS + nc]) count++;
        }
        return count;
    }

    private void startGame() {
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

    private void stopGame() {
        running = false;
        if (gameThread != null) gameThread.interrupt();
    }

    synchronized void singleStep() {
        computeStep();
        drawFrame();
    }

    synchronized void clear() {
        java.util.Arrays.fill(grid, false);
        java.util.Arrays.fill(pixels, BG);
        drawFrame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game of Life");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GameOfLife game = new GameOfLife();

            JPanel bar = new JPanel();
            bar.setBackground(Color.DARK_GRAY);
            for (String label : new String[] { "Start", "Step", "Clear" }) {
                JButton btn = label.equals("Start") ? game.startBtn : new JButton(label);
                btn.setActionCommand(label);
                btn.addActionListener(e -> {
                    switch (e.getActionCommand()) {
                        case "Start" -> {
                            game.startGame();
                            game.startBtn.setText("Stop");
                            game.startBtn.setActionCommand("Stop");
                        }
                        case "Stop" -> {
                            game.stopGame();
                            game.startBtn.setText("Start");
                            game.startBtn.setActionCommand("Start");
                        }
                        case "Step" -> {
                            if (!game.running) game.singleStep();
                        }
                        case "Clear" -> game.clear();
                    }
                });
                bar.add(btn);
            }

            frame.add(game, BorderLayout.CENTER);
            frame.add(bar, BorderLayout.NORTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
