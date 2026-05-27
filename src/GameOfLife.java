import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameOfLife extends JPanel implements ActionListener {

    private static final int ROWS = 100,
        COLS = 100,
        CELL = 12;
    private static final Color BG = Color.decode("#1e1e1e"),
        FG = Color.decode("#d4d4d4");
    private boolean[][] grid = new boolean[ROWS][COLS];
    private boolean running = false;
    private final Timer timer = new Timer(100, e -> step());
    private final JButton startBtn = new JButton("Start");

    public GameOfLife() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
        setBackground(BG);

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

    private void toggle(MouseEvent e) {
        int r = e.getY() / CELL,
            c = e.getX() / CELL;
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            grid[r][c] = !grid[r][c];
            repaint();
        }
    }

    private void step() {
        boolean[][] next = new boolean[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            int n = neighbors(r, c);
            next[r][c] = grid[r][c] ? (n == 2 || n == 3) : (n == 3);
        }
        grid = next;
        repaint();
    }

    private int neighbors(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) continue;
            int nr = (r + dr + ROWS) % ROWS,
                nc = (c + dc + COLS) % COLS;
            if (grid[nr][nc]) count++;
        }
        return count;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(FG);
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) if (grid[r][c]) g.fillRect(c * CELL + 1, r * CELL + 1, CELL - 1, CELL - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Start" -> {
                running = true;
                timer.start();
                startBtn.setText("Stop");
                startBtn.setActionCommand("Stop");
            }
            case "Stop" -> {
                running = false;
                timer.stop();
                startBtn.setText("Start");
                startBtn.setActionCommand("Start");
            }
            case "Step" -> step();
            case "Clear" -> {
                for (boolean[] row : grid) java.util.Arrays.fill(row, false);
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GameOfLife game = new GameOfLife();

        JPanel bar = new JPanel();
        bar.setBackground(Color.DARK_GRAY);
        for (String label : new String[] { "Start", "Step", "Clear" }) {
            JButton btn = label.equals("Start") ? game.startBtn : new JButton(label);
            btn.setActionCommand(label);
            btn.addActionListener(game);
            bar.add(btn);
        }

        frame.add(game, BorderLayout.CENTER);
        frame.add(bar, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
