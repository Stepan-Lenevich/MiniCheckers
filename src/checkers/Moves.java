
package checkers;

import checkers.Enums.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Moves {

    public static Set<Long> getSetOfLegalMoves(byte[][] position, Player p) {  //main loop  
        Set<Long> moves = new HashSet<>();
        Set<Long> jumps = new HashSet<>();

        for (byte y = 0; y < Data.r.bHeight; y++) {
            for (byte x = 0; x < Data.r.bWidth; x++) {
                if (position[y][x] == p.id) {

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

    private static Set<Long> getMovesForSquare(byte[][] position, byte y, byte x, Player p) {
        Set<Long> moves = new HashSet<>();
        if (x > 0) {
            
            int newI = y + p.direction;
            int newJ = x - 1;
            boolean isOnBoard = Data.isOnBoard(newI, newJ);
            
            if (!isOnBoard) {//remove this later
                System.out.println("Got a problem");
                System.out.println(p + " x: " + x+ " y: " + y );

                Data.r.printBoard(position);
                int pause = 1;
            }
            
            boolean leftIsfree = position[y + p.direction][x - 1] == Player.EMPTY.id;
            if (leftIsfree) {
                byte[][] left = Data.r.copyBoard(position);
                left[y][x] = Player.EMPTY.id;
                left[y + p.direction][x - 1] = p.id;
                
                
                
                if((p == Player.WHITE && y==0 )||(p == Player.BLACK && y==Data.r.bHeight-1 )){


                Data.r.printBoard(left);

                }  //remove this later
                
                
                
                
                
                moves.add(Data.r.toIndex(left));
            }
        }
        if (x < Data.r.bWidth - 1) {
            boolean rightIsFree = position[y + p.direction][x + 1] == Player.EMPTY.id;
            if (rightIsFree) {
                byte[][] right = Data.r.copyBoard(position);
                right[y][x] = Player.EMPTY.id;
                right[y + p.direction][x + 1] = p.id;
                
                
                                
                if((p == Player.WHITE && y==0 )||(p == Player.BLACK && y==Data.r.bHeight-1 )){


                Data.r.printBoard(right);
               
                }  
                
                
                
                moves.add(Data.r.toIndex(right));
            }
        }
        return moves;
    }

    public static Map<Long, byte[][]> getMapOfLegalMoves(byte[][] position, Player p) {

        Set<Long> moves = getSetOfLegalMoves(position, p);
        Map<Long, byte[][]> map = new HashMap<>();
        for (Long move : moves) {
            map.put(move, Data.r.toBoard(move));
        }
        return map;
    }

    public static boolean checkIfOpponentHasMoves(Position pos, Long childPositionId) {
        Player p = pos.p == Player.WHITE ? Player.BLACK : Player.WHITE;

    Set<Long> setOfChildMoves = getSetOfLegalMoves(Data.r.toBoard(childPositionId),p);
    return !setOfChildMoves.isEmpty();
    
    }
    
        public static Set<Long> getJumpsForSquare(byte[][] position, byte i, byte j, Player p) {

        Set<Long> processedJumps = new HashSet<>();
        Map<Long, byte[]> jumpsToProcess = getFirstJumps(position, i, j , p);
        while (!jumpsToProcess.isEmpty()) {

            Map<Long, byte[]> newJumps = new HashMap<>();

            for (Map.Entry<Long, byte[]> entry : jumpsToProcess.entrySet()) {
                Long currentPosition = entry.getKey();    
                byte[] currentSquare = entry.getValue();

                newJumps.putAll(getFollowingJumps(currentPosition, currentSquare[0],currentSquare[1], p));
                processedJumps.add(currentPosition);
            }

            newJumps.keySet().removeAll(processedJumps);
            jumpsToProcess = newJumps;

        }
        return processedJumps;

    }
    


    private static Map<Long, byte[]> getFirstJumps(byte[][] position, byte i, byte j, Player p) {
        
        Map<Long, byte[]> validJumps = new HashMap<>();
        if(canJumpForward(p, i)){
            
                    Enums.Side[] sidesToCheck = p.forward;  //get front        
        
        validJumps = getJumps(position,  i,  j, p, sidesToCheck);
        }
       return validJumps;

    }
    
    private static Map<Long, byte[]> getFollowingJumps(Long positionID, byte i, byte j, Player p) {
        return getFollowingJumps(Data.r.toBoard(positionID),  i,  j, p);
    }

    private static Map<Long, byte[]> getFollowingJumps(byte[][] position, byte i, byte j, Player p) {
        
        Enums.Side[] sidesToCheck = Enums.Side.values();
        return getJumps(position, i, j, p, sidesToCheck);
    }
    
    private static Map<Long, byte[]> getJumps(byte[][] position, byte i, byte j, Player p, Enums.Side[] sidesToCheck){
        
        Map<Long, byte[]> result = new HashMap<>();
        
        for (Enums.Side side : sidesToCheck) {
            
            boolean isLandingOnBoard = isLandingSquareWithinBoard(i+2*side.y,j+2*side.x);
            
            if(isLandingOnBoard){
            
            //to remove

            
            byte landing = position[i+2*side.y][j+2*side.x];
            byte flying = position[i+side.y][j+side.x];
            if(landing == Player.EMPTY.id && flying == p.opponentID){
                
                byte[][] newPosition = Data.r.copyBoard(position);
                newPosition[i][j] = Player.EMPTY.id;
                newPosition[i+side.y][j+side.x] = Player.EMPTY.id;
                newPosition[i+2*side.y][j+2*side.x] = p.id;
                
                byte[] location = {(byte)(i+2*side.y), (byte)(j+2*side.x)};
                
                result.put(Data.r.toIndex(newPosition), location);
            }
            }  
        }        
        return result;        
    }
    
        private static boolean isLandingSquareWithinBoard(int i, int j) {
            boolean isItWithinBoard = (i>=0 && i<Data.r.bHeight && j>=0 && j < Data.r.bWidth);  
            return isItWithinBoard;
    }
    
        
    private static boolean canJumpForward(Player p, int i) {
        if (p == Player.WHITE) {
            return i > 1;
        }
        if (p == Player.BLACK) {
            return i < Data.r.bHeight - 2;
        }
        System.out.println(p.toString() + " " + i + " wrong input");
        return false;
    }

}
