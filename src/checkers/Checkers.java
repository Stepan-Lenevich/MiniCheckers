/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import gui.BoardFrame;

/**
 * MiniCheckers is a game based on Checkers 6x6. Two players play on 6x6 board,
 * starting with 6 pieces each. Rules are similar to standard checkers. Keep in
 * mind these features:
 *
 *
 * (1) Captures are forced. Player must capture all opponent's pieces he can.
 * (2) Landing on last line is an instant win condition. (3) If player's transit
 * jump lands on last line, but he can further capture, he must capture and the
 * game will continue.
 *
 * It takes several seconds to compute moves. Then the game board shows up.
 *
 * @author stepan
 */
public class Checkers {

    public static Data d;

    public static void main(String[] args) {

        System.out.println("Working Directory = "
                + System.getProperty("user.dir"));

        boolean showRules = true;  //

        if (showRules) {
            System.out.println(""
                    + " * (1) Captures are forced. Player must capture all opponent's pieces he can.\n"
                    + " * (2) Landing on last line is an instant win condition. \n"
                    + " * (3) If player's transit jump lands on last line, but he can further capture, he must capture and the game will continue.");
        }

        d = new Data();
        Checkers c = new Checkers();
        c.init();
    }

    private void init() {
        Solver s = new Solver();
        s.solve();
        BoardFrame b = new BoardFrame();
    }

}
