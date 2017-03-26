package checkers;

import checkers.Enums.Player;
import java.util.HashMap;
import java.util.Map;

public class Data {

    private static final Map<Long, Integer> positionToPlyMap;
    public static final Rules r;
    public static final Moves m;
    //public static final Position initialPosition;

    static {
        r = new Rules(6, 6, 6); //(6, 6, 6)
        m = new Moves();

        //initialPosition = new Position();
        positionToPlyMap = new HashMap<>();

    }

    public static void updateBestPly(Long positionID, Player p, Integer length) {
        Long id = p == Player.WHITE ? positionID : -1 * positionID;

        boolean betterValueFound = isThisVariantBetter(id, length);

        if (betterValueFound) {
            positionToPlyMap.put(id, length);
        }
    }

    public static int getBestPly(Long positionID, Player p) {
        Long id = p == Player.WHITE ? positionID : -1 * positionID;
        if (!positionToPlyMap.containsKey(id)) {
            return 0;
        } else {
            return positionToPlyMap.get(id);
        }
    }

    static boolean isOnBoard(int y, int x) {
        return y >= 0 && x >= 0 && y < r.bHeight && x < r.bWidth;

    }

    public static String getSize() {

        return "PositionToPlyMap size is " + positionToPlyMap.size();
    }

    /*
    public static boolean isPositionValid(Position pos) {
        int tokken = pos.p.getId();
        int opponentTokken = tokken == 0 ? 1 : 0;
        int lastLineIndex = opponentTokken * (Data.r.bHeight - 1);

        byte[] lastLine = pos.board[lastLineIndex];
        for (int i = 0; i < lastLine.length; i++) {
            if (lastLine[i] == opponentTokken) {
                return false;
            }

        }
        int sumOfActivePlayersPieces = 0;
        for (byte[] board : pos.board) {
            for (int j = 0; j < board.length; j++) {
                if (board[j] == tokken) {
                    sumOfActivePlayersPieces += 1;
                }
            }
        }
        if (sumOfActivePlayersPieces == 0) {
            return false;
        }
        Set<Long> moves = Moves.getSetOfLegalMoves(pos.board, pos.p);
        return !moves.isEmpty();
    } */
    private static boolean isThisVariantBetter(Long id, Integer length) {

        if (!positionToPlyMap.containsKey(id)) { //if no record - make a record
            return true;

        } else {
            Integer previousValue = positionToPlyMap.get(id);

            if (length > 0 && (previousValue < 0 || previousValue > length)) { //if this move winning and previous record was losing or longer winning
                return true;
            }
            if (length < 0 && previousValue > length) { //if this move is loosing but over longer time than recorded move
                return true;
            }

        }
        return false;

    }

}
