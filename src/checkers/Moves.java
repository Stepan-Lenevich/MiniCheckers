package checkers;

import checkers.Enums.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Moves {

    public static Map<Integer, Set<Integer>> getCoordinatesOfPiecesThatCanMove(Position pos) {

        return getCoordinatesOfPiecesThatCanMove(pos.board, pos.p);

    }

    public static Map<Integer, Set<Integer>> getCoordinatesOfPiecesThatCanMove(byte[][] position, Player p) {  //main loop  

        Map<Integer, Set<Integer>> movablePiecesCoordinates = new HashMap<>();
        Set<Integer> moves = new HashSet<>();
        Set<Integer> jumps = new HashSet<>();

        int remainingPieces = Rules.countPieces(position, p);
        if (remainingPieces < 1) {
            movablePiecesCoordinates.put(-1, moves);
            return movablePiecesCoordinates;
        }

        for (byte y = 0; y < Data.r.bHeight; y++) {
            for (byte x = 0; x < Data.r.bWidth; x++) {
                if (position[y][x] == p.getId()) {

                    Set<Long> legalJumps = getJumpsForSquare(position, y, x, p);
                    if (!legalJumps.isEmpty()) {
                        Integer posID = getPosId(x, y);
                        jumps.add(posID);

                    }
                    if (jumps.isEmpty()) {
                        Set<Long> legalMoves = getMovesForSquare(position, y, x, p);
                        if (!legalMoves.isEmpty()) {
                            Integer posID = getPosId(x, y);
                            moves.add(posID);
                        }
                    }
                }
            }
        }

        if (jumps.isEmpty()) {
            if (moves.isEmpty()) {
                movablePiecesCoordinates.put((-1), moves);

            } else {
                movablePiecesCoordinates.put(1, moves);
            }
            return movablePiecesCoordinates;
        }
        movablePiecesCoordinates.put(2, jumps);

        return movablePiecesCoordinates;
    }

    public static List<Integer> getArrayOfMovablePieces(byte[][] board, Player p) {
        List<Integer> jumpingPieces = new ArrayList<>();
        List<Integer> movablePieces = new ArrayList<>();

        for (int y = 0; y < Data.r.bHeight; y++) {
            for (int x = 0; x < Data.r.bWidth; x++) {
                if (board[y][x] == p.getId()) {

                    Set<Long> legalJumps = getJumpsForSquare(board, y, x, p);
                    if (!legalJumps.isEmpty()) {

                        jumpingPieces.add(getPosId(x, y));
                    }
                    if (legalJumps.isEmpty()) {
                        Set<Long> moves = getMovesForSquare(board, (byte) y, (byte) x, p);
                        if (!moves.isEmpty()) {
                            movablePieces.add(getPosId(x, y));

                        }
                    }
                }
            }
        }
        return jumpingPieces.isEmpty() ? movablePieces : jumpingPieces;
    }

    public static boolean hasJumps(byte[][] board, Player p) {  //main loop 

        for (byte y = 0; y < Data.r.bHeight; y++) {
            for (byte x = 0; x < Data.r.bWidth; x++) {
                if (board[y][x] == p.getId()) {

                    Set<Long> legalJumps = getJumpsForSquare(board, y, x, p);

                    if (!legalJumps.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
    public static Set<Long> getMovesForSquare(Long positionID, int selectedPieceID) {
        byte[][] board = Data.r.toBoard(positionID);
        int[] xy = Moves.squareIDtoColumnAndRow(selectedPieceID);
        Player p = board[xy[1]][xy[0]] == Player.WHITE.getId() ? Player.WHITE : Player.BLACK;
             return Moves.getMovesForSquare(board, (byte) xy[1], (byte) xy[0], p);
    }*/
    public static Set<Long> getMovesForSquare(byte[][] position, byte y, byte x, Player p) {
        Set<Long> moves = new HashSet<>();
        if (x > 0) {
            boolean leftIsfree = position[y + p.direction][x - 1] == Player.EMPTY.getId();
            if (leftIsfree) {
                byte[][] left = Data.r.copyBoard(position);
                left[y][x] = Player.EMPTY.getId();
                left[y + p.direction][x - 1] = p.getId();

                moves.add(Data.r.toIndex(left));
            }
        }
        if (x < Data.r.bWidth - 1) {
            boolean rightIsFree = position[y + p.direction][x + 1] == Player.EMPTY.getId();
            if (rightIsFree) {
                byte[][] right = Data.r.copyBoard(position);
                right[y][x] = Player.EMPTY.getId();
                right[y + p.direction][x + 1] = p.getId();

                if ((p == Player.WHITE && y == 0) || (p == Player.BLACK && y == Data.r.bHeight - 1)) {

                    Data.r.printBoard(right);

                }

                moves.add(Data.r.toIndex(right));
            }
        }
        return moves;
    }

    /*
    public static Map<Integer, Long> getMovesForSelectedSquare(byte[][] position, byte y, byte x, Player p) {
        Map<Integer, Long> moves = new HashMap<>();
        if (x > 0) {
            boolean leftIsfree = position[y + p.direction][x - 1] == Player.EMPTY.getId();
            if (leftIsfree) {
                Integer squareID = getPosId(x - 1, y + p.direction);
                byte[][] newPos = Data.r.copyBoard(position);
                newPos[y][x] = Player.EMPTY.getId();
                newPos[y + p.direction][x - 1] = p.getId();
                Long newPosID = Data.r.toIndex(newPos);
                moves.put(squareID, newPosID);

            }
        }
        if (x < Data.r.bWidth - 1) {
            boolean rightIsFree = position[y + p.direction][x + 1] == Player.EMPTY.getId();
            if (rightIsFree) {

                Integer squareID = getPosId(x + 1, y + p.direction);
                byte[][] newPos = Data.r.copyBoard(position);
                newPos[y][x] = Player.EMPTY.getId();
                newPos[y + p.direction][x + 1] = p.getId();
                Long newPosID = Data.r.toIndex(newPos);
                moves.put(squareID, newPosID);

            }
        }
        return moves;
    }
     */
    public static Set<Long> getSetOfLegalMoves(byte[][] position, Player p) {  //main loop  
        Set<Long> moves = new HashSet<>();
        Set<Long> jumps = new HashSet<>();

        for (byte y = 0; y < Data.r.bHeight; y++) {
            for (byte x = 0; x < Data.r.bWidth; x++) {
                if (position[y][x] == p.getId()) {

                    Set<Long> legalJumps = getJumpsForSquare(position, y, x, p);

                    jumps.addAll(legalJumps);

                    if (jumps.isEmpty()) {
                        Set<Long> legalMoves = getMovesForSquare(position, y, x, p);
                        moves.addAll(legalMoves);
                    }
                }
            }
        }

        if (jumps.isEmpty()) {
            return moves;
        }
        return jumps;
    }

    public static Map<Long, byte[][]> getMapOfLegalMoves(byte[][] position, Player p) {

        Set<Long> moves = getSetOfLegalMoves(position, p);
        Map<Long, byte[][]> map = new HashMap<>();
        for (Long move : moves) {
            map.put(move, Data.r.toBoard(move));
        }
        return map;
    }

    /*
    public static boolean checkIfOpponentHasMoves(Position pos, Long childPositionId) {
        Player p = pos.p == Player.WHITE ? Player.BLACK : Player.WHITE;

        Set<Long> setOfChildMoves = getSetOfLegalMoves(Data.r.toBoard(childPositionId), p);
        return !setOfChildMoves.isEmpty();

    }
     */
    public static Set<Long> getJumpsForSquare(byte[][] position, int i, int j, Player p) {

        Set<Long> terminalJumps = new HashSet<>();
        Set<Long> transientJumps = new HashSet<>();

        Map<Long, byte[]> jumpsToProcess = getFirstJumps(position, i, j, p);

        while (!jumpsToProcess.isEmpty()) {

            Map<Long, byte[]> newJumps = new HashMap<>();

            for (Map.Entry<Long, byte[]> entry : jumpsToProcess.entrySet()) {
                Long currentPosition = entry.getKey();
                byte[] currentSquare = entry.getValue();

                Map<Long, byte[]> jumpsIdToBoard = getFollowingJumps(currentPosition, currentSquare[0], currentSquare[1], p);

                if (jumpsIdToBoard.isEmpty()) {
                    terminalJumps.add(currentPosition);

                } else {
                    newJumps.putAll(jumpsIdToBoard);
                    transientJumps.add(currentPosition);
                }
            }

            newJumps.keySet().removeAll(terminalJumps);
            newJumps.keySet().removeAll(transientJumps);

            jumpsToProcess = newJumps;

        }
        return terminalJumps;
    }

    public static Map<Long, byte[]> getFirstJumps(byte[][] position, int i, int j, Player p) {

        Map<Long, byte[]> validJumps = new HashMap<>();
        if (canJumpForward(p, i)) {

            Enums.Side[] sidesToCheck = p.forward;  //get front        

            validJumps = getJumps(position, i, j, p, sidesToCheck);
        }
        return validJumps;
    }

    public static Map<Long, Integer> getFirstJumps(Long positionID, int startingSquareID, Player p) {

        Map<Long, Integer> squareAndPosIDMap = new HashMap<>();
        Map<Long, byte[]> validJumps = new HashMap<>();

        int[] xy = squareIDtoColumnAndRow(startingSquareID);

        if (canJumpForward(p, xy[1])) {

            Enums.Side[] sidesToCheck = p.forward;  //get front        

            validJumps = getJumps(Data.r.toBoard(positionID), xy[1], xy[0], p, sidesToCheck);
        }

        for (Map.Entry<Long, byte[]> entry : validJumps.entrySet()) {
            Long key = entry.getKey();
            byte[] value = entry.getValue();

            Integer endSquareId = getPosId(value[1], value[0]);

            squareAndPosIDMap.put(key, endSquareId);

        }
        return squareAndPosIDMap;
    }

    public static Map<Long, Integer> getFollowingJumps(Long positionID, int startingSquareID, Player p) {

        Map<Long, Integer> squareAndPosIDMap = new HashMap<>();
        int[] xy = squareIDtoColumnAndRow(startingSquareID);

        Map<Long, byte[]> jumpsMap = getFollowingJumps(Data.r.toBoard(positionID), (byte) xy[1], (byte) xy[0], p);

        for (Map.Entry<Long, byte[]> entry : jumpsMap.entrySet()) {
            Long key = entry.getKey();
            byte[] value = entry.getValue();
            System.out.println(value[1] + " " + value[0]);
            Integer endSquareId = getPosId(value[1], value[0]);

            squareAndPosIDMap.put(key, endSquareId);

        }

        return squareAndPosIDMap;
    }

    public static Map<Long, byte[]> getFollowingJumps(Long positionID, byte y, byte x, Player p) {
        return getFollowingJumps(Data.r.toBoard(positionID), y, x, p);
    }

    public static Map<Long, byte[]> getFollowingJumps(byte[][] position, byte y, byte x, Player p) {

        Enums.Side[] sidesToCheck = Enums.Side.values();
        return getJumps(position, y, x, p, sidesToCheck);
    }

    private static Map<Long, byte[]> getJumps(byte[][] position, int y, int x, Player p, Enums.Side[] sidesToCheck) {

        Map<Long, byte[]> result = new HashMap<>();

        for (Enums.Side side : sidesToCheck) {

            boolean isLandingOnBoard = isLandingSquareWithinBoard(y + 2 * side.y, x + 2 * side.x);

            if (isLandingOnBoard) {

                byte landing = position[y + 2 * side.y][x + 2 * side.x];
                byte flying = position[y + side.y][x + side.x];
                if (landing == Player.EMPTY.getId() && flying == p.opponentID) {

                    byte[][] newPosition = Data.r.copyBoard(position);
                    newPosition[y][x] = Player.EMPTY.getId();
                    newPosition[y + side.y][x + side.x] = Player.EMPTY.getId();
                    newPosition[y + 2 * side.y][x + 2 * side.x] = p.getId();

                    byte[] location = {(byte) (y + 2 * side.y), (byte) (x + 2 * side.x)};

                    result.put(Data.r.toIndex(newPosition), location);
                }
            }
        }
        return result;
    }

    private static boolean isLandingSquareWithinBoard(int i, int j) {
        boolean isItWithinBoard = (i >= 0 && i < Data.r.bHeight && j >= 0 && j < Data.r.bWidth);
        return isItWithinBoard;
    }

    private static boolean canJumpForward(Player p, int i) {
        if (p == Player.WHITE) {
            return i > 1;
        }
        if (p == Player.BLACK) {
            return i < Data.r.bHeight - 2;
        }
        return false;
    }

    public static int getPosId(int column, int row) {

        if (column < 0 || row < 0 || column >= Data.r.bWidth || row >= Data.r.bHeight) {
            return -1;
        }
        return row * Data.r.bWidth + column;
    }

    public static int[] squareIDtoColumnAndRow(int squareID) {

        int x = squareID % Data.r.bWidth;
        int y = squareID / Data.r.bWidth;

        return new int[]{x, y};

    }

    public static int[] posIdToArray(Long posId) {
        byte[][] board = Data.r.toBoard(posId);

        int[] boardArray = new int[Data.r.bHeight * Data.r.bWidth];
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                boardArray[y * board[0].length + x] = board[y][x];
            }
        }
        return boardArray;
    }

}
