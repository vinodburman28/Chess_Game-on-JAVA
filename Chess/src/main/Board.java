package main;

import java.awt.*;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int square_size = 70;
    public static final int half_square = square_size / 2;

    public void draw(Graphics2D g2) {
      int c=0;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++)
            {
                if(c==0){
                    g2.setColor(new Color(12,116,105)); //Green Color
                    c=1;
                }
                else{
                    g2.setColor(new Color(175,115,70)); //brown color
                    c=0;
                }
                g2.fillRect(col*square_size, row*square_size, square_size,square_size);

            }
            if(c==0) {
                c=1;
            }
            else {
                c=0;
            }

        }
    }
}