package main;

import javax.swing.JFrame;

public class main {
    public static void main(String[]args){
        JFrame window =new JFrame("Chess Game");// frame of the game
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        //ADD GAME PANEL TO THE WINDOW
        GamePanel gp = new GamePanel(); // object of the game panel
        window.add(gp);
        window.pack(); // by this window adjust its size to this gamepanel

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.LaunchGame(); // start the game


    }
}
