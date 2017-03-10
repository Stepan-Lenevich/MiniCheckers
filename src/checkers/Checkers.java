/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

/**
 *
 * @author stepan
 */
public class Checkers {   
    public static Data d;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         
        d = new Data();
        Checkers c = new Checkers();
        c.init();
       
        
       
        
    }

    private void init() {        
       Solver s = new Solver();
       Arena hwc = new Arena();
    }
    
}
