/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-06-08 (yyyy-dd-mm)

    Description: Main class to run the game.
*/

import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) throws LineUnavailableException, IOException {

        // create a frame to hold the game
        JFrame frame = new JFrame("Tower Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        
        TowerGame game = new TowerGame();
        
        // add the game to the frame
        frame.add(game);
        
        // frame.pack();
        frame.setVisible(true);

    }
}


