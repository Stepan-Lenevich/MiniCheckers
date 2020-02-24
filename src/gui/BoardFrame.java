package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

public class BoardFrame {

    BoardPanel bPanel;
    private GameTracker gameTracker;
    JFrame mainFrame;
    MouseListener mouseListener;

    public static int JFRAME_DIMENSIONS = 700;

    public BoardFrame() {

        mainFrame = new JFrame("Mini Checkers");
        mainFrame.setSize(JFRAME_DIMENSIONS + 200, JFRAME_DIMENSIONS);
        mainFrame.setTitle("Mini Checkers");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bPanel = new BoardPanel();
        gameTracker = new GameTracker(bPanel);

        mouseListener = getMouseListener();

        bPanel.addMouseListener(mouseListener);

        mainFrame.add(bPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        /*        
        JTextArea output = new JTextArea(gameTracker.getScoreText(), 5, 20);
        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        JScrollPane js = new JScrollPane(output, v, h);
         */
        JCheckBox checkboxWhitePlayer = new JCheckBox("White is AI");
        JCheckBox checkboxBlackPlayer = new JCheckBox("Black is AI");
        JCheckBox boardOrientation = new JCheckBox("Flip Board");
        checkboxWhitePlayer.setSelected(gameTracker.isWhiteIsAI());
        checkboxBlackPlayer.setSelected(gameTracker.isBlackIsAI());
        boardOrientation.setSelected(gameTracker.isBoardFlipped());

        checkboxWhitePlayer.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                gameTracker.setWhiteIsAI(checkboxWhitePlayer.isSelected());
            }
        });

        checkboxBlackPlayer.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                gameTracker.setBlackIsAI(checkboxBlackPlayer.isSelected());
            }
        });

        boardOrientation.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                gameTracker.setIsBoardFlipped(boardOrientation.isSelected());
            }
        });
        controlPanel.add(checkboxWhitePlayer);
        controlPanel.add(checkboxBlackPlayer);
        controlPanel.add(boardOrientation);

        if (checkboxWhitePlayer.isSelected()) {
            gameTracker.setWhiteIsAI(true);
        } else {
            gameTracker.setWhiteIsAI(false);
        }

        if (checkboxBlackPlayer.isSelected()) {
            gameTracker.setBlackIsAI(true);
        } else {
            gameTracker.setBlackIsAI(false);
        }
        if (boardOrientation.isSelected()) {
            gameTracker.setIsBoardFlipped(true);
        } else {
            gameTracker.setIsBoardFlipped(false);
        }

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ButtonListener());
        controlPanel.add(backButton);

        JButton forwardButton = new JButton("Forward");
        forwardButton.addActionListener(new ButtonListener());
        controlPanel.add(forwardButton);

        JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ButtonListener());
        controlPanel.add(newGame);

        mainFrame.add(controlPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);

    }

    BoardPanel getBPanel() {
        return bPanel;
    }

    class ButtonListener implements ActionListener {

        ButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("New Game")) {

                gameTracker.startGame();
                bPanel.refresh();
            } else if (e.getActionCommand().equals("Back")) {
                gameTracker.goBack();
            } else if (e.getActionCommand().equals("Forward")) {
                gameTracker.goForward();

            }
        }
    }

    private MouseListener getMouseListener() {

        return new MouseListener() {
            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                gameTracker.processCoordinates(x, y);
                bPanel.refresh();
            }
        };

    }
}
