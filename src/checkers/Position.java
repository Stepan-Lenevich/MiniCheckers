package checkers;

import checkers.Enums.Player;
import checkers.Position.Stage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Position {

    public Set<Long> getUncheckedMoves() {
        return uncheckedMoves;
    }

    public void setUncheckedMoves(Set<Long> uncheckedMoves) {
        this.uncheckedMoves = uncheckedMoves;
    }

    public Position getParent() {
        return parent;
    }

    public enum Stage {
        A_CREATED, //checked that position is not terminal and generated Collection of child moves
        B_CHILD_POSITIONS_POPULATED, //Child ids (Long) converted to moves
        C_PROCESSING_CHILDREN, //started working on children
        D_SOLVED, //Each child was solved
        E_TERMINAL_POSITION            //this position is a victory of opponent (no poieces on the board, no legal moves, opponent on last line
    }

    private final Position parent;   // //
    private Set<Long> uncheckedMoves;        //
    private Map<Long, Position> unsolvedChildren;  //
    public final Player p;           // //
    public final Long positionID;   //  //positive for white, negative for black
    //public Long bestChild;
    public int lengthOfBestSequence;        //negative for losing, positive for winning, 0 for unsolved

    public byte[][] board;
    private Stage stage;
    public Position selectedChild; //used to keep track of the actual game

    public Position() {    //returns initial position

        parent = null;
        p = Player.WHITE;
        board = Data.r.getInitialPosition();
        positionID = Data.r.toIndex(board);

        unsolvedChildren = new HashMap<>();
        lengthOfBestSequence = 0;
        uncheckedMoves = Moves.getSetOfLegalMoves(board, p);
        stage = Stage.A_CREATED;
    }

    public Position(Long posID, Position parentInstance) {

        parent = parentInstance;
        p = parent.p == Player.WHITE ? Player.BLACK : Player.WHITE;
        positionID = posID;
        board = Data.r.toBoard(positionID);

        unsolvedChildren = new HashMap<>();

        boolean isLosingPos = Data.r.isLosingPosition(p, board);

        if (isLosingPos) {
            lengthOfBestSequence = -1;
            stage = Stage.E_TERMINAL_POSITION;
        } else {
            uncheckedMoves = Moves.getSetOfLegalMoves(board, p);
            if (uncheckedMoves.isEmpty()) {
                lengthOfBestSequence = -1;
                stage = Stage.E_TERMINAL_POSITION;
            }
            stage = Stage.A_CREATED;
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Map<Long, Position> getUnsolvedChildren() {
        return unsolvedChildren;
    }

    public void setUnsolvedChildren(Map<Long, Position> unsolvedChildren) {
        this.unsolvedChildren = unsolvedChildren;
    }

    public static int translateChildPlyToParentPly(int childPly) {
        if (childPly < 0) {
            return -1 * childPly;
        } else {
            return -1 * childPly - 1;
        }

    }

}
