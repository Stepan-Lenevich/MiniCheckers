package checkers;

import checkers.Enums.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

public class Arena {
    
    boolean[] isAI = {true,false};
    

    boolean isRotatedBoard = true;
    public Position pos;



    public Arena() {
        
        init();

    }

    private void init() {

        while (true) {
            pos = new Position();

            Enums.State boardState = Data.r.getPositionState(pos.board);

            while (boardState == Enums.State.VALID) {
                Data.r.printBoard(pos.board, isRotatedBoard);
                System.out.println("\n" + pos.p + " 's move\n");
                
                if (isAIMove(pos.p)) {
                
                    pos = selectComputerMove();

                } else {
                    pos = getHumanMove();

                }

                boardState = Data.r.getPositionState(pos.board);
                if (boardState == Enums.State.WHITEWINS) {
                    Data.r.printBoard(pos.board, isRotatedBoard);
                    System.out.println("\n\n---------------------------------------------\nWHITE WINS\n---------------------------------------------\n");
                }
                if (boardState == Enums.State.BLACKWINS) {
                    Data.r.printBoard(pos.board, isRotatedBoard);
                    System.out.println("\n\n---------------------------------------------\nBLACK WINS\n---------------------------------------------\n");
                
                }

            }

        }

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
        System.out.println("Best ply is "+ bestKnownPly+", moveID is "+bestKnownMove);
        return new Position(bestKnownMove, pos);
    }

    private Position getHumanMove() {

        Set<Long> moves = Moves.getSetOfLegalMoves(pos.board, pos.p);
        List<Long> possibleMoves = new ArrayList<>(moves);
        Data.r.printPossibleMoves(possibleMoves, isRotatedBoard);
        
        
        Scanner sc = new Scanner(System.in);
        int userInput = 0;
        while (true) {
            System.out.println("What is your move?\n");
            try {
                userInput = Integer.parseInt(sc.next());
                if(userInput >= 0 && userInput < possibleMoves.size())break; 
                
            } catch (NumberFormatException ignore) {
                System.out.println("Invalid input. What is your move?\n");
            }
        }
        
        

        return new Position(possibleMoves.get(userInput), pos);

    }

    private boolean isAIMove(Player p) {
        
        int index = p==Player.WHITE ? 0: 1;
        return isAI[index];
    }

}
