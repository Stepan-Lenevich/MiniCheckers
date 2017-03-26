package gui;

import checkers.Data;
import checkers.Enums.Player;
import checkers.Moves;
import static checkers.Moves.getPosId;
import checkers.Position;
import checkers.Solver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameTracker {

    BoardPanel bPanel;

    private List<Long> gameSequence;
    public static final int squareWidth = 71;
    public static final int coordinatesOfFirstCenter = 135;
    public static final int coordAccuracy = 30;

    private boolean whiteIsAI;

    private boolean blackIsAI;
    //private boolean whiteIsAI = false;
    //private boolean blackIsAI = true;
    private boolean isBoardFlipped = false;

    private Position pos;
    private int plysBack = 0;

    private String scoreText = "New game";

    private String textAreaString = "moves:\n";

    public static final int boardCoordinateX = 100;
    public static final int boardCoordinateY = 100;
    public static final int offset = 2;

    Map<Integer, Long> movesForPos;

    //private boolean posHasJumps;   //position in the begining of a ply. if false, then pos only has moves (game ending is checked upstream)
    private Long positionSnapshotID;  // is different from pos.ID when in the middle of jumping
    private int selectedPieceID;   //6*y+x  or -1 if not selected
    boolean isBoardUpToDate;
    private int moveType; //0: not checked yet, 1: moves, 2: first jump, 3: sequential jumps, -1: terminal position (no moves)

    GameTracker(BoardPanel bPanel) {
        this.bPanel = bPanel;
        gameSequence = new ArrayList<>();
        selectedPieceID = -1;
        pos = new Position();
        positionSnapshotID = pos.positionID;
        updatePly();
    }

    void startGame() {
        gameSequence = new ArrayList<>();
        selectedPieceID = -1;
        moveType = 0;
        pos = new Position();
        positionSnapshotID = pos.positionID;
        updatePly();
    }

    private void updatePly() {  //done after new position was written down

        selectedPieceID = -1;
        positionSnapshotID = pos.positionID;

        if (pos.getParent() != null) {
            pos.getParent().selectedChild = pos;
        }
        moveType = 0;

        populateLastMoveMarkers();
        populatePieces();
        if (Data.r.isLosingPosition(pos.p, pos.board)) {
            moveType = -1;
            endGame();
        } else {
            polulateMoves();

            if (isAIMove()) {
                getAIMove();

            } else if (moveType == -1) {
                endGame();

            } else {
                bPanel.refresh();
            }
        }
    }

    public void processCoordinates(int x, int y) {   //after mouseclick listener sends coords

        if (isAIMove()) {
            updatePly();
        }

        if (moveType == -1 || isAIMove() || !clickIsOnBoard(x, y)) {
            return;
        }

        int squareID = getOffsetForSquareID(x, y);
        if (isValidPieceSelection(squareID)) {
            processPieceSelection(squareID);
        } else {
            processMoveSeiection(squareID);
        }
    }

    private void processMoveSeiection(int squareID) {
        if (selectedPieceID < 0) {
            return;
        }

        if (bPanel.destinationSquaresPosIDs.contains(squareID)) {
            if (moveType == 1) {
                updateMove(squareID);
            } else {
                updateJump(squareID);

            }
        }
    }

    private void updateMove(int squareID) {
        if (bPanel.destinationSquaresPosIDs.contains(squareID)) {
            positionSnapshotID = getNewSnapshotPosition(positionSnapshotID, selectedPieceID, squareID);
            gameSequence.add(positionSnapshotID);
            pos = new Position(positionSnapshotID, pos);
            updatePly();
        }
    }

    private void updateJump(int squareID) {
        long oldPosSpanshot = positionSnapshotID;
        positionSnapshotID = getNewSnapshotPosition(positionSnapshotID, selectedPieceID, squareID);
        selectedPieceID = squareID;
        bPanel.destinationSquaresPosIDs = new HashSet<>();
        bPanel.moveablePiecesPosIDs = new HashSet<>();
        bPanel.moveablePiecesPosIDs.add(selectedPieceID);

        Map<Long, Integer> nextJumps = Moves.getFollowingJumps(positionSnapshotID, selectedPieceID, pos.p);

        if (nextJumps.isEmpty()) {
            gameSequence.add(positionSnapshotID);
            pos = new Position(positionSnapshotID, pos);
            updatePly();

        } else {
            for (Map.Entry<Long, Integer> entry : nextJumps.entrySet()) {
                Integer landingSquareID = entry.getValue();
                bPanel.destinationSquaresPosIDs.add(landingSquareID);
            }
            moveType = 3; //sequential jumps 
            populateLastMoveMarkers(positionSnapshotID, oldPosSpanshot);
            populatePieces();
        }
    }

    private void processPieceSelection(int squareID) {

        selectedPieceID = squareID;
        if (moveType == 1) {
            populateImmidiateMoves();
        }
        if (moveType == 2) {
            populateImmidiateJumps();
        }
    }

    private void populateImmidiateMoves() {
        bPanel.destinationSquaresPosIDs = new HashSet<>();
        byte[][] b = Data.r.toBoard(positionSnapshotID);
        int x = selectedPieceID % pos.board[0].length;
        int y = selectedPieceID / pos.board[0].length;

        if (pos.p == Player.WHITE) {
            if (y > 0 && x > 0 && b[y - 1][x - 1] == Player.EMPTY.getId()) {
                int landingSpotID = (y - 1) * pos.board[0].length + x - 1;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
            if (y > 0 && x < (pos.board[0].length - 1) && b[y - 1][x + 1] == Player.EMPTY.getId()) {
                int landingSpotID = (y - 1) * pos.board[0].length + x + 1;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
        } else {

            if (y < (pos.board.length - 1) && x > 0 && b[y + 1][x - 1] == Player.EMPTY.getId()) {
                int landingSpotID = (y + 1) * pos.board[0].length + x - 1;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
            if (y < (pos.board.length - 1) && x < (pos.board[0].length - 1) && b[y + 1][x + 1] == Player.EMPTY.getId()) {
                int landingSpotID = (y + 1) * pos.board[0].length + x + 1;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
        }
    }

    private void populateImmidiateJumps() {

        bPanel.destinationSquaresPosIDs = new HashSet<>();
        byte[][] b = Data.r.toBoard(positionSnapshotID);

        int x = selectedPieceID % pos.board[0].length;
        int y = selectedPieceID / pos.board[0].length;
        byte oppId = Data.r.togglePlayerID(pos.p.getId());

        if (pos.p == Player.WHITE || moveType == 3) {

            if (y > 1 && x > 1 && b[y][x] == pos.p.getId() && b[y - 1][x - 1] == oppId && b[y - 2][x - 2] == Player.EMPTY.getId()) {
                int landingSpotID = (y - 2) * pos.board[0].length + x - 2;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
            if (y > 1 && x < (pos.board[0].length - 2) && b[y][x] == pos.p.getId() && b[y - 1][x + 1] == oppId && b[y - 2][x + 2] == Player.EMPTY.getId()) {
                int landingSpotID = (y - 2) * pos.board[0].length + x + 2;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }

        }
        if (pos.p == Player.BLACK || moveType == 3) {

            if (y < (pos.board.length - 2) && x > 1 && b[y][x] == pos.p.getId() && b[y + 1][x - 1] == oppId && b[y + 2][x - 2] == Player.EMPTY.getId()) {
                int landingSpotID = (y + 2) * pos.board[0].length + x - 2;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
            if (y < (pos.board[0].length - 2) && x < (pos.board[0].length - 2) && b[y][x] == pos.p.getId() && b[y + 1][x + 1] == oppId && b[y + 2][x + 2] == Player.EMPTY.getId()) {
                int landingSpotID = (y + 2) * pos.board[0].length + x + 2;
                bPanel.destinationSquaresPosIDs.add(landingSpotID);
            }
        }

    }

    private void endGame() {
        System.out.println(pos.getParent().p + " WON");
    }

    public Long getNewSnapshotPosition(Long parentPos, int oldSquareID, int newSquareID) {
        byte[][] newBoard = Data.r.copyBoard(Data.r.toBoard(parentPos));

        int[] oldSqrXY = Moves.squareIDtoColumnAndRow(oldSquareID);
        int[] newSqrXY = Moves.squareIDtoColumnAndRow(newSquareID);

        boolean isJump = Math.abs(oldSqrXY[0] - newSqrXY[0]) == 2;

        newBoard[newSqrXY[1]][newSqrXY[0]] = pos.p.getId();
        newBoard[oldSqrXY[1]][oldSqrXY[0]] = Player.EMPTY.getId();

        if (isJump) {
            int[] coordOfRemovedPiece = new int[]{(int) (oldSqrXY[0] + newSqrXY[0]) / 2, (int) (oldSqrXY[1] + newSqrXY[1]) / 2};

            newBoard[coordOfRemovedPiece[1]][coordOfRemovedPiece[0]] = Player.EMPTY.getId();
        }

        return Data.r.toIndex(newBoard);
    }

    private void populateLastMoveMarkers() {

        if (pos.getParent() == null) { //is initial position
            bPanel.whiteRemovedPiecesPosIDs = new HashSet<>();
            bPanel.blackRemovedPiecesPosIDs = new HashSet<>();
            bPanel.lastMovingPiece = new HashSet<>();
        } else {
            populateLastMoveMarkers(pos.positionID, pos.getParent().positionID);
        }

    }

    private void populateLastMoveMarkers(Long newPos, Long oldPos) {
        bPanel.whiteRemovedPiecesPosIDs = new HashSet<>();
        bPanel.blackRemovedPiecesPosIDs = new HashSet<>();
        bPanel.lastMovingPiece = new HashSet<>();

        if (pos.getParent() == null) { //is initial position
            return;
        }
        int[] currentBoardArray = Moves.posIdToArray(newPos);
        int[] oldBoardArray = Moves.posIdToArray(oldPos);

        for (int i = 0; i < currentBoardArray.length; i++) {
            if (currentBoardArray[i] == Player.EMPTY.getId()) {
                if (oldBoardArray[i] == Player.WHITE.getId()) {
                    bPanel.whiteRemovedPiecesPosIDs.add(i);

                } else if (oldBoardArray[i] == Player.BLACK.getId()) {
                    bPanel.blackRemovedPiecesPosIDs.add(i);
                }

            } else if (oldBoardArray[i] == Player.EMPTY.getId()) {
                bPanel.lastMovingPiece.add(i);
            }
        }
    }

    private void polulateMoves() {

        bPanel.moveablePiecesPosIDs = new HashSet<>();
        Map<Integer, Set<Integer>> movablePieces = Moves.getCoordinatesOfPiecesThatCanMove(pos);  //this is really a Map.Entry because only one pair is in this map
        for (Map.Entry<Integer, Set<Integer>> entry : movablePieces.entrySet()) {
            moveType = entry.getKey();
            Set<Integer> piecesPosIDs = entry.getValue();

            if (moveType > 0) {
                bPanel.moveablePiecesPosIDs.addAll(piecesPosIDs);
            }

        }

    }

    private void populatePieces() {
        bPanel.whitePiecesPosIDs = new HashSet<>();
        bPanel.blackPiecesPosIDs = new HashSet<>();

        int[] boardArray = Moves.posIdToArray(positionSnapshotID);
        for (int i = 0; i < boardArray.length; i++) {
            if (boardArray[i] == Player.WHITE.getId()) {
                bPanel.whitePiecesPosIDs.add(i);
            } else if (boardArray[i] == Player.BLACK.getId()) {
                bPanel.blackPiecesPosIDs.add(i);
            }
        }
    }

    private void getAIMove() {
        bPanel.moveablePiecesPosIDs = new HashSet<>();
        bPanel.refresh();
        /*
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameTracker.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        pos = selectComputerMove();
        updatePly();
    }

    private Position selectComputerMove() {
        Set<Long> moves = Moves.getSetOfLegalMoves(pos.board, pos.p);

        Long bestKnownMove = 0L;
        int bestKnownPly = 0;
        Player opponent = pos.p == Player.WHITE ? Player.BLACK : Player.WHITE;

        for (Long currentMove : moves) {
            int bestPlyForCurrentMove = Data.getBestPly(currentMove, opponent);
            if (bestKnownPly == 0) {
                bestKnownPly = bestPlyForCurrentMove;
                bestKnownMove = currentMove;
            } else {
                boolean doesThisPositionHurtOpponentMoreThanPrevious = Solver.isSecondPlyCountBetterThanFirst(bestPlyForCurrentMove, bestKnownPly);
                if (doesThisPositionHurtOpponentMoreThanPrevious) {

                    bestKnownPly = bestPlyForCurrentMove;
                    bestKnownMove = currentMove;
                }
            }
        }
        System.out.println("Best ply is " + bestKnownPly + ", moveID is " + bestKnownMove);
        return new Position(bestKnownMove, pos);
    }

    public boolean isAIMove() {

        if (moveType == -1) { //game ended
            return false;
        }
        if (pos.p == Player.WHITE) {
            return whiteIsAI;
        } else {
            return blackIsAI;
        }
    }

    public Integer getOffsetForSquareID(int mouseX, int mouseY) {

        // mouseX = pos.board[0].length - 1 - mouseX;
        // mouseY = pos.board.length - 1 - mouseY;
        int column = (mouseX - 102) / squareWidth;
        int row = (mouseY - 102) / squareWidth;

        if (isBoardFlipped) {
            int squareID = getPosId(pos.board[0].length - 1 - column, pos.board.length - 1 - row);
            return squareID;
        } else {
            int squareID = getPosId(column, row);
            return squareID;
        }

    }

    private boolean isValidPieceSelection(int squareID) {
        if (moveType > 2 || moveType < 1) {
            return false;
        }
        if (squareID < 0 || squareID >= pos.board[0].length * pos.board.length) {
            return false;
        }
        if (selectedPieceID == squareID) {
            return false;
        }
        return bPanel.moveablePiecesPosIDs.contains(squareID);

    }

    public boolean clickIsOnBoard(int mouseX, int mouseY) {
        if (mouseX < boardCoordinateX + offset) {
            return false;
        }
        if (mouseY < boardCoordinateY + offset) {
            return false;
        }
        if (mouseX > boardCoordinateX + offset + squareWidth * pos.board[0].length) {
            return false;
        }
        if (mouseX > boardCoordinateY + offset + squareWidth * pos.board.length) {
            return false;
        }
        return true;
    }

    public static int[] getOnScreenCoordinates(boolean isFlipped, int squareID) {

        int[] xy = Moves.squareIDtoColumnAndRow(squareID);
        return getOnScreenCoordinates(xy[0], xy[1], isFlipped);
    }

    public static int[] getOnScreenCoordinates(int x, int y, boolean isFlipped) {

        if (isFlipped) {

            int xCoord = boardCoordinateX + offset + (Data.r.bWidth - 1 - x) * squareWidth;
            int yCoord = boardCoordinateY + offset + (Data.r.bHeight - 1 - y) * squareWidth;

            return new int[]{xCoord, yCoord};

        } else {
            int xCoord = boardCoordinateX + offset + x * squareWidth;
            int yCoord = boardCoordinateY + offset + y * squareWidth;

            return new int[]{xCoord, yCoord};

        }
    }

    public String getScoreText() {
        return scoreText;
    }

    public boolean isWhiteIsAI() {
        return whiteIsAI;
    }

    public void setWhiteIsAI(boolean whiteIsAI) {
        this.whiteIsAI = whiteIsAI;
    }

    public boolean isBlackIsAI() {
        return blackIsAI;
    }

    public void setBlackIsAI(boolean blackIsAI) {
        this.blackIsAI = blackIsAI;
    }

    public boolean isBoardFlipped() {
        return isBoardFlipped;
    }

    public void setIsBoardFlipped(boolean isBoardFlipped) {
        this.isBoardFlipped = isBoardFlipped;
        bPanel.isFlipped = isBoardFlipped;
        bPanel.refresh();
    }

    void goBack() {
        if (pos.getParent() != null) {
            pos = pos.getParent();
            updatePly();
        }
    }

    void goForward() {
        if (pos.selectedChild != null) {
            pos = pos.selectedChild;
            updatePly();
        }

    }

}
