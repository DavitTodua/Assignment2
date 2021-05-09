import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris {
    private static JCheckBox brainMode;
    private static DefaultBrain brain = new DefaultBrain();
    private int counter;
    private Brain.Move nextMove = new Brain.Move();
    private JSlider adversary = new JSlider(0, 100, 0);
    private JPanel little;
    private JLabel okStatus;

    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JBrainTetris(int pixels) {
        super(pixels);
    }

    //@Override
    public JComponent createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // COUNT
        countLabel = new JLabel("0");
        panel.add(countLabel);

        // SCORE
        scoreLabel = new JLabel("0");
        panel.add(scoreLabel);

        // TIME
        timeLabel = new JLabel(" ");
        panel.add(timeLabel);

        panel.add(Box.createVerticalStrut(12));

        // START button
        startButton = new JButton("Start");
        panel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // STOP button
        stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });

        enableButtons();

        JPanel row = new JPanel();

        // SPEED slider
        panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Speed:"));
        speed = new JSlider(0, 200, 75);    // min, max, current
        speed.setPreferredSize(new Dimension(100, 15));

        updateTimer();
        row.add(speed);

        panel.add(row);
        speed.addChangeListener(new ChangeListener() {
            // when the slider changes, sync the timer to its value
            public void stateChanged(ChangeEvent e) {
                updateTimer();
            }
        });

        testButton = new JCheckBox("Test sequence");
        panel.add(testButton);

        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain Active:");
        panel.add(brainMode);
        adversary.setPreferredSize(new Dimension(100,15));
        okStatus = new JLabel("ok");
        little = new JPanel();
        little.add(new JLabel("Adversary:*"));
        little.add(adversary);
        panel.add(little);
        panel.add(okStatus);

        return panel;
    }

    public static void main(String[] args) {


        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);

        DefaultBrain brain = new DefaultBrain();
        brainMode.doClick();


        frame.setVisible(true);
    }

    @Override
    public void tick(int verb) {

        if (brainMode.isSelected()) {

            if (count > counter) {
                board.undo();
                brain.bestMove(board, currentPiece, HEIGHT, nextMove);
                counter++;
            }
            if (!currentPiece.equals(nextMove.piece)) {
                currentPiece = currentPiece.fastRotation();
            }
            if (currentX < nextMove.x) {
                super.tick(RIGHT);
            }
            if (currentX > nextMove.x) {
                super.tick(LEFT);
            }
            super.tick(DOWN);


        } else {

            super.tick(verb);
        }
    }

    @Override
    public Piece pickNextPiece() {
        if (random.nextInt(99) < adversary.getValue()) {
            Piece[] pieces = Piece.getPieces();
            int worst = 0;
            double worstScore = 0;
            for (int i = 0; i < pieces.length; i++) {
                board.undo();
                brain.bestMove(board, pieces[i], HEIGHT, nextMove);
                if (nextMove.score > worstScore) {
                    worstScore = nextMove.score;
                    worst = i;
                }
            }
            okStatus.setText("*ok*");
            return pieces[worst];
        }
        okStatus.setText("ok");
        return super.pickNextPiece();
    }
}
