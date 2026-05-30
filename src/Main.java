import java.awt.*;
import javax.swing.*;

public class Main {

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
