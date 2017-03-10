package checkers;

import checkers.Position.Stage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solver {

    public final Long initPositionID;
    private Position pos;
    boolean solved;
   

    public Solver() {

        
        solved = false;
        pos = new Position();
        initPositionID = pos.positionID;
        
        solve();
    }

    private void solve() {

        while (!solved) {
            switch (pos.getStage()) {

                case A_CREATED:  //ok
                    generateChildPositionsFromPositionIDs();  //ok

                    break;
                case B_CHILD_POSITIONS_POPULATED:   //ok
                    pos = moveToChild();
                    break;

                case C_PROCESSING_CHILDREN: //need
                    pos = moveToChild();  //keep

                    break;

                case D_SOLVED:  //need
                    pos = gotoParent(); //keep
            }

        }
    }

    private void generateChildPositionsFromPositionIDs() {

        Enums.Player childP = pos.p == Enums.Player.WHITE ? Enums.Player.BLACK : Enums.Player.WHITE;

        Set<Long> uncheckedMovesToProcess = pos.getUncheckedMoves();

        for (Long move : uncheckedMovesToProcess) {
            int childsBestPly = Data.getBestPly(move, childP);
            if (childsBestPly == 0) {
                Position childPos = new Position(move, pos);

                if (Stage.E_TERMINAL_POSITION == childPos.getStage()) {
                    Data.updateBestPly(childPos.positionID, childPos.p, childPos.lengthOfBestSequence);
                    pos.lengthOfBestSequence = updateNegPosChildPly(pos.lengthOfBestSequence, childPos.lengthOfBestSequence);
                    pos.setStage(Position.Stage.D_SOLVED);
                    return;
                }

                pos.getUnsolvedChildren().put(move, childPos);

            } else {
                pos.lengthOfBestSequence = updateNegPosChildPly(pos.lengthOfBestSequence, childsBestPly);
            }
        }
        pos.setUncheckedMoves(new HashSet<>());
        pos.setStage(Position.Stage.B_CHILD_POSITIONS_POPULATED);
    }

    private Position moveToChild() {
        if (pos.getUnsolvedChildren().isEmpty()) {
            pos.setStage(Position.Stage.D_SOLVED);
            return pos;
        }
        Map<Long, Position> unsolvedChildren = pos.getUnsolvedChildren();
        for (Map.Entry<Long, Position> entry : unsolvedChildren.entrySet()) {
            Long childID = entry.getKey();
            Position childPosition = entry.getValue();

            int childsBestPly = Data.getBestPly(childPosition.positionID, childPosition.p);

            if (childsBestPly != 0) { //childs is already solved
                pos.lengthOfBestSequence = updateNegPosChildPly(pos.lengthOfBestSequence, childPosition.lengthOfBestSequence);
                pos.getUnsolvedChildren().remove(childID);
            } else {
                pos.setStage(Stage.C_PROCESSING_CHILDREN);
                pos.getUnsolvedChildren().remove(childID);
                return childPosition;
            }
        }
        pos.setStage(Stage.D_SOLVED);
        return pos;
    }

    private Position gotoParent() {

        Data.updateBestPly(pos.positionID, pos.p, pos.lengthOfBestSequence);

        if (pos.parent == null) {
            System.out.println("Solved. Fastest win in " + pos.lengthOfBestSequence + " moves");

            System.out.println(Data.getSize());

            solved = true;
            return pos;
        } else {

            Long idToRemove = pos.positionID;
            Position parent = pos.parent;

            parent.lengthOfBestSequence = updateNegPosChildPly(parent.lengthOfBestSequence, pos.lengthOfBestSequence);

            parent.getUnsolvedChildren().remove(idToRemove);

            return parent;
        }
    }

    private int updateNegPosChildPly(int parentBestPly, int currentChildsPly) {

        if (currentChildsPly == 0) {
            return parentBestPly;
        }

        int plyForParentFromChild = Position.translateChildPlyToParentPly(currentChildsPly);

        boolean newValueIsBetter = isSecondPlyCountBetterThanFirst(parentBestPly, plyForParentFromChild);

        if (newValueIsBetter) {
            return plyForParentFromChild;
        } else {
            return parentBestPly;
        }

    }

    public static boolean isSecondPlyCountBetterThanFirst(int strongestKnownPlyCount, int newPly) {

        if (strongestKnownPlyCount == 0) {
            return true;
        }

        if (strongestKnownPlyCount > 0 && newPly > 0 && newPly < strongestKnownPlyCount) { //this child move gives a faster victory than the one we know
            return true;

        }
        if (strongestKnownPlyCount < 0 && newPly > 0) {  //we only knew losing moves, but this move is winning
            return true;
        }
        if (strongestKnownPlyCount < 0 && strongestKnownPlyCount > newPly) {  //known moves are losing, this child move leads to defeat. But a longewr one

            return true;
        }
        return false;
    }
}
