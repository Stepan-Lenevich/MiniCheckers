package checkers;

import checkers.Enums.Player;
import checkers.Enums.State;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rules {

    public final int bHeight;
    public final int bWidth;
    public final int numberOfPieces;
    public final int dummySquares = 0;

    public Rules(int bHeight, int bWidth, int numberOfPieces) {
        this.bHeight = bHeight;
        this.bWidth = bWidth;
        this.numberOfPieces = numberOfPieces;
    }

    public Rules(byte[][] board) {
        this.bHeight = board.length;
        this.bWidth = board[0].length;
        this.numberOfPieces = countPieces(board);
    }

    public long toIndex(byte[][] board) {
        byte coords[] = new byte[bHeight * bWidth / 2];
        int totalSquares = bHeight * bWidth / 2;
        byte k = 0;
        for (int i = 0; i < bHeight; i++) {
            for (int j = 0; j < bWidth / 2; j++) {
                byte makeItCheckers = (byte) ((i + 1) % 2);
                coords[k] = board[i][j * 2 + makeItCheckers];
                k++;
            }
        }
        long positionID = 0;
        for (int i = totalSquares - 1; i >= 0; i--) {
            positionID = positionID * 3 + coords[i];
        }
        return positionID;
    }

    public byte[][] getInitialPosition() {
        return getInitialPosition(bHeight, bWidth, numberOfPieces);
    }

    public byte[][] getInitialPosition(int bHeight, int bWidth, int numOfPieces) {

        int numOfLines = numOfPieces * 2 / bWidth;
        byte[][] position = new byte[bHeight][bWidth];

        for (int i = 0; i < bHeight; i++) {
            for (int j = 0; j < bWidth; j++) {
                boolean isDummy = (i + j) % 2 == dummySquares;
                if (isDummy) {
                    position[i][j] = Player.EMPTY.id;
                } else if (i < numOfLines) {
                    position[i][j] = Player.BLACK.id;
                } else if (i >= bHeight - numOfLines) {
                    position[i][j] = Player.WHITE.id;
                } else {
                    position[i][j] = Player.EMPTY.id;
                }
            }
        }
        return position;
    }

    private int countPieces(byte[][] board) {
        int white = countPieces(board, Player.WHITE);
        int black = countPieces(board, Player.BLACK);
        int result = Math.max(white, black);

        return result;
    }

    private int countPieces(byte[][] board, Player P) {
        int sum = 0;
        for (byte[] boardLine : board) {
            for (byte cell : boardLine) {
                if (cell == P.id) {
                    sum += 1;
                }
            }
        }
        return sum;
    }

    public byte[][] toBoard(long positionID) {
        int totalSquares = bHeight * bWidth / 2;

        int[] coords = new int[totalSquares];

        for (int i = 0; i < totalSquares; i++) {

            coords[i] = (int) (positionID % 3L);
            positionID = positionID / 3L;
        }
        byte[][] board = makeEmptyBoard(bHeight, bWidth);

        byte k = 0;
        for (int i = 0; i < bHeight; i++) {
            for (int j = 0; j < bWidth / 2; j++) {
                byte makeItCheckers = (byte) ((i + 1) % 2);
                board[i][j * 2 + makeItCheckers] = (byte) coords[k];
                k++;
            }
        }
        return board;
    }

    private byte[][] makeEmptyBoard(int bHeight, int bWidth) {
        byte[][] board = new byte[bHeight][bWidth];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = Player.EMPTY.id;

            }
        }
        return board;
    }

    public State isValidPosition(long index) {
        return Rules.this.getPositionState(toBoard(index));
    }

    public State getPositionState(byte[][] position) {

        int[] piecesOnFirstAndLastLines = countFirstLastLinePieces(position);
        if (piecesOnFirstAndLastLines[0] + piecesOnFirstAndLastLines[1] > 1) {
            return State.INVALID;
        }
        int[] remainingPieces = countRemainingPieces(position);
        int max = Math.max(remainingPieces[0], remainingPieces[1]);

        if (max > numberOfPieces || max == 0) {
            return State.INVALID;
        }

        if (piecesOnFirstAndLastLines[Player.WHITE.id] == 1) {
            return State.WHITEWINS;
        }
        if (piecesOnFirstAndLastLines[Player.BLACK.id] == 1) {
            return State.BLACKWINS;
        }

        if (remainingPieces[1] == 0) {
            return State.WHITEWINS;
        }
        if (remainingPieces[0] == 0) {
            return State.BLACKWINS;
        }
        return State.VALID;
    }

    public boolean isLosingPosition(Position pos) {
        return isLosingPosition(pos.p, pos.board);

    }

    public boolean isLosingPosition(Player p, byte[][] board) {

        if (!hasPieces(p, board)) {
            return true;
        }

        int lastLineID = p == Player.WHITE ? bHeight - 1 : 0;
        Player opponent = p == Player.WHITE ? Player.BLACK : Player.WHITE;
        byte valueToMatch = opponent.id;
        byte[] lastLine = board[lastLineID];
        for (int i = 0; i < lastLine.length; i++) {
            if (lastLine[i] == valueToMatch) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPieces(Player p, byte[][] board) {

        int[] pieces = countRemainingPieces(board);

        if (p == Player.WHITE) {
            return pieces[0] != 0;
        } else {
            return pieces[1] != 0;
        }
    }

    private int[] countRemainingPieces(byte[][] position) {

        int[] result = new int[2];
        for (byte[] line : position) {
            for (byte square : line) {
                if (square == Player.WHITE.id) {
                    result[0] += 1;
                }
                if (square == Player.BLACK.id) {
                    result[1] += 1;
                }
            }
        }
        return result;
    }

    private int[] countFirstLastLinePieces(byte[][] position) {
        int[] result = new int[2];

        for (byte square : position[0]) {
            if (square == Player.WHITE.id) {
                result[0] += 1;
            }
        }

        for (byte square : position[bHeight - 1]) {
            if (square == Player.BLACK.id) {
                result[1] += 1;
            }
        }
        return result;
    }

    public void printBoard(Long board) {
        printBoard(toBoard(board), true);
    }
    
        public void printBoard(byte[][] board) {
        printBoard(board, true);
    }

    public void printBoard(byte[][] board, boolean isWhite) {
        System.out.println(toString(board,isWhite) );
    }

    public String toString(Long positionID) {
        return toString(toBoard(positionID));
    }

    public String toString(byte[][] board) {
        return toString(board, true);
    }

    public String toString(byte[][] board, boolean isWhiteView) {
        
        if(!isWhiteView){
        board = rotateBoard(board);
        }

        StringBuilder sb = new StringBuilder("");

        String upperSpacer = getSpacer(bWidth, true, isWhiteView);
        String lowerSpacer = getSpacer(bWidth, false, isWhiteView);
        int counter = isWhiteView ? 1 : bHeight;
        int step = isWhiteView ? 1 : -1;
        sb.append("\n").append(upperSpacer).append("\n").append(counter).append(" |");

        for (int y = 0; y < bHeight; y++) {
            for (int x = 0; x < bWidth; x++) {

                if (x == 0 && y != 0) {

                    counter += step;
                    sb.append("|\n").append(counter).append(" |");
                }
                Player p = getPlayerByID(board[y][x]);

                char tokken;
                if (p == Player.WHITE || p == Player.BLACK) {
                    tokken = p.tokken;
                } else {
                    int squareParity = (y + x) % 2;

                    if (squareParity == dummySquares) {
                        tokken = ' ';
                    } else {
                        tokken = '.';
                    }
                }

                sb.append(" ").append(tokken).append(" ");
            }
        }
        sb.append("|\n").append(lowerSpacer);
        return sb.toString();
    }

    private String getSpacer(int boardWidth, boolean isTop, boolean isWhiteView) {
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};

        StringBuilder sbLetters = new StringBuilder("   ");
        StringBuilder sbBorder = new StringBuilder("  ");

        for (int i = 0; i < boardWidth; i++) {

            if (isWhiteView) {
                sbLetters.append(" ").append(letters[i]).append(" ");
            } else {
                sbLetters.append(" ").append(letters[boardWidth - 1 - i]).append(" ");
            }

            sbBorder.append("---");
        }
        sbBorder.append("-");
        if (isTop) {
            return sbBorder.toString();
        } else {
            return sbBorder.append("\n").append(sbLetters.toString()).toString();
        }
    }

    public void printMove(byte[][] boardOld, byte[][] boardNew) {
        char[][] overlay = computeMove(boardOld, boardNew);

        String upperSpacer = getSpacer(bWidth, true, true);
        String lowerSpacer = getSpacer(bWidth, false, true);
        int couter = 1;
        System.out.print("\n" + upperSpacer + "\n" + couter + " |");
        for (int y = 0; y < bHeight; y++) {

            for (int x = 0; x < bWidth; x++) {
                if (x == 0 && y != 0) {
                    couter++;
                    System.out.print("\n" + couter + " |");
                }

                System.out.print(" " + overlay[y][x] + " ");
            }
        }
        System.out.println("|\n" + lowerSpacer);
    }

    public static char[][] parseString(String rawPosition, int width) {

        int totalSquares = rawPosition.length();
        int height = totalSquares / width;
        char[][] board = new char[height][width];

        for (int it = 0; it < totalSquares; it++) {
            int i = it / width;
            int j = it % width;
            board[i][j] = rawPosition.charAt(it);
        }
        return board;
    }

    public static byte[][] charToByteBoard(char[][] board) {
        byte[][] boardPosition = new byte[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardPosition[i][j] = Enums.charToByteBoard(board[i][j]);
            }
        }

        return boardPosition;

    }

    private char[][] computeMove(byte[][] boardOld, byte[][] boardNew) {
        char[][] overlay = new char[boardOld.length][boardOld[0].length];

        for (int i = 0; i < boardOld.length; i++) {
            for (int j = 0; j < boardOld[0].length; j++) {
                if (boardOld[i][j] == Player.EMPTY.id) {
                    if (boardNew[i][j] == Player.EMPTY.id) {
                        overlay[i][j] = ' ';
                    } else if (boardNew[i][j] == Player.WHITE.id) {
                        overlay[i][j] = 'X';
                    } else if (boardNew[i][j] == Player.BLACK.id) {
                        overlay[i][j] = '@';
                    }
                } else if (boardOld[i][j] == Player.WHITE.id) {
                    if (boardNew[i][j] == Player.EMPTY.id) {
                        overlay[i][j] = '+';

                    } else if (boardNew[i][j] == Player.WHITE.id) {
                        overlay[i][j] = 'x';

                    }

                } else if (boardOld[i][j] == Player.BLACK.id) {
                    if (boardNew[i][j] == Player.EMPTY.id) {
                        overlay[i][j] = 'o';

                    } else if (boardNew[i][j] == Player.BLACK.id) {
                        overlay[i][j] = 'O';

                    }

                }

            }
        }
        return overlay;
    }

    public Player getPlayerByID(byte playerID) {
        for (Player p : Player.values()) {
            if (p.id == playerID) {
                return p;
            }
        }
        return Player.INVALID;
    }

    public byte[][] copyBoard(byte[][] boardState) {
        byte[][] newBoardState = new byte[bHeight][bWidth];

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                newBoardState[i][j] = boardState[i][j];
            }
        }
        return newBoardState;
    }

    private Long toIndexInConstructor(byte[][] position) {
        return toIndex(position);
    }

    public byte togglePlayerID(byte id) {
        if (id == Player.WHITE.id) {
            return Player.BLACK.id;
        }
        if (id == Player.BLACK.id) {
            return Player.WHITE.id;
        }
        try {
            throw new Exception("Player id " + id + " is invalid");
        } catch (Exception ex) {
            Logger.getLogger(Enums.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    


    void printPossibleMoves(List<Long> possibleMoves, boolean isWhiteView) {

        String boardsRepresentation = boardsToString(possibleMoves, isWhiteView);
        System.out.println(boardsRepresentation);

    }
    
    private String boardsToString(List<Long> possibleMoves) {
        return boardsToString(possibleMoves, true);
    }
    

    private String boardsToString(List<Long> possibleMoves, boolean isWhiteView) {

        StringBuilder sb = new StringBuilder("");

        List<byte[][]> boards = idsToBoards(possibleMoves);
        
        
        List<String[]> boardLines = new ArrayList<>();
        

        for (byte[][] board : boards) {

            String[] lines = toString(board, isWhiteView).split("\n");
            boardLines.add(lines);
        }
        int boardLength = boardLines.get(0)[4].length();
        for (int currentLine = 0; currentLine < boardLines.get(0).length; currentLine++) {

            String tokken = "";
            for (int currentBoard = 0; currentBoard < boardLines.size(); currentBoard++) {

                sb.append(tokken).append(boardLines.get(currentBoard)[currentLine]);

                if (currentLine == 1 || currentLine > boardLines.get(0).length - 3) {
                    tokken = "   ";
                } else {
                    tokken = "  ";
                }
            }
            sb.append("\n");
        }
        String firstCentering = new String(new char[boardLength / 2]).replace('\0', ' ');
        sb.append("\n").append(firstCentering);
        for (int i = 0; i < boardLines.size(); i++) {

            String spacesBetweenBoards = new String(new char[boardLength + 1]).replace('\0', ' ');

            sb.append(i).append(spacesBetweenBoards);
        }
        sb.append("\n");

        return sb.toString();

    }

    private List<byte[][]> idsToBoards(List<Long> possibleMoves) {

        List<byte[][]> result = new ArrayList<>();

        for (Long possibleMove : possibleMoves) {
            byte[][] board = toBoard(possibleMove);
            result.add(board);
        }

        return result;
    }

    public static byte[][] rotateBoard(byte[][] originalBoard) {


        byte[][] result = new byte[originalBoard.length][originalBoard[0].length];

        for (int i = 0; i < originalBoard.length; i++) {
            for (int j = 0; j < originalBoard[0].length; j++) {
                result[originalBoard.length - i - 1][originalBoard[0].length - j - 1] = originalBoard[i][j];
            }
        }
        return result;

    }

}
