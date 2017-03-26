package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {

    private BufferedImage boardImage;
    private BufferedImage whitePieceImage;
    private BufferedImage blackPieceImage;
    private BufferedImage highlightGreen;
    private BufferedImage highlightWhite;
    private BufferedImage highlightBlue;
    private BufferedImage whiteRemovedImage;
    private BufferedImage blackRemovedImage;

    int[] xyBoard;
    public boolean isFlipped;

    public Set<Integer> whitePiecesPosIDs;
    public Set<Integer> blackPiecesPosIDs;

    public Set<Integer> whiteRemovedPiecesPosIDs;
    public Set<Integer> blackRemovedPiecesPosIDs;
    public Set<Integer> lastMovingPiece;

    public Set<Integer> moveablePiecesPosIDs;
    public Set<Integer> destinationSquaresPosIDs;

    public void refresh() {
        removeAll();
        repaint();

    }

    public BoardPanel() {
        whitePiecesPosIDs = new HashSet<>();
        blackPiecesPosIDs = new HashSet<>();
        whiteRemovedPiecesPosIDs = new HashSet<>();
        blackRemovedPiecesPosIDs = new HashSet<>();
        lastMovingPiece = new HashSet<>();
        moveablePiecesPosIDs = new HashSet<>();
        destinationSquaresPosIDs = new HashSet<>();

        try {
            boardImage = ImageIO.read(new File("img/board6x6.jpg"));
            whitePieceImage = ImageIO.read(new File("img/checker-red.png"));
            blackPieceImage = ImageIO.read(new File("img/checker-black.png"));
            highlightGreen = ImageIO.read(new File("img/checkers-highlight-green.png"));
            highlightWhite = ImageIO.read(new File("img/checkers-white-selection.png"));
            highlightBlue = ImageIO.read(new File("img/checkers-highlight-green.png"));
            //whiteRemovedImage = ImageIO.read(new File("img/checkers-highlight-green.png"));
            //blackRemovedImage = ImageIO.read(new File("img/checkers-highlight-green.png"));
        } catch (IOException ex) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(boardImage, GameTracker.boardCoordinateX, GameTracker.boardCoordinateY, this);
        for (Integer whitePos : whitePiecesPosIDs) {
            int[] xy = GameTracker.getOnScreenCoordinates(isFlipped, whitePos);
            g.drawImage(whitePieceImage, xy[0], xy[1], this);
        }
        for (Integer blackPos : blackPiecesPosIDs) {
            int[] xy = GameTracker.getOnScreenCoordinates(isFlipped, blackPos);
            g.drawImage(blackPieceImage, xy[0], xy[1], this);
        }
        for (Integer startCheckerPos : moveablePiecesPosIDs) {
            int[] xy = GameTracker.getOnScreenCoordinates(isFlipped, startCheckerPos);
            g.drawImage(highlightGreen, xy[0] - 10, xy[1] - 10, this);
        }

        if (!destinationSquaresPosIDs.isEmpty()) {
            for (Integer endCheckerPos : destinationSquaresPosIDs) {
                int[] xy = GameTracker.getOnScreenCoordinates(isFlipped, endCheckerPos);
                g.drawImage(highlightWhite, xy[0] - 10, xy[1] - 10, this);
            }
        }
        for (Integer endCheckerPos : lastMovingPiece) {
            int[] xy = GameTracker.getOnScreenCoordinates(isFlipped, endCheckerPos);
            g.drawImage(highlightWhite, xy[0] - 10, xy[1] - 10, this);
        }

        /*
          for (Integer endCheckerPos : whiteRemovedPiecesPosIDs) {
           int[] xy = GameTracker.getOnScreenCoordinates(endCheckerPos);
            g.drawImage(whiteRemovedImage, xy[0]-10, xy[1]-10, this);
        }
                for (Integer endCheckerPos : blackRemovedPiecesPosIDs) {
           int[] xy = GameTracker.getOnScreenCoordinates(endCheckerPos);
            g.drawImage(blackRemovedImage, xy[0]-10, xy[1]-10, this);
        }*/
    }

}
