package checkers;


public class Enums {
    
    public enum State {
        VALID("valid"), WHITEWINS("white wins"), BLACKWINS("black wins"), INVALID("invalid"), UNCHECKED("Position haven't been checked");
        public final String value;

        private State(String value) {
            this.value = value;
        }
    };


    public enum Side {
        NE(-1, 1), SE(1, 1), SW(1, -1), NW(-1, -1);

        public final byte y;
        public final byte x;

        private Side(int i, int j) {
            this.y = (byte) i;
            this.x = (byte) j;

        }
    };

    public static enum Player {

        WHITE(0, 1, -1, 'X', new Side[]{Side.NE, Side.NW}),
        BLACK(1, 0, 1, 'O', new Side[]{Side.SE, Side.SW}),
        EMPTY(2, 2, 0, ' ', new Side[]{}),
        INVALID(-1, 3, 3, '*', new Side[]{}
        );

        byte id;
        byte opponentID;
        byte direction;
        char tokken;
        Side[] forward;

        private Player(int id, int opponentID, int direction, char tokken, Side[] forward) {
            this.id = (byte) id;
            this.opponentID = (byte) opponentID;
            this.direction = (byte) direction;
            this.tokken = tokken;
            this.forward = forward;
        }
    };

    public static byte charToByteBoard(char tokken) {
        tokken = Character.toUpperCase(tokken);
        for (Player p : Player.values()) {
            if (p.tokken == tokken) {
                return p.id;
            }
        }
        return Player.EMPTY.id;

    }
    
    public static Player getPlayerFromPositionID(Long posID){
        if(posID>0) return Player.WHITE;
        if(posID<0) return Player.BLACK;
        return Player.INVALID;
        
    }



}

