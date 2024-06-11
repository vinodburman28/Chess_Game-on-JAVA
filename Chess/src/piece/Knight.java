package piece;

import main.GamePanel;
import main.Type;

//for horse
public class Knight extends Piece{
    public Knight(int color, int col, int row) {
        super(color, col, row);

        type= Type.KNIGHT;
        if(color== GamePanel.WHITE ){
            image=getImage("/piece/white horse");
        }
        else {
            image=getImage("/piece/black horse");
        }
    }
    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol,targetRow)){
            //horse movement ration is 1:2 or 2:1
            if(Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==2){
                if(isValidSquare(targetCol,targetRow)){
                    return true;
                }
            }
        }

return false;
    }
}
