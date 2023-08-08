/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-17-07
    Course: COMP 452
    Student ID: 3433058
    Assignment: 1
    Description: Creating a tower defense game with the implementation of Basic steering behaviors such as wander, arrive, flee and a complex steering behavior of formation.
    Class Description: This is the main class that runs the game.
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


