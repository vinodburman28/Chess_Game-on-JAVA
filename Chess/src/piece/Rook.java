package piece;

import main.GamePanel;
import main.Type;

//for elephant
public class Rook extends Piece {
    public Rook(int color, int col, int row) {
        super(color, col, row);

        type= Type.ROOK;
        if(color== GamePanel.WHITE ){
            image=getImage("/piece/white elephant");
        }
        else {
            image=getImage("/piece/black elephant");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false){
            //horse can move as long as either its col or row is the same
            if(targetCol==preCol||targetRow==preRow)
            {
                if(isValidSquare(targetCol,targetRow)&& pieceIsOnStraightLine(targetCol,targetRow)==false){
                    return true;
                }

            }
        }
        return false;
    }
}
